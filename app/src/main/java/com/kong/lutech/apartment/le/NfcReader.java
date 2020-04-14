package com.kong.lutech.apartment.le;

import android.bluetooth.le.ScanResult;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.kongtech.smapsdk.SmapConfig;
import com.kongtech.smapsdk.utils.HexUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class NfcReader {

    private String name;
    private String macAddress;
    private int rssi;

    private long keyLong;
    private ParcelUuid uuid;
    private int major;
    private int minor;

    private int apt;
    private int dong1;
    private int dong2;
    private int seq;

    public NfcReader(String name, String macAddress, int rssi, long long1, long long2, int major, int minor) {
        this.name = name;
        this.macAddress = macAddress;

        this.keyLong = long1;

        this.uuid = new ParcelUuid(new UUID(long1, long2));
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(keyLong);
        buffer.rewind();
        buffer.get(); // 게이트 코드
        apt = buffer.getShort();
        int dong = buffer.getShort();
        dong1 = dong / 10;
        dong2 = dong % 10;
        seq = (buffer.get() & 0xff) << 16 | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff);
    }

    public String getName() {
        return name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public long getKeyLong() {
        return keyLong;
    }

    public ParcelUuid getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getApt() {
        return apt;
    }

    public int getDong1() {
        return dong1;
    }

    public int getDong2() {
        return dong2;
    }

    public int getSeq() {
        return seq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NfcReader nfcReader = (NfcReader) o;
        return Objects.equals(macAddress, nfcReader.macAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress);
    }

    @Nullable
    public static NfcReader makeAutoDoorFromScanResult(String authCode, ScanResult result) {
        if (result == null || TextUtils.isEmpty(authCode) || authCode.length() < 5) return null;

        final byte[] manufacturerSpecificData = result.getScanRecord().getManufacturerSpecificData(0x59);
        if (manufacturerSpecificData == null || manufacturerSpecificData.length < 22) return null;

        final byte[] uuidBytes = Arrays.copyOfRange(manufacturerSpecificData, 2, 18);
        if (!(uuidBytes[0] == 'G' && Arrays.equals(Arrays.copyOfRange(uuidBytes, 13, 16), SmapConfig.LUTECH_SERIAL)))
            return null;

        final byte[] codeArr = HexUtils.hexStringToBytes(authCode.substring(1, 5));
        if (!Arrays.equals(Arrays.copyOfRange(uuidBytes, 1, 3), codeArr)) {
            return null;
        }

        if (uuidBytes[8] != 1 && uuidBytes[8] != 2 && uuidBytes[8] != 3) {
            return null;
        }

        final byte[] majorBytes = Arrays.copyOfRange(manufacturerSpecificData, 18, 20);
        final byte[] minorBytes = Arrays.copyOfRange(manufacturerSpecificData, 20, 22);

        final ByteBuffer proximityUUIDBuffer = ByteBuffer.wrap(uuidBytes);

        final int major = ((majorBytes[0] & 0xff) << 8) | (majorBytes[1] & 0xff);
        final int minor = ((minorBytes[0] & 0xff) << 8) | (minorBytes[1] & 0xff);
        final long long1 = proximityUUIDBuffer.getLong();
        final long long2 = proximityUUIDBuffer.getLong();

        return new NfcReader(result.getDevice().getName(), result.getDevice().getAddress(), result.getRssi(), long1, long2, major, minor);
    }
}
