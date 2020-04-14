package com.kong.lutech.apartment.network.api.apartment;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kong.lutech.apartment.network.interceptor.AddQueryInterceptor;
import com.kong.lutech.apartment.model.NFC;
import com.kong.lutech.apartment.model.NFCResponse;
import com.kong.lutech.apartment.model.NoticeDetail;
import com.kong.lutech.apartment.model.list.CarList;
import com.kong.lutech.apartment.model.list.CctvLogList;
import com.kong.lutech.apartment.model.list.DeliveryList;
import com.kong.lutech.apartment.model.list.GateLogList;
import com.kong.lutech.apartment.model.list.NoticeList;

import java.util.Collections;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class ApartmentRestUtil {
    private static final String TAG = "AuthenticationRestUtil";
    private static final String BASE_URL = "https://smap.kong-tech.com";

    private static ApartmentRestUtil instance;
    private ApartmentInterface interfaces;

    public static ApartmentRestUtil getIntsance(Context context) {
        if (instance == null) {
            instance = newInstance(context);
        }
        return instance;
    }

    public static ApartmentRestUtil getInstance(Context context, String dateFormat) {
        return new ApartmentRestUtil(context, dateFormat);
    }

    private static ApartmentRestUtil newInstance(Context context) {
        return new ApartmentRestUtil(context);
    }

    private ApartmentRestUtil(Context context) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new AddQueryInterceptor(context))
                .build();

        final Gson gson = new GsonBuilder()
                .setDateFormat("yyyy.MM.dd hh:mm:ss")
                .create();

        interfaces = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ApartmentInterface.class);
    }

    private ApartmentRestUtil(Context context, String format) {

        ConnectionSpec spec = new
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new AddQueryInterceptor(context))
                .build();

        final Gson gson = new GsonBuilder()
                .setDateFormat(format)
                .create();

        interfaces = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ApartmentInterface.class);
    }

    public Single<Response<DeliveryList>> getDeliveries(String accessToken, int aptId, int dongId, int homeId, int page, int size) {
        return interfaces.getDeliveries(accessToken, aptId, dongId, homeId, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<DeliveryList>> getDeliveriesMinSequence(String accessToken, int aptId, int dongId, int homeId, int minSequence) {
        return interfaces.getDeliveriesMinSequence(accessToken, aptId, dongId, homeId, minSequence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<NoticeList>> getNotices(String accessToken, int aptId, int page, int size) {
        return interfaces.getNotices(accessToken, aptId, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<NoticeList>> getNoticesMinSequence(String accessToken, int aptId, int minSequence) {
        return interfaces.getNoticesMinSequence(accessToken, aptId, minSequence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<GateLogList>> getGateLogs(String accessToken, int aptId, int page, int size) {
        return interfaces.getGateLogs(accessToken, aptId, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<CarList>> getCars(String accessToken, int aptId, int dongId, int homeId) {
        return interfaces.getCars(accessToken, aptId, dongId, homeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<Response<CctvLogList>> getCctvLogs(String accessToken, int aptId, int dong, int home, String carNumber, int page, int size) {
        return interfaces.getCctvLogs(accessToken, aptId, dong, home, carNumber, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<ApartmentInterface.LatestCctvLog>> getLatestCctvLog(String accessToken, int aptId, int carId) {
        return interfaces.getLatestCctvLog(accessToken, aptId, carId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<NoticeDetail>> getNoticeDetail(String accessToken, int aptId, int sequence) {
        return interfaces.getNoticeDetail(accessToken, aptId, sequence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<NFCResponse>> postNfc(String accessToken, NFC nfc) {
        return interfaces.postNFC(accessToken, nfc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // 주차장 이미지 정보 조회
}
