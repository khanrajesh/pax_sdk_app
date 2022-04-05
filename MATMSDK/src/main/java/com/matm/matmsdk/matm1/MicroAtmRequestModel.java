package com.matm.matmsdk.matm1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MicroAtmRequestModel {


    @SerializedName("amount")
    @Expose
    private String amount;


    @SerializedName("transactionType")
    @Expose
    private String transactionType;

    @SerializedName("transactionMode")
    @Expose
    private String transactionMode;

    public MicroAtmRequestModel(String amount, String transactionType, String transactionMode) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionMode = transactionMode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }
}
