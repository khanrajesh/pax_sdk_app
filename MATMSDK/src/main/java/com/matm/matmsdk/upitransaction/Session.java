package com.matm.matmsdk.upitransaction;


import android.content.Context;
import android.content.SharedPreferences;


public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;


    public String getUserToken() {
        return prefs.getString(UPIConstant.USER_TOKEN_KEY, null);
    }

    public void setUserToken(String userToken) {
        editor.putString(UPIConstant.USER_TOKEN_KEY, userToken);
        editor.commit();
    }

 /*   public void setLoggedIn(boolean logggedIn) {
        editor.putBoolean(AepsSdkConstants.EASY_AEPS_USER_LOGGED_IN_KEY, logggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(AepsSdkConstants.EASY_AEPS_USER_LOGGED_IN_KEY, false);
    }*/
    public void setUsername(String username) {
        editor.putString (UPIConstant.EASY_AEPS_USER_NAME_KEY, username);
        editor.commit();
    }

    public String getUserName() {
        return prefs.getString (UPIConstant.EASY_AEPS_USER_NAME_KEY, null);
    }
   /* public void setEncryptedString(String encryptedString) {
        editor.putString (AepsSdkConstants.encryptedString, encryptedString);
        editor.commit();
    }


    public String getEncryptedString() {
        return prefs.getString(AepsSdkConstants.encryptedString, null);
    }*/
}