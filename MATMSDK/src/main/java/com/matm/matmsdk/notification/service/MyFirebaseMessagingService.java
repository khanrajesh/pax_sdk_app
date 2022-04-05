package com.matm.matmsdk.notification.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.matm.matmsdk.Dashboard.MainActivity;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.aadharpay.Model.FCMNotificationData;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.notification.NotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

//import com.dashboard.app.notification.util.NotificationHelper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    FCMNotificationData FCMNotificationData;
    private NotificationHelper notificationHelper;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            try {
                Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                notificationHelper = new NotificationHelper(getApplication());
                notificationHelper.create(0, notificationHelper.createNotification(getApplicationContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), MainActivity.class, ""));

                handleNotification(remoteMessage.getNotification().getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        try {
            if (!new ForegroundCheckTask().execute(getApplication()).get()) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(SdkConstants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationHelper notificationUtils = new NotificationHelper(getApplicationContext());
                notificationUtils.playNotificationSound();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleDataMessage(JSONObject json) throws JSONException {

        if (json.toString() != null || !json.toString().equals("")) {
            String msg = json.toString();
            Log.e(TAG, "FCMMessage: " + msg);
            Constants.NOTIFICATION_RECEIVED = true;

            FCMNotificationData = new FCMNotificationData(
                    json.getString("status"),
                    json.getString("statusDesc"),
                    json.getString("transactionMode"),
                    json.getString("txId"),
                    json.getString("apiTid"),
                    json.getString("origin_identifier"),
                    json.getString("bankName"),
                    json.getString("updatedDate"),
                    json.getString("balance"),
                    "N/A"
            );

            Gson gson = new Gson();
            Constants.THE_FIREBASE_DATA = gson.toJson(FCMNotificationData);

            Log.e(TAG, "handleDataMessage: " + Constants.THE_FIREBASE_DATA);

        }

//        Log.e(TAG, "push json: " + json.toString());

        //   String FirebaseData = json.toString();

//        System.out.println("From FCM:::" + json.toString());

//        Constants.THE_FIREBASE_DATA = json.toString();

//        System.out.println("The Constants:::" + Constants.THE_FIREBASE_DATA);

        /*{
	"bankName": "BANK OF BARODA",
	"apiComment": "failed",
	"apiTid": "",
	"status": "FAILED",
	"updatedDate": "2020-12-28 07:22:03.750",
	"createdDate": "2020-12-28 07:22:02.687",
	"balance": "NA",
	"bcId": "43751",
	"txId": "793015894149177344",
	"statusDesc": "failed",
	"origin_identifier": "xxxx-xxxx-6272",
	"message": "502 Bad Gateway: [<html>\r\n<head><title>502 Bad Gateway<\/title><\/head>\r\n<body>\r\n<center><h1>502 Bad Gateway<\/h1><\/center>\r\n<hr><center>nginx\/1.16.1<\/center>\r\n<\/body>\r\n<\/html>\r\n]",
	"transactionMode": "AEPS_BALANCE_ENQUIRY",
	"npci_tran_data": ""
}*/

        try {

            String title = json.getString("title");
            String message = json.getString("message");
            String imageUrl = json.getString("image");
            String timestamp = json.getString("timestamp");
            String payload = json.getString("payload");
            String txId = json.getString("txId");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "payload: " + payload);
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            notificationHelper = new NotificationHelper(getApplication());
            notificationHelper.create(0, notificationHelper.createNotification(getApplicationContext(), title, message, MainActivity.class, ""));


            if (!new ForegroundCheckTask().execute(getApplication()).get()) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(SdkConstants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationHelper notificationUtils = new NotificationHelper(getApplicationContext());
                notificationUtils.playNotificationSound();


            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationHelper = new NotificationHelper(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationHelper.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationHelper = new NotificationHelper(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationHelper.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


}
