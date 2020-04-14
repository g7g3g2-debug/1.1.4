package com.kong.lutech.apartment.network.api.parking;

import com.kong.lutech.apartment.model.CarImageParkingInfo;
import com.kong.lutech.apartment.model.ParkImageParkingInfo;
import com.kong.lutech.apartment.model.PreferedParkingInfo;
import com.kong.lutech.apartment.model.list.ParkImageList;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gimdonghyeog on 19/11/2018.
 * KDH
 */
public interface ParkingInterface {

    @GET("/api/apartments/{aptId}/park-images")
    Single<Response<ParkImageList>> getParkImages(@Header("Authorization") String accessToken, @Path("aptId") int aptId);


    @GET("/api/apartments/prefered-parking-info")
    Single<Response<PreferedParkingInfo>> getPreferedParkingInfo(@Header("Authorization") String accessToken);

    @PUT("/api/apartments/prefered-parking-info")
    Single<Response<PreferedParkingInfo>> putPreferedParkingInfo(@Header("Authorization") String accessToken, @Body RequestBody params);

    @GET("/api/apartments/{aptId}/park-images/{sequence}/parking-info")
    Single<Response<ParkImageParkingInfoData>> getParkImageParkingInfo(@Header("Authorization") String accessToken, @Path("aptId") int aptId, @Path("sequence") int sequence);


    @GET("/api/apartments/{aptId}/cars/{sequence}/parking-image")
    Single<Response<PreferedParkingInfo>> getCarParkingImage(@Header("Authorization") String accessToken, @Path("aptId") int aptId, @Path("sequence") int sequence, @Query("dong") int dong, @Query("home") int home);

    @GET("/api/apartments/{aptId}/cars/{sequence}/parking-info")
    Single<Response<CarImageParkingInfoData>> getCarParkingInfo(@Header("Authorization") String accessToken, @Path("aptId") int aptId, @Path("sequence") int sequence, @Query("dong") int dong, @Query("home") int home);


    class ParkImageParkingInfoData {

        private ParkImageParkingInfo data;

        public ParkImageParkingInfo getData() {
            return data;
        }

        public void setData(ParkImageParkingInfo data) {
            this.data = data;
        }

        public ParkImageParkingInfoData(ParkImageParkingInfo data) {
            this.data = data;
        }
    }

    class CarImageParkingInfoData {

        private CarImageParkingInfo data;

        public CarImageParkingInfoData(CarImageParkingInfo data) {
            this.data = data;
        }

        public CarImageParkingInfo getData() {
            return data;
        }

        public void setData(CarImageParkingInfo data) {
            this.data = data;
        }
    }
}
