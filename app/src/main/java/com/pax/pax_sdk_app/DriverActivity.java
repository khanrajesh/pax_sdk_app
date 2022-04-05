package com.pax.pax_sdk_app;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


import com.matm.matmsdk.Utils.SdkConstants;


import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import fingerprintmodel.DeviceInfo;
import fingerprintmodel.EvoluteDeviceInfo;
import fingerprintmodel.EvolutePidData;
import fingerprintmodel.FM220DeviceInfo;
import fingerprintmodel.FM220PidData;
import fingerprintmodel.MorphoDeviceInfo;
import fingerprintmodel.MorphoPidData;
import fingerprintmodel.Opts;
import fingerprintmodel.PidData;
import fingerprintmodel.PidOptions;
import fingerprintmodel.PrecisionDeviceInfo;
import fingerprintmodel.PrecisionPidData;
import fingerprintmodel.uid.AuthReq;
import fingerprintmodel.uid.AuthRes;
import fingerprintmodel.uid.Meta;
import fingerprintmodel.uid.Uses;
import signer.XMLSigner;

import static com.matm.matmsdk.Utils.SdkConstants.Bluetoothname;
import static com.matm.matmsdk.Utils.SdkConstants.RECEIVE_DRIVER_DATA;
import static com.matm.matmsdk.Utils.SdkConstants.bluetoothConnector;


public class DriverActivity extends AppCompatActivity implements ConnectionLostCallback {

    String driverFlag = "";
    private PidData pidData;
    private MorphoPidData morphoPidData;
    private FM220PidData fm220PidData;
    private EvolutePidData evolutePidData;
    private PrecisionPidData precisionPidData;

    boolean usbconnted = false;
    String deviceSerialNumber = "0";
    String mantradeviceid = "MANTRA";
    String morphodeviceid = "SAGEM SA";
    String morphoe2device = "Morpho";
    String precisiondeviceid = "Mvsilicon";
    String fmDeviceId = "Startek Eng-Inc.";
    String fmDeviceId2 = "Startek Eng-Inc.\u0000";
    String fmDeviceId3 = "Startek Eng. Inc.";
    String fmDeviceId4 = "Startek";
    String TATVIK = "TATVIK";
    String evolutedeviceid = "FREESCALE SEMICONDUCTOR INC.";

    UsbManager musbManager;
    private UsbDevice usbDevice;

    private Serializer serializer;
    private ArrayList<String> positions;
    String freshnessFactor="";
    String aadharNo = "";


    public static final int MANTRA_CAPTURE_REQUEST_CODE = 2;
    public static final int MORPHO_CAPTURE_REQUEST_CODE = 3;
    public static final int STATEK_CAPTURE_REQUEST_CODE = 4;
    public static final int PRICISION_CAPTURE_REQUEST_CODE = 5;
    public static final int TATVIK_CAPTURE_REQUEST_CODE = 7;
    public static final int EVOLUTE_CAPTURE_REQUEST_CODE = 6;

    PlugInControlReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_driver);
        serializer = new Persister();
        SdkConstants.RECEIVE_DRIVER_DATA = "";
        usbReceiver = new PlugInControlReceiver(DriverActivity.this, DriverActivity.this);


        driverFlag = getIntent().getStringExtra("driverFlag");
        freshnessFactor = getIntent().getStringExtra("freshnesFactor");
        aadharNo = getIntent().getStringExtra("AadharNo");


        positions = new ArrayList<>();
        positions = new ArrayList<>();
        musbManager = (UsbManager) getSystemService(Context.USB_SERVICE);


        if (bluetoothConnector) {
            if (Bluetoothname.equalsIgnoreCase("EVOLUTE")) {
                boolean appInstall = appInstalledOrNot("com.evolute.rdservice");
                if (appInstall) {
                    evoluteCapture();
                    //Toast.makeText(this, "App Installed", Toast.LENGTH_SHORT).show();
                } else {
                    evoluteMessage();
//                Toast.makeText(this, "App Not Installed", Toast.LENGTH_SHORT).show();
                }
            } else if (Bluetoothname.equalsIgnoreCase("BLUPRINT")) {
                boolean appInstall = appInstalledOrNot("com.nextbiometrics.onetouchrdservice");
                if (appInstall) {
                    bluprintCapture();
                    //Toast.makeText(this, "App Installed", Toast.LENGTH_SHORT).show();
                } else {
                    bluprintMessage();
//                Toast.makeText(this, "App Not Installed", Toast.LENGTH_SHORT).show();
                }
            }
            //  evoluteCapture();
        } else {
            callRdService();

        }


    }

    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(usbReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (usbReceiver != null) {
            unregisterReceiver(usbReceiver);
        }
    }


    public void callRdService() {
        updateDeviceList();
        if (usbDevice != null) {
            if (usbDevice.getManufacturerName().trim().equalsIgnoreCase(mantradeviceid)) {
                mantraCapture();
            } else if (usbDevice.getManufacturerName().trim().equalsIgnoreCase(morphodeviceid) ||
                    usbDevice.getManufacturerName().trim().equalsIgnoreCase(morphoe2device)) {
                morophoCapture();
            } else if (usbDevice.getManufacturerName().trim().equalsIgnoreCase(precisiondeviceid)) {
                precisionCapture();
            } else if (usbDevice.getManufacturerName().trim().equalsIgnoreCase(TATVIK)) {
                tatvikCapture();
            } else if (usbDevice.getManufacturerName().trim().contains(fmDeviceId4) || usbDevice.getManufacturerName().trim().equalsIgnoreCase(fmDeviceId2) ||
                    usbDevice.getManufacturerName().trim().equalsIgnoreCase(fmDeviceId) || usbDevice.getManufacturerName().trim().equalsIgnoreCase(fmDeviceId3)) {
                FM220Capture();
            }
        } else {


            musbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            updateDeviceList();


        }

    }

    private void updateDeviceList() {
        HashMap<String, UsbDevice> connectedDevices = musbManager.getDeviceList();
        usbDevice = null;
        if (connectedDevices.isEmpty()) {
            usbconnted = false;
            deviceConnectMessgae();
        } else {
            for (UsbDevice device : connectedDevices.values()) {
                usbconnted = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (device != null && device.getManufacturerName() != null) {
                        usbDevice = device;
                        deviceSerialNumber = usbDevice.getManufacturerName();
                    }

                }
            }
            biometricDeviceCheck();
        }
    }

    private void biometricDeviceCheck() {
        if (usbDevice != null) {
            if (driverFlag.equalsIgnoreCase(mantradeviceid)) {
                mantraInstallCheck();

            } else if (driverFlag.equalsIgnoreCase(morphodeviceid) || driverFlag.equalsIgnoreCase(morphoe2device)) {
                morphoinstallcheck();
            } else if (driverFlag.equalsIgnoreCase(precisiondeviceid)) {
                precisioninstallcheck();
            } else if (driverFlag.equalsIgnoreCase(fmDeviceId) || driverFlag.contains(fmDeviceId4) || driverFlag.equalsIgnoreCase(fmDeviceId2) || driverFlag.equalsIgnoreCase(fmDeviceId3)) {
                startekinstallcheck();
            } else if (driverFlag.equalsIgnoreCase(TATVIK)) {
                precisioninstallcheck();
            } else {
                deviceConnectMessgae();
            }
        }
    }

    private void mantraInstallCheck() {
        boolean isAppInstalled = appInstalledOrNot("com.mantra.clientmanagement");
        boolean serviceAppInstalled = appInstalledOrNot("com.mantra.rdservice");
        if (isAppInstalled) {
            if (serviceAppInstalled) {
                Intent intent = new Intent("in.gov.uidai.rdservice.fp.INFO");
                intent.setPackage("com.mantra.rdservice");
                startActivityForResult(intent, 1);
            } else {
                mantraRDserviceMessage();

            }
        } else {
            mantraMessage();
        }
    }


    private void morphoinstallcheck() {
        boolean isAppInstalled = appInstalledOrNot("com.scl.rdservice");
        if (isAppInstalled) {
            Intent intent1 = new Intent();
            intent1.setAction("in.gov.uidai.rdservice.fp.INFO");
            intent1.setPackage("com.scl.rdservice");
            startActivityForResult(intent1, 1);
        } else {
            morphoMessage();
        }
    }

    private void startekinstallcheck() {
        boolean isAppInstalled = appInstalledOrNot("com.acpl.registersdk");
        if (isAppInstalled) {
            Intent intent1 = new Intent();
            intent1.setAction("in.gov.uidai.rdservice.fp.INFO");
            intent1.setPackage("com.acpl.registersdk");
            startActivityForResult(intent1, 1);
        } else {
            startekMessage();
        }
    }

    private void precisioninstallcheck() {
        boolean isAppInstalled = appInstalledOrNot("com.precision.pb510.rdservice");
        if (isAppInstalled) {
            Intent intent1 = new Intent();
            intent1.setAction("in.gov.uidai.rdservice.fp.INFO");
            intent1.setPackage("com.precision.pb510.rdservice");
            startActivityForResult(intent1, 1);
        } else {
            precisionMessage();
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

    private void mantraRDserviceMessage() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(isumatm.androidsdk.equitas.R.string.mantra_install))
                .setMessage(getResources().getString(isumatm.androidsdk.equitas.R.string.mantra_rd_service))
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.mantra.rdservice"; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .show();
    }

    private void mantraMessage() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(isumatm.androidsdk.equitas.R.string.mantra_client_management_install))
                .setMessage(getResources().getString(isumatm.androidsdk.equitas.R.string.mantra))
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.mantra.clientmanagement"; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .show();
    }


    private void deviceConnectMessgae() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(isumatm.androidsdk.equitas.R.string.device_connect))
                .setMessage(getResources().getString(isumatm.androidsdk.equitas.R.string.setting_device))
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.show();



    }


    private void mantraCapture() {
        try {
            String pidOption = getPIDOptions();
            if (pidOption != null) {
                Intent intent2 = new Intent();
                intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                intent2.setPackage("com.mantra.rdservice");
                intent2.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent2, MANTRA_CAPTURE_REQUEST_CODE);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void morophoCapture() {
        try {
            String pidOption = getPIDOptions();
            if (pidOption != null) {
                Intent intent = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.setPackage("com.scl.rdservice");
                intent.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent, MORPHO_CAPTURE_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    private void precisionCapture() {
        try {
            String pidOption = getPIDOptions();
            if (pidOption != null) {
                Intent intent = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.setPackage("com.precision.pb510.rdservice");
                intent.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent, PRICISION_CAPTURE_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    private void tatvikCapture() {
        try {
            String pidOption = getPIDOptions();
            if (pidOption != null) {
                Intent intent = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.setPackage("com.tatvik.bio.tmf20");
                intent.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent, TATVIK_CAPTURE_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    private void FM220Capture() {
        try {
            String pidOption = getPIDOptions();
            if (pidOption != null) {
                Intent intent = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.setPackage("com.acpl.registersdk");
                intent.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent, STATEK_CAPTURE_REQUEST_CODE);
            }
        } catch (Exception e) {
        }
    }

    private String getPIDOptions() {
        try {
            //String posh = getResources ().getString ( isumatm.androidsdk.equitas.R.string.posh );
            String posh = "UNKNOWN";
            if (positions.size() > 0) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "0";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = "0";
            opts.pidVer = "2.0";
            opts.timeout = "10000";
            opts.posh = posh;
            opts.env = "P";

            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;

            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("DEVICE_INFO");
                            String rdService = data.getStringExtra("RD_SERVICE_INFO");
                            String display = "";
                            if (rdService != null) {
                                display = "RD Service Info :\n" + rdService + "\n\n";
                            }
                            if (result != null) {
                            }
                        }
                    } catch (Exception e) {
                        showAlertMessage();

                    }

                }
                break;
            case MANTRA_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            byte[] dataPid = result.getBytes("UTF-8");
                            String base64pidData = Base64.encodeToString(dataPid, Base64.DEFAULT);

                            if (result != null) {
                                pidData = serializer.read(PidData.class, result);
                                if (pidData._Data != null) {
                                    new AuthRequest(aadharNo, pidData, base64pidData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                } else {
                                    showAlertMessage();
                                }


                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        showAlertMessage();
                    }
                }
                break;
            case MORPHO_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            byte[] dataPid = result.getBytes("UTF-8");
                            String base64pidData = Base64.encodeToString(dataPid, Base64.DEFAULT);
                            if (result != null) {
                                morphoPidData = serializer.read(MorphoPidData.class, result);
                                if (morphoPidData != null) {
                                    new AuthRequestMorpho(aadharNo, morphoPidData, base64pidData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    showAlertMessage();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        showAlertMessage();
                    }
                }
                break;

            case PRICISION_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            byte[] dataPid = result.getBytes("UTF-8");
                            String base64pidData = Base64.encodeToString(dataPid, Base64.DEFAULT);
                            if (result != null) {
                                precisionPidData = serializer.read(PrecisionPidData.class, result);
                                new AuthRequestPrecision(aadharNo, precisionPidData, base64pidData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                showAlertMessage();
                            }

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        showAlertMessage();
                    }
                }
                break;
            case TATVIK_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            byte[] dataPid = result.getBytes("UTF-8");
                            String base64pidData = Base64.encodeToString(dataPid, Base64.DEFAULT);
                            if (result != null) {
                                precisionPidData = serializer.read(PrecisionPidData.class, result);
                                new AuthRequestTatvik(aadharNo, precisionPidData, base64pidData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                showAlertMessage();
                            }

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        showAlertMessage();
                    }
                }
                break;

            case STATEK_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            byte[] dataPid = result.getBytes("UTF-8");
                            String base64pidData = Base64.encodeToString(dataPid, Base64.DEFAULT);
                            if (result != null) {
                                fm220PidData = serializer.read(FM220PidData.class, result);

                                if (fm220PidData._Data != null) {
                                    new AuthRequestStartek(aadharNo, fm220PidData, base64pidData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                } else {
                                    showAlertMessage();
                                }

                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        showAlertMessage();
                    }
                }
                break;


            case EVOLUTE_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            byte[] dataPid = result.getBytes("UTF-8");
                            String base64pidData = Base64.encodeToString(dataPid, Base64.DEFAULT);
                            if (result != null) {
                                evolutePidData = serializer.read(EvolutePidData.class, result);

                                if (evolutePidData._Data != null) {
                                    new AuthRequestEvolute(aadharNo, evolutePidData, base64pidData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                } else {
                                    showAlertMessage();
                                }

                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        showAlertMessage();
                    }
                }
                break;
        }
    }

    @Override
    public void connectionLost() {
        //unregisterReceiver(usbReceiver);
        // showAlertMessage();
        // onBackPressed();
        System.out.println("Hello");
    }

    public void showAlertMessage() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
            builder.setTitle("");
            builder.setMessage("Please connect your device  properly. ");
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

    private class AuthRequest extends AsyncTask<Void, Void, String> {

        private String uid;
        private PidData pidData;
        private ProgressDialog dialog;
        private int posFingerFormat = 0;
        String base64pidData;
        Meta meta;
        AuthReq authReq;
        DeviceInfo info;

        private AuthRequest(String uid, PidData pidData, String base64pidData) {
            this.uid = uid;
            this.pidData = pidData;
            this.base64pidData = base64pidData;
            dialog = new ProgressDialog(DriverActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                info = pidData._DeviceInfo;

                Uses uses = new Uses();
                uses.pi = "n";
                uses.pa = "n";
                uses.pfa = "n";
                uses.bio = "y";
                if (posFingerFormat == 1) {
                    uses.bt = "FIR";
                } else {
                    uses.bt = "FMR";
                }
                uses.pin = "n";
                uses.otp = "n";
                meta = new Meta();
                meta.udc = "MANT0";
                meta.rdsId = info.rdsId;
                meta.rdsVer = info.rdsVer;
                meta.dpId = info.dpId;
                meta.dc = info.dc;
                meta.mi = info.mi;
                meta.mc = info.mc;

                authReq = new AuthReq();
                authReq.uid = uid;
                authReq.rc = "Y";
                authReq.tid = "registered";
                authReq.ac = "public";
                authReq.sa = "public";
                authReq.ver = "2.0";
                authReq.txn = generateTXN();
                authReq.lk = "MEaMX8fkRa6PqsqK6wGMrEXcXFl_oXHA-YuknI2uf0gKgZ80HaZgG3A"; //AUA
                authReq.skey = pidData._Skey;
                authReq.Hmac = pidData._Hmac;
                authReq.data = pidData._Data;
                authReq.meta = meta;
                authReq.uses = uses;
                authReq.freshnessFactor = freshnessFactor;

                StringWriter writer = new StringWriter();
                serializer.write(authReq, writer);
                String pass = "public";
                String reqXML = writer.toString();
                String signAuthXML = XMLSigner.generateSignXML(reqXML, getAssets().open("staging_signature_privateKey.p12"), pass);
                URL url = new URL(getAuthURL(uid));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                OutputStreamWriter writer2 = new OutputStreamWriter(conn.getOutputStream());
                writer2.write(signAuthXML);
                writer2.flush();
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response;
                while ((response = reader.readLine()) != null) {
                    sb.append(response).append("\n");
                }
                response = sb.toString();

                AuthRes authRes = serializer.read(AuthRes.class, response);
                String res;
                if (authRes.err != null) {
                    if (authRes.err.equals("0")) {
                        res = "Authentication Success" + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    } else {
                        res = "Error Code: " + authRes.err + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    }
                } else {
                    res = "Authentication Success" + "\n"
                            + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                            + "TXN: " + authRes.txn + "\n"
                            + "";
                }
                return res;
            } catch (Exception e) {
                return "Error: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try{
                if (res != null && authReq != null && meta != null && info != null) {


                    String vid = null;
                    String uid = null;
                    String value = authReq.skey.value.toString();
                    String last = String.valueOf(value.charAt(value.length() - 1));
                    if (last.equalsIgnoreCase("\n")) {
                        value = value.replace("\n", "");
                    }


                    SdkConstants.RECEIVE_DRIVER_DATA = "";
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("CI", authReq.skey.ci);
                        obj.put("DC", meta.dc);

                        obj.put("DPID", meta.dpId);
                        obj.put("DATAVALUE", authReq.data.value);
                        obj.put("HMAC", authReq.Hmac);
                        obj.put("MC", meta.mc);
                        obj.put("MI", meta.mi);

                        obj.put("RDSID", meta.rdsId);
                        obj.put("RDSVER", meta.rdsVer);
                        obj.put("value", value);
                        obj.put("pidata_qscore", pidData._Resp.qScore);
                        obj.put("base64pidData", base64pidData);
                        SdkConstants.RECEIVE_DRIVER_DATA = obj.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //---------------Please scan your Finger---------------
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class AuthRequestMorpho extends AsyncTask<Void, Void, String> {

        private String uid;
        private MorphoPidData morphoPidData;
        private ProgressDialog dialog;
        private int posFingerFormat = 0;
        String base64pidData;
        Meta meta;
        AuthReq authReq;
        MorphoDeviceInfo morphoDeviceInfo;

        private AuthRequestMorpho(String uid, MorphoPidData morphoPidData, String base64pidData) {
            this.uid = uid;
            this.morphoPidData = morphoPidData;
            this.base64pidData = base64pidData;
            dialog = new ProgressDialog(DriverActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                morphoDeviceInfo = morphoPidData._DeviceInfo;

                Uses uses = new Uses();
                uses.pi = "n";
                uses.pa = "n";
                uses.pfa = "n";
                uses.bio = "y";
                if (posFingerFormat == 1) {
                    uses.bt = "FIR";
                } else {
                    uses.bt = "FMR";
                }
                uses.pin = "n";
                uses.otp = "n";

                meta = new Meta();
                meta.udc = "MANT0";
                meta.rdsId = morphoDeviceInfo.rdsId;
                meta.rdsVer = morphoDeviceInfo.rdsVer;
                meta.dpId = morphoDeviceInfo.dpId;
                meta.dc = morphoDeviceInfo.dc;
                meta.mi = morphoDeviceInfo.mi;
                meta.mc = morphoDeviceInfo.mc;

                authReq = new AuthReq();
                authReq.uid = uid;
                authReq.rc = "Y";
                authReq.tid = "registered";
                authReq.ac = "public";
                authReq.sa = "public";
                authReq.ver = "2.0";
                authReq.txn = generateTXN();
                authReq.lk = "MEaMX8fkRa6PqsqK6wGMrEXcXFl_oXHA-YuknI2uf0gKgZ80HaZgG3A"; //AUA
                authReq.skey = morphoPidData._Skey;
                authReq.Hmac = morphoPidData._Hmac;
                authReq.data = morphoPidData._Data;
                authReq.meta = meta;
                authReq.uses = uses;
                authReq.freshnessFactor = freshnessFactor;

                StringWriter writer = new StringWriter();
                serializer.write(authReq, writer);
                String pass = "public";
                String reqXML = writer.toString();
                String signAuthXML = com.matm.matmsdk.aepsmodule.signer.XMLSigner.generateSignXML(reqXML, getAssets().open("staging_signature_privateKey.p12"), pass);
                URL url = new URL(getAuthURL(uid));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                OutputStreamWriter writer2 = new OutputStreamWriter(conn.getOutputStream());
                writer2.write(signAuthXML);
                writer2.flush();
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response;
                while ((response = reader.readLine()) != null) {
                    sb.append(response).append("\n");
                }
                response = sb.toString();

                AuthRes authRes = serializer.read(AuthRes.class, response);
                String res;
                if (authRes.err != null) {
                    if (authRes.err.equals("0")) {
                        res = "Authentication Success" + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    } else {
                        res = "Error Code: " + authRes.err + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    }
                } else {
                    res = "Authentication Success" + "\n"
                            + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                            + "TXN: " + authRes.txn + "\n"
                            + "";
                }
                return res;
            } catch (Exception e) {
                return "Error: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try{
                if (res != null && authReq != null && meta != null && morphoDeviceInfo != null) {


                    String vid = null;
                    String uid = null;
                    String value = authReq.skey.value.toString();
                    String last = String.valueOf(value.charAt(value.length() - 1));
                    if (last.equalsIgnoreCase("\n")) {
                        value = value.replace("\n", "");
                    }
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("CI", authReq.skey.ci);
                        obj.put("DC", meta.dc);

                        obj.put("DPID", meta.dpId);
                        obj.put("DATAVALUE", authReq.data.value);
                        obj.put("HMAC", authReq.Hmac);
                        obj.put("MC", meta.mc);
                        obj.put("MI", meta.mi);

                        obj.put("RDSID", meta.rdsId);
                        obj.put("RDSVER", meta.rdsVer);
                        obj.put("value", value);
                        obj.put("base64pidData", base64pidData);
                        if ((Integer.parseInt(morphoPidData._Resp.qScore)) >= 10 && (Integer.parseInt(morphoPidData._Resp.qScore) <= 40)) {
                            int score = (Integer.parseInt(morphoPidData._Resp.qScore)) + 35;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else if ((Integer.parseInt(morphoPidData._Resp.qScore)) > 40 && (Integer.parseInt(morphoPidData._Resp.qScore) <= 50)) {
                            int score = (Integer.parseInt(morphoPidData._Resp.qScore)) * 2;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else {
                            obj.put("pidata_qscore", morphoPidData._Resp.qScore);
                        }

                        SdkConstants.RECEIVE_DRIVER_DATA = obj.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    //---------------Please scan your Finger---------------
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class AuthRequestPrecision extends AsyncTask<Void, Void, String> {

        private String uid;
        private PrecisionPidData precisionPidData;
        private ProgressDialog dialog;
        private int posFingerFormat = 0;
        Meta meta;
        String base64pidData;
        AuthReq authReq;
        PrecisionDeviceInfo precisionDeviceInfo;

        private AuthRequestPrecision(String uid, PrecisionPidData precisionPidData, String base64pidData) {
            this.uid = uid;
            this.precisionPidData = precisionPidData;
            this.base64pidData = base64pidData;
            dialog = new ProgressDialog(DriverActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                precisionDeviceInfo = precisionPidData._DeviceInfo;

                Uses uses = new Uses();
                uses.pi = "n";
                uses.pa = "n";
                uses.pfa = "n";
                uses.bio = "y";
                if (posFingerFormat == 1) {
                    uses.bt = "FIR";
                } else {
                    uses.bt = "FMR";
                }
                uses.pin = "n";
                uses.otp = "n";
                meta = new Meta();
                meta.udc = "MANT0";
                meta.rdsId = precisionDeviceInfo.rdsld;
                meta.rdsVer = precisionDeviceInfo.rdsVer;
                meta.dpId = precisionDeviceInfo.dpld;
                meta.dc = precisionDeviceInfo.dc;
                meta.mi = precisionDeviceInfo.mi;
                meta.mc = precisionDeviceInfo.mc;

                authReq = new AuthReq();
                authReq.uid = uid;
                authReq.rc = "Y";
                authReq.tid = "registered";
                authReq.ac = "public";
                authReq.sa = "public";
                authReq.ver = "2.0";
                authReq.txn = generateTXN();
                authReq.lk = "MEaMX8fkRa6PqsqK6wGMrEXcXFl_oXHA-YuknI2uf0gKgZ80HaZgG3A"; //AUA
                authReq.skey = precisionPidData._Skey;
                authReq.Hmac = precisionPidData.Hmac;
                authReq.data = precisionPidData._Data;
                authReq.meta = meta;
                authReq.uses = uses;
                authReq.freshnessFactor = freshnessFactor;

                StringWriter writer = new StringWriter();
                serializer.write(authReq, writer);
                String pass = "public";
                String reqXML = writer.toString();
                String signAuthXML = XMLSigner.generateSignXML(reqXML, getAssets().open("staging_signature_privateKey.p12"), pass);
                URL url = new URL(getAuthURL(uid));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                OutputStreamWriter writer2 = new OutputStreamWriter(conn.getOutputStream());
                writer2.write(signAuthXML);
                writer2.flush();
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response;
                while ((response = reader.readLine()) != null) {
                    sb.append(response).append("\n");
                }
                response = sb.toString();

                AuthRes authRes = serializer.read(AuthRes.class, response);
                String res;
                if (authRes.err != null) {
                    if (authRes.err.equals("0")) {
                        res = "Authentication Success" + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    } else {
                        res = "Error Code: " + authRes.err + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    }
                } else {
                    res = "Authentication Success" + "\n"
                            + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                            + "TXN: " + authRes.txn + "\n"
                            + "";
                }
                return res;
            } catch (Exception e) {
                return "Error: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try{
                if (res != null && authReq != null && meta != null && precisionDeviceInfo != null) {


                    String vid = null;
                    String uid = null;
                    String value = authReq.skey.value.toString();
                    String last = String.valueOf(value.charAt(value.length() - 1));
                    if (last.equalsIgnoreCase("\n")) {
                        value = value.replace("\n", "");
                    }
                    //Intent intent = new Intent();
                    //startActivityForResult(intent, 1);

                    SdkConstants.RECEIVE_DRIVER_DATA = "";
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("CI", authReq.skey.ci);
                        obj.put("DC", meta.dc);

                        obj.put("DPID", meta.dpId);
                        obj.put("DATAVALUE", authReq.data.value);
                        obj.put("HMAC", authReq.Hmac);
                        obj.put("MC", meta.mc);
                        obj.put("MI", meta.mi);

                        obj.put("RDSID", meta.rdsId);
                        obj.put("RDSVER", meta.rdsVer);
                        obj.put("value", value);
                        obj.put("pidata_qscore", precisionPidData._Resp.qScore);
                        obj.put("base64pidData", base64pidData);
                        SdkConstants.RECEIVE_DRIVER_DATA = obj.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //---------------Please scan your Finger---------------
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class AuthRequestTatvik extends AsyncTask<Void, Void, String> {

        private String uid;
        private PrecisionPidData precisionPidData;
        private ProgressDialog dialog;
        private int posFingerFormat = 0;
        String base64pidData;
        Meta meta;
        AuthReq authReq;
        PrecisionDeviceInfo precisionDeviceInfo;

        private AuthRequestTatvik(String uid, PrecisionPidData precisionPidData, String base64pidData) {
            this.uid = uid;
            this.precisionPidData = precisionPidData;
            this.base64pidData = base64pidData;
            dialog = new ProgressDialog(DriverActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                precisionDeviceInfo = precisionPidData._DeviceInfo;

                Uses uses = new Uses();
                uses.pi = "n";
                uses.pa = "n";
                uses.pfa = "n";
                uses.bio = "y";
                if (posFingerFormat == 1) {
                    uses.bt = "FIR";
                } else {
                    uses.bt = "FMR";
                }
                uses.pin = "n";
                uses.otp = "n";
                meta = new Meta();
                meta.udc = "MANT0";
                meta.rdsId = precisionDeviceInfo.rdsld;
                meta.rdsVer = precisionDeviceInfo.rdsVer;
                meta.dpId = precisionDeviceInfo.dpld;
                meta.dc = precisionDeviceInfo.dc;
                meta.mi = precisionDeviceInfo.mi;
                meta.mc = precisionDeviceInfo.mc;

                authReq = new AuthReq();
                authReq.uid = uid;
                authReq.rc = "Y";
                authReq.tid = "registered";
                authReq.ac = "public";
                authReq.sa = "public";
                authReq.ver = "2.0";
                authReq.txn = generateTXN();
                authReq.lk = "MEaMX8fkRa6PqsqK6wGMrEXcXFl_oXHA-YuknI2uf0gKgZ80HaZgG3A"; //AUA
                authReq.skey = precisionPidData._Skey;
                authReq.Hmac = precisionPidData.Hmac;
                authReq.data = precisionPidData._Data;
                authReq.meta = meta;
                authReq.uses = uses;
                authReq.freshnessFactor = freshnessFactor;

                StringWriter writer = new StringWriter();
                serializer.write(authReq, writer);
                String pass = "public";
                String reqXML = writer.toString();
                String signAuthXML = XMLSigner.generateSignXML(reqXML, getAssets().open("staging_signature_privateKey.p12"), pass);
                URL url = new URL(getAuthURL(uid));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                OutputStreamWriter writer2 = new OutputStreamWriter(conn.getOutputStream());
                writer2.write(signAuthXML);
                writer2.flush();
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response;
                while ((response = reader.readLine()) != null) {
                    sb.append(response).append("\n");
                }
                response = sb.toString();

                AuthRes authRes = serializer.read(AuthRes.class, response);
                String res;
                if (authRes.err != null) {
                    if (authRes.err.equals("0")) {
                        res = "Authentication Success" + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    } else {
                        res = "Error Code: " + authRes.err + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    }
                } else {
                    res = "Authentication Success" + "\n"
                            + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                            + "TXN: " + authRes.txn + "\n"
                            + "";
                }
                return res;
            } catch (Exception e) {
                return "Error: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try{
                if (res != null && authReq != null && meta != null && precisionDeviceInfo != null) {


                    String vid = null;
                    String uid = null;
                    String value = authReq.skey.value.toString();
                    String last = String.valueOf(value.charAt(value.length() - 1));
                    if (last.equalsIgnoreCase("\n")) {
                        value = value.replace("\n", "");
                    }
                    //Intent intent = new Intent();
                    //startActivityForResult(intent, 1);

                    SdkConstants.RECEIVE_DRIVER_DATA = "";
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("CI", authReq.skey.ci);
                        obj.put("DC", meta.dc);

                        obj.put("DPID", meta.dpId);
                        obj.put("DATAVALUE", authReq.data.value);
                        obj.put("HMAC", authReq.Hmac);
                        obj.put("MC", meta.mc);
                        obj.put("MI", meta.mi);

                        obj.put("RDSID", meta.rdsId);
                        obj.put("RDSVER", meta.rdsVer);
                        obj.put("value", value);
                        obj.put("base64pidData", base64pidData);
                        obj.put("pidata_qscore", precisionPidData._Resp.nmPoints);

                        if ((Integer.parseInt(precisionPidData._Resp.nmPoints)) >= 1 && (Integer.parseInt(precisionPidData._Resp.nmPoints) <= 10)) {
                            int score = (Integer.parseInt(precisionPidData._Resp.nmPoints)) + 60;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else if ((Integer.parseInt(precisionPidData._Resp.nmPoints)) >= 11 && (Integer.parseInt(precisionPidData._Resp.nmPoints) <= 30)) {
                            int score = (Integer.parseInt(precisionPidData._Resp.nmPoints)) * 2 + 20;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else if ((Integer.parseInt(precisionPidData._Resp.nmPoints)) >= 31 && (Integer.parseInt(precisionPidData._Resp.nmPoints) <= 50)) {
                            int score = (Integer.parseInt(precisionPidData._Resp.nmPoints)) * 2;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else {
                            obj.put("pidata_qscore", precisionPidData._Resp.nmPoints);
                        }
                        SdkConstants.RECEIVE_DRIVER_DATA = obj.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //---------------Please scan your Finger---------------
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class AuthRequestStartek extends AsyncTask<Void, Void, String> {

        private String uid;
        private FM220PidData fm220PidData;
        private ProgressDialog dialog;
        private int posFingerFormat = 0;
        String base64pidData;
        Meta meta;
        AuthReq authReq;
        FM220DeviceInfo info;

        private AuthRequestStartek(String uid, FM220PidData fm220PidData, String base64pidData) {
            this.uid = uid;
            this.fm220PidData = fm220PidData;
            this.base64pidData = base64pidData;
            dialog = new ProgressDialog(DriverActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                info = fm220PidData._DeviceInfo;

                Uses uses = new Uses();
                uses.pi = "n";
                uses.pa = "n";
                uses.pfa = "n";
                uses.bio = "y";
                if (posFingerFormat == 1) {
                    uses.bt = "FIR";
                } else {
                    uses.bt = "FMR";
                }
                uses.pin = "n";
                uses.otp = "n";
                meta = new Meta();
                meta.udc = "MANT0";
                meta.rdsId = info.rdsld;
                meta.rdsVer = info.rdsVer;
                meta.dpId = info.dpld;
                meta.dc = info.dc;
                meta.mi = info.mi;
                meta.mc = info.mc;

                authReq = new AuthReq();
                authReq.uid = uid;
                authReq.rc = "Y";
                authReq.tid = "registered";
                authReq.ac = "public";
                authReq.sa = "public";
                authReq.ver = "2.0";
                authReq.txn = generateTXN();
                authReq.lk = "MEaMX8fkRa6PqsqK6wGMrEXcXFl_oXHA-YuknI2uf0gKgZ80HaZgG3A"; //AUA
                authReq.skey = fm220PidData._Skey;
                authReq.Hmac = fm220PidData.Hmac;
                authReq.data = fm220PidData._Data;
                authReq.meta = meta;
                authReq.uses = uses;
                authReq.freshnessFactor = freshnessFactor;

                StringWriter writer = new StringWriter();
                serializer.write(authReq, writer);
                String pass = "public";
                String reqXML = writer.toString();
                String signAuthXML = XMLSigner.generateSignXML(reqXML, getAssets().open("staging_signature_privateKey.p12"), pass);
                URL url = new URL(getAuthURL(uid));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                OutputStreamWriter writer2 = new OutputStreamWriter(conn.getOutputStream());
                writer2.write(signAuthXML);
                writer2.flush();
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response;
                while ((response = reader.readLine()) != null) {
                    sb.append(response).append("\n");
                }
                response = sb.toString();

                AuthRes authRes = serializer.read(AuthRes.class, response);
                String res;
                if (authRes.err != null) {
                    if (authRes.err.equals("0")) {
                        res = "Authentication Success" + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    } else {
                        res = "Error Code: " + authRes.err + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    }
                } else {
                    res = "Authentication Success" + "\n"
                            + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                            + "TXN: " + authRes.txn + "\n"
                            + "";
                }
                return res;
            } catch (Exception e) {
                return "Error: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try{
                if (res != null && authReq != null && meta != null && info != null) {


                    String vid = null;
                    String uid = null;
                    String value = authReq.skey.value.toString();
                    String last = String.valueOf(value.charAt(value.length() - 1));
                    if (last.equalsIgnoreCase("\n")) {
                        value = value.replace("\n", "");
                    }

                    SdkConstants.RECEIVE_DRIVER_DATA = "";
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("CI", authReq.skey.ci);
                        obj.put("DC", meta.dc);

                        obj.put("DPID", meta.dpId);
                        obj.put("DATAVALUE", authReq.data.value);
                        obj.put("HMAC", authReq.Hmac);
                        obj.put("MC", meta.mc);
                        obj.put("MI", meta.mi);

                        obj.put("RDSID", meta.rdsId);
                        obj.put("RDSVER", meta.rdsVer);
                        obj.put("value", value);
                        obj.put("pidata_qscore", fm220PidData._Resp.qScore);
                        obj.put("base64pidData", base64pidData);

                        if ((Integer.parseInt(fm220PidData._Resp.qScore)) >= 1 && (Integer.parseInt(fm220PidData._Resp.qScore) <= 10)) {
                            int score = (Integer.parseInt(fm220PidData._Resp.qScore)) + 60;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else if ((Integer.parseInt(fm220PidData._Resp.qScore)) >= 11 && (Integer.parseInt(fm220PidData._Resp.qScore) <= 30)) {
                            int score = (Integer.parseInt(fm220PidData._Resp.qScore)) * 2 + 20;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else if ((Integer.parseInt(fm220PidData._Resp.qScore)) >= 31 && (Integer.parseInt(fm220PidData._Resp.qScore) <= 50)) {
                            int score = (Integer.parseInt(fm220PidData._Resp.qScore)) * 2;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else {
                            obj.put("pidata_qscore", fm220PidData._Resp.qScore);
                        }
                        SdkConstants.RECEIVE_DRIVER_DATA = obj.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //---------------Please scan your Finger---------------
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String generateTXN() {
        try {
            Date tempDate = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
            String dTTXN = formatter.format(tempDate);
            return dTTXN;
        } catch (Exception e) {
            return "";
        }
    }

    private String getAuthURL(String UID) {
        String url = "http://developer.uidai.gov.in/auth/";
        url += "public/" + UID.charAt(0) + "/" + UID.charAt(1) + "/";
        url += "MG41KIrkk5moCkcO8w-2fc01-P7I5S-6X2-X7luVcDgZyOa2LXs3ELI"; //ASA
        return url;
    }

    private void morphoMessage() {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(isumatm.androidsdk.equitas.R.string.morpho))
                .setMessage(getResources().getString(isumatm.androidsdk.equitas.R.string.install_morpho_message))
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.scl.rdservice"; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .show();
    }

    private void startekMessage() {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(isumatm.androidsdk.equitas.R.string.Fm220U_install))
                .setMessage(getResources().getString(isumatm.androidsdk.equitas.R.string.fm220U_service))
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.acpl.registersdk"; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .show();
    }

    private void precisionMessage() {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle("Precision RD Service")
                .setMessage("Unable to find Precision Service app. Please install it from playstore.")
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.precision.pb510.rdservice"; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .show();
    }
    /*Methods for the Bluprint biometric device*/

    private void bluprintCapture() {
        try {
            String pidOption = getPIDOptions();
            if (pidOption != null) {
                Intent intent = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.setPackage("com.nextbiometrics.onetouchrdservice");
                intent.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent, EVOLUTE_CAPTURE_REQUEST_CODE);
            }
        } catch (Exception e) {
        }
    }


    private void bluprintMessage() {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle("Bluprint Biometric RD Service")
                .setMessage("Unable to find Bluprint Biometric Service app. Please install it from playstore.")
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.nextbiometrics.onetouchrdservice"; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .show();
    }



    /*Methods for the evolute biometric device*/

    private void evoluteCapture() {
        try {
            String pidOption = getPIDOptions();
            if (pidOption != null) {
                Intent intent = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
                intent.setPackage("com.evolute.rdservice");
                intent.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent, EVOLUTE_CAPTURE_REQUEST_CODE);
            }
        } catch (Exception e) {
        }
    }


    private void evoluteMessage() {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(DriverActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle("Evolute RD Service")
                .setMessage("Unable to find Evolute Service app. Please install it from playstore.")
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.evolute.rdservice"; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .show();
    }

    private class AuthRequestEvolute extends AsyncTask<Void, Void, String> {

        private String uid;
        private EvolutePidData evolutePidData;
        private ProgressDialog dialog;
        private int posFingerFormat = 0;
        String base64pidData;
        Meta meta;
        AuthReq authReq;
        EvoluteDeviceInfo info;

        private AuthRequestEvolute(String uid, EvolutePidData evolutePidData, String base64pidData) {
            this.uid = uid;
            this.evolutePidData = evolutePidData;
            this.base64pidData = base64pidData;
            dialog = new ProgressDialog(DriverActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                info = evolutePidData._DeviceInfo;

                Uses uses = new Uses();
                uses.pi = "n";
                uses.pa = "n";
                uses.pfa = "n";
                uses.bio = "y";
                if (posFingerFormat == 1) {
                    uses.bt = "FIR";
                } else {
                    uses.bt = "FMR";
                }
                uses.pin = "n";
                uses.otp = "n";
                meta = new Meta();
                meta.udc = "MANT0";
                meta.rdsId = info.rdsld;
                meta.rdsVer = info.rdsVer;
                meta.dpId = info.dpld;
                meta.dc = info.dc;
                meta.mi = info.mi;
                meta.mc = info.mc;

                authReq = new AuthReq();
                authReq.uid = uid;
                authReq.rc = "Y";
                authReq.tid = "registered";
                authReq.ac = "public";
                authReq.sa = "public";
                authReq.ver = "2.0";
                authReq.txn = generateTXN();
                authReq.lk = "MEaMX8fkRa6PqsqK6wGMrEXcXFl_oXHA-YuknI2uf0gKgZ80HaZgG3A"; //AUA
                authReq.skey = evolutePidData._Skey;
                authReq.Hmac = evolutePidData._Hmac;
                authReq.data = evolutePidData._Data;
                authReq.meta = meta;
                authReq.uses = uses;
                authReq.freshnessFactor = freshnessFactor;

                StringWriter writer = new StringWriter();
                serializer.write(authReq, writer);
                String pass = "public";
                String reqXML = writer.toString();
                String signAuthXML = XMLSigner.generateSignXML(reqXML, getAssets().open("staging_signature_privateKey.p12"), pass);
                URL url = new URL(getAuthURL(uid));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                OutputStreamWriter writer2 = new OutputStreamWriter(conn.getOutputStream());
                writer2.write(signAuthXML);
                writer2.flush();
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response;
                while ((response = reader.readLine()) != null) {
                    sb.append(response).append("\n");
                }
                response = sb.toString();

                AuthRes authRes = serializer.read(AuthRes.class, response);
                String res;
                if (authRes.err != null) {
                    if (authRes.err.equals("0")) {
                        res = "Authentication Success" + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    } else {
                        res = "Error Code: " + authRes.err + "\n"
                                + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                                + "TXN: " + authRes.txn + "\n"
                                + "";
                    }
                } else {
                    res = "Authentication Success" + "\n"
                            + "Auth Response: " + authRes.ret.toUpperCase() + "\n"
                            + "TXN: " + authRes.txn + "\n"
                            + "";
                }
                return res;
            } catch (Exception e) {
                return "Error: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try{
                if (res != null && authReq != null && meta != null && info != null) {


                    String vid = null;
                    String uid = null;
                    String value = authReq.skey.value.toString();
                    String last = String.valueOf(value.charAt(value.length() - 1));
                    if (last.equalsIgnoreCase("\n")) {
                        value = value.replace("\n", "");
                    }

                    SdkConstants.RECEIVE_DRIVER_DATA = "";
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("CI", authReq.skey.ci);
                        obj.put("DC", meta.dc);

                        obj.put("DPID", meta.dpId);
                        obj.put("DATAVALUE", authReq.data.value);
                        obj.put("HMAC", authReq.Hmac);
                        obj.put("MC", meta.mc);
                        obj.put("MI", meta.mi);

                        obj.put("RDSID", meta.rdsId);
                        obj.put("RDSVER", meta.rdsVer);
                        obj.put("value", value);
                        obj.put("pidata_qscore", evolutePidData._Resp.nmPoints);
                        obj.put("base64pidData", base64pidData);

                        if ((Integer.parseInt(evolutePidData._Resp.nmPoints)) >= 1 && (Integer.parseInt(evolutePidData._Resp.nmPoints) <= 10)) {
                            int score = (Integer.parseInt(fm220PidData._Resp.nmPoints)) + 50;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else if ((Integer.parseInt(evolutePidData._Resp.nmPoints)) >= 11 && (Integer.parseInt(evolutePidData._Resp.nmPoints) <= 30)) {
                            int score = (Integer.parseInt(evolutePidData._Resp.nmPoints)) * 2 + 20;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else if ((Integer.parseInt(evolutePidData._Resp.nmPoints)) >= 31 && (Integer.parseInt(evolutePidData._Resp.nmPoints) <= 50)) {
                            int score = (Integer.parseInt(evolutePidData._Resp.nmPoints)) * 2;
                            obj.put("pidata_qscore", String.valueOf(score));
                        } else {
                            obj.put("pidata_qscore", evolutePidData._Resp.nmPoints);
                        }
                        SdkConstants.RECEIVE_DRIVER_DATA = obj.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //---------------Please scan your Finger---------------
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
