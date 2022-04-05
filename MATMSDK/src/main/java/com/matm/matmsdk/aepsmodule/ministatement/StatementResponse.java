package com.matm.matmsdk.aepsmodule.ministatement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatementResponse {
    @SerializedName("agentName")
    @Expose
    private String agentName;
    @SerializedName("agentId")
    @Expose
    private Object agentId;
    @SerializedName("agentLocation")
    @Expose
    private Object agentLocation;
    @SerializedName("terminalId")
    @Expose
    private String terminalId;
    @SerializedName("shopName")
    @Expose
    private Object shopName;
    @SerializedName("contactNumber")
    @Expose
    private Object contactNumber;
    @SerializedName("customerAadhaarNo")
    @Expose
    private String customerAadhaarNo;
    @SerializedName("benificiaryAadhaarNo")
    @Expose
    private Object benificiaryAadhaarNo;
    @SerializedName("customerName")
    @Expose
    private Object customerName;
    @SerializedName("stan")
    @Expose
    private Object stan;
    @SerializedName("rrn")
    @Expose
    private String rrn;
    @SerializedName("uAuthCode")
    @Expose
    private Object uAuthCode;
    @SerializedName("transactionStatus")
    @Expose
    private String transactionStatus;
    @SerializedName("amount")
    @Expose
    private Object amount;
    @SerializedName("balance")
    @Expose
    private Object balance;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("apiComment")
    @Expose
    private String apiComment;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("txId")
    @Expose
    private String txId;
    @SerializedName("bankName")
    @Expose
    private String bankName;
    @SerializedName("miniStatement")
    @Expose
    private Object miniStatement;
    @SerializedName("miniStatementString")
    @Expose
    private String miniStatementString;

    @SerializedName("errors")
    @Expose
    private String errors;

    public Object getuAuthCode() {
        return uAuthCode;
    }

    public void setuAuthCode(Object uAuthCode) {
        this.uAuthCode = uAuthCode;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Object getAgentId() {
        return agentId;
    }

    public void setAgentId(Object agentId) {
        this.agentId = agentId;
    }

    public Object getAgentLocation() {
        return agentLocation;
    }

    public void setAgentLocation(Object agentLocation) {
        this.agentLocation = agentLocation;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Object getShopName() {
        return shopName;
    }

    public void setShopName(Object shopName) {
        this.shopName = shopName;
    }

    public Object getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Object contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCustomerAadhaarNo() {
        return customerAadhaarNo;
    }

    public void setCustomerAadhaarNo(String customerAadhaarNo) {
        this.customerAadhaarNo = customerAadhaarNo;
    }

    public Object getBenificiaryAadhaarNo() {
        return benificiaryAadhaarNo;
    }

    public void setBenificiaryAadhaarNo(Object benificiaryAadhaarNo) {
        this.benificiaryAadhaarNo = benificiaryAadhaarNo;
    }

    public Object getCustomerName() {
        return customerName;
    }

    public void setCustomerName(Object customerName) {
        this.customerName = customerName;
    }

    public Object getStan() {
        return stan;
    }

    public void setStan(Object stan) {
        this.stan = stan;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public Object getUAuthCode() {
        return uAuthCode;
    }

    public void setUAuthCode(Object uAuthCode) {
        this.uAuthCode = uAuthCode;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Object getAmount() {
        return amount;
    }

    public void setAmount(Object amount) {
        this.amount = amount;
    }

    public Object getBalance() {
        return balance;
    }

    public void setBalance(Object balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Object getMiniStatement() {
        return miniStatement;
    }

    public void setMiniStatement(Object miniStatement) {
        this.miniStatement = miniStatement;
    }

    public String getMiniStatementString() {
        return miniStatementString;
    }

    public void setMiniStatementString(String miniStatementString) {
        this.miniStatementString = miniStatementString;
    }

}
