package com.kong.lutech.apartment.network.interceptor;

import android.content.Context;

import com.kong.lutech.apartment.network.monitor.LiveNetworkMonitor;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class AddQueryInterceptor implements Interceptor {

    private Context context;

    public AddQueryInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request original = chain.request();
        final HttpUrl originalHttpUrl = original.url();

        final HttpUrl url = originalHttpUrl.newBuilder()
                .addQueryParameter("type", "m")
                .build();

        final Request.Builder requestBuilder = original.newBuilder()
                .url(url);

        final Request request = requestBuilder.build();

        final LiveNetworkMonitor networkMonitor = new LiveNetworkMonitor(context);
        if (networkMonitor.isConnected()) {
            return chain.proceed(request);
        } else {
            return chain.proceed(chain.request());
        }
    }
}
