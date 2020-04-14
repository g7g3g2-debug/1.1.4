package com.kong.lutech.apartment.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.Bundle;
import android.view.WindowManager;

import com.kongtech.lutech.apartment.R;
import com.kongtech.smapsdk.services.PersistentService;

public class TranslucentSettingActivity extends Activity {
    public static final String INTENT_SETUP_SUCCESS = "INTENT_SETUP_SUCCESS";

    public static final String KEY_ISAFTER_ADV = "KEY_IS_AFTER_ADV";

    private BroadcastReceiver scanResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String ACTION = intent.getAction();

            if(ACTION.equals(PersistentService.INTENT_SCAN_FINISHED)) {
                if (isReceiverRegistered) {
                    unregisterReceiver(scanResultReceiver);
                    isReceiverRegistered = false;
                }
                TranslucentSettingActivity.this.finish();
            }
        }
    };

    private boolean isReceiverRegistered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        );
        setContentView(R.layout.activity_bluetooth_setting);

        // 화면 잠금일 때 속도가 빨라질 수 있게 화면을 꺠움
        wakeScreen();

        // BroadcastReceiver 등록
        registerReceiver();

        // 정상 실행 됬다는 Intent 전송
        sendOnCreate();
    }

    private void sendOnCreate() {

        final Intent onCreateIntent = new Intent(TranslucentSettingActivity.INTENT_SETUP_SUCCESS);
        sendBroadcast(onCreateIntent);
    }

    private void registerReceiver() {

        if (!isReceiverRegistered) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PersistentService.INTENT_SCAN_FINISHED);
            registerReceiver(scanResultReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void wakeScreen() {
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "SMAP Background Bluetooth");
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        //wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        if (isReceiverRegistered) {
            unregisterReceiver(scanResultReceiver);
            isReceiverRegistered = false;
        }
        super.onDestroy();
    }
}
