package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class PosTransRequest {
    private String amount;

    @Override
    public String toString() {
        return "PosTransRequest{" +
                "amount='" + amount + '\'' +
                ", stan='" + stan + '\'' +
                ", cardSequenceNumber='" + cardSequenceNumber + '\'' +
                ", trackData='" + trackData + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", pinblock='" + pinblock + '\'' +
                ", deviceData='" + deviceData + '\'' +
                '}';
    }

    @SerializedName("stan")
    private String stan;
    @SerializedName("cardSequenceNumber")
    private String cardSequenceNumber;

    public PosTransRequest(String amount, String stan, String cardSequenceNumber, String trackData, String terminalId, String pinblock, String deviceData) {
        this.amount = amount;
        this.stan = stan;
        this.cardSequenceNumber = cardSequenceNumber;
        this.trackData = trackData;
        this.terminalId = terminalId;
        this.pinblock = pinblock;
        this.deviceData = deviceData;
    }
    @SerializedName("trackData")
    private String trackData;
    @SerializedName("terminalId")
    private String terminalId;
    @SerializedName("pinblock")
    private String pinblock;
    @SerializedName("deviceData")
    private String deviceData;


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getCardSequenceNumber() {
        return cardSequenceNumber;
    }

    public void setCardSequenceNumber(String cardSequenceNumber) {
        this.cardSequenceNumber = cardSequenceNumber;
    }

    public String getTrackData() {
        return trackData;
    }

    public void setTrackData(String trackData) {
        this.trackData = trackData;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getPinblock() {
        return pinblock;
    }

    public void setPinblock(String pinblock) {
        this.pinblock = pinblock;
    }

    public String getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(String deviceData) {
        this.deviceData = deviceData;
    }
}