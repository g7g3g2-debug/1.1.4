package com.kong.lutech.apartment.utils.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;

import java.util.ArrayList;
import java.util.List;

public class BleManager {
    public static final int SCAN_FOREGROUND = ScanSettings.SCAN_MODE_LOW_LATENCY;
    public static final int SCAN_BACKGROUND = ScanSettings.SCAN_MODE_LOW_POWER;
    public static final int SCAN_OPPORTUNISTIC = ScanSettings.SCAN_MODE_OPPORTUNISTIC;
    public static final int SCAN_BALANCED = ScanSettings.SCAN_MODE_BALANCED;

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean isScanning = false;

    public BleManager() {
        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter()
                .getBluetoothLeScanner();
    }

    public void startScan(int scanmode, ScanCallback scanCallback) {
        final ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(scanmode)
                .setReportDelay(0)
                .build();

        final List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(new ScanFilter.Builder().setDeviceName("Test Beacon 1").build());
        scanFilters.add(new ScanFilter.Builder().setDeviceName("Test Beacon 2").build());

        bluetoothLeScanner.startScan(scanFilters, scanSettings,scanCallback);
        isScanning = true;

    }

    public void stop(ScanCallback scanCallback) {
        if (bluetoothLeScanner != null && isScanning) {
            bluetoothLeScanner.stopScan(scanCallback);
            isScanning = false;
        }
    }

    public boolean isScanning() {
        return isScanning;
    }
}
