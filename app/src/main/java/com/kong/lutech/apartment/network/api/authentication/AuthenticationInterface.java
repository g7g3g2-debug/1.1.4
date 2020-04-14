package com.kong.lutech.apartment.network.api.authentication;

import com.kong.lutech.apartment.model.Mobile;
import com.kong.lutech.apartment.model.MobileRssi;
import com.kong.lutech.apartment.model.Permission;
import com.kong.lutech.apartment.model.list.ApartmentList;
import com.kong.lutech.apartment.model.list.DongList;
import com.kong.lutech.apartment.model.list.HomeList;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

public interface AuthenticationInterface {
    @Headers("Content-Type: application/json")
    @POST("/api/identity/clients/authenticate")
    Single<Response<ResponseBody>> certificationMobileClient(@Header("Authorization") String token);


    @Headers("Content-Type: application/json")
    @GET("/api/apartments")
    Single<Response<ApartmentList>> getApartmentList(@Header("token") String clientToken);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/dongs")
    Single<Response<DongList>> getDongList(@Header("token") String clientToken, @Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/{id}/homes")
    Single<Response<HomeList>> getHomeList(@Header("token") String clientToken, @Path("id") int id, @Query("dong") int sequence);


    @Headers("Content-Type: application/json")
    @POST("/api/identity/mobiles/login")
    Single<Response<LoginResult>> login(@Header("token") String basicToken, @Body RequestBody params);

    @Headers("Content-Type: application/json")
    @GET("/api/apartments/0/mobile-rssi")
    Single<Response<MobileRssiResult>> getMobileRssi(@Header("token") String accessToken, @Query("deviceName") String deviceName);

    //Single<Response<LoginResult>> login(@Header("token") String basicToken, @Field("authCode") String authCode, @Field("apartmentId") int apartmentId, @Field("dongId") int dongId, @Field("homeId") int homeId, @Field("sequence") int sequence);


    class LoginResult {
        private String accessToken;
        private Mobile mobile;
        private Permission permission;

        public Permission getPermission() {
            return permission;
        }

        public void setPermission(Permission permission) {
            this.permission = permission;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public Mobile getMobile() {
            return mobile;
        }

        public void setMobile(Mobile mobile) {
            this.mobile = mobile;
        }
    }

    class MobileRssiResult {
        private MobileRssi mobileRssi;

        public MobileRssi getMobileRssi() {
            return mobileRssi;
        }

        public void setMobileRssi(MobileRssi mobileRssi) {
            this.mobileRssi = mobileRssi;
        }

        public MobileRssiResult() {
        }

        public MobileRssiResult(MobileRssi mobileRssi) {
            this.mobileRssi = mobileRssi;
        }
    }
}