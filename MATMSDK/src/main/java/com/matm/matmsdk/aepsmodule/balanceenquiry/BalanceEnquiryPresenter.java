package com.matm.matmsdk.aepsmodule.balanceenquiry;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAEPS2RequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAPI;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryContract;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryRequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryResponse;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.ministatement.StatementResponse;
import com.matm.matmsdk.aepsmodule.utils.AEPSAPIService;
import com.matm.matmsdk.aepsmodule.utils.Session;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BalanceEnquiryPresenter implements com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryContract.UserActionsListener {
    /**
     * Initialize LoginView
     */
    private com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryContract.View balanceEnquiryContractView;
    private AEPSAPIService aepsapiService;
    private Session session;

    /**
     * Initialize LoginPresenter
     */
    public BalanceEnquiryPresenter(BalanceEnquiryContract.View balanceEnquiryContractView) {
        this.balanceEnquiryContractView = balanceEnquiryContractView;
    }


    @Override
    public void performBalanceEnquiry(Context context, String retailer, String token, BalanceEnquiryRequestModel balanceEnquiryRequestModel) {

        balanceEnquiryContractView.showLoader();

        if (this.aepsapiService == null) {
            this.aepsapiService = new AEPSAPIService();
        }

        BalanceEnquiryAPI balanceEnquiryAPI = this.aepsapiService.getClient().create(BalanceEnquiryAPI.class);
        System.out.println("Request Balance Enquery :" + balanceEnquiryRequestModel.toString());

        balanceEnquiryAPI.checkBalanceEnquiry(retailer, token, balanceEnquiryRequestModel).enqueue(new Callback<BalanceEnquiryResponse>() {
            @Override
            public void onResponse(Call<BalanceEnquiryResponse> call, Response<BalanceEnquiryResponse> response) {

                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && !response.body().getStatus().matches("")) {

                        System.out.println("Response:" + response.body().toString());

                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryStatus(response.body().getStatus(), response.body().getStatusDesc(), response.body());
                    } else {
                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Balance Enquiry Failed", null);
                    }

                } else {

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
                                String error = "";
                                try {
                                    error = obj.getString("error");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkBalanceEnquiryStatus(errorResponse.getStatus(), error, null);

                            } else {

                                if (Integer.parseInt(statusCode) == 400) {

                                    String message = "";
                                    try {
                                        message = obj.getString("apiComment");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    balanceEnquiryContractView.hideLoader();
                                    balanceEnquiryContractView.checkBalanceEnquiryStatus(errorResponse.getStatus(), message, errorResponse);


                                } else {
                                    String message = "";
                                    try {
                                        message = obj.getString("message");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    balanceEnquiryContractView.hideLoader();
                                    balanceEnquiryContractView.checkBalanceEnquiryStatus(errorResponse.getStatus(), message, errorResponse);

                                }

                            }
                        } catch (IOException | JSONException ex) {
                            ex.printStackTrace();
                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Cash Withdrawal Failed", null);
                        }
                    } else {
                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryStatus("", "Cash Withdrawal Failed", null);

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

    @Override
    public void performBalanceEnquiryAEPS2(Context context, final String token, final BalanceEnquiryAEPS2RequestModel balanceEnquiryRequestModel, String transaction_type) {
        balanceEnquiryContractView.showLoader();
        if (this.aepsapiService == null) {
            this.aepsapiService = new AEPSAPIService();
        }
        // if (transaction_type.equalsIgnoreCase("Request Balance")) {

        AndroidNetworking.get("https://vpn.iserveu.tech/generate/v76")
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
                           // encodedUrl = "https://rms.iserveu.online/checkBalance";
                            encodedUrl = "https://rms.iserveu.online/checkBalance";
//                            encodedUrl = "https://rmsstag.iserveu.online/checkBalance";
                            // if (transaction_type.equalsIgnoreCase("Request Balance")) {
                            encryptBalanceEnquiry(context,token, balanceEnquiryRequestModel, encodedUrl);
                            // }

                               /* else{
                                    encryptStatementEnquiry(token, balanceEnquiryRequestModel, encodedUrl);
                                }*/

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
        // }
        /*  else {

         *//**
         *  Mini statement AEPS2
         *
         * *//*
            AndroidNetworking.get("https://vpn.iserveu.tech/generate/v78")
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
                                encryptStatementEnquiry(token, balanceEnquiryRequestModel, encodedUrl);

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

            // String encoded_url  = "https://indusindaeps.iserveu.online/aeps2/ibl/miniStatement";
            //encryptStatementEnquiry(token,balanceEnquiryRequestModel,encoded_url);
        }*/
    }

    public void encryptBalanceEnquiry(Context context,final String token, final BalanceEnquiryAEPS2RequestModel balanceEnquiryRequestModel, String encodedUrl) {
        // com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAPI balanceEnquiryAPI = this.aepsapiService.getClient().create(com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAPI.class);

        System.out.println("Request Balance Inquery :" + balanceEnquiryRequestModel.toString());
        JSONObject obj = new JSONObject();
        try {
            obj.put("shakey", balanceEnquiryRequestModel.getShakey());
            obj.put("isBe", balanceEnquiryRequestModel.getBe());
            obj.put("latLong", balanceEnquiryRequestModel.getLatLong());
            obj.put("paramC", balanceEnquiryRequestModel.getParamC());
            obj.put("paramB", balanceEnquiryRequestModel.getParamB());
            obj.put("paramA", balanceEnquiryRequestModel.getParamA());
            obj.put("isSL", balanceEnquiryRequestModel.getSL());
            obj.put("retailer", balanceEnquiryRequestModel.getRetailer());
            obj.put("apiUser", balanceEnquiryRequestModel.getApiUser());
            obj.put("sKey", balanceEnquiryRequestModel.getsKey());
            obj.put("rdsVer", balanceEnquiryRequestModel.getRdsVer());
            obj.put("rdsId", balanceEnquiryRequestModel.getRdsId());
            obj.put("operation", balanceEnquiryRequestModel.getOperation());
            obj.put("mobileNumber", balanceEnquiryRequestModel.getMobileNumber());
            obj.put("mi", balanceEnquiryRequestModel.getMi());
            obj.put("mcData", balanceEnquiryRequestModel.getMcData());
            obj.put("iin", balanceEnquiryRequestModel.getIin());
            obj.put("hMac", balanceEnquiryRequestModel.gethMac());
            obj.put("freshnessFactor", balanceEnquiryRequestModel.getFreshnessFactor());
            obj.put("encryptedPID", balanceEnquiryRequestModel.getEncryptedPID());
            obj.put("dpId", balanceEnquiryRequestModel.getDpId());
            obj.put("deviceSerial", balanceEnquiryRequestModel.getDeviceSerial());
            obj.put("dc", balanceEnquiryRequestModel.getDc());
            obj.put("ci", balanceEnquiryRequestModel.getCi());
            obj.put("virtualId", balanceEnquiryRequestModel.getVirtualId());
            obj.put("aadharNo", balanceEnquiryRequestModel.getAadharNo());
            obj.put("amount", balanceEnquiryRequestModel.getAmount());


            AndroidNetworking.post(encodedUrl)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .addHeaders("Authorization",token)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");
                                if (status.equalsIgnoreCase("1")) {
                                    balanceEnquiryContractView.hideLoader();
                                    String operation = obj.getString("operation");
                                    if (operation.equalsIgnoreCase("BalanceEnquiry")) {
//                                        UpdateAepsData("BE",balanceEnquiryRequestModel.getShakey());
                                        JSONObject objinner = obj.getJSONObject("data");
                                        System.out.println("Data : "+objinner);
                                        Gson gson = new Gson();
                                        AepsResponse aepsResponse = gson.fromJson(String.valueOf(objinner), AepsResponse.class);
                                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2(status, "Balance Enquiry Success", aepsResponse);




                                    } else if (operation.equalsIgnoreCase("MiniStatement")) {

//                                        UpdateAepsData("RMS",balanceEnquiryRequestModel.getShakey());

                                        JSONObject objinner = obj.getJSONObject("data");
                                        System.out.println("Data : "+objinner);
                                        balanceEnquiryContractView.checkStatementEnquiryAEPS2(status, "Statement Enquiry Success", objinner);



                                    } else {
                                        balanceEnquiryContractView.checkStatementEnquiryAEPS2("", " Enquiry Failed", null);

                                    }

                                } else {
                                    //Error
                                    balanceEnquiryContractView.hideLoader();
                                    balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", " Enquiry Failed", null);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", " Enquiry Failed", null);

                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            balanceEnquiryContractView.hideLoader();
                            try {
                                if(anError.getErrorCode()==400){
                                    JSONObject obj  = new JSONObject(anError.getErrorBody());
                                    String statusStr = obj.getString("status");
                                    if(statusStr.equalsIgnoreCase("0")){
                                        String statusCodee = obj.getString("statusCode");
                                        String operation = obj.getString("operation");

                                        if(operation.equalsIgnoreCase("MiniStatement")) {

                                            // error from raj
                                            if (statusCodee.equalsIgnoreCase("001")) {
                                                JSONObject innerObj = obj.getJSONObject("data");
                                                //String apiComment = innerObj.getString("apiComment");
                                                balanceEnquiryContractView.checkStatementEnquiryAEPS2(statusCodee, "Statement Enquiry Failed", innerObj);


                                            } else if (statusCodee.equalsIgnoreCase("002")) {

                                                JSONObject innerObj = obj.getJSONObject("data");
                                                balanceEnquiryContractView.checkStatementEnquiryAEPS2(statusCodee, "Statement Enquiry Failed", innerObj);

                                            }
                                        }else if(operation.equalsIgnoreCase("BalanceEnquiry"))  {

                                            // error from raj
                                            if (statusCodee.equalsIgnoreCase("001")) {
                                                JSONObject innerObj = obj.getJSONObject("data");
                                                //String apiComment = innerObj.getString("apiComment");
                                               // balanceEnquiryContractView.checkStatementEnquiryAEPS2(statusCodee, "Statement Enquiry Failed", innerObj);
                                                System.out.println("Data : "+innerObj);
                                                Gson gson = new Gson();
                                                AepsResponse aepsResponse = gson.fromJson(String.valueOf(innerObj), AepsResponse.class);
                                                 balanceEnquiryContractView.checkBalanceEnquiryAEPS2(statusStr, "Balance Enquiry Failed", aepsResponse);



                                            } else if (statusCodee.equalsIgnoreCase("002")) {

                                                JSONObject innerObj = obj.getJSONObject("data");
                                                Gson gson = new Gson();
                                                AepsResponse aepsResponse = gson.fromJson(String.valueOf(innerObj), AepsResponse.class);
                                                balanceEnquiryContractView.checkBalanceEnquiryAEPS2(statusStr, "Balance Enquiry Failed", aepsResponse);

                                            }
                                        }



                                    }


                                }
                                else  if(anError.getErrorCode()==500){
                                    // 500 error code

                                    Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT).show();
                                }
                            }catch (JsonIOException | JSONException e){
                                e.printStackTrace();
                            }


                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
            balanceEnquiryContractView.hideLoader();
            balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", " Enquiry Failed", null);
        }
    }

    private void UpdateAepsData(String operation,String aadharHash) {
        String url = "https://aeps-limit-vn3k2k7q7q-uc.a.run.app/update_limit";
        JSONObject obj = new JSONObject();
        try {
            obj.put("aadhaar_hash", aadharHash);
            obj.put("operation_performed", operation);
            obj.put("gateway", "G2");

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("RAJESH", "Success ");
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("RAJESH", "Fail ");
                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }






    }
}


















/*
        balanceEnquiryAPI.checkBalanceEnquiryy(token, balanceEnquiryRequestModel, encodedUrl).enqueue(new Callback<BalanceInqAeps2Model>() {
            @Override
            public void onResponse(Call<BalanceInqAeps2Model> call, Response<BalanceInqAeps2Model> response) {
                try {

                    if (response.isSuccessful()) {
                        if(response.body().getStatus().equalsIgnoreCase("1")){
                            if(response.body().getOperation().equalsIgnoreCase("BalanceEnquiry")){

                                balanceEnquiryContractView.checkBalanceEnquiryAEPS2(response.body().getStatus(), "Balance Enquiry Success", response.body().getData());


                            }else if(response.body().getOperation().equalsIgnoreCase("MiniStatement")){
                                if(response.body().getData().toString().isEmpty()){
                                    balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed", null);
                                }else{
                                    balanceEnquiryContractView.checkStatementEnquiryAEPS2(response.body().getStatus(), "Statement Enquiry Success", response.body().getData());

                                }



                            }

                        }else if(response.body().getStatus().equalsIgnoreCase("0")){

                        }else{
                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", " Enquiry Failed", null);

                        }

                    } else {

                      */
/*  if (response.errorBody() != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                AepsResponse errorResponse = gson.fromJson(mJson, AepsResponse.class);
                                // balanceEnquiryContractView.hideLoader();
                                // balanceEnquiryContractView.checkBalanceEnquiryAEPS2(errorResponse.getStatus(), "Balance Enquiry Failed", errorResponse);
                                JSONObject obj = new JSONObject(mJson.toString());
                                String statusCode = errorResponse.getStatus();
                                if (Integer.parseInt(statusCode) >= 500) {
                                    String error = "";
                                    try {
                                        error = obj.getString("error");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    balanceEnquiryContractView.hideLoader();
                                    balanceEnquiryContractView.checkBalanceEnquiryAEPS2(errorResponse.getStatus(), error, null);
                                } else {
                                    if (Integer.parseInt(statusCode) == 400) {
                                        String message = "";
                                        try {
                                            message = obj.getString("apiComment");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        balanceEnquiryContractView.hideLoader();
                                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2(errorResponse.getStatus(), message, errorResponse);
                                    }else if(Integer.parseInt(statusCode)==-1) {
                                        String message = "";
                                        try {
                                            message = obj.getString("apiComment");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        balanceEnquiryContractView.hideLoader();
                                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2(errorResponse.getStatus(), message, errorResponse);
                                    }
                                    else{
                                        String message = "";
                                        try {
                                            message = obj.getString("message");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        balanceEnquiryContractView.hideLoader();
                                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2(errorResponse.getStatus(), message, errorResponse);

                                    }

                                }

                            } catch (IOException ex) {
                                ex.printStackTrace();
                                balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed", null);
                            }
                        } else {
                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed", null);

                        }*//*

                    }
                   */
/* }else{
                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed",null);

                    }*//*


                } catch (Exception e) {
                    balanceEnquiryContractView.hideLoader();
                    balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed", null);

                }


            }

            @Override
            public void onFailure(Call<BalanceInqAeps2Model> call, Throwable t) {
                balanceEnquiryContractView.hideLoader();
                balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed", null);
            }
        });
*/
    //}

/*
    public void encryptStatementEnquiry(final String token, final BalanceEnquiryAEPS2RequestModel balanceEnquiryRequestModel, String encodedUrl) {
        com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAPI balanceEnquiryAPI = this.aepsapiService.getClient().create(BalanceEnquiryAPI.class);

        balanceEnquiryAPI.checkStatementEnquiryy(token, balanceEnquiryRequestModel, encodedUrl).enqueue(new Callback<BalanceInqAeps2Model>() {
            @Override
            public void onResponse(Call<BalanceInqAeps2Model> call, Response<BalanceInqAeps2Model> response) {

                try {
                    if (response.body() != null) {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && !response.body().getStatus().matches("")) {

                                //balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkStatementEnquiryAEPS2(response.body().getStatus(), "Statement Enquiry Success", response.body());
                            } else {
                                balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed", null);
                            }

                        } else {

                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed", null);



                        }

                    } else {

                        if (response.errorBody() != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {
                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                StatementResponse errorResponse = gson.fromJson(mJson, StatementResponse.class);
                                balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkStatementEnquiryAEPS2(errorResponse.getStatus(), "Statement Enquiry Failed", errorResponse);

                            } catch (IOException ex) {
                                ex.printStackTrace();
                                balanceEnquiryContractView.hideLoader();
                                balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed", null);
                            }
                        } else {
                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed", null);

                        }


                        // balanceEnquiryContractView.hideLoader();
                        // balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed",null);

                    }
                } catch (Exception exc) {
                    balanceEnquiryContractView.hideLoader();
                    balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed", null);

                }
            }

            @Override
            public void onFailure(Call<BalanceInqAeps2Model> call, Throwable t) {
                balanceEnquiryContractView.hideLoader();
                balanceEnquiryContractView.checkStatementEnquiryAEPS2("", "Statement Enquiry Failed", null);
            }
        });
    }
*/

//}
