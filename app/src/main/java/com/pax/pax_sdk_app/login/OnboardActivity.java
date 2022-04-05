package com.pax.pax_sdk_app.login;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardActivity extends AppCompatActivity {
    private static final String TAG = OnboardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        boolean installed = appInstalledOrNot("com.matm.matmservice_1");

        Log.e(TAG, "onCreate: installed "+installed );

        try {
            if (installed) {
                if (LoginConstants.adminName!=null && !LoginConstants.adminName.trim().equals("")){
                    if (LoginConstants.createdBy!=null && !LoginConstants.createdBy.trim().equals("")){
                        PackageManager manager = getPackageManager();
                        Intent sIntent = manager.getLaunchIntentForPackage("com.matm.matmservice_1");
                        sIntent.putExtra("ActivityName", "SelfOnBoard");
                        sIntent.putExtra("AdminName", LoginConstants.adminName);
                        sIntent.putExtra("CreatedBy", LoginConstants.createdBy);
                        sIntent.putExtra("OnboardStatus", LoginConstants.onBoardStatus);
                        sIntent.putExtra("Package", getPackageName());
                        Log.e(TAG, "onClick: intent data " + sIntent.getExtras());
                        sIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(sIntent);
                        finish();
                    } else {
                        showDownloadAlert();
                    }
                } else {
                    showDownloadAlert();
                }

            } else {
                showDownloadAlert(OnboardActivity.this);
            }
        } catch (Exception e) {
        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public void showDownloadAlert(Context context) {
        try {

            AlertDialog.Builder alertbuilderupdate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertbuilderupdate = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alertbuilderupdate = new AlertDialog.Builder(context);
            }
            alertbuilderupdate.setCancelable(false);
            String message = "Please download the MATM Service 1 app from the playstore.";
            alertbuilderupdate.setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton("Download Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            redirectToPlayStore();
                            finish();
                        }
                    })
                    .setNegativeButton("Not Now", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
//                    .show();
            AlertDialog alert11 = alertbuilderupdate.create();
            alert11.show();

        } catch (Exception e) {

        }
    }

    public void redirectToPlayStore() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.matm.matmservice_1&hl=en_US");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.matm.matmservice_1&hl=en_US")));
        }
    }

    public void showDownloadAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(OnboardActivity.this);
            builder.setTitle("Alert!!");
            builder.setMessage("Admin name not found.\nPlease restart the app or contact your admin.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
