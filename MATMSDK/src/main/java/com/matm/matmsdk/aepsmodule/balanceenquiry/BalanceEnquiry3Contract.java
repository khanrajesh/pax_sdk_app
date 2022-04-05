package com.matm.matmsdk.aepsmodule.balanceenquiry;

import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.ministatement.StatementResponse;

public class BalanceEnquiry3Contract {

    public interface View {


        void checkBalanceEnquiryStatus(String status, String message, BalanceEnquiryResponse balanceEnquiryResponse);

        void checkBalanceEnquiryAEPS3(String status, String message, AepsResponse balanceEnquiryResponse);


        void checkStatementEnquiryAEPS3(String status, String message, StatementResponse statementResponse);

        void checkEmptyFields();
        void showLoader();
        void hideLoader();


    }

    interface UserActionsListener {
        void performBalanceEnquiry(String retailer, String token, BalanceEnquiryRequestModel balanceEnquiryRequestModel);

        void performBalanceEnquiryAEPS3(String token, BalanceEnquiryAEPS3RequestModel balanceEnquiry3RequestModel, String transaction_type);



    }



}
