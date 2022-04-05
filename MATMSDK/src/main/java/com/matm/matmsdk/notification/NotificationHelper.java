package com.matm.matmsdk.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;

import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import isumatm.androidsdk.equitas.R;

public class NotificationHelper extends ContextWrapper {

    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "com.matm.matmsdk.aepsmodule.transactionstatus";
    public static final String CHANNEL_ONE_NAME = "AEPS Transaction Status";
    Context mContext;
    Class notificationHistoryActivity;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
        this.mContext = base;
        String accessClassName = SdkConstants.NOTIFICATION_ACTIVITY;
        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            notificationHistoryActivity = targetActivity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void createChannels() {

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }

    }

    public NotificationCompat.Builder createNotification(Context context, String title, String body, Class activityClass, String extra) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ONE_ID);
        try {
            Intent ii = new Intent(context, notificationHistoryActivity);
            ii.putExtra("intentData", extra);
            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.drawable.ic_status_transaction);
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setAutoCancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = CHANNEL_ONE_ID;
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_HIGH);
            notifManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        return builder;
    }

    public NotificationCompat.Builder createTransactionStatusNotif(Context context, String title, String body, Class activityClass, TransactionStatusModel extra) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ONE_ID);

        Intent ii = new Intent(context, notificationHistoryActivity);
        ii.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, extra);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_status_transaction);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setAutoCancel(true);

        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = CHANNEL_ONE_ID;
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_HIGH);
            notifManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        return builder;
    }

    public NotificationCompat.Builder createFailNotification(Context context, String title, String body, Class activityClass, String extra) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ONE_ID);

        Intent ii = new Intent(context, notificationHistoryActivity);
        ii.putExtra("intentData", extra);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_status_error);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setAutoCancel(true);

        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = CHANNEL_ONE_ID;
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_HIGH);
            notifManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        return builder;
    }

    public void create(int id, NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }


    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "101");

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, title, message, timeStamp, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_status_transaction)
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SdkConstants.NOTIFICATION_ID, notification);
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_status_transaction)
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SdkConstants.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
