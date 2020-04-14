package com.kong.lutech.apartment.network.monitor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by gimdonghyeog on 2017. 7. 27..
 */

public class LiveNetworkMonitor implements NetworkMonitor {

    private final Context applicationContext;

    public LiveNetworkMonitor(Context context) {
        applicationContext = context;
    }

    @Override
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
