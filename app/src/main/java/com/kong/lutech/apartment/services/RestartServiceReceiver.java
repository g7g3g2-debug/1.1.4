package com.kong.lutech.apartment.services;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.kong.lutech.apartment.Config;
import com.kong.lutech.apartment.ui.MainActivity;
import com.kong.lutech.apartment.ui.PushActivity;
import com.kongtech.lutech.apartment.R;
import com.kongtech.smapsdk.services.PersistentService;

/**
 * Created by gimdonghyeog on 2017. 6. 5..
 *
 * 핸드폰이 부탕이 될 때 이벤트를 받는 Receiver로
 * 이벤트를 받게 되면 PersistentService를 실행 시킴
 */

public class RestartServiceReceiver extends BroadcastReceiver {
    private static final String TAG = "RestartServiceReceiver";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "RestartServiceReceiver called : " + intent.getAction());

        /**
         * 서비스 죽일 때 알람으로 다시 서비스 등록
         */
        if (intent.getAction().equals("ACTION.RESTART.PersistentService")) {
            Log.i(TAG, "ACTION.RESTART.PersistentService");
            if (Config.isLogin(context) && BluetoothAdapter.getDefaultAdapter().isEnabled())
            {
                startPersistentService(context);
            }
        }
        /**
         * 기기가 재시작 할 때 서비스 등록
         */
        else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && Config.isLogin(context)) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED");

            startPersistentService(context);
        }
    }

    private void startPersistentService(Context context) {
        this.context = context;

        Log.d(TAG, "Start PersistentService");
        Config.settingData(context);

        Intent persistentService = new Intent(context, PersistentService.class);
        persistentService.putExtra(PersistentService.INTENT_SETTING_PERSISTENT, new PersistentService.PersistentSetting(MainActivity.class.getName(), R.mipmap.ic_launcher, PushActivity.class.getName()));
        persistentService.putExtra(PersistentService.INTENT_SETTING_THRESHOLD, Config.getThreshold(context));
        persistentService.putExtra(PersistentService.INTENT_SETTING_DISCOVERRANGE, Config.getDiscoverRange(context));
        persistentService.putExtra(PersistentService.INTENT_SETTING_RATIOVALUE, Config.getRatioValue());
        persistentService.putExtra(PersistentService.INTENT_SETTING_AUTHCODE, Config.getAuthCode());

        persistentService.putExtra(PersistentService.INTENT_SETTING_VIBRATE,
                context.getSharedPreferences(Config.PREFERENCE_KEY, Context.MODE_PRIVATE)
                        .getBoolean("pushVibrate", true));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Runned startForegroundService");
            context.startForegroundService(persistentService);
        } else {
            Log.d(TAG, "Runned startService");
            context.startService(persistentService);
        }
    }
}
