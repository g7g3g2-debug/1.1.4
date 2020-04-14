package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by gimdonghyeog on 2017. 5. 24..
 */

public class Car implements Parcelable{
    private int carId;
    private int sequence;
    private String code;
    private String number;
    private String cctvName;
    private String parkName;
    private Date parkDate;



    public Car(int carId, int sequence, String code, String number, String cctvName, String parkName, Date parkDate) {
        this.carId = carId;
        this.sequence = sequence;
        this.code = code;
        this.number = number;
        this.cctvName = cctvName;
        this.parkName = parkName;
        this.parkDate = parkDate;
    }

    protected Car(Parcel in) {
        carId = in.readInt();
        sequence = in.readInt();
        code = in.readString();
        number = in.readString();
        cctvName = in.readString();
        parkName = in.readString();
        parkDate = new Date(in.readLong());
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public Date getParkDate() {
        return parkDate;
    }

    public void setParkDate(Date parkDate) {
        this.parkDate = parkDate;
    }

    public String getCctvName() {
        return cctvName;
    }

    public void setCctvName(String cctvName) {
        this.cctvName = cctvName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(carId);
        dest.writeInt(sequence);
        dest.writeString(code);
        dest.writeString(number);
        dest.writeString(cctvName);
        dest.writeString(parkName);

        if (parkDate != null) dest.writeLong(parkDate.getTime());
    }

}
