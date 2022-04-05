package com.matm.matmsdk.aepsmodule.aadharpay.Model;

public class FCMNotificationData {

    private String status;
    private String statusDesc;
    private String transactionMode;
    private String txId;
    private String rrn;
    public String aadhaarNumber;
    private String bankName;
    public String updatedDate;
    private String balanceAmount;
    private String transactionAmount;

    public FCMNotificationData() {
    }

    public FCMNotificationData(String status, String statusDesc, String transactionMode, String txId, String rrn, String aadhaarNumber, String bankName, String updatedDate, String balanceAmount, String transactionAmount) {
        this.status = status;
        this.statusDesc = statusDesc;
        this.transactionMode = transactionMode;
        this.txId = txId;
        this.rrn = rrn;
        this.aadhaarNumber = aadhaarNumber;
        this.bankName = bankName;
        this.updatedDate = updatedDate;
        this.balanceAmount = balanceAmount;
        this.transactionAmount = transactionAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
