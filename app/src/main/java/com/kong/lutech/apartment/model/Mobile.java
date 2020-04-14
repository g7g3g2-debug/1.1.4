package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class Mobile implements Parcelable {
    private String authCode;

    private int apartmentId;

    private Dong dong;
    private Home home;


    protected Mobile(Parcel in) {
        authCode = in.readString();
        apartmentId = in.readInt();
        dong = in.readParcelable(Dong.class.getClassLoader());
        home = in.readParcelable(Home.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authCode);
        dest.writeInt(apartmentId);
        dest.writeParcelable(dong, flags);
        dest.writeParcelable(home, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Mobile> CREATOR = new Creator<Mobile>() {
        @Override
        public Mobile createFromParcel(Parcel in) {
            return new Mobile(in);
        }

        @Override
        public Mobile[] newArray(int size) {
            return new Mobile[size];
        }
    };

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public int getDongId() {
        return this.dong.getDongId();
    }

    public void setDongId(int dongId) {
        this.dong.setDongId(dongId);
    }

    public int getDongSequence() {
        return this.dong.getSequence();
    }

    public void setDongSequence(int dongSequence) {
        this.dong.setSequence(dongSequence);
    }

    public Dong getDong() {
        return dong;
    }

    public void setDong(Dong dong) {
        this.dong = dong;
    }

    public int getHomeId() {
        return this.home.getHomeId();
    }

    public void setHomeId(int homeId) {
        this.home.setHomeId(homeId);
    }

    public int getHomeSequence() {
        return this.home.getSequence();
    }

    public void setHomeSequence(int homeSequence) {
        this.home.setSequence(homeSequence);
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public Mobile(String authCode, int apartmentId, Dong dong, Home home) {
        this.authCode = authCode;
        this.apartmentId = apartmentId;
        this.dong = dong;
        this.home = home;
    }
}
