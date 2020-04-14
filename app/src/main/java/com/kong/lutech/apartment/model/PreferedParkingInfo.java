package com.kong.lutech.apartment.model;

/**
 * Created by gimdonghyeog on 19/11/2018.
 * KDH
 */
public class PreferedParkingInfo {

    private ParkImage parkImage;

    private String preferedParkLocation;

    public PreferedParkingInfo(ParkImage parkImage, String preferedParkLocation) {
        this.parkImage = parkImage;
        this.preferedParkLocation = preferedParkLocation;
    }

    public ParkImage getParkImage() {
        return parkImage;
    }

    public void setParkImage(ParkImage parkImage) {
        this.parkImage = parkImage;
    }

    public String getPreferedParkLocation() {
        return preferedParkLocation;
    }

    public void setPreferedParkLocation(String preferedParkLocation) {
        this.preferedParkLocation = preferedParkLocation;
    }
}
