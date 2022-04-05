package com.matm.matmsdk.aepsmodule.balanceenquiry;

import com.matm.matmsdk.aepsmodule.utils.AEPSAPIService;

public class BalanceEnquiryAEPS3Presenter implements com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiry3Contract.UserActionsListener  {

    private com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiry3Contract.View balanceEnquiryContractView;
    private AEPSAPIService aepsapiService;




    @Override
    public void performBalanceEnquiry(String retailer, String token, BalanceEnquiryRequestModel balanceEnquiryAEPS3RequestModel) {


    }

    @Override
    public void performBalanceEnquiryAEPS3(String token, BalanceEnquiryAEPS3RequestModel balanceEnquiry3RequestModel, String transaction_type) {
        balanceEnquiryContractView.showLoader();
        if (this.aepsapiService == null) {
            this.aepsapiService = new AEPSAPIService();
        }
        if (transaction_type.equalsIgnoreCase("Request Balance")) {


        }

    }
}
