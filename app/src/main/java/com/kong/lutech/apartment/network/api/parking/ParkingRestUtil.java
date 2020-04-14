package com.kong.lutech.apartment.network.api.parking;

import android.content.Context;

import com.google.gson.JsonObject;
import com.kong.lutech.apartment.model.PreferedParkingInfo;
import com.kong.lutech.apartment.model.list.ParkImageList;

import java.util.Collections;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gimdonghyeog on 19/11/2018.
 * KDH
 */
public class ParkingRestUtil {
    private static final String TAG = "AuthenticationRestUtil";
    private static final String BASE_URL = "https://smap.kong-tech.com";

    private static ParkingRestUtil instance;
    private ParkingInterface interfaces;

    public static ParkingRestUtil getIntsance(Context context) {
        if (instance == null) {
            instance = newInstance(context);
        }
        return instance;
    }

    private static ParkingRestUtil newInstance(Context context) {
        return new ParkingRestUtil(context);
    }

    private ParkingRestUtil(Context context) {

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
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
                //.addInterceptor(new AddQueryInterceptor(context));

        final OkHttpClient okHttpClient = builder.build();

        interfaces = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ParkingInterface.class);
    }


    // 아파트 주차장 이미지 조회
    public Single<Response<ParkImageList>> getParkImages(String accessToken, int aptId) {
        return interfaces.getParkImages(accessToken, aptId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 선호 주차 구역 조회
    public Single<Response<PreferedParkingInfo>> getPreferedParkingInfo(String accessToken) {
        return interfaces.getPreferedParkingInfo(accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 선호 주차 구역 업로드
    public Single<Response<PreferedParkingInfo>> putPreferedParkingInfo(String accessToken, int parkImageId, float[] insets) {
        if (insets == null || insets.length != 4) return null;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parkImageId", parkImageId);
        jsonObject.addProperty("preferedParkLocation", (int)insets[0] + "^" + (int)insets[1] + "^" + (int)insets[2] + "^" + (int)insets[3]);

        return interfaces.putPreferedParkingInfo(accessToken, RequestBody.create(MediaType.parse("application/json"), jsonObject.toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 주차 이미지 정보 조회
    public Single<Response<ParkingInterface.ParkImageParkingInfoData>> getParkImageParkingInfo(String accessToken, int aptId, int sequence) {
        return interfaces.getParkImageParkingInfo(accessToken, aptId, sequence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // 차량 기준 주차 이미지 조회
    public Single<Response<PreferedParkingInfo>> getCarParkingImage(String accessToken, int aptId, int sequence, int dong, int home) {
        return interfaces.getCarParkingImage(accessToken, aptId, sequence, dong, home)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 차량 기준 주차 정보 조회
    public Single<Response<ParkingInterface.CarImageParkingInfoData>> getCarParkingInfo(String accessToken, int aptId, int sequence, int dong, int home) {
        return interfaces.getCarParkingInfo(accessToken, aptId, sequence, dong, home)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}