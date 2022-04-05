package com.matm.matmsdk.Utils;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JWTUtils {
    public static void decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        } catch (UnsupportedEncodingException e) {
            //Error
        }
    }

    public static String getUser(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
            JSONObject obj =new JSONObject(getJson(split[1]));
            String user = obj.getString("sub");
            return user;
        } catch (UnsupportedEncodingException e) {
            //Error
            return null;
        }

    }

    public static Long getExpire(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
            JSONObject obj =new JSONObject(getJson(split[1]));
            String user = obj.getString("exp");
            Long userlong = Long.valueOf(user);
            return userlong;
        } catch (UnsupportedEncodingException e) {
            //Error
            return null;
        }

    }


    public static Boolean isExpired(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
            JSONObject obj =new JSONObject(getJson(split[1]));
            String user = obj.getString("exp");
            Long userlong = Long.valueOf(user);
            Date exp = new Date(userlong);
            Date curDate = new Date();
            if(exp.after(curDate)){
                return true;
            }
            else{
                return false;
            }

        } catch (UnsupportedEncodingException e) {
            //Error
            return true;
        }

    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
