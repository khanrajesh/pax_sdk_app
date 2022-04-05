package com.pax.pax_sdk_app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;



public class PlugInControlReceiver extends BroadcastReceiver {
    //private ConnectionLostCallback listener;
    private Activity mcontext;


    public PlugInControlReceiver() {
    }

    public PlugInControlReceiver(Activity context, ConnectionLostCallback listener) {

       // this.listener = listener;
        this.mcontext = context;

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.v("PlugInControlReceiver", "action: " + action);


        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {

           /* if (intent.getExtras().getBoolean("connected")) {
                Toast.makeText(context, "USB Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "USB Disconnected", Toast.LENGTH_SHORT).show();
              *//*  if (listener != null) {
                    listener.connectionLost();
                }*//*
            }*/
            Toast.makeText(context, "USB Connected", Toast.LENGTH_SHORT).show();
        }
        else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            //listener.connectionLost();
            Toast.makeText(context, "USB Disconnected", Toast.LENGTH_SHORT).show();

        }

    }
}


