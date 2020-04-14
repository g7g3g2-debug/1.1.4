package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class CctvLog implements Parcelable{
    private int cctvLogId;
    private int sequence;

    private int dongId;
    private String dongName;
    private int homeId;
    private String homeName;
    private int parkId;
    private String parkName;
    private int cctvId;
    private String cctvName;
    private String carNumber;

    private Date date;

    private String picture;


    public CctvLog(int cctvLogId, int sequence, int dongId, String dongName, int homeId, String homeName, int parkId, String parkName, int cctvId, String cctvName, String carNumber, Date date, String picture) {
        this.cctvLogId = cctvLogId;
        this.sequence = sequence;
        this.dongId = dongId;
        this.dongName = dongName;
        this.homeId = homeId;
        this.homeName = homeName;
        this.parkId = parkId;
        this.parkName = parkName;
        this.cctvId = cctvId;
        this.cctvName = cctvName;
        this.carNumber = carNumber;
        this.date = date;
        this.picture = picture;
    }

    protected CctvLog(Parcel in) {
        cctvLogId = in.readInt();
        sequence = in.readInt();
        dongId = in.readInt();
        dongName = in.readString();
        homeId = in.readInt();
        homeName = in.readString();
        parkId = in.readInt();
        parkName = in.readString();
        cctvId = in.readInt();
        cctvName = in.readString();
        carNumber = in.readString();
        date = new Date(in.readLong());
        picture = in.readString();
    }

    public static final Creator<CctvLog> CREATOR = new Creator<CctvLog>() {
        @Override
        public CctvLog createFromParcel(Parcel in) {
            return new CctvLog(in);
        }

        @Override
        public CctvLog[] newArray(int size) {
            return new CctvLog[size];
        }
    };

    public int getCctvLogId() {
        return cctvLogId;
    }

    public void setCctvLogId(int cctvLogId) {
        this.cctvLogId = cctvLogId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getDongId() {
        return dongId;
    }

    public void setDongId(int dongId) {
        this.dongId = dongId;
    }

    public String getDongName() {
        return dongName;
    }

    public void setDongName(String dongName) {
        this.dongName = dongName;
    }

    public int getHomeId() {
        return homeId;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public int getParkId() {
        return parkId;
    }

    public void setParkId(int parkId) {
        this.parkId = parkId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public int getCctvId() {
        return cctvId;
    }

    public void setCctvId(int cctvId) {
        this.cctvId = cctvId;
    }

    public String getCctvName() {
        return cctvName;
    }

    public void setCctvName(String cctvName) {
        this.cctvName = cctvName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cctvLogId);
        dest.writeInt(sequence);
        dest.writeInt(dongId);
        dest.writeString(dongName);
        dest.writeInt(homeId);
        dest.writeString(homeName);
        dest.writeInt(parkId);
        dest.writeString(parkName);
        dest.writeInt(cctvId);
        dest.writeString(cctvName);
        dest.writeString(carNumber);
        dest.writeLong(date.getTime());
        dest.writeString(picture);
    }
}
