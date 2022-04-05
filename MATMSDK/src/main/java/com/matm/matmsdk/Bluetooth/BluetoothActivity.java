package com.matm.matmsdk.Bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import isumatm.androidsdk.equitas.R;

import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.MPOS.PosActivity;
import com.matm.matmsdk.Utils.Dialog;
import com.matm.matmsdk.Utils.EnvData;
import com.matm.matmsdk.Utils.PreferenceUtility;
import com.matm.matmsdk.Utils.ResultEvent;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.Utils.Session;
import com.matm.matmsdk.Utils.Tools;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.api.ResponseCode;
import com.paxsz.easylink.device.DeviceInfo;
import com.paxsz.easylink.listener.SearchDeviceListener;
import com.paxsz.easylink.model.ShowPageInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

public class BluetoothActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    ConstraintLayout pairedDeviceLayout;
    TextView pairedDeviceTitle;
    ListView availableDevicesList;
    CoordinatorLayout container;
    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String DEVICE_MAC = "DEVICE_MAC";
    private String pairedDeviceName;
    private String pairedDeviceMac,terminal_address="Bhubaneswer,Odisha";
    private ImageView refresh;
    private ImageView back;
    private RotateAnimation anim;
    private ArrayList<HashMap<String, String>> deviceInfo = new ArrayList<>();
    private EasyLinkSdkManager manager;
    private Handler handler;
    private ExecutorService backgroundExecutor;
    private AlertDialog alertDialog;
    private boolean isStopBtn = false;
    ProgressDialog progressDialog;
    private String lactStr="";
    private String LngStr="";

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    String currentName;
    String currentMac;
    ProgressDialog pd;
    Session session;
    PreferenceUtility  prefUtlity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        new CustomThemes(this);
        if (SdkConstants.bluetoothLayout == 0) {
            setContentView(R.layout.activity_bluetooth);
        } else {
            setContentView(SdkConstants.bluetoothLayout);
        }
        prefUtlity = new PreferenceUtility(BluetoothActivity.this);

        setUpGClient();
        pd = new ProgressDialog(BluetoothActivity.this);
        session =  new Session(BluetoothActivity.this);
        manager = EasyLinkSdkManager.getInstance(this);
        if(SdkConstants.LogOut.equalsIgnoreCase("0")){
            logoutDevice();
        }

        if(SdkConstants.applicationType.equalsIgnoreCase("CORE")){

            EnvData.user_id = getIntent().getStringExtra("userName");
            EnvData.token = getIntent().getStringExtra("user_token");;

        }else {
            EnvData.user_id = SdkConstants.loginID;
            getUserAuthToken();

        }
        //EnvData.user_id = getIntent().getStringExtra("user_id");

        pairedDeviceLayout = findViewById(R.id.pairedDeviceLayout);
        pairedDeviceTitle = findViewById(R.id.pairedDeviceTitle);
        availableDevicesList = findViewById(R.id.availableDevicesList);
        container = findViewById(R.id.container);


        if(SdkConstants.LogOut.equalsIgnoreCase("0")){
            logoutDevice();
        }

        if(SdkConstants.applicationType.equalsIgnoreCase("CORE")){

            EnvData.user_id = getIntent().getStringExtra("userName");
            EnvData.token = getIntent().getStringExtra("user_token");


        }else {
            EnvData.user_id = SdkConstants.loginID;
            getUserAuthToken();

        }
        handler = new Handler();
        backgroundExecutor = Executors.newFixedThreadPool(10, runnable -> {
            Thread thread = new Thread(runnable, "Background executor service");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setDaemon(true);
            return thread;
        });

        register(this);
        initView();
        setListener();

        refresh.setOnClickListener(v -> {
            refresh.startAnimation(anim);
            deviceInfo.clear();

            updateBluetoothList();
            manager.searchDevices(new CustomDeviceSearchListener(), 10000);
        });

        back.setOnClickListener(view -> onBackPressed());


    }

    public void doEvent(ResultEvent event) {
        EventBus.getDefault().post(event);
    }

    public void runInBackground(final Runnable runnable) {
        backgroundExecutor.submit(runnable);
    }

    private void initView() {


        getSupportActionBar().setTitle("Bluetooth");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setCustomView(R.layout.custom_ble_actionbar);
        View view = getSupportActionBar().getCustomView();

        resetPairedDeviceUI();

        refresh = ((View) view).findViewById(R.id.refresh);
        back = findViewById(R.id.back);
        anim = new RotateAnimation(360.0f, 0.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1000);

        refresh.setAnimation(anim);
        deviceInfo.clear();
        updateBluetoothList();
        manager.searchDevices(new CustomDeviceSearchListener(), 40000);
    }

    private void setListener() {
        availableDevicesList.setOnItemClickListener((parent, arg1, pos, id) -> onItemClick(pos));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ResultEvent event) {
        switch ((ResultEvent.Status) event.getStatus()) {
            case DISCOVER_ONE:
                addDevice((DeviceInfo) event.getData());
                break;
            case SEARCH_COMPLETE:
                refresh.clearAnimation();
                break;
            case CONNECT_SUCCESS:
                refresh.clearAnimation();
                //dialogDismiss();
                //resetPairedDeviceUI();
                //updateBluetoothList();
                String devName="";
                //devName = pref.getString("DEVICE_NAME","");
                devName = prefUtlity.getString("DEVICE_NAME","");

                if(devName.equalsIgnoreCase(currentName.replace("D180-","").trim())){
                    String deviceName  = prefUtlity.getString("DEVICE_NAME",null);
                    String devceMac = prefUtlity.getString("DEVICE_MAC",null);
                    // Generate TPK TDK by------- RAJESH
                    deviceRegisteration(deviceName,devceMac);

                }else{

                    // Device MAP by Rajesh

                    checkDeviceStatus(currentName,currentMac);
                    //saveDeviceInfoForMapping(currentName, currentMac);

                }
                break;
            case CONNECT_FAILED:
                refresh.clearAnimation();
                dialogDismiss();
                resetPairedDeviceUI();
                updateBluetoothList();

                showAlertDeviceIspaired("Already one device is paired with this user. Please try again and re connect.");

                //showAlert("Already Device is paired.");
                //Snackbar.showSnackBar(container, ResponseCode.getRespCodeMsg((int) event.getData()));
                break;
            default:
                break;
        }
    }

    private void resetPairedDeviceUI() {
        if (!manager.isConnected(DeviceInfo.CommType.BLUETOOTH)) {
            pairedDeviceLayout.setVisibility(View.GONE);
            if (pairedDeviceName != null && pairedDeviceMac != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put(DEVICE_NAME, pairedDeviceName);
                map.put(DEVICE_MAC, pairedDeviceMac);
                deviceInfo.add(map);
                pairedDeviceName = "";
                pairedDeviceMac = "";
            }
            return;
        }
        pairedDeviceName = manager.getConnectedDevice().getDeviceName();
        pairedDeviceMac = manager.getConnectedDevice().getIdentifier();
        pairedDeviceLayout.setVisibility(View.VISIBLE);
        pairedDeviceTitle.setText(pairedDeviceName);

        //remove paired device from available devices list
        for (int i = 0; i < deviceInfo.size(); i++) {
            if (pairedDeviceName.equals(deviceInfo.get(i).get(DEVICE_NAME))
                    && pairedDeviceMac.equals(deviceInfo.get(i).get(DEVICE_MAC))) {
                deviceInfo.remove(i);
                break;
            }
        }
    }

    private void updateBluetoothList() {
        final BluetoothListAdapter adapter = new BluetoothListAdapter(this, deviceInfo);
        availableDevicesList.setAdapter(adapter);
        availableDevicesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * BluetoothList  On Item click ---- Rajesh
     *
     *  */

    private void onItemClick(int pos) {


        alertDialog = new AlertDialog.Builder(this).create();
        Dialog.showProgressDialog(this, alertDialog,"Connecting", "Please wait..", false);

        currentName = deviceInfo.get(pos).get(DEVICE_NAME);
        currentMac = deviceInfo.get(pos).get(DEVICE_MAC);


        // runInBackground(() -> connectDevice(currentName, currentMac));

        checkDeviceStatus(currentName, currentMac);

        manager.stopSearchingDevice();

    }

   /* private void saveDeviceInfoForMapping(String currentName, String currentMac){


        editor.putString("DEVICE_NAMEE",currentName);
        editor.putString("DEVICE_MACC",currentMac);
        editor.commit();
    }*/

    private void saveDeviceInfo(String currentName, String currentMac){

        prefUtlity.saveString("DEVICE_NAME",currentName);
        prefUtlity.saveString("DEVICE_MAC",currentMac);

    }

    // Pair device Connect  -------Rajesh
    private void connectDevice(String deviceName, String deviceMac) {
        DeviceInfo deviceInfo = new DeviceInfo(DeviceInfo.CommType.BLUETOOTH, deviceName, deviceMac);
        int ret = manager.connect(deviceInfo);
        if (isDestroyed()) {
            return;
        }
        if (ret == ResponseCode.EL_RET_OK) {
            Log.i("log", "connect success");
            Log.i("log", deviceName);
            doEvent(new ResultEvent(ResultEvent.Status.CONNECT_SUCCESS));
        } else {
            doEvent(new ResultEvent(ResultEvent.Status.CONNECT_FAILED, ret));
        }
    }

    private class CustomDeviceSearchListener implements SearchDeviceListener {
        @Override
        public void discoverOneDevice(DeviceInfo deviceInfo) {
            doEvent(new ResultEvent(ResultEvent.Status.DISCOVER_ONE, deviceInfo));
        }

        @Override
        public void discoverComplete() {
            doEvent(new ResultEvent(ResultEvent.Status.SEARCH_COMPLETE));
        }
    }

    public class BluetoothListAdapter extends BaseAdapter {

        private Context context;

        public BluetoothListAdapter(Context context, ArrayList<HashMap<String, String>> deviceInfo) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return deviceInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return deviceInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                int layout = R.layout.bluetooth_list;
                if (SdkConstants.bluetoothItem != 0){
                    layout = SdkConstants.bluetoothItem;
                }
                view = LayoutInflater.from(context).inflate(layout, parent, false);
            } else {
                view = convertView;
            }
            TextView tv = (TextView) view.findViewById(R.id.bluetoothDeviceName);// display text
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            if(deviceInfo.size()>0){
                tv.setText(deviceInfo.get(position).get(DEVICE_NAME));
                imageView.setImageResource(R.drawable.icn_bluetooth);}
            return view;
        }
    }


    private void addDevice(DeviceInfo deviceInf) {
        //avoid add the same device
        if (deviceInf.getDeviceName().equals(pairedDeviceName)
                && deviceInf.getIdentifier().equals(pairedDeviceMac)) {
            return;
        }
        for (int i = 0; i < deviceInfo.size(); i++) {
            if (deviceInf.getDeviceName().equals(deviceInfo.get(i).get(DEVICE_NAME))
                    && deviceInf.getIdentifier().equals(deviceInfo.get(i).get(DEVICE_MAC))) {
                return;
            }
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(DEVICE_NAME, deviceInf.getDeviceName());
        map.put(DEVICE_MAC, deviceInf.getIdentifier());
        deviceInfo.add(map);
        updateBluetoothList();
    }

    private void dialogDismiss() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void unregister(Object obj) {
        if (EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().unregister(obj);
        }
    }

    public void register(Object obj) {
        if (!EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().register(obj);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "requestPermissions success");
            } else {
                Log.d(TAG, "requestPermissions fail");
            }
        }
        int permissionLocation = ContextCompat.checkSelfPermission(BluetoothActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister(this);
    }
    // ------------ THerry API for Device Mapping---------------
   /* public void checkDeviceStatus(final String currentName, final String currentMac){
        //runProgressDialog();
        //deviceRegisteration(currentName,currentMac);
        try {
            JSONObject obj = new JSONObject();
            obj.put("device_sn", currentName.replace("D180-","").trim());
            obj.put("user_id", Integer.valueOf(EnvData.user_id));

            AndroidNetworking.post("https://app2.iserveu.website/matm_device_validity")
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");

                                if(status.equalsIgnoreCase("1")){
                                    //session.setDeviceName(currentName);
                                    //session.setDeviceMac(currentMac);
                                    //saveDeviceInfoForMapping(currentName, currentMac);
                                    deviceRegisteration(currentName,currentMac);

                                }else{
                                    String responseString = obj.getString("response");
                                    dialogDismiss();
                                    showUserOnboardStatusFailure(responseString+"\nplease call or email helpdesk to continue ATM transactions.");
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                                dialogDismiss();
                                showUserOnboardStatusFailure("Unauthorized device paired please call or email helpdesk to continue ATM transactions.");
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            System.out.println("Error  "+anError.getErrorDetail());
                            dialogDismiss();
                            showUserOnboardStatusFailure("Unauthorized device paired please call or email helpdesk to continue ATM transactions.");

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }*/

    //-----------Pravin API for Device Mapping ------------------------

    public void checkDeviceStatus(final String currentName, final String currentMac){
        try {
            JSONObject obj = new JSONObject();
            obj.put("deviceSlNo", currentName.replace("D180-","").trim());
            obj.put("userName", EnvData.user_id);
            //obj.put("userId", Integer.valueOf(EnvData.user_id));


            AndroidNetworking.post("https://us-central1-creditapp-29bf2.cloudfunctions.net/isuApi/matmmapping/mapwithusername")
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");
                                if(status.equalsIgnoreCase("0")){
                                    //saveDeviceInfoForMapping(currentName, currentMac);
                                    // deviceRegisteration(currentName,currentMac);
                                    // pairing pax with blueetooth........Rajesh
                                    prefUtlity.saveString("DEVICE_NAME",currentName.replace("D180-","").trim());
                                    prefUtlity.saveString("DEVICE_MAC",currentMac.trim());
                                    runInBackground(() -> connectDevice(currentName, currentMac));

                                }else{
                                    String responseString = obj.getString("desc");
                                    dialogDismiss();
                                    showUserOnboardStatusFailure(responseString+"\nplease call or email helpdesk to continue ATM transactions.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dialogDismiss();
                                showUserOnboardStatusFailure("Unauthorized device paired please call or email helpdesk to continue ATM transactions.");
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            System.out.println("Error  "+anError.getErrorDetail());
                            dialogDismiss();
                            String errorStr = anError.getErrorBody();
                            try {
                                JSONObject obj = new JSONObject(errorStr);
                                String status = obj.getString("status");
                                if(status.equalsIgnoreCase("1")){
                                    String sttsDesc = obj.getString("errorMessage");
                                    String message ="";
                                    message = obj.optString("message");
                                    Toast.makeText(BluetoothActivity.this, sttsDesc, Toast.LENGTH_LONG).show();
                                    showUserOnboardStatusFailure(sttsDesc);

                                }
                                else{
                                    Toast.makeText(BluetoothActivity.this, "Oops!! Server error.", Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void doGenerateTPK_TDK(String currentName, String currentMac ) {

        pairedDeviceName = manager.getConnectedDevice().getDeviceName();
        String identifier =  manager.getConnectedDevice().getIdentifier();
        int  productId = manager.getConnectedDevice().getProductId();
        int venderId = manager.getConnectedDevice().getVendorId();

        pairedDeviceName = pairedDeviceName.replace("D180-","").trim();
        System.out.println("identifier"+identifier+"-----"+"productId"+productId+"-----------"+"venderId"+venderId);
    }

    private void showUserOnboardStatusSuccess(final String statusDesc){


        AlertDialog.Builder builder1 = new AlertDialog.Builder(BluetoothActivity.this);
        builder1.setMessage(statusDesc);
        builder1.setTitle("Alert");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogDismiss();
                        onBackPressed();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void showUserOnboardStatusFailure(final String statusDesc){
        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(BluetoothActivity.this);
            builder1.setMessage(statusDesc);
            builder1.setTitle("Alert");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialogDismiss();
                            manager.disconnect();
                            onBackPressed();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }catch (Exception e){

        }
    }
    public void deviceRegisteration(final String currentName, final String currentMac){
        //runProgressDialog();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                try {
                    long time = getTime();
                    Calendar current_calendar = Calendar.getInstance();
                    current_calendar.setTimeInMillis(time);
                    long injectTime = prefUtlity.getLong("INJECTED_TIME",0);

                    if(injectTime>0) {
                        String device_name = prefUtlity.getString("DEVICE_NAME",null);
                        long saved_time = prefUtlity.getLong("INJECTED_TIME",0);
                        Calendar saved_calendar = Calendar.getInstance();
                        saved_calendar.setTimeInMillis(saved_time);

                        long diff = current_calendar.getTimeInMillis() - saved_calendar.getTimeInMillis();

                        float dayCount = (float) diff / (24 * 60 * 60 * 1000);


                        if(device_name.equalsIgnoreCase(currentName.replace("D180-",""))) {
                            if (dayCount >= 1) {
                                injectKeys(currentName, currentMac, time);
                            } else {

                                saveDeviceInfo(currentName, currentMac);
                                runInBackground(() -> connectDevice(currentName, currentMac));
                            }
                        }else{
                            injectKeys(currentName,currentMac,time);
                        }

                    }else{
                        injectKeys(currentName,currentMac,time);
                    }
                    // System.out.println("CURRENT TIME"+current_time);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public void injectKeys(final String currentName, final String currentMac,final long time)
    {
        String Url="";
        if(SdkConstants.applicationType.equalsIgnoreCase("CORE")){  //For app
            Url = EnvData.TEST_URL+"/generateTPKandTDK/"+currentName.replace("D180-","").trim();

        }
        else {
            //For SDK user
            Url = EnvData.TEST_URL+"/generateTPKandTDK/"+currentName.replace("D180-","").trim()+"/"+ EnvData.user_id;

        }

        //https://matm.iserveu.tech/DONTDEPLOY/
        Url = EnvData.TEST_URL+"/DONTDEPLOY/generateTPKandTDK/"+currentName.replace("D180-","").trim()+"/"+ SdkConstants.loginID;
        AndroidNetworking.post(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", EnvData.token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MPOS :",response.toString());
                        try {
                            if(response!=null){
                                JSONObject jObj = new JSONObject(response.toString());
                                //String jsonSts = jObj.getString("status");
                               /* if(jsonSts.equalsIgnoreCase("-1")){
                                    String stsDesc = jObj.getString("statusDesc");
                                    showAlert(stsDesc);

                                }else{*/
                                //saveDeviceInfoForMapping(currentName, currentMac);


                                String tpkString  = jObj.getString("tpk");
                                String tdkString  = jObj.getString("tdk");

                                int TMK = Tools.testWriteKey_TMK(manager);
                                int TPK = Tools.testWriteKey_EncryptTPK(manager,tpkString);
                                int TDK = Tools.testWriteKey_EncryptTDK(manager,tdkString);

                                if(TPK==0&&TDK==0) {
                                    saveDeviceInfo(currentName, currentMac);
                                    resetPairedDeviceUI();
                                    updateBluetoothList();
                                    showUserOnboardStatusSuccess("Bluetooth device paired successfully");
                                    prefUtlity.saveString("DEVICE_NAME",currentName.replace("D180-","").trim());
                                    prefUtlity.saveLong("INJECTED_TIME",time);
                                    SdkConstants.BlueToothPairFlag = "1";
                                }else {
                                    showUserOnboardStatusFailure("Key Exchange Failed");
                                }
                            }

                            // }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        // pd.dismiss();
                        Log.d("MPOS :",anError.getErrorBody());
                        String response = anError.getErrorBody();
                        //{"status":-1,"statusDesc":"Device is not registered with us."}
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if(status.equalsIgnoreCase("-1")){
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void insertDevice(final String currentName, final String currentMac,final long time)
    {
        try {
            JSONObject obj = new JSONObject();
            obj.put("address", "");

            String Url = EnvData.TEST_URL + "/generateTPKandTDK/" + currentName.replace("D180-", "").trim();
            AndroidNetworking.post(Url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .addHeaders("Authorization", EnvData.token)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("MPOS :", response.toString());
                            try {
                                JSONObject jObj = new JSONObject(response.toString());
                                injectKeys(currentName, currentMac, time);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            // pd.dismiss();
                            Log.d("MPOS :", anError.getErrorBody());
                            String response = anError.getErrorBody();

                        }
                    });
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private long getTime() throws Exception {
        String url = "https://time.is/Unix_time_now";
        Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        String[] tags = new String[] {
                "div[id=time_section]",
                "div[id=clock0_bg]"
        };
        Elements elements= doc.select(tags[0]);
        for (int i = 0; i <tags.length; i++) {
            elements = elements.select(tags[i]);
        }
        return Long.parseLong(elements.text() + "000");
    }
    //-----------------
    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mylocation = location;

        if (mylocation != null) {


            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            lactStr = String.valueOf(latitude);
            LngStr = String.valueOf(longitude);


            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            Log.e("latitude", "latitude--" + latitude);
            try {
                Log.e("latitude", "inside latitude--" + latitude);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    terminal_address = city+","+state;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }

    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(BluetoothActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }
    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(BluetoothActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                            .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(BluetoothActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(BluetoothActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }
    private void getUserAuthToken(){
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        String url = SdkConstants.BASE_URL+"/api/getAuthenticateData" ;
        //String url = "https://newapp.iserveu.online/AEPS2NEW"+"/api/getAuthenticateData";

        JSONObject obj = new JSONObject();
        try {
            obj.put("encryptedData",SdkConstants.encryptedData);
            obj.put("retailerUserName",SdkConstants.loginID);
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");

                                if(status.equalsIgnoreCase("success")) {
                                    String userName = obj.getString("username");
                                    String userToken = obj.getString("usertoken");
                                    //String userToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTg4MTkxODU5NjMzLCJleHAiOjE1ODgxOTM2NTl9.0tZb8XrRIkFJ3ZamNuoL3n5OkqXvXPc4xU2EoJzbivrOOlg1jMse_WzpJtZDRH9-ESKBBOlfQ680V8U09WwUKg";
                                    EnvData.token = userToken;

                                    // getUserId(userToken,"https://mobile.9fin.co.in/user/user_details");

                                    pd.dismiss();

                                }else {
                                    showAlert("Error");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                pd.dismiss();
                                showAlert("Invalid Encrypted Data");
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            pd.dismiss();
                        }

                    });
        }catch ( Exception e){
            e.printStackTrace();
        }
    }
   /* public void getUserId(String token, String urlString){
        AndroidNetworking.get(urlString)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization",token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                             JSONObject obj = new JSONObject(response.toString());
                            String id = obj.getString("id");
                            EnvData.user_id = id;
                            System.out.println(obj);
                            pd.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            pd.dismiss();
                            System.out.println("Error  "+" We are not getting UserID .Please try again.");
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                         System.out.println("Error  "+anError.getErrorDetail());
                        pd.dismiss();
                    }
                });
    }*/


    public void showAlert(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
        builder.setTitle("Alert!!");
        builder.setMessage(msg);
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
    }

    public void showAlertDeviceIspaired(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
        builder.setTitle("Alert!!");
        builder.setMessage(msg);
        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                prefUtlity.saveString("DEVICE_NAME","");
                prefUtlity.saveString("DEVICE_MAC","");
                prefUtlity.saveLong("INJECTED_TIME",0);
                prefUtlity.clearAll(BluetoothActivity.this);
                manager.disconnect();
                finish();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }


    public  void logoutDevice(){
        prefUtlity.saveString("DEVICE_NAME","");
        prefUtlity.saveString("DEVICE_MAC","");
        prefUtlity.saveLong("INJECTED_TIME",0);
        prefUtlity.clearAll(BluetoothActivity.this);
        manager.disconnect();
        finish();
        SdkConstants.LogOut = "";
    }
    //-------------------------------------------------------


}
