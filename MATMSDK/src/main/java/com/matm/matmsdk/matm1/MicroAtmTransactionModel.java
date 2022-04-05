package com.matm.matmsdk.matm1;

import java.io.Serializable;

public class MicroAtmTransactionModel implements Serializable {

    private String txnStatus,txnAmt,rnr,cardNumber,availableBalance,transactionDateandTime,type,terminalId,accountNo,balanceEnquiryStatus;
    private String errormsg;
    private String statusError;


    public MicroAtmTransactionModel (){}

    public String getStatusError() {
        return statusError;
    }

    public void setStatusError(String statusError) {
        this.statusError = statusError;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBalanceEnquiryStatus() {
        return balanceEnquiryStatus;
    }

    public void setBalanceEnquiryStatus(String balanceEnquiryStatus) {
        this.balanceEnquiryStatus = balanceEnquiryStatus;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getRnr() {
        return rnr;
    }

    public void setRnr(String rnr) {
        this.rnr = rnr;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getTransactionDateandTime() {
        return transactionDateandTime;
    }

    public void setTransactionDateandTime(String transactionDateandTime) {
        this.transactionDateandTime = transactionDateandTime;
    }
}
