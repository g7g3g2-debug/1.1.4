package com.kong.lutech.apartment.model;

import java.util.ArrayList;

/**
 * Created by gimdonghyeog on 20/11/2018.
 * KDH
 */
public class CarImageParkingInfo {


    private String type;

    private String mapName;

    private ArrayList<Integer> mapLocation;

    private int parkCount;

    private ArrayList<ParkImageParkingInfo.Location> locations;

    private ParkImageParkingInfo.Cctv cctv;

    public CarImageParkingInfo(String type, String mapName, ArrayList<Integer> mapLocation, int parkCount, ArrayList<ParkImageParkingInfo.Location> locations, ParkImageParkingInfo.Cctv cctv) {
        this.type = type;
        this.mapName = mapName;
        this.mapLocation = mapLocation;
        this.parkCount = parkCount;
        this.locations = locations;
        this.cctv = cctv;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public ArrayList<Integer> getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(ArrayList<Integer> mapLocation) {
        this.mapLocation = mapLocation;
    }

    public int getParkCount() {
        return parkCount;
    }

    public void setParkCount(int parkCount) {
        this.parkCount = parkCount;
    }

    public ArrayList<ParkImageParkingInfo.Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<ParkImageParkingInfo.Location> locations) {
        this.locations = locations;
    }

    public ParkImageParkingInfo.Cctv getCctv() {
        return cctv;
    }

    public void setCctv(ParkImageParkingInfo.Cctv cctv) {
        this.cctv = cctv;
    }
}
