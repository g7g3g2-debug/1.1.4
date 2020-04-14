package com.kong.lutech.apartment.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class FirebasePushInstanceIDService extends FirebaseInstanceIdService {
    private final String TAG = getClass().getSimpleName();
    public static final String INTENT_REFRESH_TOKEN = "INTENT_REFRESH_TOKEN";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.d(TAG, FirebaseInstanceId.getInstance().getToken());

        sendBroadcast(new Intent(INTENT_REFRESH_TOKEN));
    }

}
