package com.matm.matmsdk.aepsmodule.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.matm.matmsdk.Utils.SdkConstants;


public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;


    public String getUserLatLong() {
        return prefs.getString(SdkConstants.USER_LATLONG, null);
    }

    public void setUserLatLong(String userToken) {
        editor.putString(SdkConstants.USER_LATLONG, userToken);
        editor.commit();
    }

    public String getUserPincode() {
        return prefs.getString(SdkConstants.USER_PINCODE, null);
    }

    public void setUserPincode(String userToken) {
        editor.putString(SdkConstants.USER_PINCODE, userToken);
        editor.commit();
    }

    public String getUserCity() {
        return prefs.getString(SdkConstants.USER_CITY, null);
    }

    public void setUserCity(String userToken) {
        editor.putString(SdkConstants.USER_CITY, userToken);
        editor.commit();
    }

    public String getUserState() {
        return prefs.getString(SdkConstants.USER_STATE, null);
    }

    public void setUserState(String userToken) {
        editor.putString(SdkConstants.USER_STATE, userToken);
        editor.commit();
    }


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
}