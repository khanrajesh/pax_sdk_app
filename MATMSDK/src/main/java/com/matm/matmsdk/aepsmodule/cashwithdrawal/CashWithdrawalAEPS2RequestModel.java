package com.matm.matmsdk.aepsmodule.cashwithdrawal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matm.matmsdk.FileUtils;

public class CashWithdrawalAEPS2RequestModel {
    @SerializedName("amount")
    @Expose
    private String amount;


    @SerializedName("aadharNo")
    @Expose
    private String aadharNo;

    @SerializedName("virtualId")
    @Expose
    private String virtualId;

    @SerializedName("ci")
    @Expose
    private String ci;


    @SerializedName("dc")
    @Expose
    private String dc;


    @SerializedName("deviceSerial")
    @Expose
    private String deviceSerial;


    @SerializedName("dpId")
    @Expose
    private String dpId;


    @SerializedName("encryptedPID")
    @Expose
    private String encryptedPID;


    @SerializedName("freshnessFactor")
    @Expose
    private String freshnessFactor;


    @SerializedName("hMac")
    @Expose
    private String hMac;


    @SerializedName("iin")
    @Expose
    private String iin;


    @SerializedName("mcData")
    @Expose
    private String mcData;


    @SerializedName("mi")
    @Expose
    private String mi;


    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;


    @SerializedName("operation")
    @Expose
    private String operation;


    @SerializedName("rdsId")
    @Expose
    private String rdsId;


    @SerializedName("rdsVer")
    @Expose
    private String rdsVer;


    @SerializedName("sKey")
    @Expose
    private String sKey;


    @SerializedName("apiUser")
    @Expose
    private String apiUser;

    @SerializedName("retailer")
    @Expose
    private String retailer;

    @SerializedName("isSL")
    @Expose
    private Boolean isSL;


    @SerializedName("paramA")
    @Expose
    private String paramA;

    @SerializedName("paramB")
    @Expose
    private String paramB;

    @SerializedName("paramC")
    @Expose
    private String paramC;
    @SerializedName("shakey")
    @Expose
    private String  shakey;


    @SerializedName("latLong")
    @Expose
    private String latLong;


    public CashWithdrawalAEPS2RequestModel(String amount, String aadharNo, String virtualid, String ci, String dc, String deviceSerial, String dpId, String encryptedPID, String freshnessFactor, String hMac, String iin, String mcData, String mi, String mobileNumber, String operation, String rdsId, String rdsVer, String sKey, String apiUser, String retailer, Boolean isSL, String paramA, String paramB, String paramC,String latLong,String  shakey) {
        this.amount = amount;
        this.aadharNo = aadharNo;
        this.virtualId = virtualid;
        this.ci = ci;
        this.dc = dc;
        this.deviceSerial = deviceSerial;
        this.dpId = dpId;
        this.encryptedPID = encryptedPID;
        this.freshnessFactor = freshnessFactor;
        this.hMac = hMac;
        this.iin = iin;
        this.mcData = mcData;
        this.mi = mi;
        this.mobileNumber = mobileNumber;
        this.operation = operation;
        this.rdsId = rdsId;
        this.rdsVer = rdsVer;
        this.sKey = sKey;
        this.apiUser = apiUser;
        this.retailer = retailer;
        this.isSL = isSL;
        this.paramA = paramA;
        this.paramB = paramB;
        this.paramC = paramC;
        this.latLong = latLong;
        this.shakey = shakey;

    }

    public String getVirtualid() {
        return virtualId;
    }

    public void setVirtualid(String virtualid) {
        this.virtualId = virtualid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getDpId() {
        return dpId;
    }

    public void setDpId(String dpId) {
        this.dpId = dpId;
    }

    public String getEncryptedPID() {
        return encryptedPID;
    }

    public void setEncryptedPID(String encryptedPID) {
        this.encryptedPID = encryptedPID;
    }

    public String getFreshnessFactor() {
        return freshnessFactor;
    }

    public void setFreshnessFactor(String freshnessFactor) {
        this.freshnessFactor = freshnessFactor;
    }

    public String gethMac() {
        return hMac;
    }

    public void sethMac(String hMac) {
        this.hMac = hMac;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getMcData() {
        return mcData;
    }

    public void setMcData(String mcData) {
        this.mcData = mcData;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public String getVirtualId() {
        return virtualId;
    }

    public void setVirtualId(String virtualId) {
        this.virtualId = virtualId;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public Boolean getSL() {
        return isSL;
    }

    public void setSL(Boolean SL) {
        isSL = SL;
    }

    public String getParamA() {
        return paramA;
    }

    public void setParamA(String paramA) {
        this.paramA = paramA;
    }

    public String getParamB() {
        return paramB;
    }

    public void setParamB(String paramB) {
        this.paramB = paramB;
    }

    public String getParamC() {
        return paramC;
    }

    public void setParamC(String paramC) {
        this.paramC = paramC;
    }
    public String getLatLong() {return latLong; }

    public void setLatLong(String latLong) {this.latLong = latLong; }


}
