package com.matm.matmsdk.notification.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.matm.matmsdk.Utils.SdkConstants;

public class SubscribeGlobal {
    BroadcastReceiver mRegistrationBroadcastReceiver;
    Context context;

    public SubscribeGlobal(Context context) {
        this.context = context;
    }

    public void subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic(SdkConstants.DEVICE_TOPIC);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context mContext, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(SdkConstants.COMPLETE_REGISTRATION)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications

                } else if (intent.getAction().equals(SdkConstants.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Toast.makeText(context, "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    public void Unsubscribe() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(SdkConstants.DEVICE_TOPIC);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context mContext, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(SdkConstants.COMPLETE_REGISTRATION)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications

                } else if (intent.getAction().equals(SdkConstants.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Toast.makeText(context, "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public void registerBroadcast() {
// register GCM registration complete receiver
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(SdkConstants.COMPLETE_REGISTRATION));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(SdkConstants.PUSH_NOTIFICATION));

    }
}
