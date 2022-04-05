package com.matm.matmsdk.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import isumatm.androidsdk.equitas.BuildConfig;


public class PreferenceUtility {
    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String ENABLE_REGISTRATION = "ENABLE_REGISTRATION";
    private static PreferenceUtility preferenceUtility;
    private final String APP_PREFERENCE = BuildConfig.LIBRARY_PACKAGE_NAME;
    private final Context context;

    public PreferenceUtility(Context context) {
        this.context = context;
    }

    /**
     * Gets instance.
     *
     * @param context the context
     * @return the instance
     */
    public static PreferenceUtility getInstance(Context context) {
        if (preferenceUtility == null)
            preferenceUtility = new PreferenceUtility(context);
        return preferenceUtility;
    }
    /**
     * Save string.
     *
     * @param key   the key
     * @param value the value
     */
    public void saveString(String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Save boolean.
     *
     * @param key   the key
     * @param value the value
     */
    public void saveBoolean(String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Save integer.
     *
     * @param key   the key
     * @param value the value
     */
    public void saveInteger(String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }



    /**
     * Save Longg.
     *
     * @param key   the key
     * @param value the value
     */
    public void saveLong(String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Gets string.
     *
     * @param key      the key
     * @param defValue the def value
     * @return the string
     */
    public String getString(String key, String defValue) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    /**
     * Gets integer.
     *
     * @param key      the key
     * @param defValue the def value
     * @return the integer
     */
    public int getInteger(String key, int defValue) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getInt(key, defValue);
    }

    /**
     * Gets longg.
     *
     * @param key      the key
     * @param defValue the def value
     * @return the longg
     */
    public long getLong(String key, long defValue) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getLong(key, defValue);
    }
    /**
     * Gets boolean.
     *
     * @param key      the key
     * @param defValue the def value
     * @return the boolean
     */
    public boolean getBoolean(String key, boolean defValue) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defValue);
    }

    /**
     * Clear all.
     */
    public  void clearAll(Context context) {
        boolean enableRegistration = getBoolean(ENABLE_REGISTRATION, false);
        String deviceToken = getString(DEVICE_TOKEN, "");
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        saveString(DEVICE_TOKEN, deviceToken);
        saveBoolean(ENABLE_REGISTRATION, enableRegistration);
    }
}
