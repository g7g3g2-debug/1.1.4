package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gimdonghyeog on 2018. 3. 5..
 * KDH
 */
public class MobileRssi implements Parcelable {

    private String authCode;
    private String deviceName;

    private int lowValue;
    private int middleValue;
    private int highValue;

    private boolean isModified;

    private double rtValue;


    protected MobileRssi(Parcel in) {
        authCode = in.readString();
        deviceName = in.readString();
        lowValue = in.readInt();
        middleValue = in.readInt();
        highValue = in.readInt();
        isModified = in.readByte() != 0;
        rtValue = in.readDouble();
    }

    public static final Creator<MobileRssi> CREATOR = new Creator<MobileRssi>() {
        @Override
        public MobileRssi createFromParcel(Parcel in) {
            return new MobileRssi(in);
        }

        @Override
        public MobileRssi[] newArray(int size) {
            return new MobileRssi[size];
        }
    };

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getLowValue() {
        return lowValue;
    }

    public void setLowValue(int lowValue) {
        this.lowValue = lowValue;
    }

    public int getMiddleValue() {
        return middleValue;
    }

    public void setMiddleValue(int middleValue) {
        this.middleValue = middleValue;
    }

    public int getHighValue() {
        return highValue;
    }

    public void setHighValue(int highValue) {
        this.highValue = highValue;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public double getRatioValue() {
        return rtValue;
    }

    public void setRatioValue(double ratioValue) {
        this.rtValue = ratioValue;
    }

    public MobileRssi() {
    }

    public MobileRssi(String authCode, String deviceName, int lowLevel, int middleLevel, int highLevel, boolean isModified, double ratioValue) {
        this.authCode = authCode;
        this.deviceName = deviceName;
        this.lowValue = lowLevel;
        this.middleValue = middleLevel;
        this.highValue = highLevel;
        this.isModified = isModified;
        this.rtValue = ratioValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authCode);
        dest.writeString(deviceName);
        dest.writeInt(lowValue);
        dest.writeInt(middleValue);
        dest.writeInt(highValue);
        dest.writeByte((byte) (isModified ? 1 : 0));
        dest.writeDouble(rtValue);
    }
}
