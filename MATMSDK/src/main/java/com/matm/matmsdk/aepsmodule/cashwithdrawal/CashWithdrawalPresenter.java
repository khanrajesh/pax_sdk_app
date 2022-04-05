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
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryResponse;
import com.matm.matmsdk.aepsmodule.utils.AEPSAPIService;
import com.matm.matmsdk.aepsmodule.utils.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CashWithdrawalPresenter implements CashWithDrawalContract.UserActionsListener {

    private CashWithDrawalContract.View cashWithDrawalContractView;
    private AEPSAPIService aepsapiService;
    private Session session;

    /**
     * Initialize LoginPresenter
     */
    public CashWithdrawalPresenter(CashWithDrawalContract.View cashWithDrawalContractView) {
        this.cashWithDrawalContractView = cashWithDrawalContractView;
    }

    @Override
    public void performCashWithdrawal(String retailer, String token, CashWithdrawalRequestModel cashWithdrawalRequestModel) {
        cashWithDrawalContractView.showLoader();

        if (this.aepsapiService == null) {
            this.aepsapiService = new AEPSAPIService();
        }

        CashWithdrawalAPI cashWithdrawalAPI = this.aepsapiService.getClient().create(CashWithdrawalAPI.class);
        // System.out.println("Request Cash withdraw :"+cashWithdrawalAPI.toString());
        System.out.println("Request Cash withdraw :" + cashWithdrawalRequestModel.toString());

        cashWithdrawalAPI.checkCashWithDrawal(retailer, token, cashWithdrawalRequestModel).enqueue(new Callback<CashWithdrawalResponse>() {

            @Override
            public void onResponse(Call<CashWithdrawalResponse> call, Response<CashWithdrawalResponse> response) {

                try {

                    if (response.isSuccessful()) {
                        // String message = "";
                        if (response.body().getStatus() != null && !response.body().getStatus().matches("")) {
                            //message = "Login Successful";
                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalStatus(response.body().getStatus(), response.body().getStatusDesc(), response.body());
                        } else {
                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed", null);
                        }

                    } else {
                        //cashWithDrawalContractView.hideLoader();
                        //cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed",null);
                        if (response.errorBody() != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                CashWithdrawalResponse errorResponse = gson.fromJson(mJson, CashWithdrawalResponse.class);
                                JSONObject obj = new JSONObject(mJson.toString());
                                String statusCode = errorResponse.getStatus();
                                if (Integer.parseInt(statusCode) >= 500) {
                                    String error = "";
                                    try {
                                        error = obj.getString("error");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    cashWithDrawalContractView.hideLoader();
                                    cashWithDrawalContractView.checkCashWithdrawalStatus(errorResponse.getStatus(), error, null);

                                } else {

                                    if (Integer.parseInt(statusCode) == 400) {
                                        String message = "";
                                        try {
                                            message = obj.getString("apiComment");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        cashWithDrawalContractView.hideLoader();
                                        cashWithDrawalContractView.checkCashWithdrawalStatus(errorResponse.getStatus(), message, errorResponse);


                                    } else {
                                        String message = "";
                                        try {
                                            message = obj.getString("message");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        cashWithDrawalContractView.hideLoader();
                                        cashWithDrawalContractView.checkCashWithdrawalStatus(errorResponse.getStatus(), message, errorResponse);

                                    }


                                }
                            } catch (IOException | JSONException ex) {
                                ex.printStackTrace();
                                cashWithDrawalContractView.hideLoader();
                                cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed", null);
                            }
                        } else {
                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed", null);

                        }


                    }
                } catch (Exception e) {
                    cashWithDrawalContractView.hideLoader();
                    cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed", null);
                }
            }

            @Override
            public void onFailure(Call<CashWithdrawalResponse> call, Throwable t) {
                cashWithDrawalContractView.hideLoader();
                cashWithDrawalContractView.checkCashWithdrawalStatus("", "Cash Withdrawal Failed", null);
            }


        });


    }

    @Override
    public void performCashWithdrawalAEPS2(final String token, final CashWithdrawalAEPS2RequestModel cashWithdrawalRequestModel) {
        cashWithDrawalContractView.showLoader();
        if (this.aepsapiService == null) {
            this.aepsapiService = new AEPSAPIService();
        }
        AndroidNetworking.get("https://vpn.iserveu.tech/generate/v99")
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
//                            encodedUrl = "https://vpn.iserveu.tech/AEPSFRM/aeps2/ibl/cashWithDrawl/";
                            encryptCashWithDraw(token, cashWithdrawalRequestModel, encodedUrl);
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

    }

    public void encryptCashWithDraw(String token, CashWithdrawalAEPS2RequestModel cashWithdrawalRequestModel, String encodedUrl) {
        CashWithdrawalAPI cashWithdrawalAPI = this.aepsapiService.getClient().create(CashWithdrawalAPI.class);
        cashWithdrawalAPI.checkCashWithDrawall(token, cashWithdrawalRequestModel, encodedUrl).enqueue(new Callback<AepsResponse>() {
            @Override
            public void onResponse(Call<AepsResponse> call, Response<AepsResponse> response) {
                try {

                    if (response.isSuccessful()) {
                        // String message = "";
                        if (response.body().getStatus() != null && !response.body().getStatus().matches("")) {
                            //message = "Login Successful";
//                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalAEPS2(response.body().getStatus(), "Cash Withdrawal Success", response.body());
                        } else {
                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed", null);
                        }
                    } else {

                        if (response.errorBody() != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                AepsResponse errorResponse = gson.fromJson(mJson, AepsResponse.class);
                                JSONObject obj = new JSONObject(mJson.toString());
                                String statusCode = errorResponse.getStatus();
                                if (Integer.parseInt(statusCode) >= 500) {
                                    String error = "";
                                    try {
                                        error = obj.getString("error");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    cashWithDrawalContractView.hideLoader();
                                    cashWithDrawalContractView.checkCashWithdrawalAEPS2(errorResponse.getStatus(), error, null);

                                } else {
                                    if (Integer.parseInt(statusCode) == 400) {
                                        String message = "";
                                        try {
                                            message = obj.getString("apiComment");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        cashWithDrawalContractView.hideLoader();
                                        cashWithDrawalContractView.checkCashWithdrawalAEPS2(errorResponse.getStatus(), message, errorResponse);
                                    } else if (Integer.parseInt(statusCode) == -1) {
                                        String message = "";
                                        try {
                                            message = obj.getString("apiComment");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        cashWithDrawalContractView.hideLoader();
                                        cashWithDrawalContractView.checkCashWithdrawalAEPS2(errorResponse.getStatus(), message, errorResponse);
                                    } else {

                                        String message = "";
                                        try {
                                            message = obj.getString("message");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        cashWithDrawalContractView.hideLoader();
                                        cashWithDrawalContractView.checkCashWithdrawalAEPS2(errorResponse.getStatus(), message, errorResponse);
                                    }

                                    //cashWithDrawalContractView.hideLoader();
                                    //cashWithDrawalContractView.checkCashWithdrawalAEPS2(errorResponse.getStatus(), "Cash Withdrawal Failed",errorResponse);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                cashWithDrawalContractView.hideLoader();
                                cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed", null);
                            }
                        }
                    }

                } catch (Exception e) {
                    cashWithDrawalContractView.hideLoader();
                    cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed", null);

                }
            }
            @Override
            public void onFailure(Call<AepsResponse> call, Throwable t) {
                cashWithDrawalContractView.hideLoader();
                cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed", null);
            }


            });
        }

    }
