package com.kong.lutech.apartment.network.api.authentication;

import android.content.Context;

import com.google.gson.JsonObject;
import com.kong.lutech.apartment.network.interceptor.AddQueryInterceptor;
import com.kong.lutech.apartment.model.list.ApartmentList;
import com.kong.lutech.apartment.model.list.DongList;
import com.kong.lutech.apartment.model.list.HomeList;
import com.kong.lutech.apartment.network.NetworkConfig;

import java.util.Collections;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class AuthenticationRestUtil {
    private static final String TAG = "AuthenticationRestUtil";
    private static final String BASE_URL = "https://smap.kong-tech.com";

    private static AuthenticationRestUtil instance;
    private AuthenticationInterface interfaces;

    public static AuthenticationRestUtil getIntsance(Context context) {
        if (instance == null) {
            instance = newInstance(context);
        }
        return instance;
    }

    private static AuthenticationRestUtil newInstance(Context context) {
        return new AuthenticationRestUtil(context);
    }

    private AuthenticationRestUtil(Context context) {

        ConnectionSpec spec = new
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();



        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new AddQueryInterceptor(context));

        final OkHttpClient okHttpClient = builder.build();

        interfaces = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(AuthenticationInterface.class);
    }

    public Single<Response<ResponseBody>> certificationMobileClient() {
        return interfaces.certificationMobileClient(NetworkConfig.BASIC_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<ApartmentList>> getApartmentList(String clientToken) {
        return interfaces.getApartmentList(clientToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<DongList>> getDongList(String clientToken, int aptId) {
        return interfaces.getDongList(clientToken, aptId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<HomeList>> getHomeList(String clientToken, int aptId, int sequence) {
        return interfaces.getHomeList(clientToken, aptId, sequence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<AuthenticationInterface.MobileRssiResult>> getMobileRssi(String accessToken, String deviceName) {
        return interfaces.getMobileRssi(accessToken, deviceName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<AuthenticationInterface.LoginResult>> login(String basicToken, String authCode, int aptId, int dongId, int dongSequence, int homeId, int homeSequence, String pushToken) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("authCode", authCode);
        jsonObject.addProperty("apartmentId", aptId);
        jsonObject.addProperty("dongId", dongId);
        jsonObject.addProperty("dongSequence", dongSequence);
        jsonObject.addProperty("homeId", homeId);
        jsonObject.addProperty("homeSequence", homeSequence);

        jsonObject.addProperty("token", pushToken);

        return interfaces.login(basicToken, RequestBody.create(MediaType.parse("application/json"), jsonObject.toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
