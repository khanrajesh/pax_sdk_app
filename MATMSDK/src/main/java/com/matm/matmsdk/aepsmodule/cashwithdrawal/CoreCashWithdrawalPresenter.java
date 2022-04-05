package com.matm.matmsdk.aepsmodule.cashwithdrawal;

import android.util.Base64;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
 *
 * @author Subhalaxmi Panda
 * @date 21/06/18.
 *
 */


public class CoreCashWithdrawalPresenter implements CoreCashWithDrawalContract.UserActionsListener {
    /**
     * Initialize LoginView
     */
    private CoreCashWithDrawalContract.View cashWithDrawalContractView;
    private AEPSAPIService aepsapiService;
    private Session session;
    /**
     * Initialize LoginPresenter
     */
    public CoreCashWithdrawalPresenter(CoreCashWithDrawalContract.View cashWithDrawalContractView) {
        this.cashWithDrawalContractView = cashWithDrawalContractView;
    }




    @Override
    public void performCashWithdrawal(final String token,final CashWithdrawalRequestModel cashWithdrawalRequestModel) {
       /* if (cashWithdrawalRequestModel!=null && cashWithdrawalRequestModel.getAadharNo() !=null && !cashWithdrawalRequestModel.getAadharNo().matches("") &&
                cashWithdrawalRequestModel.getAmount() !=null && !cashWithdrawalRequestModel.getAmount().matches("") &&
                cashWithdrawalRequestModel.getCi() !=null && !cashWithdrawalRequestModel.getCi().matches("") &&
                cashWithdrawalRequestModel.getDc() !=null && !cashWithdrawalRequestModel.getDc().matches("") &&
                cashWithdrawalRequestModel.getDpId() !=null && !cashWithdrawalRequestModel.getDpId().matches("") &&
                cashWithdrawalRequestModel.getEncryptedPID() !=null && !cashWithdrawalRequestModel.getEncryptedPID().matches("") &&
                cashWithdrawalRequestModel.getFreshnessFactor() !=null && !cashWithdrawalRequestModel.getFreshnessFactor().matches("") &&
                cashWithdrawalRequestModel.gethMac() !=null && !cashWithdrawalRequestModel.gethMac().matches("") &&
                cashWithdrawalRequestModel.getIin() !=null && !cashWithdrawalRequestModel.getIin().matches("") &&
                cashWithdrawalRequestModel.getMcData() !=null && !cashWithdrawalRequestModel.getMcData().matches("") &&
                cashWithdrawalRequestModel.getMi() !=null && !cashWithdrawalRequestModel.getMi().matches("") &&
                cashWithdrawalRequestModel.getMobileNumber() !=null && !cashWithdrawalRequestModel.getMobileNumber().matches("") &&
                cashWithdrawalRequestModel.getOperation() !=null && !cashWithdrawalRequestModel.getOperation().matches("") &&
                cashWithdrawalRequestModel.getRdsId() !=null && !cashWithdrawalRequestModel.getRdsId().matches("") &&
                cashWithdrawalRequestModel.getRdsVer() !=null && !cashWithdrawalRequestModel.getRdsVer().matches("") &&
                cashWithdrawalRequestModel.getsKey() !=null && !cashWithdrawalRequestModel.getsKey().matches("")
                ) {*/
            cashWithDrawalContractView.showLoader();
            if (this.aepsapiService == null) {
                this.aepsapiService = new AEPSAPIService();
            }

            AndroidNetworking.get("https://itpl.iserveu.tech/generate/v22")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String key = obj.getString("hello");
                            System.out.println(">>>>-----"+key);
                            byte[] data = Base64.decode(key,Base64.DEFAULT);
                            String encodedUrl = new String(data, "UTF-8");

                            encryptCashWithDraw(token,cashWithdrawalRequestModel,encodedUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

       /* } else {
            cashWithDrawalContractView.hideLoader();
            cashWithDrawalContractView.checkEmptyFields();
        }*/
    }

    public void encryptCashWithDraw(String token, CashWithdrawalRequestModel cashWithdrawalRequestModel, String encodedUrl){
       Log.d("REQUEST"," Cash Withdraw Data "+cashWithdrawalRequestModel.toString());

        CoreCashWithdrawalAPI cashWithdrawalAPI =this.aepsapiService.getClient().create(CoreCashWithdrawalAPI.class);
        cashWithdrawalAPI.checkCashWithDrawal(token,cashWithdrawalRequestModel,encodedUrl).enqueue(new Callback<CashWithdrawalResponse>() {
            @Override
            public void onResponse(Call<CashWithdrawalResponse> call, Response<CashWithdrawalResponse> response) {
                if(response.isSuccessful()) {
                    // String message = "";
                    if (response.body().getStatus() !=null && !response.body().getStatus().matches("")) {
                        //message = "Login Successful";
//                        cashWithDrawalContractView.hideLoader();
                        cashWithDrawalContractView.checkCashWithdrawalStatus(response.body().getStatus(), response.body().getStatusDesc(),response.body());
                    }else{
                        cashWithDrawalContractView.hideLoader();
                        cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed",null);
                    }
                }else{
                   // cashWithDrawalContractView.hideLoader();
                   // cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed",null);
                    //cashWithDrawalContractView.hideLoader();
                    //cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed",null);
                    if(response.errorBody() != null) {
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            CashWithdrawalResponse errorResponse = gson.fromJson(mJson, CashWithdrawalResponse.class);
                            JSONObject obj = new JSONObject(mJson.toString());
                            String statusCode = errorResponse.getStatus();
                            if(Integer.parseInt(statusCode)>=500){
                                String error = obj.getString("error");
                                cashWithDrawalContractView.hideLoader();
                                cashWithDrawalContractView.checkCashWithdrawalStatus(errorResponse.getStatus(), error, null);

                            }else {
                                if(Integer.parseInt(statusCode)==400) {
                                    String message = obj.getString("apiComment");
                                    cashWithDrawalContractView.hideLoader();
                                    cashWithDrawalContractView.checkCashWithdrawalStatus(errorResponse.getStatus(), message, errorResponse);

                                }else{
                                    String message = obj.getString("message");
                                    cashWithDrawalContractView.hideLoader();
                                    cashWithDrawalContractView.checkCashWithdrawalStatus(errorResponse.getStatus(), message, errorResponse);

                                }

                            }
                            } catch (Exception ex) {
                            ex.printStackTrace();
                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed",null);
                        }
                    }else{
                        cashWithDrawalContractView.hideLoader();
                        cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed",null);

                    }

                }
            }
            @Override
            public void onFailure(Call<CashWithdrawalResponse> call, Throwable t) {
                cashWithDrawalContractView.hideLoader();
                cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed",null);
            }
        });
    }


}
