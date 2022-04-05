package com.matm.matmsdk.aepsmodule.aadharpay.Model;

public class AadharpayResponse {
//    {"transactionStatus":"Transaction request received","status":0,"txId":"822068476981026816","errors":null}

    public  String transactionStatus;
    public int status;
    public  String txId;
    public  String errors;
    public  String transactionAmount;
    public  String bankName;
    public String mobileNumber;

    public AadharpayResponse() {

    }

        public AadharpayResponse(String transactionStatus, int status, String txId, String errors,String transactionAmount, String bankName,String mobileNumber) {
        this.transactionStatus = transactionStatus;
        this.status = status;
        this.txId = txId;
        this.errors = errors;
        this.transactionAmount = transactionAmount;
        this.bankName = bankName;
        this.mobileNumber = mobileNumber;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public int getStatus() {
        return status;
    }

    public String getTxId() {
        return txId;
    }

    public String getErrors() {
        return errors;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
