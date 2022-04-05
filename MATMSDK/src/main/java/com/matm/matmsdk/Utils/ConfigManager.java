package com.matm.matmsdk.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ConfigManager {
    public static final int CONN_TIMEOUT_DEFAULT = 37000; //ms
    public static final int READ_TIMEOUT_DEFAULT = 60000;	//ms   //jason
    private static String TAG = "ConfigManager";

    public String commType = "bluetooth";		//ip/bluetooth

    public void saveTagValue(String tag, String value){
        if(null == tag || tag.trim().equals("")){
            Log.i(TAG, "invalid tag");
            return;
        }

        if(null != settings){
            settings = context.getSharedPreferences(CONFIG_FILE_NAME, context.MODE_PRIVATE);
        }

        Editor editor = settings.edit();
        editor.putString(tag, value);
        Log.i(TAG, "save...");
        Log.i(TAG, tag);
        Log.i(TAG, value);
        boolean result = editor.commit();
        Log.i(TAG, "save result " + result);
    }


    public String getValueByTag(String tag, String defVal){
        if(null == tag || tag.trim().equals("")){
            Log.i(TAG, "invalid tag");
            return null;
        }

        if(null != settings){
            settings = context.getSharedPreferences(CONFIG_FILE_NAME, context.MODE_PRIVATE);
        }

        String val = settings.getString(tag, defVal);
        Log.i(TAG, "get value...");
        Log.i(TAG, tag);
        Log.i(TAG, val);
        return val;
    }

//	public String serverAddr = "172.16.20.60";
    public String serverAddr = "211.162.210.217";

//	public int serverPort = 10297;  
    public int serverPort = 33449;

    public String bluetoothMac = "";
    public int receiveTimeout = READ_TIMEOUT_DEFAULT;

    //make connect timeout unconfigurable by user app
    private int connectTimeout = CONN_TIMEOUT_DEFAULT;

    private static final String CONFIG_FILE_NAME = "mposSettings";
    private static ConfigManager configManager;
    private SharedPreferences settings;
    private Context context;

    private ConfigManager(Context context) {
        this.context = context;
        load();
    }

    public static ConfigManager getInstance(Context context) {
        if (configManager == null) {
            configManager = new ConfigManager(context);
        }
        return configManager;
    }

    public void load() {
        settings = context.getSharedPreferences(CONFIG_FILE_NAME, context.MODE_PRIVATE);
        commType = settings.getString("commType", commType);
        serverAddr = settings.getString("serverAddr", serverAddr);
        serverPort = settings.getInt("serverPort", serverPort);
        //connectTimeout = settings.getInt("connectTimeout", connectTimeout);
        receiveTimeout = settings.getInt("receiveTimeout", receiveTimeout);
        bluetoothMac = settings.getString("bluetoothMac", bluetoothMac);
    }

    public void save() {
        Editor editor = settings.edit();
        editor.putString("commType", commType);
        editor.putString("serverAddr", serverAddr);
        editor.putInt("serverPort", serverPort);
        //editor.putInt("connectTimeout", connectTimeout);
        editor.putInt("receiveTimeout", receiveTimeout);
        editor.putString("bluetoothMac", bluetoothMac);
        boolean result = editor.commit();
        Log.i(TAG, "save result " + result);
    }
}

