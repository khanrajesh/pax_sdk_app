package com.matm.matmsdk.aepsmodule.cashwithdrawal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashWithdrawalResponse{


    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("statusDesc")
    @Expose
    private String statusDesc;

    @SerializedName("nextFreshnessFactor")
    @Expose
    private String nextFreshnessFactor;

    @SerializedName("balance")
    @Expose
    private String balance;

    @SerializedName("apiTid")
    @Expose
    private String apiTid;

    @SerializedName("apiComment")
    @Expose
    private String apiComment;

    @SerializedName("createdDate")
    @Expose
    private String createdDate;

    @SerializedName("referenceNo")
    @Expose
    private String referenceNo;


    @SerializedName("txId")
    @Expose
    private String txId;

    @SerializedName("bankName")
    @Expose
    private String bankName;

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

    public String getNextFreshnessFactor() {
        return nextFreshnessFactor;
    }

    public void setNextFreshnessFactor(String nextFreshnessFactor) {
        this.nextFreshnessFactor = nextFreshnessFactor;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getApiTid() {
        return apiTid;
    }

    public void setApiTid(String apiTid) {
        this.apiTid = apiTid;
    }

    public String getApiComment() {
        return apiComment;
    }

    public void setApiComment(String apiComment) {
        this.apiComment = apiComment;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public CashWithdrawalResponse() {
    }

    @Override
    public String toString() {
        return "CashWithdrawalResponse{" +
                "status='" + status + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                ", nextFreshnessFactor='" + nextFreshnessFactor + '\'' +
                ", balance='" + balance + '\'' +
                ", apiTid='" + apiTid + '\'' +
                ", apiComment='" + apiComment + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", referenceNo='" + referenceNo + '\'' +
                ", txId='" + txId + '\'' +
                ", bankName='" + bankName + '\'' +
                '}';
    }
}
