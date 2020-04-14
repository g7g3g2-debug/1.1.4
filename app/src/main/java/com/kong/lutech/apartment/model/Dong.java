package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class Dong implements Parcelable {
    private int dongId;
    private int sequence;
    private String name;

    @SerializedName("numberOfHome")
    private int home;

    @SerializedName("numberOfGate")
    private int gate;

    @SerializedName("numberOfAuth")
    private int auth;

    @SerializedName("numberOfCard")
    private int card;

    @SerializedName("numberOfCar")
    private int car;

    @SerializedName("numberOfEmptyHome")
    private int emptyHome;

    @SerializedName("numberOfLivedHome")
    private int livedHome;

    public Dong(int dongId, int sequence, String name, int home, int gate, int auth, int card, int car, int emptyHome, int livedHome) {
        this.dongId = dongId;
        this.sequence = sequence;
        this.name = name;
        this.home = home;
        this.gate = gate;
        this.auth = auth;
        this.card = card;
        this.car = car;
        this.emptyHome = emptyHome;
        this.livedHome = livedHome;
    }

    protected Dong(Parcel in) {
        dongId = in.readInt();
        sequence = in.readInt();
        name = in.readString();
        home = in.readInt();
        gate = in.readInt();
        auth = in.readInt();
        card = in.readInt();
        car = in.readInt();
        emptyHome = in.readInt();
        livedHome = in.readInt();
    }

    public static final Creator<Dong> CREATOR = new Creator<Dong>() {
        @Override
        public Dong createFromParcel(Parcel in) {
            return new Dong(in);
        }

        @Override
        public Dong[] newArray(int size) {
            return new Dong[size];
        }
    };

    public int getDongId() {
        return dongId;
    }

    public void setDongId(int dongId) {
        this.dongId = dongId;
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

    public int getEmptyHome() {
        return emptyHome;
    }

    public void setEmptyHome(int emptyHome) {
        this.emptyHome = emptyHome;
    }

    public int getLivedHome() {
        return livedHome;
    }

    public void setLivedHome(int livedHome) {
        this.livedHome = livedHome;
    }

    @Override
    public String toString() {
        return name + "동";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dong) {
            final Dong dong = (Dong) obj;
            return dong.getDongId() == this.getDongId();
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dongId);
        dest.writeInt(sequence);
        dest.writeString(name);
        dest.writeInt(home);
        dest.writeInt(gate);
        dest.writeInt(auth);
        dest.writeInt(card);
        dest.writeInt(car);
        dest.writeInt(emptyHome);
        dest.writeInt(livedHome);
    }
}
