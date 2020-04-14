package com.kong.lutech.apartment.model;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class GateLog {
    private int gateLogId;
    private int sequence;

    private String date;

    private int dongId;
    private String dongName;
    private int homeId;
    private String homeName;
    private int gateDongId;
    private String gateDongName;
    private int gateId;
    private String gateName;
    private String houseMember;


    public GateLog(int gateLogId, int sequence, String date, int dongId, String dongName, int homeId, String homeName, int gateDongId, String gateDongName, int gateId, String gateName, String houseMember) {
        this.gateLogId = gateLogId;
        this.sequence = sequence;
        this.date = date;
        this.dongId = dongId;
        this.dongName = dongName;
        this.homeId = homeId;
        this.homeName = homeName;
        this.gateDongId = gateDongId;
        this.gateDongName = gateDongName;
        this.gateId = gateId;
        this.gateName = gateName;
        this.houseMember = houseMember;
    }

    public int getGateLogId() {
        return gateLogId;
    }

    public void setGateLogId(int gateLogId) {
        this.gateLogId = gateLogId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDongId() {
        return dongId;
    }

    public void setDongId(int dongId) {
        this.dongId = dongId;
    }

    public String getDongName() {
        return dongName;
    }

    public void setDongName(String dongName) {
        this.dongName = dongName;
    }

    public int getHomeId() {
        return homeId;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public int getGateDongId() {
        return gateDongId;
    }

    public void setGateDongId(int gateDongId) {
        this.gateDongId = gateDongId;
    }

    public String getGateDongName() {
        return gateDongName;
    }

    public void setGateDongName(String gateDongName) {
        this.gateDongName = gateDongName;
    }

    public int getGateId() {
        return gateId;
    }

    public void setGateId(int gateId) {
        this.gateId = gateId;
    }

    public String getGateName() {
        return gateName;
    }

    public void setGateName(String gateName) {
        this.gateName = gateName;
    }

    public String getHouseMember() {
        return houseMember;
    }

    public void setHouseMember(String houseMember) {
        this.houseMember = houseMember;
    }
}
