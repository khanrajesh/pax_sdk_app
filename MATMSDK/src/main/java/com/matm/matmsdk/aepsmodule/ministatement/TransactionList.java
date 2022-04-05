package com.matm.matmsdk.aepsmodule.ministatement;

public class TransactionList {
    String date, d_c, transaction_remark, amount;

    public TransactionList(String date, String d_c, String transaction_remark, String amount) {
        this.date = date;
        this.d_c = d_c;
        this.transaction_remark = transaction_remark;
        this.amount = amount;
    }

}
