package com.kong.lutech.apartment.model;

import java.util.Date;

/**
 * Created by gimdonghyeog on 2017. 5. 23..
 */

public class Delivery {
    private int deliveryId;
    private int sequence;
    private int status;

    private Date receiveDate;
    private Date sendDate;

    private int dongId;
    private int homeId;

    private String receiver;
    private String sender;
    private String recipient;

    public Delivery(int deliveryId, int sequence, int status, Date receiveDate, Date sendDate, int dongId, int homeId, String receiver, String sender, String recipient) {
        this.deliveryId = deliveryId;
        this.sequence = sequence;
        this.status = status;
        this.receiveDate = receiveDate;
        this.sendDate = sendDate;
        this.dongId = dongId;
        this.homeId = homeId;
        this.receiver = receiver;
        this.sender = sender;
        this.recipient = recipient;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public int getDongId() {
        return dongId;
    }

    public void setDongId(int dongId) {
        this.dongId = dongId;
    }

    public int getHomeId() {
        return homeId;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Delivery)obj).deliveryId == deliveryId;
    }
}
