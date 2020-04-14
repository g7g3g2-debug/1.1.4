package com.kong.lutech.apartment.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gimdonghyeog on 19/11/2018.
 * KDH
 */
public class ParkImage implements Parcelable {

    private int id;

    private int apartmentId;

    private int sequence;

    private String parkName;

    private int mapNo;
    private int mapWidth;
    private int mapHeight;

    private String photoType;
    private String photoPath;


    protected ParkImage(Parcel in) {
        id = in.readInt();
        apartmentId = in.readInt();
        sequence = in.readInt();
        parkName = in.readString();
        mapNo = in.readInt();
        mapWidth = in.readInt();
        mapHeight = in.readInt();
        photoType = in.readString();
        photoPath = in.readString();
    }

    public static final Creator<ParkImage> CREATOR = new Creator<ParkImage>() {
        @Override
        public ParkImage createFromParcel(Parcel in) {
            return new ParkImage(in);
        }

        @Override
        public ParkImage[] newArray(int size) {
            return new ParkImage[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public int getMapNo() {
        return mapNo;
    }

    public void setMapNo(int mapNo) {
        this.mapNo = mapNo;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public ParkImage(int id, int apartmentId, int sequence, String parkName, int mapNo, int mapWidth, int mapHeight, String photoType, String photoPath) {
        this.id = id;
        this.apartmentId = apartmentId;
        this.sequence = sequence;
        this.parkName = parkName;
        this.mapNo = mapNo;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.photoType = photoType;
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return this.parkName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(apartmentId);
        dest.writeInt(sequence);
        dest.writeString(parkName);
        dest.writeInt(mapNo);
        dest.writeInt(mapWidth);
        dest.writeInt(mapHeight);
        dest.writeString(photoType);
        dest.writeString(photoPath);
    }
}
