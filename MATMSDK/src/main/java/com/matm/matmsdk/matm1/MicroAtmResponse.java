package com.matm.matmsdk.matm1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MicroAtmResponse {

    @SerializedName("encData")
    @Expose
    private String encData;


    @SerializedName("authentication")
    @Expose
    private String authentication;


    @SerializedName("errorResponse")
    @Expose
    private String errorResponse;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    @SerializedName("txnId")
    @Expose
    private String txnId;


    public MicroAtmResponse() {
    }

    public String getEncData() {
        return encData;
    }

    public void setEncData(String encData) {
        this.encData = encData;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(String errorResponse) {
        this.errorResponse = errorResponse;
    }
}
