package com.kong.lutech.apartment.network.api.apartment;

import com.kong.lutech.apartment.model.CctvLog;
import com.kong.lutech.apartment.model.NFC;
import com.kong.lutech.apartment.model.NFCResponse;
import com.kong.lutech.apartment.model.NoticeDetail;
import com.kong.lutech.apartment.model.list.CarList;
import com.kong.lutech.apartment.model.list.CctvLogList;
import com.kong.lutech.apartment.model.list.DeliveryList;
import com.kong.lutech.apartment.model.list.GateLogList;
import com.kong.lutech.apartment.model.list.NoticeList;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */
public interface ApartmentInterface {
    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/deliveries")
    Single<Response<DeliveryList>> getDeliveries(@Header("token") String accessToken, @Path("id") int aptId, @Query("dongId") int dongId, @Query("homeId") int homeId, @Query("page") int page, @Query("size") int size);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/deliveries")
    Single<Response<DeliveryList>> getDeliveriesMinSequence(@Header("token") String accessToken, @Path("id") int aptId, @Query("dongId") int dongId, @Query("homeId") int homeId, @Query("minSequence") int minSequence);


    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/notices")
    Single<Response<NoticeList>> getNotices(@Header("token") String accessToken, @Path("id") int aptId, @Query("page") int page, @Query("size") int size);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/notices")
    Single<Response<NoticeList>> getNoticesMinSequence(@Header("token") String accessToken, @Path("id") int aptId, @Query("minSequence") int minSequence);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/gate-logs")
    Single<Response<GateLogList>> getGateLogs(@Header("token") String accessToken, @Path("id") int aptId, @Query("page") int page, @Query("size") int size);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/cars")
    Single<Response<CarList>> getCars(@Header("token") String accessToken, @Path("id") int aptId, @Query("dong") int dongId, @Query("home") int homeId);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/cctv-logs")
    Single<Response<CctvLogList>> getCctvLogs(@Header("token") String accessToken, @Path("id") int aptId, @Query("dong") int dongId, @Query("home") int homeId, @Query("carNumber") String carNumber, @Query("page") int page, @Query("size") int size);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/cctv-logs/latest")
    Single<Response<LatestCctvLog>> getLatestCctvLog(@Header("token") String accesToken, @Path("id") int aptId, @Query("carId") int carId);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/notices/{sequence}")
    Single<Response<NoticeDetail>> getNoticeDetail(@Header("token") String accessToken, @Path("id") int aptId, @Path("sequence") int sequence);

    @Headers("Content-Type: application/json")
    @POST("/api/apartments/m/nfc")
    Single<Response<NFCResponse>> postNFC(@Header("Authorization") String accessToken, @Body NFC nfc);

    class LatestCctvLog {
        private CctvLog cctvLog;

        public CctvLog getCctvLog() {
            return cctvLog;
        }

        public void setCctvLog(CctvLog cctvLog) {
            this.cctvLog = cctvLog;
        }
    }
}
