package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class Apartment implements Parcelable {
    private int apartmentId;
    private String name;

    @SerializedName("numberOfDong")
    private int dong;

    @SerializedName("numberOfPark")
    private int park;

    @SerializedName("numberOfDelivery")
    private int numberOfDelivery;

    @SerializedName("numberOfNotice")
    private int notice;

    @SerializedName("numberOfHome")
    private int home;

    @SerializedName("numberOfGate")
    private int gate;

    @SerializedName("numberOfCctv")
    private int cctv;

    @SerializedName("numberOfAuth")
    private int auth;

    @SerializedName("numberOfCard")
    private int numberOfCard;

    @SerializedName("numberOfCar")
    private int numberOfCar;

    @SerializedName("numberOfEmptyHome")
    private int emptyHome;

    @SerializedName("numberOfLivedHome")
    private int numberOfLivedHome;

    public Apartment(int apartmentId, String name, int dong, int park, int numberOfDelivery, int notice, int home, int gate, int cctv, int auth, int numberOfCard, int numberOfCar, int emptyHome, int numberOfLivedHome) {
        this.apartmentId = apartmentId;
        this.name = name;
        this.dong = dong;
        this.park = park;
        this.numberOfDelivery = numberOfDelivery;
        this.notice = notice;
        this.home = home;
        this.gate = gate;
        this.cctv = cctv;
        this.auth = auth;
        this.numberOfCard = numberOfCard;
        this.numberOfCar = numberOfCar;
        this.emptyHome = emptyHome;
        this.numberOfLivedHome = numberOfLivedHome;
    }

    protected Apartment(Parcel in) {
        apartmentId = in.readInt();
        name = in.readString();
        dong = in.readInt();
        park = in.readInt();
        numberOfDelivery = in.readInt();
        notice = in.readInt();
        home = in.readInt();
        gate = in.readInt();
        cctv = in.readInt();
        auth = in.readInt();
        numberOfCard = in.readInt();
        numberOfCar = in.readInt();
        emptyHome = in.readInt();
        numberOfLivedHome = in.readInt();
    }

    public static final Creator<Apartment> CREATOR = new Creator<Apartment>() {
        @Override
        public Apartment createFromParcel(Parcel in) {
            return new Apartment(in);
        }

        @Override
        public Apartment[] newArray(int size) {
            return new Apartment[size];
        }
    };

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDong() {
        return dong;
    }

    public void setDong(int dong) {
        this.dong = dong;
    }

    public int getPark() {
        return park;
    }

    public void setPark(int park) {
        this.park = park;
    }

    public int getNumberOfDelivery() {
        return numberOfDelivery;
    }

    public void setNumberOfDelivery(int numberOfDelivery) {
        this.numberOfDelivery = numberOfDelivery;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public int getGate() {
        return gate;
    }

    public void setGate(int gate) {
        this.gate = gate;
    }

    public int getCctv() {
        return cctv;
    }

    public void setCctv(int cctv) {
        this.cctv = cctv;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public int getNumberOfCard() {
        return numberOfCard;
    }

    public void setNumberOfCard(int numberOfCard) {
        this.numberOfCard = numberOfCard;
    }

    public int getNumberOfCar() {
        return numberOfCar;
    }

    public void setNumberOfCar(int numberOfCar) {
        this.numberOfCar = numberOfCar;
    }

    public int getEmptyHome() {
        return emptyHome;
    }

    public void setEmptyHome(int emptyHome) {
        this.emptyHome = emptyHome;
    }

    public int getNumberOfLivedHome() {
        return numberOfLivedHome;
    }

    public void setNumberOfLivedHome(int numberOfLivedHome) {
        this.numberOfLivedHome = numberOfLivedHome;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(apartmentId);
        dest.writeString(name);
        dest.writeInt(dong);
        dest.writeInt(park);
        dest.writeInt(numberOfDelivery);
        dest.writeInt(notice);
        dest.writeInt(home);
        dest.writeInt(gate);
        dest.writeInt(cctv);
        dest.writeInt(auth);
        dest.writeInt(numberOfCard);
        dest.writeInt(numberOfCar);
        dest.writeInt(emptyHome);
        dest.writeInt(numberOfLivedHome);
    }
}
