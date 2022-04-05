package com.matm.matmsdk.Utils;

/*
 * for matm
 * @author Subhashree*/

import android.content.Context;
import android.content.SharedPreferences;


public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;




    public String getUserToken() {
        return prefs.getString(SdkConstants.USER_TOKEN_KEY, null);
    }

    public void setUserToken(String userToken) {
        editor.putString(SdkConstants.USER_TOKEN_KEY, userToken);
        editor.commit();
    }

    public String getFreshnessFactor() {
        return prefs.getString(SdkConstants.NEXT_FRESHNESS_FACTOR, null);
    }

    public void setFreshnessFactor(String freshnessFactor) {
        editor.putString(SdkConstants.NEXT_FRESHNESS_FACTOR, freshnessFactor);
        editor.commit();
    }



    public void clear(){
        prefs = ctx.getSharedPreferences(SdkConstants.EASY_AEPS_PREF_KEY, Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.clear().commit();
    }


    public Session(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(SdkConstants.EASY_AEPS_PREF_KEY, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(boolean logggedIn) {
        editor.putBoolean(SdkConstants.EASY_AEPS_USER_LOGGED_IN_KEY, logggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(SdkConstants.EASY_AEPS_USER_LOGGED_IN_KEY, false);
    }
    public void setUsername(String username) {
        editor.putString (SdkConstants.EASY_AEPS_USER_NAME_KEY, username);
        editor.commit();
    }

    public String getUserName() {
        return prefs.getString (SdkConstants.EASY_AEPS_USER_NAME_KEY, null);
    }
    public void setEncryptedString(String encryptedString) {
        editor.putString (SdkConstants.encryptedString, encryptedString);
        editor.commit();
    }


    public String getEncryptedString() {
        return prefs.getString(SdkConstants.encryptedString, null);
    }

    public void setDeviceName(String deviceName) {
        editor.putString (SdkConstants.devicename, deviceName);
        editor.commit();
    }
    public String getDeviceName() {
        return prefs.getString(SdkConstants.devicename, null);
    }

    public void setDeviceMac(String deviceMac) {
        editor.putString (SdkConstants.devicemac, deviceMac);
        editor.commit();
    }
    public String getDeviceMac() {
        return prefs.getString(SdkConstants.devicemac, null);
    }
}