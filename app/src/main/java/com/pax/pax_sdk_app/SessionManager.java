package com.pax.pax_sdk_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "isu";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_TOKEN = "token";
    public static final String KEY_ADMIN = "admin";

    public static final String userName = "userName";
    public static final String userType = "userType";
    public static final String userBrand = "userBrand";

    public static final String userBalance = "userBalance";
    public static final String adminName = "adminName";
    public static final String mposNumber = "mposNumber";
    public static final String profileName = "profileName";
    public static final String mobileNo = "mobileNo";

    public static final String ManufacturerName = "manufacturername";
    public static final String DeviceSerialNumber = "deviceSerialnumber";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_TOKEN, name);
        editor.putString(KEY_ADMIN, email);
        editor.commit();
    }

    public void createLoginSession_Username(String username){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(userName, username);
        editor.commit();
    }
    public void saveIntent(){
        editor.putBoolean("IS_INTENT", true);
        editor.commit();
    }
    public boolean getIntent(){
        if(pref.contains("IS_INTENT")){
            return true;
        }else{
            return false;
        }
    }
    public void removeIntent(){
        if(pref.contains("IS_INTENT")) {
            editor.remove("IS_INTENT");
            editor.commit();
        }
    }

    public void saveBankListinSession(Set<String> banklist_set){
        editor.putStringSet("bank_list_Session",banklist_set);
        editor.apply();
        editor.commit();
    }
    public Set<String> getBankListinSession(){
        Set<String> banklist_arr=new HashSet<>();
        banklist_arr=pref.getStringSet("bank_list_Session",null);
        return banklist_arr;
    }

    public void createUserSession(String _userName, String _userType,String _userBalance,String _adminName,String _mposNumber, String _userBrand, String _profileName, String _mobileNo){
        editor.putString(userName, _userName);
        editor.putString(userType, _userType);
        editor.putString(userBalance, _userBalance);
        editor.putString(adminName, _adminName);
        editor.putString(mposNumber, _mposNumber);
        editor.putString(userBrand,_userBrand);
        editor.putString(profileName,_profileName);
        editor.putString(mobileNo,_mobileNo);
        editor.commit();
    }

    public void createDeviceInfoSession(String _manufacturername, String _deviceSerialnumber) {
        editor.putString(ManufacturerName, _manufacturername);
        editor.putString(DeviceSerialNumber, _deviceSerialnumber);
        editor.commit();
    }

    public HashMap<String, String> getDeviceInfoSession() {
        HashMap<String, String> deviceInfo = new HashMap<String, String>();
        deviceInfo.put(ManufacturerName, pref.getString(ManufacturerName, null));
        deviceInfo.put(DeviceSerialNumber, pref.getString(DeviceSerialNumber, null));
        return deviceInfo;
    }


//    public void createUserSession(String name, String email){
//        editor.putString(KEY_TOKEN, name);
//        editor.putString(KEY_ADMIN, email);
//        editor.commit();
//    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */


    public void checkLogin(){
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_ADMIN, pref.getString(KEY_ADMIN, null));
        return user;
    }

    public HashMap<String, String> getUserSession(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(userName, pref.getString(userName, null));
        user.put(userType, pref.getString(userType, null));
        user.put(userBalance, pref.getString(userBalance, null));
        user.put(adminName, pref.getString(adminName, null));
        user.put(mposNumber, pref.getString(mposNumber, null));
        user.put(userBrand, pref.getString(userBrand, null));
        user.put(profileName, pref.getString(profileName,null));
        user.put(mobileNo,pref.getString(mobileNo,null));
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);

    }

    /**
     * Quick check for login
     * **/

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
