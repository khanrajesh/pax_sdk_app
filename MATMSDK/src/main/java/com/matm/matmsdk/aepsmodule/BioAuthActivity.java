package com.matm.matmsdk.aepsmodule;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAEPS2RequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryPresenter;
import com.matm.matmsdk.aepsmodule.signer.XMLSigner;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.moos.library.HorizontalProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import isumatm.androidsdk.equitas.R;

public class BioAuthActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    ImageView two_fact_fingerprint;
    // ProgressBar two_fact_depositBar;
    private HorizontalProgressView depositBar;
    Button two_fact_submitButton;
    ProgressDialog loadingView;
    Session session;
    TextView userName;



    EditText balanceAadharNumber, balanceAadharVID;

    private Location mylocation;




    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    String postalCode = "751017";
    int pincode = 751017;
    private Boolean location_flag = false;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    String userNameStr = "";
    Boolean isSL = false;
    LinearLayout bio_ll;

    String bankIINNumber = "";

    String flagNameRdService = "";
    Class driverActivity;
    String balanaceInqueryAadharNo = "";
    Boolean flagFromDriver = false;
    boolean mInside = false;
    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    final static String MARKER = "|"; // filtered in layout not to be in the string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_auth);
        bio_ll = findViewById(R.id.bio_ll);
        SdkConstants.RECEIVE_DRIVER_DATA = "";
        session = new Session(BioAuthActivity.this);
        getRDServiceClass();

        setUpGClient();
        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            userNameStr = SdkConstants.applicationUserName;
            checkAddressStatus();
            isSL = false;
            showLoader();

        }else {
            isSL = true;
            if (SdkConstants.encryptedData.trim().length() != 0 && SdkConstants.transactionType.trim().length() != 0 && SdkConstants.loginID.trim().length() != 0) {
                getUserAuthToken();
            } else {
                showAlert("Request parameters are missing. Please check and try again..");
            }
        }


        two_fact_fingerprint = findViewById(R.id.two_fact_fingerprint);
        two_fact_fingerprint.setEnabled(true);
        depositBar = findViewById(R.id.depositBar);
        depositBar.setVisibility(View.GONE);

        two_fact_submitButton = findViewById(R.id.two_fact_submitButton);
        RadioButton bl_aadhar_no_rd = findViewById(R.id.bl_aadhar_no_rd);
        RadioButton bl_aadhar_uid_rd = findViewById(R.id.bl_aadhar_uid_rd);
        balanceAadharNumber = findViewById(R.id.balanceAadharNumber);
        balanceAadharVID = findViewById(R.id.balanceAadharVID);

        userName = findViewById(R.id.userName);
        userName.setText(SdkConstants.applicationUserName);


        balanceAadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    balanceAadharNumber.setError(getResources().getString(R.string.aadhaarnumber));

                }
                if (s.length() > 0) {
                    balanceAadharNumber.setError(null);
                    String aadharNo = balanceAadharNumber.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if(balanaceInqueryAadharNo.length()>=12){
                            two_fact_fingerprint.setEnabled(true);
                        }
                    }else{
                        balanaceInqueryAadharNo = aadharNo;
                        if(balanaceInqueryAadharNo.length()>=12){
                            two_fact_fingerprint.setEnabled(true);
                        }
                    }
                    if (Util.validateAadharNumber(aadharNo) == false) {
                        balanceAadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                    }
                }
            }
        });

        balanceAadharVID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    balanceAadharVID.setError(getResources().getString(R.string.aadhaarnumber_vid));

                }

                if (s.length() > 0) {
                    balanceAadharVID.setError(null);
                    String aadharNo = balanceAadharVID.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo =aadharNo;
                        if(balanaceInqueryAadharNo.length()>=12){
                            two_fact_fingerprint.setEnabled(true);
                        }
                    }else{
                            balanaceInqueryAadharNo = aadharNo;
                            if(balanaceInqueryAadharNo.length()>=12){
                                two_fact_fingerprint.setEnabled(true);
                            }
                        }
                    if (Util.validateAadharVID(aadharNo) == false) {
                        balanceAadharVID.setError(getResources().getString(R.string.valid_aadhar__uid_error));
                    }
                }
            }
        });

        two_fact_fingerprint.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

              /*  if (bl_aadhar_no_rd.isChecked()) {
                    balanaceInqueryAadharNo = balanceAadharNumber.getText().toString();
                    if (balanaceInqueryAadharNo.contains("-")) {
                        balanaceInqueryAadharNo = balanaceInqueryAadharNo.replaceAll("-", "").trim();
                    }
                    if (Util.validateAadharNumber(balanaceInqueryAadharNo) == false) {
                        balanceAadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                }
                if (bl_aadhar_uid_rd.isChecked()) {
                    balanaceInqueryAadharNo = balanceAadharVID.getText().toString();
                    if (balanaceInqueryAadharNo.contains("-")) {
                        balanaceInqueryAadharNo = balanaceInqueryAadharNo.replaceAll("-", "").trim();
                    }
                    if (Util.validateAadharVID(balanaceInqueryAadharNo) == false) {
                        balanceAadharVID.setError(getResources().getString(R.string.valid_aadhar__uid_error));
                        return;
                    }
                }*/
                if (balanaceInqueryAadharNo.length() > 0) {
                    //  showLoader();
                    flagFromDriver = true;
                    Intent launchIntent = new Intent(BioAuthActivity.this, driverActivity);
                    launchIntent.putExtra("driverFlag", flagNameRdService);
                    launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                    launchIntent.putExtra("AadharNo", balanaceInqueryAadharNo);
                    startActivityForResult(launchIntent, 1);
                }
            }
        });
        two_fact_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                    String CI = respObj.getString("CI");
                    String DC = respObj.getString("DC");
                    String DPID = respObj.getString("DPID");
                    String DATAVALUE = respObj.getString("DATAVALUE");
                    String HMAC = respObj.getString("HMAC");
                    String MI = respObj.getString("MI");
                    String MC = respObj.getString("MC");
                    String RDSID = respObj.getString("RDSID");
                    String RDSVER = respObj.getString("RDSVER");
                    String value = respObj.getString("value");

                    JSONObject obj = new JSONObject();
                    obj.put("aadharNo", balanaceInqueryAadharNo);
                    obj.put("dpId", DPID);
                    obj.put("rdsId", RDSID);
                    obj.put("rdsVer", RDSVER);
                    obj.put("dc", DC);
                    obj.put("mi", MI);
                    obj.put("mcData", MC);
                    obj.put("sKey", value);
                    obj.put("hMac", HMAC);
                    obj.put("encryptedPID", DATAVALUE);
                    obj.put("ci", CI);
                    obj.put("operation", "");
                    obj.put("retailer", SdkConstants.loginID);
                    obj.put("isSL", isSL);
                    submitBioAuthh(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    /*
     *  url for the sync of the data for the
     */

    private String getAuthURL(String UID) {
        String url = "http://developer.uidai.gov.in/auth/";
        url += "public/" + UID.charAt(0) + "/" + UID.charAt(1) + "/";
        url += "MG41KIrkk5moCkcO8w-2fc01-P7I5S-6X2-X7luVcDgZyOa2LXs3ELI"; //ASA
        return url;
    }

    private void getRDServiceClass() {
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;//getIntent().getStringExtra("activity");
        flagNameRdService = SdkConstants.MANUFACTURE_FLAG;//getIntent().getStringExtra("driverFlag");

        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            driverActivity = targetActivity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//............Rajesh
    private void submitBioAuthh(JSONObject obj1) {
        showLoader();
        AndroidNetworking.get("https://vpn.iserveu.tech/generate/v80")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String key = obj.getString("hello");
                            System.out.println(">>>>-----" + key);
                            byte[] data = Base64.decode(key, Base64.DEFAULT);
                            String encodedUrl = new String(data, "UTF-8");
                            //encodedUrl = "https://vpn2.iserveu.online/aeps2/ibl/bioAuth";
                            submitBioAuth(obj1, encodedUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }


    private void submitBioAuth( JSONObject obj, final String encodedUrl) {
        // showLoader();
        AndroidNetworking.post(encodedUrl)
                .setPriority(Priority.HIGH)
                .addJSONObjectBody(obj)
                .addHeaders("Authorization", session.getUserToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoader();

                        try {
                            // profile_id,is_declaration
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("0")) {
                                //initView();
                                SdkConstants.bioauth = false;
                                Toast.makeText(BioAuthActivity.this, "BioAuth Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(BioAuthActivity.this, AEPS2HomeActivity.class));
                                finish();
                            } else {
                                balanceAadharNumber.getText().clear();
                                depositBar.setVisibility(View.GONE);
                                Toast.makeText(BioAuthActivity.this, "BioAuth Failed", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoader();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.getErrorBody();
                        hideLoader();
                        Toast.makeText(BioAuthActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    public void showLoader() {
        if (loadingView == null) {
            loadingView = new ProgressDialog(BioAuthActivity.this);
            loadingView.setCancelable(false);
            loadingView.setMessage("Please Wait..");
        }
        loadingView.show();
    }
    public void hideLoader() {
        try {
            if (loadingView!=null){
                loadingView.dismiss();
            }
        }catch (Exception e){
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {


        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();

        if (flagFromDriver) {
            if (SdkConstants.RECEIVE_DRIVER_DATA.isEmpty() || SdkConstants.RECEIVE_DRIVER_DATA.equalsIgnoreCase("")) {

                two_fact_fingerprint.setEnabled(true);
                two_fact_submitButton.setEnabled(false);
            } else if (balanaceInqueryAadharNo.equalsIgnoreCase("") || balanaceInqueryAadharNo.isEmpty()) {
                balanceAadharNumber.setError("Enter Aadhar No.");
                fingerStrength();
            } else {
                fingerStrength();
                two_fact_fingerprint.setEnabled(false);
                two_fact_submitButton.setEnabled(true);
            }

        } else {
            checkPermission();
        }

    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(BioAuthActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                // ActivityCompat.requestPermissions(getActivity(), permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERA_PERMISSIONS);

            }else{
                getMyLocation();
                //retriveAUTH();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        int permissionLocation = ContextCompat.checkSelfPermission(BioAuthActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }else {
            Toast.makeText(this, "Please accept the location permission", Toast.LENGTH_SHORT).show();

            if(!isFinishing()) {
                finish();
            }
        }
    }
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
    public void onLocationChanged(Location location) {
        mylocation = location;

        if (mylocation != null) {
            location_flag=true;
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e("latitude", "latitude--" + latitude);
            try {
                Log.e("latitude", "inside latitude--" + latitude);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    if(postalCode==null){
                        postalCode="751017";
                    }
                    getPin();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle bundle) {
        checkPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(BioAuthActivity.this,
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
                                            .checkSelfPermission(BioAuthActivity.this,
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
                                        status.startResolutionForResult(BioAuthActivity.this,
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


    public void getPin(){
        if (mylocation != null) {
            // updateLocationInterface.onLocationUpdate(location);
            location_flag=true;

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            double latitude = mylocation.getLatitude();
            double longitude = mylocation.getLongitude();
            Log.e("latitude", "latitude--" + latitude);
            try {
                Log.e("latitude", "inside latitude--" + latitude);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {
                    postalCode = addresses.get(0).getPostalCode();
                    if(postalCode==null){
                        postalCode="751017";
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    //--------------

    private void checkAddressStatus(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("number", userNameStr);

            AndroidNetworking.post("https://vpn.iserveu.tech/generate/v81")
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String key = obj.getString("hello");
                                System.out.println(">>>>-----"+key);
                                byte[] data = Base64.decode(key,Base64.DEFAULT);
                                String encodedUrl = new String(data, "UTF-8");
                                viewUserPropAddress(encodedUrl);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideLoader();
                            }catch (UnsupportedEncodingException e) {
                                hideLoader();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.getErrorBody();
                            hideLoader();
                            Toast.makeText(BioAuthActivity.this, "Service is unavailable for this user, please contact our help desk for details.", Toast.LENGTH_LONG).show();
                        }
                    });
        }catch (Exception e){
            hideLoader();
            e.printStackTrace();
        }
    }
    private void viewUserPropAddress(String url){
        String token = session.getUserToken();
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization",session.getUserToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String statusDesc = obj.getString("statusDesc");
                            if(obj.has("response")) {
                                JSONObject res_obj = obj.getJSONObject("response");
                                String pin = res_obj.getString("pincode");
                                String state = res_obj.getString("state");
                                String city = res_obj.getString("city");
                                boolean bio_auth_status = res_obj.getBoolean("bioauth");
                                if(!bio_auth_status){
                                    JSONObject jsonObject = new JSONObject();

                                    if(postalCode.length()!=0){
                                        pincode = Integer.valueOf(postalCode);
                                    }
                                    jsonObject.put("pin",pincode);
                                    getAddressFromPin(jsonObject);
                                }else{
                                    hideLoader();
                                    Toast.makeText(BioAuthActivity.this, "BioAuth Success", Toast.LENGTH_SHORT).show();
                                    sendAEPS2Intent(true);
                                }
                            }else{

                                JSONObject jsonObject = new JSONObject();
                                if(postalCode.length()!=0){
                                    pincode = Integer.valueOf(postalCode);
                                }
                                jsonObject.put("pin",pincode);
                                getAddressFromPin(jsonObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoader();
                            Toast.makeText(BioAuthActivity.this, "Something went wrong, please contact our help desk for details.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoader();
                        anError.getErrorBody();
                        String errorStr = anError.getErrorBody();
                        try {
                            JSONObject obj = new JSONObject(errorStr);
                            String status = obj.getString("status");
                            if(status.equalsIgnoreCase("-1")){
                                String sttsDesc = obj.getString("statusDesc");
                                //Toast.makeText(BioAuthActivity.this, sttsDesc, Toast.LENGTH_LONG).show();
                                showAlert(sttsDesc);

                            }else if(status.equalsIgnoreCase("400")){
                                String message ="";
                                message = obj.optString("message");
                                //Toast.makeText(BioAuthActivity.this, message, Toast.LENGTH_LONG).show();
                                showAlert(message);

                            }
                            else{
                                showAlert("Oops!! Server error.");
                               // Toast.makeText(BioAuthActivity.this, "Oops!! Server error.", Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void getAddressFromPin(JSONObject obj) {
        // showLoader();
        AndroidNetworking.post("https://us-central1-iserveustaging.cloudfunctions.net/pincodeFetch/api/v1/getCitystateAndroid")
                .setPriority(Priority.HIGH)
                .addJSONObjectBody(obj)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            JSONObject data_obj = obj.getJSONObject("data");
                            String status = data_obj.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                JSONObject data_pin = data_obj.getJSONObject("data");
                                String pincode = data_pin.getString("pincode");
                                String state = data_pin.getString("state");
                                String shortState = Util.getShortState(state);
                                String city = data_pin.getString("city");

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("state", shortState);
                                jsonObject.put("pincode", pincode);
                                jsonObject.put("city", city);
                                String lat = "0.0";
                                String lng = "0.0";

                                if (mylocation != null) {
                                    lat = String.valueOf(mylocation.getLatitude());
                                    lng = String.valueOf(mylocation.getLongitude());
                                }
                                jsonObject.put("latLong", lat + "," + lng);
                                setAddress(jsonObject);
                            } else {
                                hideLoader();
                                Toast.makeText(BioAuthActivity.this, "Invalid area pin, please try after sometimes", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoader();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.getErrorBody();
                        hideLoader();
                        Toast.makeText(BioAuthActivity.this, "Invalid area pin, please try again.", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void setAddress(JSONObject objj) {

        try {
            JSONObject obj = new JSONObject();
            obj.put("number", userNameStr);

            AndroidNetworking.post("https://vpn.iserveu.tech/generate/v82")
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String key = obj.getString("hello");
                                System.out.println(">>>>-----" + key);
                                byte[] data = Base64.decode(key, Base64.DEFAULT);
                                String encodedUrl = new String(data, "UTF-8");
                                // encodedUrl = "https://vpn.iserveu.tech/updateUserPropAddress/"+userNameStr;
                                updateUserPropAddress(objj, encodedUrl);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideLoader();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.getErrorBody();
                            hideLoader();
                        }
                    });

        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }


    }
    private void updateUserPropAddress(JSONObject obj,String url){

        AndroidNetworking.post(url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", session.getUserToken())
                .addJSONObjectBody(obj)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoader();
                        try {
                            // {"statusDesc":"connected","status":"0"}
                            JSONObject obj = new JSONObject(response.toString());

                            String status = obj.getString("status");
                            String statusDesc = obj.getString("statusDesc");
                            if (status.equalsIgnoreCase("0")) {
                                bio_ll.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(BioAuthActivity.this, statusDesc, Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoader();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.getErrorBody();
                        hideLoader();
                    }
                });
    }

    public void sendAEPS2Intent(boolean bioauth) {
        SdkConstants.bioauth = bioauth;
        Intent intent = new Intent(BioAuthActivity.this, AEPS2HomeActivity.class);
        startActivityForResult(intent, SdkConstants.REQUEST_CODE);
        finish();
    }

    private void getUserAuthToken() {
        showLoader();
        String url = SdkConstants.BASE_URL+"/api/getAuthenticateData" ;
        //String url = "https://newapp.iserveu.online/AEPS2NEW"+"/api/getAuthenticateData";
        JSONObject obj = new JSONObject();
        try {
            obj.put("encryptedData", SdkConstants.encryptedData);
            obj.put("retailerUserName", SdkConstants.loginID);

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

                                if (status.equalsIgnoreCase("success")) {

                                    userNameStr = obj.getString("username");
                                    String userToken = obj.getString("usertoken");
                                    session.setUsername(userNameStr);
                                    session.setUserToken(userToken);
//                                    hideLoader();
                                    checkAddressStatus();

                                } else {
                                    showAlert(status);
                                    hideLoader();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideLoader();
                                showAlert("Invalid Encrypted Data");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hideLoader();

                        }

                    });
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }
    }

    public void showAlert(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(BioAuthActivity.this);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void fingerStrength() {
        try {
            JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
            String scoreStr = respObj.getString("pidata_qscore");
            if (Float.parseFloat(scoreStr) <= 60) {
                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.red));
                depositBar.setStartColor(getResources().getColor(R.color.red));
                //depositNote.setVisibility(View.VISIBLE);
                //fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            } else if (Float.parseFloat(scoreStr) >= 60 && Float.parseFloat(scoreStr) <= 70) {

                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.yellow));
                depositBar.setStartColor(getResources().getColor(R.color.yellow));
                //depositNote.setVisibility(View.VISIBLE);
                //fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            } else {

                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.green));
                depositBar.setStartColor(getResources().getColor(R.color.green));
                //depositNote.setVisibility(View.VISIBLE);
                //fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
