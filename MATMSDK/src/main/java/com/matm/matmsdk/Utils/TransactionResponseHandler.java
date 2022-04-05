package com.matm.matmsdk.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionResponseHandler {

    public static void responseHandler(String details){

        ResponseConstant.DE0 = "";
        ResponseConstant.DE11 = "";
        ResponseConstant.DE12 = "";
        ResponseConstant.DE13 = "";
        ResponseConstant.DE22 = "";
        ResponseConstant.DE23 = "";
        ResponseConstant.DE24 = "";
        ResponseConstant.DE25 = "";
        ResponseConstant.DE3 = "";
        ResponseConstant.DE35 = "";
        ResponseConstant.DE37 = "";
        ResponseConstant.DE38 = "";
        ResponseConstant.DE39 = "";
        ResponseConstant.DE4 = "";
        ResponseConstant.DE41 = "";
        ResponseConstant.DE42 = "";
        ResponseConstant.DE43 = "";
        ResponseConstant.DE45 = "";
        ResponseConstant.DE48 = "";
        ResponseConstant.DE52 = "";
        ResponseConstant.DE55 = "";
        ResponseConstant.DE61 = "";
        ResponseConstant.DE7 = "";
        try {
            JSONObject jsonObject = new JSONObject(details);

            if (jsonObject.has("0")) {
                ResponseConstant.DE0 = jsonObject.getString("0");
            }
            if(jsonObject.has("11")){
                ResponseConstant.DE11 = jsonObject.getString("11");
            }
            if(jsonObject.has("12")){
                ResponseConstant.DE12 = jsonObject.getString("12");
            }
            if(jsonObject.has("13")){
                ResponseConstant.DE13 = jsonObject.getString("13");
            }
            if(jsonObject.has("22")){
                ResponseConstant.DE22 = jsonObject.getString("22");
            }
            if(jsonObject.has("23")){
                ResponseConstant.DE23 = jsonObject.getString("23");
            }
            if(jsonObject.has("24")){
                ResponseConstant.DE24 = jsonObject.getString("24");
            }
            if(jsonObject.has("25")){
                ResponseConstant.DE25 = jsonObject.getString("25");
            }
            if(jsonObject.has("3")){
                ResponseConstant.DE3 = jsonObject.getString("3");
            }
            if(jsonObject.has("35")){
                ResponseConstant.DE35 = jsonObject.getString("35");
            }
            if(jsonObject.has("37")){
                ResponseConstant.DE37 = jsonObject.getString("37");
            }
            if(jsonObject.has("38")){
                ResponseConstant.DE38 = jsonObject.getString("38");
            }
            if(jsonObject.has("39")){
                ResponseConstant.DE39 = jsonObject.getString("39");
            }
            if(jsonObject.has("4")){
                ResponseConstant.DE4 = jsonObject.getString("4");
            }
            if(jsonObject.has("41")){
                ResponseConstant.DE41 = jsonObject.getString("41");
            }
            if(jsonObject.has("42")){
                ResponseConstant.DE42 = jsonObject.getString("42");
            }
            if(jsonObject.has("43")){
                ResponseConstant.DE43 = jsonObject.getString("43");
            }if(jsonObject.has("45")){
                ResponseConstant.DE45 = jsonObject.getString("45");
            }
            if(jsonObject.has("48")){
                ResponseConstant.DE48 = jsonObject.getString("48");
            }
            if(jsonObject.has("52")){
                ResponseConstant.DE52 = jsonObject.getString("52");
            }
            if(jsonObject.has("54")){
               // String str = jsonObject.optString("54").replace("null", "N/A");

                ResponseConstant.DE54 = jsonObject.getString("54");
               // ResponseConstant.DE54 = str;
            }
            if(jsonObject.has("55")){
                ResponseConstant.DE55 = jsonObject.getString("55");
            }
            if(jsonObject.has("61")){
                ResponseConstant.DE61 = jsonObject.getString("61");
            }
            if(jsonObject.has("7")){
                ResponseConstant.DE7 = jsonObject.getString("7");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
