package com.kong.lutech.apartment.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kong.lutech.apartment.ui.MainActivity;
import com.kong.lutech.apartment.utils.ServiceMonitor;
import com.kong.lutech.apartment.utils.sqlite.DBManager;

import java.util.Map;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class FirebasePushMessagingService extends FirebaseMessagingService {
    private String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        handleMessage(remoteMessage.getData());

    }

    private void handleMessage(Map<String, String> data) {
        /**
         * "notification" : {
         *      "body" : "내용",
         *      "title" : "제목"
         * }
         */

        final boolean isAppInForeground = ServiceMonitor.appInForeground(getApplicationContext());

        if (data != null) {
            Log.d(TAG, "isAppInForeground : " + isAppInForeground);
            Log.d(TAG, "data : " + data.toString());
        }

        switch (data.get("eventType")) {
            case "notice":
                Log.d(TAG, "Receive New Notice");
                DBManager dbManager1 = new DBManager(getApplicationContext()).open();
                dbManager1.insertNotice(Integer.valueOf(data.get("noticeId")));
                dbManager1.close();
                if (isAppInForeground) sendBroadcast(new Intent(MainActivity.INTENT_NEW_NOTICE));
                break;
            case "delivery":
                Log.d(TAG, "Receive New Delivery");
                DBManager dbManager = new DBManager(getApplicationContext()).open();
                dbManager.insertDelivery(Integer.valueOf(data.get("deliveryId")));
                dbManager.close();
                if (isAppInForeground) sendBroadcast(new Intent(MainActivity.INTENT_NEW_DELIVERY));
                break;
            case "cctvLogs":
                Log.d(TAG, "Receive New CctvLog");
                if (isAppInForeground) sendBroadcast(new Intent(MainActivity.INTENT_NEW_PARKING));
                break;
        }
    }
}
