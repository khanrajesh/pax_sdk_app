package com.matm.matmsdk.aepsmodule.balanceenquiry;
import android.content.Context;

import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.ministatement.StatementResponse;

import org.json.JSONObject;


public class BalanceEnquiryContract {


    public interface View {


        void checkBalanceEnquiryStatus(String status, String message, BalanceEnquiryResponse balanceEnquiryResponse);

        void checkBalanceEnquiryAEPS2(String status, String message, AepsResponse balanceEnquiryResponse);


        void checkStatementEnquiryAEPS2(String status, String message, JSONObject statementResponse);

        void checkEmptyFields();
        void showLoader();
        void hideLoader();



    }

    interface UserActionsListener {
        void performBalanceEnquiry(Context context, String retailer, String token, BalanceEnquiryRequestModel balanceEnquiryRequestModel);

        void performBalanceEnquiryAEPS2(Context context, String token, BalanceEnquiryAEPS2RequestModel balanceEnquiryRequestModel, String transaction_type);



    }


}

