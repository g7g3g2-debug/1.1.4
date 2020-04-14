package com.kong.lutech.apartment.utils.ble;

import java.util.Arrays;

public class Device {

    private String name;
    private String address;
    private int rssi;

    private String serial;
    private boolean isPower;
    private int battery;
    private double hardwareVersion;
    private double m4Version;
    private double wifiVersion;
    private double bluetoothVersion;
    private int port;

    public Device(String name, String address, int rssi, byte[] manufacturerSpecificData) {

        this.name = name;
        this.address = address;
        this.rssi = rssi;

        byte[] serialBytes = Arrays.copyOfRange(manufacturerSpecificData, 2, 9);

        serial = new String(serialBytes);

        isPower = manufacturerSpecificData[9] == 1;

        battery = ((manufacturerSpecificData[10] & 0xff) << 8) | (manufacturerSpecificData[11] & 0xff);
        hardwareVersion = (double) ((int) (manufacturerSpecificData[12] & 0xff)) / 10.0;
        m4Version = (double) ((int) (manufacturerSpecificData[13] & 0xff)) / 10.0;
        wifiVersion = (double) ((int) (manufacturerSpecificData[14] & 0xff)) / 10.0;
        bluetoothVersion = (double) ((int) (manufacturerSpecificData[15] & 0xff)) / 10.0;
        port = ((manufacturerSpecificData[16] & 0xff) << 8) | (manufacturerSpecificData[17] & 0xff);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    public String getSerial() {
        return serial;
    }

    public boolean isPower() {
        return isPower;
    }

    public int getBattery() {
        return battery;
    }

    public double getHardwareVersion() {
        return hardwareVersion;
    }

    public double getM4Version() {
        return m4Version;
    }

    public double getWifiVersion() {
        return wifiVersion;
    }

    public double getBluetoothVersion() {
        return bluetoothVersion;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Device) {
            Device ptr = (Device) v;
            return ptr.getAddress().equals(this.address);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}
