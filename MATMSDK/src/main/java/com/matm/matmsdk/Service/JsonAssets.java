package com.matm.matmsdk.Service;

import android.content.Context;

import com.matm.matmsdk.Utils.SdkConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JsonAssets {
    Context context;

    public JsonAssets(Context context) {
        this.context = context;
        parseJsonAssets();
    }

    private void parseJsonAssets() {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("isu_config.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
            parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONObject info = object.getJSONObject("userInfo");
            SdkConstants.paramA = info.getString("paramA");
            SdkConstants.paramB = info.getString("paramB");
            SdkConstants.paramC = info.getString("paramC");

            if (info.getString("applicationType").equals("CORE")) {
                SdkConstants.isSL = false;
                SdkConstants.userNameFromCoreApp = info.getString("userName");
                SdkConstants.tokenFromCoreApp = info.getString("token");
            } else {
                SdkConstants.isSL = true;
                SdkConstants.loginID = info.getString("loginID");
                SdkConstants.encryptedData = info.getString("encryptedData");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
