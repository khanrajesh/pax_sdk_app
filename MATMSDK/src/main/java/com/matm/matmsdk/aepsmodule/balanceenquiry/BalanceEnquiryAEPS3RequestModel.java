package com.matm.matmsdk.aepsmodule.balanceenquiry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class BalanceEnquiryAEPS3RequestModel extends JSONObject {

    @SerializedName("latLong")
    @Expose
    private String latLong;

    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;

    @SerializedName("aadharNo")
    @Expose
    private Long aadharNo;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("iin")
    @Expose
    private String iin;

    @SerializedName("dpId")
    @Expose
    private String dpId;

    @SerializedName("rdsId")
    @Expose
    private String rdsId;

    @SerializedName("rdsVer")
    @Expose
    private String rdsVer;


    @SerializedName("dc")
    @Expose
    private String dc;

    @SerializedName("mi")
    @Expose
    private String mi;

    @SerializedName("mcData")
    @Expose
    private String mcData;

    @SerializedName("sKey")
    @Expose
    private String sKey;

    @SerializedName("hMac")
    @Expose
    private String hMac;

    @SerializedName("encryptedPID")
    @Expose
    private String encryptedPID;

    @SerializedName("ci")
    @Expose
    private String ci;

    @SerializedName("operation")
    @Expose
    private String operation;

    @SerializedName("freshnessFactor")
    @Expose
    private String freshnessFactor;

    @SerializedName("apiUser")
    @Expose
    private String apiUser;

    @SerializedName("shakey")
    @Expose
    private String shakey;

    @SerializedName("pidData")
    @Expose
    private String pidData;


    public BalanceEnquiryAEPS3RequestModel(String latLong, String mobileNumber, Long aadharNo, String amount, String iin, String dpId, String rdsId, String rdsVer, String dc, String mi, String mcData, String sKey, String hMac, String encryptedPID, String ci, String operation, String freshnessFactor, String apiUser, String shakey, String pidData) {
        this.latLong = latLong;
        this.mobileNumber = mobileNumber;
        this.aadharNo = aadharNo;
        this.amount = amount;
        this.iin = iin;
        this.dpId = dpId;
        this.rdsId = rdsId;
        this.rdsVer = rdsVer;
        this.dc = dc;
        this.mi = mi;
        this.mcData = mcData;
        this.sKey = sKey;
        this.hMac = hMac;
        this.encryptedPID = encryptedPID;
        this.ci = ci;
        this.operation = operation;
        this.freshnessFactor = freshnessFactor;
        this.apiUser = apiUser;
        this.shakey = shakey;
        this.pidData = pidData;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Long getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(Long aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getDpId() {
        return dpId;
    }

    public void setDpId(String dpId) {
        this.dpId = dpId;
    }

    public String getRdsId() {
        return rdsId;
    }

    public void setRdsId(String rdsId) {
        this.rdsId = rdsId;
    }

    public String getRdsVer() {
        return rdsVer;
    }

    public void setRdsVer(String rdsVer) {
        this.rdsVer = rdsVer;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getMcData() {
        return mcData;
    }

    public void setMcData(String mcData) {
        this.mcData = mcData;
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public String gethMac() {
        return hMac;
    }

    public void sethMac(String hMac) {
        this.hMac = hMac;
    }

    public String getEncryptedPID() {
        return encryptedPID;
    }

    public void setEncryptedPID(String encryptedPID) {
        this.encryptedPID = encryptedPID;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFreshnessFactor() {
        return freshnessFactor;
    }

    public void setFreshnessFactor(String freshnessFactor) {
        this.freshnessFactor = freshnessFactor;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getShakey() {
        return shakey;
    }

    public void setShakey(String shakey) {
        this.shakey = shakey;
    }

    public String getPidData() {
        return pidData;
    }

    public void setPidData(String pidData) {
        this.pidData = pidData;
    }
}
