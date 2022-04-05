package com.matm.matmsdk.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.matm.matmsdk.Utils.EnvData;
import com.matm.matmsdk.Utils.JWTUtils;


public class LoginSession {

    public static void storeData(String Token, String AdminName, Context context) throws Exception {
        SharedPreferences pref = context.getSharedPreferences("AuthData", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("Token", Token); // Storing string
        editor.putString("AdminName", AdminName);
        editor.putString("UserName", JWTUtils.getUser(Token));
        editor.putLong("ExpireTime", JWTUtils.getExpire(Token));

        editor.putBoolean("isLogin", true); // Storing boolean - true/false

        editor.commit();
    }

    public static boolean sessionCheck(Context context) throws Exception {

        SharedPreferences pref = context.getSharedPreferences("AuthData", 0);
        SharedPreferences.Editor editor = pref.edit();
        Log.d("isLogin", String.valueOf(pref.getString("Token",null)));
        if(pref.getBoolean("isLogin",false)){
            EnvData.token = pref.getString("Token",null);
            EnvData.AdminName = pref.getString("AdminName",null);
            if(!JWTUtils.isExpired(pref.getString("Token",null))){
                return true;
            }
            else{
                editor.remove("Token");
                editor.remove("AdminName");
                editor.remove("UserName");
                editor.remove("ExpireTime");

                editor.commit(); // commit changes
                return false;
            }
        }
        else{
            return false;
        }
    }

    public static void clearData(Context context){
        SharedPreferences pref = context.getSharedPreferences("AuthData", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("Token");
        editor.remove("AdminName");
        editor.remove("UserName");
        editor.remove("ExpireTime");

        editor.commit(); // commit changes
    }
}
