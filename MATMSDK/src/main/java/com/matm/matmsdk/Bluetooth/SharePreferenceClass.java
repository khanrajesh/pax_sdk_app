package com.matm.matmsdk.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import com.matm.matmsdk.Utils.SdkConstants;

/**
 * Created by Rajesh on 7/11/2016.
 */
public class SharePreferenceClass {
    private static final String USER_PREFS = "ISERVEU";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public SharePreferenceClass(Context context){
        this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }
    public int getValue_int(String intKeyValue) {
        return appSharedPrefs.getInt(intKeyValue, 0);
    }

    public String getValue_string(String stringKeyValue) {
        return appSharedPrefs.getString(stringKeyValue, "");
    }
    public void setValue_int(String intKeyValue, int _intValue) {

        prefsEditor.putInt(intKeyValue, _intValue).commit();
    }
    public void setValue_string(String stringKeyValue, String _stringValue) {

        prefsEditor.putString(stringKeyValue, _stringValue).commit();

    }

    public void setValue_int(String intKeyValue) {

        prefsEditor.putInt(intKeyValue,0).commit();
    }
    public void clearData(){
        prefsEditor.clear().commit();

    }

    public void setBluetoothInstance(BluetoothDevice bluetoothInstance) {
        SdkConstants.bluetoothDevice = bluetoothInstance;
        String json = bluetoothInstance.getAddress();
        prefsEditor.putString("BluetoothInstance", json);
        prefsEditor.commit();
    }
    public String getBluetoothInstance(){
        String json = appSharedPrefs.getString("BluetoothInstance", null);
        return json;

    }
    public void setConnectedRD_Device(String device_name) {

        prefsEditor.putString("RD_DEVICE", device_name).commit();

    }

    public String getConnectedRD_Device() {
        return appSharedPrefs.getString("RD_DEVICE", "");
    }

    public void setUsbDevice(String usbDevice) {
        prefsEditor.putString (SdkConstants.USB_DEVICE, usbDevice);
        prefsEditor.commit();
    }

    public String getUsbDevice() {
        return appSharedPrefs.getString(SdkConstants.USB_DEVICE, null);
    }

    public void setUsbDeviceSerial(String usbDevice) {
        prefsEditor.putString ("DeviceSerialNumber", usbDevice);
        prefsEditor.commit();
    }

    public String getUsbDeviceSerial() {
        return appSharedPrefs.getString("DeviceSerialNumber", null);
    }

    public void setAdminName(String admin_name) {
        prefsEditor.putString ("ADMIN_NAME", admin_name);
        prefsEditor.commit();
    }

    public String getAdminName() {
        return appSharedPrefs.getString("ADMIN_NAME", "APES/MATM");
    }




}
