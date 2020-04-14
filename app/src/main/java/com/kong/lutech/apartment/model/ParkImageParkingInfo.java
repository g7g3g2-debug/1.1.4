package com.kong.lutech.apartment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimdonghyeog on 19/11/2018.
 * KDH
 */
public class ParkImageParkingInfo {

    private String type;

    private String mapName;

    private ArrayList<Integer> mapLocation;

    private int parkCount;

    private ArrayList<Location> locations;

    private ArrayList<Cctv> cctvs;



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

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<Cctv> getCctvs() {
        return cctvs;
    }

    public void setCctvs(ArrayList<Cctv> cctvs) {
        this.cctvs = cctvs;
    }

    public ParkImageParkingInfo(String type, String mapName, ArrayList<Integer> mapLocation, int parkCount, ArrayList<Location> locations, ArrayList<Cctv> cctvs) {
        this.type = type;
        this.mapName = mapName;
        this.mapLocation = mapLocation;
        this.parkCount = parkCount;
        this.locations = locations;
        this.cctvs = cctvs;
    }

    public class Location {

        private String camNo;
        private String parkNo;
        private ArrayList<Integer> pLocation;
        private String pStatus;

        public Location(String camNo, String parkNo, ArrayList<Integer> pLocation, String pStatus) {
            this.camNo = camNo;
            this.parkNo = parkNo;
            this.pLocation = pLocation;
            this.pStatus = pStatus;
        }

        public String getCamNo() {
            return camNo;
        }

        public void setCamNo(String camNo) {
            this.camNo = camNo;
        }

        public String getParkNo() {
            return parkNo;
        }

        public void setParkNo(String parkNo) {
            this.parkNo = parkNo;
        }

        public ArrayList<Integer> getpLocation() {
            return pLocation;
        }

        public void setpLocation(ArrayList<Integer> pLocation) {
            this.pLocation = pLocation;
        }

        public String getpStatus() {
            return pStatus;
        }

        public void setpStatus(String pStatus) {
            this.pStatus = pStatus;
        }
    }

    public class Cctv {

        private int cctvId;

        private int sequence;

        private String name;
        private String code;

        private int xPosition;
        private int yPosition;


        public int getCctvId() {
            return cctvId;
        }

        public void setCctvId(int cctvId) {
            this.cctvId = cctvId;
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

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getxPosition() {
            return xPosition;
        }

        public void setxPosition(int xPosition) {
            this.xPosition = xPosition;
        }

        public int getyPosition() {
            return yPosition;
        }

        public void setyPosition(int yPosition) {
            this.yPosition = yPosition;
        }

        public Cctv(int cctvId, int sequence, String name, String code, int xPosition, int yPosition) {
            this.cctvId = cctvId;
            this.sequence = sequence;
            this.name = name;
            this.code = code;
            this.xPosition = xPosition;
            this.yPosition = yPosition;
        }
    }
}
