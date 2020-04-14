package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gimdonghyeog on 27/12/2018.
 * KDH
 */
public class Permission implements Parcelable {
    private int apartmentId;

    private boolean parkingInfo;
    private boolean gateLog;
    private boolean delivery;
    private boolean notice;

    public Permission(int apartmentId, boolean parkingInfo, boolean gateLog, boolean delivery, boolean notice) {
        this.apartmentId = apartmentId;
        this.parkingInfo = parkingInfo;
        this.gateLog = gateLog;
        this.delivery = delivery;
        this.notice = notice;
    }

    protected Permission(Parcel in) {
        apartmentId = in.readInt();
        parkingInfo = in.readByte() != 0;
        gateLog = in.readByte() != 0;
        delivery = in.readByte() != 0;
        notice = in.readByte() != 0;
    }

    public static final Creator<Permission> CREATOR = new Creator<Permission>() {
        @Override
        public Permission createFromParcel(Parcel in) {
            return new Permission(in);
        }

        @Override
        public Permission[] newArray(int size) {
            return new Permission[size];
        }
    };

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public boolean isParkingInfo() {
        return parkingInfo;
    }

    public void setParkingInfo(boolean parkingInfo) {
        this.parkingInfo = parkingInfo;
    }

    public boolean isGateLog() {
        return gateLog;
    }

    public void setGateLog(boolean gateLog) {
        this.gateLog = gateLog;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public boolean isNotice() {
        return notice;
    }

    public void setNotice(boolean notice) {
        this.notice = notice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(apartmentId);
        dest.writeByte((byte) (parkingInfo ? 1 : 0));
        dest.writeByte((byte) (gateLog ? 1 : 0));
        dest.writeByte((byte) (delivery ? 1 : 0));
        dest.writeByte((byte) (notice ? 1 : 0));
    }
}
