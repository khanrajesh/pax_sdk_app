package com.matm.matmsdk.matm1;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.matm.matmsdk.aepsmodule.utils.AEPSAPIService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MicroAtmPresenter implements MicroAtmContract.UserActionsListener {

    private MicroAtmContract.View microAtmContractView;
    private AEPSAPIService aepsapiService;

    public MicroAtmPresenter(MicroAtmContract.View microAtmContractView) {
        this.microAtmContractView = microAtmContractView;
    }

    @Override
    public void performRequestData(String retailer, String token, final MicroAtmRequestModel microAtmRequestModel) {

        if(microAtmRequestModel!=null && microAtmRequestModel.getAmount()!=null && microAtmRequestModel.getTransactionType()!=null
                && !microAtmRequestModel.getTransactionType().matches("") && microAtmRequestModel.getTransactionMode()!=null && !microAtmRequestModel.getTransactionMode().matches(""))
        {
            //microAtmContractView.showLoader();

            if (this.aepsapiService == null) {
                this.aepsapiService = new AEPSAPIService();
            }

            MicroAtmAPI microAtmAPI =this.aepsapiService.getClient().create(MicroAtmAPI.class);

            microAtmAPI.checkRequestCode(retailer,token,microAtmRequestModel).enqueue(new Callback<MicroAtmResponse>() {
                @Override
                public void onResponse(Call<MicroAtmResponse> call, Response<MicroAtmResponse> response) {
                    if(response.isSuccessful()) {
                        // String message = "";
                        if (response.body().getAuthentication()!=null && !response.body().getAuthentication().matches("")) {
                            //message = "Login Successful";
                            microAtmContractView.hideLoader();
                            microAtmContractView.checkRequestCode(response.body().getAuthentication(), response.body().getEncData(),response.body());
                        }else{
                            microAtmContractView.hideLoader();
                            microAtmContractView.checkRequestCode("", "Encryption Failed",null);
                        }

                    }else{
                        if(response.errorBody() != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                MicroAtmResponse errorResponse = gson.fromJson(mJson, MicroAtmResponse.class);
                                microAtmContractView.hideLoader();
                                microAtmContractView.checkRequestCode("",errorResponse.getErrorResponse(),null);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }else{
                            microAtmContractView.hideLoader();
                            microAtmContractView.checkRequestCode("","Service is currently unavailable",null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MicroAtmResponse> call, Throwable t) {

                    microAtmContractView.hideLoader();
                    microAtmContractView.checkRequestCode("", "Encrryption Failed",null);

                }
            });
          //  {"BalanceEnquiryStatus":"Balance Enquiry Successful","RRN":"900515029759","CardNumber":"**********956444","AvailableBalance":"29924.68","TransactionDatetime":"2019-01-05 15:41:56","AccountNo":"50100109092763","TerminalID":"IS000008"}

        } else {
            microAtmContractView.hideLoader();
            microAtmContractView.checkEmptyFields();
        }

    }
}
