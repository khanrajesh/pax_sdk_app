package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class PosTransResponse {


    @SerializedName("status")
    private Integer status;
    @SerializedName("statusDes")
    private String statusDes;
    @SerializedName("txnId")
    private String txnId;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDes() {
        return statusDes;
    }

    public void setStatusDes(String statusDes) {
        this.statusDes = statusDes;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }
}
