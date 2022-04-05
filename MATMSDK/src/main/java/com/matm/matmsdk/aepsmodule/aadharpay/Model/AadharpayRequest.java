package com.matm.matmsdk.aepsmodule.aadharpay.Model;

public class AadharpayRequest {

    public String aadharNo;
    public String iin;
    public String apiUser;
    public String latLong;
    public String ipAddress;
    public String amount;
    public String mobileNumber;
    public String retailer;
    public String paramA;
    public String paramB;
    public String paramC;
    public boolean isSL;
    public String bankName;
    public String pidData;
    public String shakey;

    public AadharpayRequest() {
    }

    public AadharpayRequest(String aadharNo, String iin, String apiUser, String latLong, String ipAddress, String amount, String mobileNumber, String retailer, String paramA, String paramB, String paramC, boolean isSL, String bankName, String pidData,String shakey) {
        this.aadharNo = aadharNo;
        this.iin = iin;
        this.apiUser = apiUser;
        this.latLong = latLong;
        this.ipAddress = ipAddress;
        this.amount = amount;
        this.mobileNumber = mobileNumber;
        this.retailer = retailer;
        this.paramA = paramA;
        this.paramB = paramB;
        this.paramC = paramC;
        this.isSL = isSL;
        this.bankName = bankName;
        this.pidData = pidData;
        this.shakey = shakey;
    }




    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
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

    public boolean getIsSL() {
        return isSL;
    }

    public void setIsSL(boolean isSL) {
        this.isSL = isSL;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPidData() {
        return pidData;
    }

    public void setPidData(String pidData) {
        this.pidData = pidData;
    }

    public boolean isSL() {
        return isSL;
    }

    public void setSL(boolean SL) {
        isSL = SL;
    }

    public String getShakey() {
        return shakey;
    }

    public void setShakey(String shakey) {
        this.shakey = shakey;
    }
}
