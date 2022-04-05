package com.matm.matmsdk.Utils;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.matm.matmsdk.aepsmodule.utils.Util.showAlert;

public class GetUserAuthToken {


    public GetUserAuthToken(Context context) {
        Session session = new Session(context);
        String url = SdkConstants.BASE_URL + "/api/getAuthenticateData";

        JSONObject obj = new JSONObject();

        try {

            obj.put("encryptedData", SdkConstants.encryptedData);
            obj.put("retailerUserName", SdkConstants.loginID);


            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");

                                if (status.equalsIgnoreCase("success")) {
                                    String userName = obj.getString("username");
                                    String userToken = obj.getString("usertoken");
                                    session.setUsername(userName);
                                    session.setUserToken(userToken);
                                    SdkConstants.userNameFromCoreApp = userName;
                                    SdkConstants.USER_TOKEN = userToken;
                                    SdkConstants.USER_NAME = userName;
                                    EnvData.token = userToken;
                                   // showAlert(context, userName,userToken );

                                } else {
                                    showAlert(context, "", status);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                showAlert(context, "", "Invalid Encrypted Data");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }

                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
