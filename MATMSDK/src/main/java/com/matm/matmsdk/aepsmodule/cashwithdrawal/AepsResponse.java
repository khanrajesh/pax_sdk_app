package com.matm.matmsdk.aepsmodule.cashwithdrawal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AepsResponse {

@SerializedName("status")
@Expose
private String status;
@SerializedName("apiComment")
@Expose
private String apiComment;
@SerializedName("agentId")
@Expose
private String agentId;
@SerializedName("agentLocation")
@Expose
private String agentLocation;
@SerializedName("terminalId")
@Expose
private String terminalId;
@SerializedName("shopName")
@Expose
private String shopName;
@SerializedName("contactNo")
@Expose
private String contactNo;
@SerializedName("customerAadhaarNo")
@Expose
private String customerAadhaarNo;
@SerializedName("customerName")
@Expose
private Object customerName;
@SerializedName("stan")
@Expose
private String stan;
@SerializedName("rrn")
@Expose
private String rrn;
@SerializedName("uAuthCode")
@Expose
private String uAuthCode;
@SerializedName("transactionStatus")
@Expose
private String transactionStatus;
@SerializedName("amount")
@Expose
private String amount;
@SerializedName("balance")
@Expose
private String balance;
@SerializedName("createdDate")
@Expose
private String createdDate;
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

public String getApiComment() {
return apiComment;
}

public void setApiComment(String apiComment) {
this.apiComment = apiComment;
}

public String getAgentId() {
return agentId;
}

public void setAgentId(String agentId) {
this.agentId = agentId;
}

public String getAgentLocation() {
return agentLocation;
}

public void setAgentLocation(String agentLocation) {
this.agentLocation = agentLocation;
}

public String getTerminalId() {
return terminalId;
}

public void setTerminalId(String terminalId) {
this.terminalId = terminalId;
}

public String getShopName() {
return shopName;
}

public void setShopName(String shopName) {
this.shopName = shopName;
}

public String getContactNo() {
return contactNo;
}

public void setContactNo(String contactNo) {
this.contactNo = contactNo;
}

public String getCustomerAadhaarNo() {
return customerAadhaarNo;
}

public void setCustomerAadhaarNo(String customerAadhaarNo) {
this.customerAadhaarNo = customerAadhaarNo;
}

public Object getCustomerName() {
return customerName;
}

public void setCustomerName(Object customerName) {
this.customerName = customerName;
}

public String getStan() {
return stan;
}

public void setStan(String stan) {
this.stan = stan;
}

public String getRrn() {
return rrn;
}

public void setRrn(String rrn) {
this.rrn = rrn;
}

public String getUAuthCode() {
return uAuthCode;
}

public void setUAuthCode(String uAuthCode) {
this.uAuthCode = uAuthCode;
}

public String getTransactionStatus() {
return transactionStatus;
}

public void setTransactionStatus(String transactionStatus) {
this.transactionStatus = transactionStatus;
}

public String getAmount() {
return amount;
}

public void setAmount(String amount) {
this.amount = amount;
}

public String getBalance() {
return balance;
}

public void setBalance(String balance) {
this.balance = balance;
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

}