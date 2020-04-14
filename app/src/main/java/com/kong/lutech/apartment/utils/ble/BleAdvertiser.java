package com.kong.lutech.apartment.utils.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by kimdonghyuk on 2017. 3. 29..
 */

public class BleAdvertiser {

    private BluetoothLeAdvertiser leAdvertiser;

    private AdvertiseCallback callback;

    public BleAdvertiser() {
        leAdvertiser = BluetoothAdapter.getDefaultAdapter()
                .getBluetoothLeAdvertiser();
    }

    public void startAdvertising(AdvertiseCallback callback) {
        this.callback = callback;
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
                .setConnectable(false)
                .build();

        //ParcelUuid pUuid = new ParcelUuid(UUID.fromString("424f4e47-0037-4020-4101-271071580001"));
        ParcelUuid pUuid = new ParcelUuid(UUID.fromString("524f4e47-0037-4020-4101-271071580001"));

        //Set AdvertiseData
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(pUuid)
                .addServiceData(pUuid, "ff".getBytes())
                .build();

        //leAdvertiser.stopAdvertising(callback);
        //Start Advertising
        leAdvertiser.startAdvertising(settings, createIBeaconAdvertiseData(pUuid.getUuid(),(short)0,(short)1,(byte)0xc5), callback);
    }

    public void stopAdvertising() {
        leAdvertiser.stopAdvertising(callback);
    }

    private AdvertiseData createIBeaconAdvertiseData(UUID proximityUuid, short major, short minor, byte txPower){
        byte[] manufacturerData = new byte[23];
        ByteBuffer bb = ByteBuffer.wrap(manufacturerData);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put((byte) 0x02);
        bb.put((byte) 0x15);
        bb.putLong(proximityUuid.getMostSignificantBits());
        bb.putLong(proximityUuid.getLeastSignificantBits());
        bb.putShort(major);
        bb.putShort(minor);
        bb.put(txPower);

        AdvertiseData.Builder builder = new AdvertiseData.Builder()
                .addManufacturerData(0x004c, manufacturerData);

        return builder.build();
    }
}
