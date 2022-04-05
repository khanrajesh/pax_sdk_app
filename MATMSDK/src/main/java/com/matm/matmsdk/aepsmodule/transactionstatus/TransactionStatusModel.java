package com.matm.matmsdk.aepsmodule.transactionstatus;

import java.io.Serializable;

public class TransactionStatusModel implements Serializable {
    private String aadharCard;
    private String status;
    private String bankName;
    private String referenceNo;
    private String balanceAmount;
    private String transactionAmount;
    private String transactionType;
    private String apiComment;
    private String statusDesc;
    private String txnID;


    public String getTxnID() {
        return txnID;
    }

    public void setTxnID(String txnID) {
        this.txnID = txnID;
    }


    public String getApiComment() {
        return apiComment;
    }

    public void setApiComment(String apiComment) {
        this.apiComment = apiComment;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatusModel() {
    }

    public String getAadharCard() {
        return aadharCard;
    }

    public void setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
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

    @Override
    public String toString() {
        return "TransactionStatusModel{" +
                "aadharCard='" + aadharCard + '\'' +
                ", status='" + status + '\'' +
                ", bankName='" + bankName + '\'' +
                ", referenceNo='" + referenceNo + '\'' +
                ", balanceAmount='" + balanceAmount + '\'' +
                ", transactionAmount='" + transactionAmount + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", apiComment='" + apiComment + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                ", txnID='" + txnID + '\'' +
                '}';
    }
}
