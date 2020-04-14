package com.kong.lutech.apartment.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kong.lutech.apartment.Config;
import com.kongtech.lutech.apartment.R;
import com.kong.lutech.apartment.network.api.authentication.AuthenticationRestUtil;
import com.kong.lutech.apartment.utils.SharedPreferenceUtil;
import com.kong.lutech.apartment.utils.ble.BlePermission;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    public static int PERMISSION_REQUEST_CODE = 1001;
    public static int REQUEST_GOOGLE_PLAY_SERVICES = 2002;
    public static int REQ_CODE_OVERLAY_PERMISSION = 3003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            final boolean isPlayServiceAvailable = checkPlayServices();
            Log.d(TAG, "isPlayServiceAvailable : " + isPlayServiceAvailable);
        }

        final List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        new BlePermission.Builder(StartActivity.this)
                .setPermissions(permissions)
                .setRequestCode(PERMISSION_REQUEST_CODE)
                .setRequestMessage("블루투스 기능을 사용합니다.")
                .check();

        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        Log.d(TAG, MainActivity.class.getName());

        final Button btnStart = (Button) findViewById(R.id.btnStart);

        if (Build.VERSION.SDK_INT >= 29) {
            if (!checkDrawOverlayPermission(this)) {
                new AlertDialog.Builder(this)
                        .setMessage("안드로이드 10.0 버전 이상 부터는 백그라운드 검색을 위해 권한이 필요합니다.\n\n[어플리케이션] > [smap] > [다른 앱 위에 그리기]를 활성화 해주세요.")
                        .setPositiveButton("확인", (dialog, which) -> {

                        })
                        .setOnDismissListener(dialog -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
                        })
                        .show();
            }
        }

        btnStart.setOnClickListener(view -> {

            final ProgressDialog certificationProgress = ProgressDialog.show(StartActivity.this, null, "클라이언트 인증 중입니다..", true, false);

            AuthenticationRestUtil.getIntsance(getApplicationContext())
                    .certificationMobileClient()
                    .compose(bindToLifecycle())
                    .subscribe(responseBodyResponse -> {
                        if (responseBodyResponse.isSuccessful()) {

                            final JSONObject jsonObject = new JSONObject(responseBodyResponse.body().string());
                            final String clientToken = jsonObject.getString(Config.PREFERENCE_KEY_ACCESS_TOKEN);

                            if (!TextUtils.isEmpty(clientToken)) {


                                Log.d(TAG, "Response result clientToken = " + clientToken);
                                if (Config.isLogin(getApplicationContext())) {
                                    new SharedPreferenceUtil.Builder(getApplicationContext())
                                            .putString("clientToken", clientToken)
                                            .commit();
                                    certificationProgress.dismiss();
                                    Config.settingData(getApplicationContext());

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                    return;
                                } else {
                                    Config.Logout();
                                    getSharedPreferences(Config.PREFERENCE_KEY, MODE_PRIVATE).edit().clear().commit();

                                    new SharedPreferenceUtil.Builder(getApplicationContext())
                                            .putString("clientToken", clientToken)
                                            .commit();
                                    certificationProgress.dismiss();


                                    startActivity(new Intent(getApplicationContext(), TermsActivity.class));
                                    return;
                                }
                            }
                        }
                        certificationProgress.dismiss();
                        failCertificationClient();

                    }, throwable -> {
                        certificationProgress.dismiss();

                        throwable.printStackTrace();
                        failCertificationClient();
                    });
        });

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        REQUEST_GOOGLE_PLAY_SERVICES).show();
            }

            return false;
        }

        return true;
    }

    private void failCertificationClient() {
        new AlertDialog.Builder(StartActivity.this)
                .setMessage("클라이언트 인증에 실패하였습니다.\n네트워크를 확인해 주세요.")
                .setPositiveButton("확인", null)
                .show();
    }

    public boolean checkDrawOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_OVERLAY_PERMISSION) {
            if (resultCode != RESULT_OK) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //권한 허가
            } else {
                //권한 거부
                new android.app.AlertDialog.Builder(StartActivity.this)
                        .setMessage("안드로이드 6.0 버전 이상부터는 블루투스 기능을 사용하기 위해서 위치 권한이 필요합니다.\n\n[설정] > [권한]에서 해당권한을 활성화해주세요.")
                        .setNegativeButton("닫기", (dialogInterface, i) -> finish())
                        .setPositiveButton("설정", (dialogInterface, i) -> {
                            final Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);
                        })
                        .show();
            }
        }
    }
}
