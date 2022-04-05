package com.matm.matmsdk.aepsmodule.unifiedaeps;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAEPS2RequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAPI;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryContract;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryRequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryResponse;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.ministatement.StatementList_Adapter;
import com.matm.matmsdk.aepsmodule.ministatement.TransactionList;
import com.matm.matmsdk.aepsmodule.utils.AEPSAPIService;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.aepsmodule.utils.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UnifiedAepsPresenter implements UnifiedAepsContract.UserActionsListener {
    /**
     * Initialize LoginView
     */
    private UnifiedAepsContract.View unifiedAepsContract;
    private AEPSAPIService aepsapiService;
    private Session session;
    private String encodedUrl;
    private String transactionTypeCW = "Cash Withdrawal";
    private String transactionTypeBE = "Request Balance";
    private String transactionTypeMS = "Mini Statement";
    private String apiVPNUrl = "";
    public UnifiedAepsPresenter(UnifiedAepsContract.View unifiedAepsContract) {
        this.unifiedAepsContract = unifiedAepsContract;
    }
    @Override
    public void performUnifiedResponse(Context context, String token, UnifiedAepsRequestModel unifiedAepsRequestModel, String transaction_type, int gatewayPriority) {
        if (transaction_type.equals(transactionTypeCW)) {
            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v103";
            } else {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v106";
            }
        } else if (transaction_type.equals(transactionTypeBE)) {
            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v102";
            } else {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v105";
            }
        } else {
            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v104";
            } else {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v107";
            }
        }
        AndroidNetworking.get(apiVPNUrl)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String key = obj.getString("hello");
                            byte[] data = Base64.decode(key, Base64.DEFAULT);
                            String encodedUrl = new String(data, "UTF-8");
                            encryptBalanceEnquiry(context, token, unifiedAepsRequestModel, encodedUrl, gatewayPriority);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.v("UNIFIEDAEPS",anError.getErrorBody().toString());
                    }
                });
    }
    public void encryptBalanceEnquiry(Context context, final String token, final UnifiedAepsRequestModel unifiedAepsRequestModel, String encodedUrl, int gatewayPriority) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latLong", unifiedAepsRequestModel.getLatLong());
            obj.put("paramC", unifiedAepsRequestModel.getParamC());
            obj.put("paramB", unifiedAepsRequestModel.getParamB());
            obj.put("paramA", unifiedAepsRequestModel.getParamA());
            obj.put("retailer", unifiedAepsRequestModel.getRetailer());
            obj.put("apiUser", unifiedAepsRequestModel.getApiUser());
            obj.put("mobileNumber", unifiedAepsRequestModel.getMobileNumber());
            obj.put("iin", unifiedAepsRequestModel.getIin());
            obj.put("aadharNo", unifiedAepsRequestModel.getAadharNo());
            obj.put("amount", unifiedAepsRequestModel.getAmount());
            obj.put("bankName", unifiedAepsRequestModel.getBankName());
            obj.put("gatewayPriority", gatewayPriority);
            String piddata = unifiedAepsRequestModel.getPidData();
            String pidata = piddata.replaceAll("\\R+", "");
            obj.put("pidData", pidata.trim());
            AndroidNetworking.post(encodedUrl)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .addHeaders("Authorization", token)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");
                                String transactionMode = obj.getString("transactionMode");
                                if (status.equalsIgnoreCase("SUCCESS")) {
                                    unifiedAepsContract.hideLoader();
                                    if (transactionMode.equalsIgnoreCase("AEPS_MINI_STATEMENT")) {
                                        Gson gson = new Gson();
                                        UnifiedTxnStatusModel aepsResponse = gson.fromJson(String.valueOf(obj), UnifiedTxnStatusModel.class);
                                        unifiedAepsContract.checkMSunifiedStatus(status, "Balance Enquiry Success", aepsResponse);
                                    } else {
                                        Gson gson = new Gson();
                                        UnifiedTxnStatusModel aepsResponse = gson.fromJson(String.valueOf(obj), UnifiedTxnStatusModel.class);
                                        unifiedAepsContract.checkUnifiedResponseStatus(status, "Balance Enquiry Success", aepsResponse);
                                    }
                                } else {
                                    Gson gson = new Gson();
                                    UnifiedTxnStatusModel aepsResponse = gson.fromJson(String.valueOf(obj), UnifiedTxnStatusModel.class);
                                    unifiedAepsContract.checkUnifiedResponseStatus(status, "Fail", aepsResponse);
                                    unifiedAepsContract.hideLoader();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                unifiedAepsContract.hideLoader();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            unifiedAepsContract.hideLoader();
                            try {
                                JSONObject jsonObject = new JSONObject(anError.getErrorBody());
                                String apiComment = jsonObject.getString("apiComment");
                                Toast.makeText(context, apiComment.toString().trim(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(context, anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                unifiedAepsContract.hideLoader();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            unifiedAepsContract.hideLoader();
        }
    }
}