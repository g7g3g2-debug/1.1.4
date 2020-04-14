package com.kong.lutech.apartment.model;

public class NFC {

    private int nfcId;
    private int sequence;
    private String code;
    private String houseMember;
    private String nfcData;

    public NFC(String houseMember, String nfcData) {
        this.houseMember = houseMember;
        this.nfcData = nfcData;
    }

    public int getNfcId() {
        return nfcId;
    }

    public int getSequence() {
        return sequence;
    }

    public String getCode() {
        return code;
    }

    public String getHouseMember() {
        return houseMember;
    }

    public String getNfcData() {
        return nfcData;
    }
}
