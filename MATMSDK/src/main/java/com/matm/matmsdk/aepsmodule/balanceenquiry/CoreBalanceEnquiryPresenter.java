package com.matm.matmsdk.aepsmodule.balanceenquiry;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.CoreAEPSHomeActivity;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CashWithdrawalResponse;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusModel;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusNewActivity;
import com.matm.matmsdk.aepsmodule.utils.AEPSAPIService;
import com.matm.matmsdk.aepsmodule.utils.Session;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * LoginPresenter class Handle Interaction between Model and View
 *
 * @author Subhalaxmi Panda
 * @date 21/06/18.
 */


public class CoreBalanceEnquiryPresenter implements CoreBalanceEnquiryContract.UserActionsListener {
    /**
     * Initialize LoginView
     */
    private CoreBalanceEnquiryContract.View balanceEnquiryContractView;
    private AEPSAPIService aepsapiService;
    private Session session;
    Context context;

    /**
     * Initialize LoginPresenter
     */
    public CoreBalanceEnquiryPresenter(CoreBalanceEnquiryContract.View balanceEnquiryContractView, Context context) {
        this.balanceEnquiryContractView = balanceEnquiryContractView;
        this.context = context;
    }


    @Override
    public void performBalanceEnquiry(final String aadharNumber, final String token, final BalanceEnquiryRequestModel balanceEnquiryRequestModel) {
       /* if (balanceEnquiryRequestModel!=null && balanceEnquiryRequestModel.getAadharNo() !=null && !balanceEnquiryRequestModel.getAadharNo().matches("") &&
                balanceEnquiryRequestModel.getCi() !=null && !balanceEnquiryRequestModel.getCi().matches("") &&
                balanceEnquiryRequestModel.getDc() !=null && !balanceEnquiryRequestModel.getDc().matches("") &&
                balanceEnquiryRequestModel.getDpId() !=null && !balanceEnquiryRequestModel.getDpId().matches("") &&
                balanceEnquiryRequestModel.getEncryptedPID() !=null && !balanceEnquiryRequestModel.getEncryptedPID().matches("") &&
                balanceEnquiryRequestModel.getFreshnessFactor() !=null && !balanceEnquiryRequestModel.getFreshnessFactor().matches("") &&
                balanceEnquiryRequestModel.gethMac() !=null && !balanceEnquiryRequestModel.gethMac().matches("") &&
                balanceEnquiryRequestModel.getIin() !=null && !balanceEnquiryRequestModel.getIin().matches("") &&
                balanceEnquiryRequestModel.getMcData() !=null && !balanceEnquiryRequestModel.getMcData().matches("") &&
                balanceEnquiryRequestModel.getMi() !=null && !balanceEnquiryRequestModel.getMi().matches("") &&
                balanceEnquiryRequestModel.getMobileNumber() !=null && !balanceEnquiryRequestModel.getMobileNumber().matches("") &&
                balanceEnquiryRequestModel.getRdsId() !=null && !balanceEnquiryRequestModel.getRdsId().matches("") &&
                balanceEnquiryRequestModel.getRdsVer() !=null && !balanceEnquiryRequestModel.getRdsVer().matches("") &&
                balanceEnquiryRequestModel.getsKey() !=null && !balanceEnquiryRequestModel.getsKey().matches("")
                ) {*/
        balanceEnquiryContractView.showLoader();

        if (this.aepsapiService == null) {
            this.aepsapiService = new AEPSAPIService();
        }
        AndroidNetworking.get("https://itpl.iserveu.tech/generate/v21")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String key = obj.getString("hello");
                            System.out.println(">>>>-----" + key);
                            byte[] data = Base64.decode(key, Base64.DEFAULT);
                            String encodedUrl = new String(data, "UTF-8");

                            encryptBalanceEnquiry(aadharNumber, token, balanceEnquiryRequestModel, encodedUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("Error: " + anError.getErrorDetail());

                    }
                });

        /*} else {
            balanceEnquiryContractView.hideLoader();
            balanceEnquiryContractView.checkEmptyFields();
        }*/
    }


    public void encryptBalanceEnquiry(String aadhar, String token, BalanceEnquiryRequestModel balanceEnquiryRequestModel, String encodedUrl) {
        CoreBalanceEnquiryAPI balanceEnquiryAPI = this.aepsapiService.getClient().create(CoreBalanceEnquiryAPI.class);

        balanceEnquiryAPI.checkBalanceEnquiry(token, balanceEnquiryRequestModel, encodedUrl).enqueue(new Callback<BalanceEnquiryResponse>() {
            @Override
            public void onResponse(Call<BalanceEnquiryResponse> call, Response<BalanceEnquiryResponse> response) {

                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && !response.body().getStatus().matches("")) {
                        //message = "Login Successful";
                        //balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryStatus(response.body().getStatus(), response.body().getStatusDesc(), response.body());

                    } else {
                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Balance Enquiry Failed", null);
                    }

                } else {
                    //balanceEnquiryContractView.hideLoader();
                    //balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Balance Enquiry Failed",null);

                    if (response.errorBody() != null) {
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            BalanceEnquiryResponse errorResponse = gson.fromJson(mJson, BalanceEnquiryResponse.class);
                            JSONObject obj = new JSONObject(mJson.toString());
                            String statusCode = errorResponse.getStatus();
                            if (Integer.parseInt(statusCode) >= 500) {
                                String error = obj.getString("error");
                                balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkBalanceEnquiryStatus(errorResponse.getStatus(), error, null);

                            } else {
                                if (Integer.parseInt(statusCode) == 400) {
                                    String message = obj.getString("apiComment");
                                    balanceEnquiryContractView.hideLoader();
                                    balanceEnquiryContractView.checkBalanceEnquiryStatus(errorResponse.getStatus(), message, errorResponse);

                                } else {
                                    String message = obj.getString("message");
                                    balanceEnquiryContractView.hideLoader();
                                    balanceEnquiryContractView.checkBalanceEnquiryStatus(errorResponse.getStatus(), message, errorResponse);

                                }


                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Balance Enquiry Failed", null);
                        }
                    } else {
                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Balance Enquiry Failed", null);

                    }
                }
            }

            @Override
            public void onFailure(Call<BalanceEnquiryResponse> call, Throwable t) {
                balanceEnquiryContractView.hideLoader();
                balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Balance Enquiry Failed", null);
            }
        });
    }
}
