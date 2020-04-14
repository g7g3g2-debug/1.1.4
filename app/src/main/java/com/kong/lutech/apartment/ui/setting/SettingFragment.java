package com.kong.lutech.apartment.ui.setting;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.kong.lutech.apartment.Config;
import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.Permission;
import com.kong.lutech.apartment.ui.NfcActivity;
import com.kong.lutech.apartment.ui.StartActivity;
import com.kong.lutech.apartment.utils.DrawableUtil;
import com.kong.lutech.apartment.utils.SharedPreferenceUtil;
import com.kong.lutech.apartment.utils.sqlite.DBManager;
import com.kong.lutech.apartment.view.AppleSwitch;
import com.kongtech.lutech.apartment.R;
import com.kongtech.smapsdk.services.PersistentService;

import java.io.IOException;

/**
 * Created by kimdonghyuk on 2017. 3. 21..
 */

public class SettingFragment extends Fragment implements View.OnClickListener {
    private static final String PUSH_PARKING = "pushParking";
    private static final String PUSH_DELIVERY = "pushDelivery";
    private static final String PUSH_NOTICE = "pushNotice";
    private static final String PUSH_APP = "pushApp";
    private static final String PUSH_VIBRATE = "pushVibrate";
    private static final int RQ_BLUETOOTH_ENABLE = 491;

    private AppleSwitch awSettingPark, awSettingDelivery, awSettingNotice, awSettingPush, awSettingVibrate;
    private TextView tvParkingEnabled, tvDeliveryEnabled, tvNoticeEnabled, tvPushEnabled, tvVibrateEnabled;
    private AppCompatRadioButton rbSensitivityLow, rbSensitivityMiddle, rbSensitivityHigh;

    private TextView tvPushSetting;
    private LinearLayout llParkingContainer, llDeliveryContainer, llNoticeContainer, llAppPushContainer, llVibrateContainer;

    private Mobile mobile;
    private SharedPreferences.Editor editor;

    private ViewGroup nfcButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mobile = Config.getMobile();

        awSettingPark = (AppleSwitch) view.findViewById(R.id.awSettingParking);
        awSettingDelivery = (AppleSwitch) view.findViewById(R.id.awSettingDeliver);
        awSettingNotice = (AppleSwitch) view.findViewById(R.id.awSettingNotice);
        awSettingPush = (AppleSwitch) view.findViewById(R.id.awSettingPush);
        awSettingVibrate = (AppleSwitch) view.findViewById(R.id.awSettingVibrate);

        tvParkingEnabled = (TextView) view.findViewById(R.id.tvParkingEnabled);
        tvDeliveryEnabled = (TextView) view.findViewById(R.id.tvDeliveryEnabled);
        tvNoticeEnabled = (TextView) view.findViewById(R.id.tvNoticeEnabled);
        tvPushEnabled = (TextView) view.findViewById(R.id.tvPushEnabled);
        tvVibrateEnabled = (TextView) view.findViewById(R.id.tvVibrateEnabled);

        final RadioGroup rgAutoDoorSensitivity = (RadioGroup) view.findViewById(R.id.rgAutoDoorSensitivity);
        rbSensitivityLow = (AppCompatRadioButton) view.findViewById(R.id.rbSensitivityLow);
        rbSensitivityMiddle = (AppCompatRadioButton) view.findViewById(R.id.rbSensitivityMiddle);
        rbSensitivityHigh = (AppCompatRadioButton) view.findViewById(R.id.rbSensitivityHigh);

        tvPushSetting = view.findViewById(R.id.tvPushSetting);
        llParkingContainer = view.findViewById(R.id.llParkingContainer);
        llDeliveryContainer = view.findViewById(R.id.llDeliveryContainer);
        llNoticeContainer = view.findViewById(R.id.llNoticeContainer);
        llAppPushContainer = view.findViewById(R.id.llAppPushContainer);
        llVibrateContainer = view.findViewById(R.id.llVibrateContainer);

        nfcButton = view.findViewById(R.id.btn_nfc);
        nfcButton.setVisibility(mobile.getApartmentId() == 653 ? View.VISIBLE : View.GONE);
        nfcButton.getChildAt(1).setOnClickListener(v -> checkPermission());

        final Button btnLogout = (Button) view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(this);

        awSettingPark.setOnClickListener(this);
        awSettingDelivery.setOnClickListener(this);
        awSettingNotice.setOnClickListener(this);
        awSettingPush.setOnClickListener(this);
        awSettingVibrate.setOnClickListener(this);

        rgAutoDoorSensitivity.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbSensitivityLow:
                    new SharedPreferenceUtil.Builder(getActivity()).putInt(Config.PREFERENCE_KEY_THRESHOLD_INDEX, 0).commit();
                    break;
                case R.id.rbSensitivityMiddle:
                    new SharedPreferenceUtil.Builder(getActivity()).putInt(Config.PREFERENCE_KEY_THRESHOLD_INDEX, 1).commit();
                    break;
                case R.id.rbSensitivityHigh:
                    new SharedPreferenceUtil.Builder(getActivity()).putInt(Config.PREFERENCE_KEY_THRESHOLD_INDEX, 2).commit();
                    break;
            }

            final Intent rssiIntent = new Intent(PersistentService.INTENT_CHANGE_RSSI);
            rssiIntent.putExtra(PersistentService.KEY_RSSI, Config.getThreshold(getActivity()));
            rssiIntent.putExtra(PersistentService.KEY_DISCOVER, Config.getDiscoverRange(getActivity()));
            getActivity().sendBroadcast(rssiIntent);
        });

        DrawableUtil.SetBackgroundDrawable(btnLogout, ContextCompat.getColor(getActivity(), R.color.colorAccent), 0, getResources().getDimension(R.dimen.button_radius_4dp));

        final SharedPreferences pref = new SharedPreferenceUtil(getActivity()).getPref();
        editor = pref.edit();

        initPermission(pref, Config.getPermission());

        final boolean pushVibration = pref.getBoolean(SettingFragment.PUSH_VIBRATE, true);
        setVibratePush(pushVibration);
        awSettingVibrate.setChecked(pushVibration);

        return view;
    }

    private void initPermission(SharedPreferences pref, Permission permission) {

        // Parking
        final boolean isParkingInfo = permission.isParkingInfo();
        llParkingContainer.setVisibility(isParkingInfo ? View.VISIBLE : View.GONE);
        llAppPushContainer.setVisibility(isParkingInfo ? View.VISIBLE : View.GONE);

        if (!isParkingInfo) {
            editor
                    .putBoolean(PUSH_PARKING, false)
                    .putBoolean(PUSH_APP, false)
                    .commit();
        }
        final boolean pushParking = isParkingInfo && pref.getBoolean(SettingFragment.PUSH_PARKING, true);
        setParkingPush(pushParking);
        awSettingPark.setChecked(pushParking);
        final boolean pushApp = isParkingInfo && pref.getBoolean(SettingFragment.PUSH_APP, true);//pref.getBoolean(SettingFragment.PUSH_APP, true);
        setAppPush(pushApp);
        awSettingPush.setChecked(pushApp);

        // Delivery
        final boolean isDelivery = permission.isDelivery();
        llDeliveryContainer.setVisibility(isDelivery ? View.VISIBLE : View.GONE);

        if (!isDelivery) {
            editor.putBoolean(PUSH_DELIVERY, false).commit();
        }
        final boolean pushDelivery = isDelivery && pref.getBoolean(SettingFragment.PUSH_DELIVERY, true);
        setDeliveryPush(pushDelivery);
        awSettingDelivery.setChecked(pushDelivery);


        // Notice
        final boolean isNotice = permission.isDelivery();
        llNoticeContainer.setVisibility(isNotice ? View.VISIBLE : View.GONE);

        if (!isNotice) {
            editor.putBoolean(PUSH_NOTICE, false).commit();
        }
        final boolean pushNotice = pref.getBoolean(SettingFragment.PUSH_NOTICE, true);
        setNoticePush(pushNotice);
        awSettingNotice.setChecked(pushNotice);


        if (llParkingContainer.getVisibility() == View.GONE &&
                llDeliveryContainer.getVisibility() == View.GONE &&
                llNoticeContainer.getVisibility() == View.GONE &&
                llAppPushContainer.getVisibility() == View.GONE) {
            tvPushSetting.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final int thresholdIndex = new SharedPreferenceUtil(getActivity()).getPref().getInt(Config.PREFERENCE_KEY_THRESHOLD_INDEX, 1);
        switch (thresholdIndex) {
            case 0:
                rbSensitivityLow.setChecked(true);
                break;
            case 1:
                rbSensitivityMiddle.setChecked(true);
                break;
            case 2:
                rbSensitivityHigh.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.awSettingParking:
                editor.putBoolean("pushParking", awSettingPark.isChecked())
                        .commit();
                setParkingPush(awSettingPark.isChecked());
                break;
            case R.id.awSettingDeliver:
                editor.putBoolean("pushDelivery", awSettingDelivery.isChecked())
                        .commit();
                setDeliveryPush(awSettingDelivery.isChecked());
                break;
            case R.id.awSettingNotice:
                editor.putBoolean("pushNotice", awSettingNotice.isChecked())
                        .commit();
                setNoticePush(awSettingNotice.isChecked());
                break;
            case R.id.awSettingPush:
                editor.putBoolean("pushApp", awSettingPush.isChecked())
                        .commit();
                setAppPush(awSettingPush.isChecked());

                Log.d("SettingFragment", "isChecked - " + awSettingPush.isChecked());
                Log.d("SettingFragment", "getBoolean - " + getActivity().getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE).getBoolean("pushApp", true));

                sendChangedAppPushEvent();
                break;
            case R.id.awSettingVibrate:
                editor.putBoolean("pushVibrate", awSettingVibrate.isChecked())
                        .commit();
                setVibratePush(awSettingVibrate.isChecked());
                break;

            case R.id.btnLogout:

                setNoticePush(false);
                setDeliveryPush(false);
                setParkingPush(false);
                setAppPush(false);

                getActivity().sendBroadcast(new Intent(PersistentService.INTENT_LOGOUT));

                Config.Logout();
                getActivity().getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                final DBManager dbManager = new DBManager(getActivity()).open();
                dbManager.removeAll();
                dbManager.close();

                new Thread(() -> {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                final Intent intent = new Intent(getActivity(), StartActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finishAffinity();
                break;
        }
    }

    private void setNoticePush(boolean subscribe) {
        if (subscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic("apartments." + mobile.getApartmentId() + ".notices");
            tvNoticeEnabled.setVisibility(View.VISIBLE);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("apartments." + mobile.getApartmentId() + ".notices");
            tvNoticeEnabled.setVisibility(View.INVISIBLE);
        }
    }

    private void setDeliveryPush(boolean subscribe) {
        if (subscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic("homes." + mobile.getHomeId() + ".deliveries");
            tvDeliveryEnabled.setVisibility(View.VISIBLE);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("homes." + mobile.getHomeId() + ".deliveries");
            tvDeliveryEnabled.setVisibility(View.INVISIBLE);
        }
    }

    private void setParkingPush(boolean subscribe) {
        if (subscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic("homes." + mobile.getHomeId() + ".cctv-logs");
            tvParkingEnabled.setVisibility(View.VISIBLE);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("homes." + mobile.getHomeId() + ".cctv-logs");
            tvParkingEnabled.setVisibility(View.INVISIBLE);
        }
    }

    private void setAppPush(boolean enable) {
        if (enable) {
            tvPushEnabled.setVisibility(View.VISIBLE);
        } else {
            tvPushEnabled.setVisibility(View.INVISIBLE);
        }
    }

    private void setVibratePush(boolean enable) {
        final Intent intent = new Intent(PersistentService.INTENT_CHANGE_VIBRATE);
        intent.putExtra(PersistentService.KEY_VIBRATE, enable);
        getActivity().sendBroadcast(intent);

        tvVibrateEnabled.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    private void sendChangedAppPushEvent() {
        getActivity().sendBroadcast(new Intent(PersistentService.INTENT_SETTING_PUSH));
    }


    public void checkPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new BasePermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        requestScan();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("NFC 단말기와의 연결을 위해서는 블루투스 권한이 필요합니다.")
                                .setPositiveButton("확인", null)
                                .setOnDismissListener(dialog -> {
                                    openPermissionSetting();
                                }).show();
                    }
                }).check();
    }

    private void requestScan() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (adapter == null) {
            showUnsupportedDevice();
        } else if (!adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, RQ_BLUETOOTH_ENABLE);
        } else if (adapter.getBluetoothLeScanner() == null) {
            showUnsupportedDevice();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(getContext())
                    .setMessage("NFC 단말기와의 연결을 위해서는 위치가 켜져 있어야 합니다.")
                    .setPositiveButton("확인", (dialog, which) -> {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    })
                    .setNegativeButton("취소", null)
                    .show();
        } else {
            startActivity(new Intent(getContext(), NfcActivity.class));
        }
    }

    private void showUnsupportedDevice() {
        new AlertDialog.Builder(getContext())
                .setMessage("이 기기에서는 해당기능을 지원하지 않습니다.")
                .setPositiveButton("확인", null)
                .show();
    }

    private void openPermissionSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQ_BLUETOOTH_ENABLE) {
            if (resultCode == Activity.RESULT_OK)
                requestScan();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
