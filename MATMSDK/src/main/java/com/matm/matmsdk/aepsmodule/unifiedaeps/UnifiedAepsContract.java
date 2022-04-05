package com.matm.matmsdk.aepsmodule.unifiedaeps;
import android.content.Context;

import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAEPS2RequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryRequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryResponse;


public class UnifiedAepsContract {


    public interface View {
        void checkUnifiedResponseStatus(String status, String message, UnifiedTxnStatusModel miniStatementResponseModel);
        void checkMSunifiedStatus(String status,String message,UnifiedTxnStatusModel miniStatementResponseModel);
        void showLoader();
        void hideLoader();
    }
    interface UserActionsListener {
        void performUnifiedResponse(Context context, String token, UnifiedAepsRequestModel unifiedAepsRequestModel, String transaction_type,int gatewayPriority);
    }
}

