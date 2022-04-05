package com.matm.matmsdk.aepsmodule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
import com.matm.matmsdk.MPOS.PosActivity;
import com.matm.matmsdk.Utils.EnvData;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import isumatm.androidsdk.equitas.R;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Location mylocation;
    Session session;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    String postalCode = "751017";
    int pincode = 751017;
    private Boolean location_flag = false;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    ProgressDialog loadingView;
    String userNameStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(LocationActivity.this);
        setUpGClient();

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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(LocationActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CAMERA_PERMISSIONS);

            } else {
                getMyLocation();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        int permissionLocation = ContextCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {
            Toast.makeText(this, "Please accept the location permission", Toast.LENGTH_SHORT).show();

            if (!isFinishing()) {
                finish();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;

        if (mylocation != null) {
            // stop location updating
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, LocationActivity.this);
            location_flag = true;

            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                session.setUserToken(SdkConstants.tokenFromCoreApp);
                session.setUsername(SdkConstants.userNameFromCoreApp);
                viewUserPropAddress();
                showLoader();

            } else {
                if (SdkConstants.encryptedData.trim().length() != 0 && SdkConstants.transactionType.trim().length() != 0 && SdkConstants.loginID.trim().length() != 0) {
                    getUserAuthToken();
                } else {
                    showAlert("Request parameters are missing. Please check and try again..");
                }
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

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(LocationActivity.this,
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
                                            .checkSelfPermission(LocationActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }


    public void getPin() {
        if (mylocation != null) {
            // updateLocationInterface.onLocationUpdate(location);
            location_flag = true;

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
                    session.setUserPincode(postalCode);
                    if (postalCode == null) {
                        postalCode = "751017";
                    }
                    updateUserPropAddress();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    private void viewUserPropAddress() {
        String token = session.getUserToken();
        String username = session.getUserName();

        String Url = "";
        if(SdkConstants.applicationType.equalsIgnoreCase("CORE")){  //For app
            Url = "https://matmtest.iserveu.website/viewUserAddress";

        }
        else {
            //For SDK user
            Url = "https://matmtest.iserveu.website/viewUserAddress/"+username;

        }
        AndroidNetworking.get(Url)
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String statusDesc = obj.getString("statusDesc");
                            if (obj.has("response")) {
                                JSONObject res_obj = obj.getJSONObject("response");
                                String pin = res_obj.getString("pincode");
                                String state = res_obj.getString("state");
                                String city = res_obj.getString("city");

                                Log.v("MPOS", "response cities == " + response);
                                //if(pin.equalsIgnoreCase("null") || pin.length()==0 || pin == null || state.length() ==0 || city.length()==0) {
                                if ((pin.equalsIgnoreCase("") || pin == null ||state.equalsIgnoreCase("") || state == null ||city.equalsIgnoreCase("")) || city == null || (pin.equalsIgnoreCase("") || pin == null || state.equalsIgnoreCase("") || (state == null) || city.equalsIgnoreCase("")|| city == null)) {

                                    JSONObject jsonObject = new JSONObject();

                                    if (postalCode.length() != 0) {
                                        pincode = Integer.valueOf(postalCode);
                                    }
                                    jsonObject.put("pin", pincode);
                                    getAddressFromPin(jsonObject);
                                } else {
                                    hideLoader();
                                    Intent intent = new Intent(LocationActivity.this, PosActivity.class);
                                    startActivityForResult(intent, SdkConstants.REQUEST_CODE);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                }


                            } else {

                                JSONObject jsonObject = new JSONObject();
                                if (postalCode.length() != 0) {
                                    pincode = Integer.valueOf(postalCode);
                                }
                                jsonObject.put("pin", pincode);
                                getAddressFromPin(jsonObject);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoader();
                            Toast.makeText(LocationActivity.this, "Something went wrong, please contact our help desk for details.", Toast.LENGTH_LONG).show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.getErrorBody();
                        hideLoader();
                        Toast.makeText(LocationActivity.this, "Service is unavailable for this user, please contact our help desk for details.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getAddressFromPin(JSONObject obj) {
        showLoader();
        AndroidNetworking.post("https://us-central1-iserveustaging.cloudfunctions.net/pincodeFetch/api/v1/getCitystateAndroid")
                .setPriority(Priority.HIGH)
                .addJSONObjectBody(obj)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            // {"statusDesc":"connected","status":"0"}
                            JSONObject obj = new JSONObject(response.toString());
                            JSONObject data_obj = obj.getJSONObject("data");
                            String status = data_obj.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                JSONObject data_pin = data_obj.getJSONObject("data");
                                String pincode = data_pin.getString("pincode");
                                String state = data_pin.getString("state");
//                                String shortState = Util.getShortState(state);
                                String city = data_pin.getString("city");

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("state", state);
                                jsonObject.put("pincode", pincode);
                                jsonObject.put("city", city);
                                String lat = "0.0";
                                String lng = "0.0";

                                session.setUserCity(city);
                                session.setUserPincode(pincode);
                                session.setUserState(state);
                                if (mylocation != null) {
                                    lat = String.valueOf(mylocation.getLatitude());
                                    lng = String.valueOf(mylocation.getLongitude());
                                }
                                jsonObject.put("latLong", lat + "," + lng);
                                session.setUserLatLong(lat + "," + lng);
                                updateUserPropAddress();
                            } else {
                                hideLoader();
                                Toast.makeText(LocationActivity.this, "Invalid area pin, please try after sometimes", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LocationActivity.this, "Invalid area pin, please try again.", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateUserPropAddress() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("pincode", session.getUserPincode());
            obj.put("city", session.getUserCity());
            obj.put("state", session.getUserState());

            String token = session.getUserToken();
            String Url = "";
            if(SdkConstants.applicationType.equalsIgnoreCase("CORE")){  //For app
                Url = "https://matmtest.iserveu.website/updateUserAddress";

            }
            else {
                //For SDK user
                Url = "https://matmtest.iserveu.website/updateUserAddress/"+session.getUserName();

            }
            AndroidNetworking.post(Url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .addHeaders("Authorization", token)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideLoader();
                            try {
                                JSONObject obj = new JSONObject(response.toString());

                                String status = obj.getString("status");
                                String statusDesc = obj.getString("statusDesc");
                                if (status.equalsIgnoreCase("0")) {

                                    Intent intent = new Intent(LocationActivity.this, PosActivity.class);
                                    startActivityForResult(intent, SdkConstants.REQUEST_CODE);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();


                                } else {
                                    Toast.makeText(LocationActivity.this, statusDesc, Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }
    }

    private void getUserAuthToken() {

        showLoader();

        String url = SdkConstants.BASE_URL + "/api/getAuthenticateData";
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
                                    hideLoader();
                                    viewUserPropAddress();
                                    showLoader();

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

    public void showLoader() {
        if (loadingView == null) {
            loadingView = new ProgressDialog(LocationActivity.this);
            loadingView.setCancelable(false);
            loadingView.setMessage("Please Wait..");
        }
        loadingView.show();
    }

    public void hideLoader() {
        try {
            if (loadingView != null) {
                loadingView.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public void showAlert(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
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


}