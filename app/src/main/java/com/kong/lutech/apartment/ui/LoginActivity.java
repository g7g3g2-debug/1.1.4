package com.kong.lutech.apartment.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.firebase.FirebasePushInstanceIDService;
import com.kong.lutech.apartment.model.Apartment;
import com.kong.lutech.apartment.model.Dong;
import com.kong.lutech.apartment.model.Home;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.MobileRssi;
import com.kong.lutech.apartment.model.Permission;
import com.kong.lutech.apartment.network.NetworkConfig;
import com.kong.lutech.apartment.network.api.authentication.AuthenticationInterface;
import com.kong.lutech.apartment.network.api.authentication.AuthenticationRestUtil;
import com.kong.lutech.apartment.utils.DeviceManager;
import com.kong.lutech.apartment.utils.SharedPreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private static final int LOGIN_TIMEOUT = 10000;

    private AppCompatSpinner spinnerDong, spinnerHome;
    private EditText etAuthcode;

    private Apartment apartment;
    private List<Dong> dongList = new ArrayList<>();
    private List<Home> homeList = new ArrayList<>();
    private ArrayAdapter<Dong> dongAdapter;
    private ArrayAdapter<Home> homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        homeButtonEnabled();

        spinnerDong = (AppCompatSpinner) findViewById(R.id.spinnerDong);
        spinnerHome = (AppCompatSpinner) findViewById(R.id.spinnerHome);
        etAuthcode = (EditText)findViewById(R.id.etAuthcode);
        findViewById(R.id.btnLogin).setOnClickListener(this);

        apartment = getIntent().getParcelableExtra("Apartment");

        dongAdapter = new ArrayAdapter(getApplication(), R.layout.spinner_item, dongList);
        dongAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDong.setAdapter(dongAdapter);
        spinnerDong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Dong selectedDong = dongList.get(position);

                final String clientToken = new SharedPreferenceUtil(getApplicationContext()).getPref().getString("clientToken", "");
                AuthenticationRestUtil.getIntsance(getApplicationContext())
                        .getHomeList("Bearer " + clientToken, apartment.getApartmentId(), selectedDong.getSequence())
                        .compose(bindToLifecycle())
                        .subscribe(homeListResponse -> {
                            if (homeListResponse.isSuccessful()) {
                                homeList = homeListResponse.body().getHomes();
                                homeAdapter.clear();
                                for (Home home : homeList) {
                                    homeAdapter.add(home);
                                }

                                homeAdapter.notifyDataSetChanged();
                            } else {
                                failLoadInfo();
                            }
                        }, throwable -> {
                            throwable.printStackTrace();
                            failLoadInfo();
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeAdapter = new ArrayAdapter(getApplication(), R.layout.spinner_item, homeList);
        homeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHome.setAdapter(homeAdapter);

        requestDongList();
    }

    private void failLoadInfo() {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("정보를 불러오는데 실패하였습니다.")
                .setPositiveButton("확인", null)
                .show();
    }

    private void requestDongList() {
        final String clientToken = new SharedPreferenceUtil(getApplicationContext()).getPref().getString("clientToken", "");
        AuthenticationRestUtil.getIntsance(getApplicationContext()).getDongList("Bearer " + clientToken, apartment.getApartmentId())
                .compose(bindToLifecycle())
                .subscribe(dongListResponse -> {
                    if (dongListResponse.isSuccessful()) {

                        dongList = dongListResponse.body().getDongs();
                        dongAdapter.clear();
                        for (Dong dong : dongList) {
                            dongAdapter.add(dong);
                        }

                        dongAdapter.notifyDataSetChanged();

                    } else {
                        failLoadInfo();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    failLoadInfo();
                });
    }

    private Handler loginRequestHandler = null;
    private Disposable loginRequestDisposable = null;

    private boolean registeredReceiver = false;

    @Override
    public void onClick(View view) {
        final String AUTHCODE = etAuthcode.getText().toString();
        if (TextUtils.isEmpty(AUTHCODE)) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setMessage("인증 번호을 입력해 주세요.")
                    .setPositiveButton("확인", null)
                    .show();
            return;
        }

        //loginDialog = ProgressDialog.show(LoginActivity.this, null, "로그인 중입니다...", true, false);

        // 10초 타임아웃을 위한 Handler
        if (loginRequestHandler != null) loginRequestHandler.removeCallbacksAndMessages(null);
        loginRequestHandler = new Handler();
        loginRequestHandler.postDelayed(() -> {
            // 대기 중인 FCM Token 해제
            if (registeredReceiver) {
                unregisterReceiver(refreshTokenReceive);
                registeredReceiver = false;
            }
            // 요청중인 로그인 중지
            if (loginRequestDisposable != null && !loginRequestDisposable.isDisposed()) loginRequestDisposable.dispose();

            //loginDialog.dismiss();

            new AlertDialog.Builder(LoginActivity.this)
                    .setMessage("로그인에 실패하였습니다.\n다시 시도해 주세요.")
                    .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                    .show();
        }, LOGIN_TIMEOUT);

        // FCM Token값이 있을 때는 바로 Login을 아닐 때는 토큰값을 대기한다.
        if(!TextUtils.isEmpty(FirebaseInstanceId.getInstance().getToken())) {
            requestLogin(AUTHCODE);
        } else {
            Log.d(TAG, "Register Receiver");
            IntentFilter intentFilter = new IntentFilter(FirebasePushInstanceIDService.INTENT_REFRESH_TOKEN);
            registerReceiver(refreshTokenReceive, intentFilter);
            registeredReceiver = true;
        }
    }

    private BroadcastReceiver refreshTokenReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "refreshTokenReceive");
            if (intent.getAction().equals(FirebasePushInstanceIDService.INTENT_REFRESH_TOKEN)) {
                final String AUTHCODE = etAuthcode.getText().toString();

                requestLogin(AUTHCODE);
            }
        }
    };

    private void requestLogin(String AUTHCODE) {
        Log.d(TAG, "requestLogin");
        final Dong selectedDong = dongList.get(spinnerDong.getSelectedItemPosition());
        final Home selectedHome = homeList.get(spinnerHome.getSelectedItemPosition());

        loginRequestDisposable = AuthenticationRestUtil.getIntsance(getApplicationContext()).login(NetworkConfig.BASIC_TOKEN, AUTHCODE, apartment.getApartmentId(), selectedDong.getDongId(), selectedDong.getSequence(), selectedHome.getHomeId(), selectedHome.getSequence(), FirebaseInstanceId.getInstance().getToken())
                .compose(bindToLifecycle())
                .subscribe(loginResultResponse -> {
                    if (loginResultResponse.isSuccessful()) {
                        final AuthenticationInterface.LoginResult loginResult = loginResultResponse.body();

                        requestMobileRssi(loginResultResponse, loginResult, selectedDong, selectedHome);
                    } else {
                        requestLoginNotSuccessful(loginResultResponse);
                    }
                }, this::requestLoginFailure);
    }

    private void requestMobileRssi(Response<AuthenticationInterface.LoginResult> loginResultResponse, AuthenticationInterface.LoginResult loginResult, final Dong dong, final Home home) {
        AuthenticationRestUtil.getIntsance(getApplicationContext()).getMobileRssi("Bearer " + loginResult.getAccessToken(), DeviceManager.getDeviceName())
                .compose(bindToLifecycle())
                .subscribe(mobileRssiResultResponse -> {
                    if (mobileRssiResultResponse.isSuccessful()) {
                        final MobileRssi mobileRssi = mobileRssiResultResponse.body().getMobileRssi();

                        requestLoginSuccess(loginResult, mobileRssi, dong, home);
                    } else {
                        requestLoginNotSuccessful(loginResultResponse);
                    }
                }, this::requestLoginFailure);
    }

    private void requestLoginSuccess(AuthenticationInterface.LoginResult loginResult, MobileRssi mobileRssi, final Dong dong, final Home home) throws Exception {
        if (loginRequestHandler != null) loginRequestHandler.removeCallbacksAndMessages(null);

        final Mobile mobile = loginResult.getMobile();
        mobile.setDong(dong);
        mobile.setHome(home);

        final Permission permission =loginResult.getPermission();

        final String encryToken = Config.Encrypt("Bearer " + loginResult.getAccessToken(), Config.KEY);
        new SharedPreferenceUtil.Builder(getApplicationContext())
                .putString(Config.PREFERENCE_KEY_ACCESS_TOKEN, encryToken)
                .putString(Config.PREFERENCE_KEY_AUTHCODE, mobile.getAuthCode())
                .putString(Config.PREFERENCE_KEY_MOBILE, new Gson().toJson(mobile))
                .putString(Config.PREFERENCE_KEY_MOBILE_RSSI, new Gson().toJson(mobileRssi))
                .putString(Config.PREFERENCE_KEY_PERMISSION, new Gson().toJson(permission))
                .putInt(Config.PREFERENCE_KEY_THRESHOLD_INDEX, 1)
                .commit();

        Log.d(TAG, "Login Request Success");
        Log.d(TAG, "Mobile RSSI : " + new Gson().toJson(mobileRssi));

        Config.setAccessToken(encryToken);
        Config.setAuthCode(mobile.getAuthCode());
        Config.setMobile(mobile);
        Config.setMobileRssi(mobileRssi);

        //if (loginDialog != null && loginDialog.isShowing()) loginDialog.dismiss();

        if (registeredReceiver) {
            unregisterReceiver(refreshTokenReceive);
            registeredReceiver = false;
        }

        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void requestLoginNotSuccessful(Response<AuthenticationInterface.LoginResult> loginResultResponse) throws IOException {
        if (loginRequestHandler != null) loginRequestHandler.removeCallbacksAndMessages(null);

        final String responseString = loginResultResponse.errorBody().string();
        final JsonObject object = new JsonParser().parse(responseString).getAsJsonObject();
        final String message = object.get("message").getAsString();

        if (registeredReceiver) {
            unregisterReceiver(refreshTokenReceive);
            registeredReceiver = false;
        }

        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("확인", null)
                .show();
    }

    private void requestLoginFailure(Throwable throwable) {
        throwable.printStackTrace();

        //if (loginDialog != null && loginDialog.isShowing()) loginDialog.dismiss();
        if (registeredReceiver) {
            unregisterReceiver(refreshTokenReceive);
            registeredReceiver = false;
        }

        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("로그인 요청에 실패하였습니다.")
                .setPositiveButton("확인", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
