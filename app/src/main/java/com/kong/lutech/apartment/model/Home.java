package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class Home implements Parcelable {
    private int homeId;
    private int sequence;
    private String name;

    @SerializedName("numberOfFamily")
    private int family;

    @SerializedName("numberOfAuth")
    private int auth;

    @SerializedName("numberOfCard")
    private int card;

    @SerializedName("numberOfCar")
    private int car;

    @SerializedName("status")
    private int status;

    private String headOfHousehold;
    private String dateOfResidence;


    public Home(int homeId, int sequence, String name, int family, int auth, int card, int car, int status, String headOfHousehold, String dateOfResidence) {
        this.homeId = homeId;
        this.sequence = sequence;
        this.name = name;
        this.family = family;
        this.auth = auth;
        this.card = card;
        this.car = car;
        this.status = status;
        this.headOfHousehold = headOfHousehold;
        this.dateOfResidence = dateOfResidence;
    }

    protected Home(Parcel in) {
        homeId = in.readInt();
        sequence = in.readInt();
        name = in.readString();
        family = in.readInt();
        auth = in.readInt();
        card = in.readInt();
        car = in.readInt();
        status = in.readInt();
        headOfHousehold = in.readString();
        dateOfResidence = in.readString();
    }

    public static final Creator<Home> CREATOR = new Creator<Home>() {
        @Override
        public Home createFromParcel(Parcel in) {
            return new Home(in);
        }

        @Override
        public Home[] newArray(int size) {
            return new Home[size];
        }
    };

    public String getHeadOfHousehold() {
        return headOfHousehold;
    }

    public void setHeadOfHousehold(String headOfHousehold) {
        this.headOfHousehold = headOfHousehold;
    }

    public String getDateOfResidence() {
        return dateOfResidence;
    }

    public void setDateOfResidence(String dateOfResidence) {
        this.dateOfResidence = dateOfResidence;
    }

    public int getHomeId() {
        return homeId;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name + "호";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(homeId);
        dest.writeInt(sequence);
        dest.writeString(name);
        dest.writeInt(family);
        dest.writeInt(auth);
        dest.writeInt(card);
        dest.writeInt(car);
        dest.writeInt(status);
        dest.writeString(headOfHousehold);
        dest.writeString(dateOfResidence);
    }
}
