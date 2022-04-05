package com.matm.matmsdk.aepsmodule.balanceenquiry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;

import org.json.JSONObject;

public class BalanceInqAeps2Model {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("operation")
    @Expose
    private String operation;

    @SerializedName("message")
    @Expose
    private String message;


    @SerializedName("data")
    @Expose
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
