package com.pax.pax_sdk_app.login;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.Utils.SdkConstants;
import com.pax.pax_sdk_app.MainActivity;
import com.pax.pax_sdk_app.R;
import com.pax.pax_sdk_app.SessionManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    public static final String ISU_PREF = "isuPref";
    public static final String USER_NAME = "userNameKey";
    public static final String USER_MPIN = "mpinKey";
    public static final String ADMIN_NAME = "adminNameKey";
    public static final String MULTI_ADMINS = "multiAdminKey";
    public static final String CREATED_BY = "createdKey";
    TextInputEditText user_name, password;
    Button submit;
    SessionManager session;
    String _user_name, _password;
    ProgressBar progressBar;
    LoginPresenter loginPresenter;
    TextView forgotpassword;
    TextInputLayout edit_layout_mobileNo, edit_layout_password;
    SharedPreferences sp;
    String firestoreAdmin;
    String firestoreCreated;
    String _message;
    TextView create_new_user;

    Boolean CrashTest = false;//Will be true when setting up crashlytics



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this);

        setContentView(R.layout.activity_login);

        user_name = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        progressBar = findViewById(R.id.progressBar);
        forgotpassword = findViewById(R.id.forgotPassword);
        edit_layout_mobileNo = findViewById(R.id.edit_layout_mobileNo);
        edit_layout_password = findViewById(R.id.edit_layout_password);
        create_new_user = findViewById(R.id.create_new_user);
        /*Added by RAshmi RAnjan*/
      /*  edit_layout_mobileNo.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(this, R.color.text_input_layout_stroke_color));
        edit_layout_password.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(this, R.color.text_input_layout_stroke_color));
*/

        sp = getSharedPreferences(ISU_PREF, Context.MODE_PRIVATE);
        firestoreAdmin = sp.getString(ADMIN_NAME, "");
        firestoreCreated = sp.getString(CREATED_BY, "");

        loginPresenter = new LoginPresenter(this);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        create_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, OnboardActivity.class);
                startActivity(i);
            }
        });
        // Session Manager
        session = new SessionManager(getApplicationContext());

        if (LoginConstants.onBoardStatus.equals("0")) {
            create_new_user.setVisibility(View.GONE);
        } else {
            create_new_user.setVisibility(View.VISIBLE);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //new add code

                if (CrashTest == true) {
                    throw new RuntimeException("This is a crash test");
                } else {


                    _user_name = user_name.getText().toString().trim();
                    _password = password.getText().toString().trim();

                    if (_user_name.length() != 0) {

                        if (_password.length() != 0) {
                            progressBar.setVisibility(View.VISIBLE);
                            loginPresenter.getV1Response("https://itpl.iserveu.tech/generate/v1/");
                        } else {
                            Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_LONG).show();

                        }
                        //checkSessionExistance(_user_name.toLowerCase());
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter user name", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

//        create_new_user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean installed = appInstalledOrNot("com.isu.service");
//
//                try {
//                    if (installed) {
//                        PackageManager manager = getPackageManager();
//                        Intent sIntent = manager.getLaunchIntentForPackage("com.isu.service");
//                        sIntent.putExtra("ActivityName", "SelfOnBoard");
//                        sIntent.putExtra("AdminName", firestoreAdmin);
//                        sIntent.putExtra("CreatedBy", firestoreCreated);
//                        sIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                        startActivity(sIntent);
//
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Please install the service app", Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
//                }
//
//
//            }
//        });

    }//oncreate

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

   /* private void loadLogin(String base64) {

        Log.i("base64 :url ::: ", base64);

        JSONObject js = new JSONObject();
        try {submit
            js.put("username", _user_name);
            js.put("password", _password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make request for JSONObject
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, base64, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("response :: ", "" + response);
                            String _token = response.getString("token");
                            String _admin = response.getString("adminName");

                            // Use user real data
                            session.createLoginSession(_token, _admin);

                            progressBar.setVisibility(View.GONE);

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
            }
        }) {

            */

    /**
     * Passing some request headers
     *//*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        // Adding request to request queue
        Volley.newRequestQueue(this).add(jsonObjReq);


    }*/


    private void loadLogin(String base64) {



        JSONObject obj = new JSONObject();
        try {
            obj.put("username", _user_name);
            obj.put("password", _password);
            AndroidNetworking.post(base64)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e(TAG, "onResponse: " + response);
                                JSONObject obj = new JSONObject(response.toString());
                                String _token = obj.getString("token");
                                String _admin = obj.getString("adminName");
                                Log.e(TAG, "Token: " + _token);

                                // Use user real data
                                session.createLoginSession(_token, _admin);
                                progressBar.setVisibility(View.INVISIBLE);

                                SdkConstants.tokenFromCoreApp = _token;
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.putExtra("token", _token);
                                startActivity(i);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                            // showSessionAlert(_message);
                            // Toast.makeText(LoginActivity.this, "Incorrect login and password", Toast.LENGTH_LONG).show();
                            try {
                                progressBar.setVisibility(View.INVISIBLE);
                                // dialog.dismiss();
                                JSONObject errorObject = new JSONObject(anError.getErrorBody());

                                Log.d(TAG, "isUpdatedResponse: " + errorObject.toString());

                                String status = errorObject.optString("status");
                                String message = errorObject.optString("message");
                                if (message.equals("Incorrect username or password"))
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                                // Toast.makeText(LoginActivity.this, statusDescription, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fetchedV1Response(boolean status, String response) {
        if (response != null) {
            loadLogin(response);
        }
    }

    private void loginWithMpin(String uN, String pin) {
        AndroidNetworking.get("https://usermpin.iserveu.tech/generate/v101")
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
                            // {"transactionType":"AEPS","fromDate":"2020-03-06","toDate":"2020-03-07"}
                            encryptedUrl(uN, pin, encodedUrl);

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


    private void encryptedUrl(String uN, String pin, String encodeUrl) {
        try {
            ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();

            // String url = "https://us-central1-creditapp-29bf2.cloudfunctions.net/user_mpin/login";
            JSONObject obj = new JSONObject();

            try {
                obj.put("token", uN);
                obj.put("m_pin", pin);

                AndroidNetworking.post(encodeUrl)
                        .setPriority(Priority.HIGH)
                        .addJSONObjectBody(obj)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse (JSONObject response) {
                                try {
                                    int status = response.getInt("status");
                                    String message = response.getString("message");

                                    if (status == 1) {
                                        //load main activity
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString(USER_MPIN, pin);
                                        editor.apply();

                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    dialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onError (ANError anError) {
                                try {

                                    // 0 mismatch
                                    // -1 expired
                                    // 1 success
                                    JSONObject errorObject = new JSONObject(anError.getErrorBody());

                                    int status = errorObject.getInt("status");

                                    if (status == 0) {
                                        String message = "MPIN has been changed.\nEnter the correct one or regenerate.";
                                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                    } else if (status == -1) {
                                        String message = "MPIN has been expired.\nRegenrated the MPIN.";
                                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                    }


                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.putExtra("token", uN);
                                    startActivity(i);
                                    finish();

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(USER_MPIN, "");
                                    editor.apply();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDeviceID() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }



    /*private void checkSessionExistance(String user_name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //asynchronously retrieve all documents

        DocumentReference docRef = db.collection("CoreApp_Session_Manager").document(user_name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            if (document.contains("login_status")) {

                                String value = document.getData().get("login_status").toString();

                                if (value.equalsIgnoreCase("false")) {
                                    updateSession(user_name);

                                } else {
                                    showSessionAlert(""); // Toast.makeText(SplashActivity.this, "", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        updateSession(user_name);
                    }
                } else {

                }
            }
        });
    }
    }*/


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }



}
