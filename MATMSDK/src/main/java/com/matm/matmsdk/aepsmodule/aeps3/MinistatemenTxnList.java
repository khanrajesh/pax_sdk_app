package com.matm.matmsdk.aepsmodule.aeps3;

public class MinistatemenTxnList {
    String date, d_c, transaction_remark;
Double amount;
    public MinistatemenTxnList(String date, String d_c, String transaction_remark, Double amount) {
        this.date = date;
        this.d_c = d_c;
        this.transaction_remark = transaction_remark;
        this.amount = amount;
    }

}
