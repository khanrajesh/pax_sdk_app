package com.matm.matmsdk.aepsmodule.aadharpay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.GpsTracker;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.aadharpay.Model.AadharpayRequest;
import com.matm.matmsdk.aepsmodule.aadharpay.Model.AadharpayResponse;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameListActivity;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameModel;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.notification.NotificationHelper;
import com.moos.library.HorizontalProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;

import isumatm.androidsdk.equitas.R;


/**
 * @author Rashmi Ranjan
 * @date 26th:mar:2021
 */
public class AadharpayActivity extends AppCompatActivity {

    final static String MARKER = "|"; // filtered in layout not to be in the string
    public EditText et_aadharNumber, et_mobileNumber, et_bankSpinner, et_enteredAmount, et_virtualID;
    public ImageView iv_aadhaar, iv_virtualID, iv_fingerprint_BIG;
    public TextView tv_underAdhaarIV, tv_underVartualID, depositNote, fingerprintStrengthDeposit;
    public Button btn_submit;
    public String aadharNumberMain = "";
    public String bankName_ = "";
    public String bankIINNumber = "";
    ProgressDialog loadingView;
    AppCompatCheckBox terms;
    Boolean flagFromDriver = false;
    Boolean adharbool = true;
    Boolean virtualbool = false;
    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    boolean mInside = false;
    TypedValue typedValue;
    Resources.Theme theme;
    Class driverActivity;
    String flagNameRdService = "";
    Session session;

    AadharpayRequest aadharpayRequest;
    AadharpayResponse aadharpayResponse = new AadharpayResponse();
    NotificationHelper notificationHelper;
    private HorizontalProgressView depositBar;

    SharedPreferences sp;
    public static final String MATM_PREF = "matmPref";
    public static final String MATM_USER = "userPref";
    public static final String MATM_TOKEN = "tokenPref";
    public static final String MATM_TIME = "timePref";
    private static final String pattern = "dd-MM-yyyy HH:mm:ss";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this);// to apply custom themes
        setContentView(R.layout.activity_aeps3_home);

//        if(SdkConstants.paramA.equals("")){
//            showAlert("ParamA is missing.\nContact to your admin");
//        }

        loadingView = new ProgressDialog(AadharpayActivity.this);
        et_aadharNumber = findViewById(R.id.et_aadharNumber);
        et_mobileNumber = findViewById(R.id.et_mobileNumber);
        et_bankSpinner = findViewById(R.id.et_bankSpinner);
        et_enteredAmount = findViewById(R.id.et_enteredAmount);
        iv_aadhaar = findViewById(R.id.iv_aadhaar);
        iv_virtualID = findViewById(R.id.iv_virtualID);
        iv_fingerprint_BIG = findViewById(R.id.iv_fingerprint);
        btn_submit = findViewById(R.id.btn_submitButton);
        et_virtualID = findViewById(R.id.et_aadharVirtualID);
        tv_underAdhaarIV = findViewById(R.id.tv_aadharText);
        tv_underVartualID = findViewById(R.id.tv_virtualidText);
        depositBar = findViewById(R.id.depositBar);
        depositBar.setVisibility(View.GONE);
        depositNote = findViewById(R.id.depositNote);
        fingerprintStrengthDeposit = findViewById(R.id.tv_fingerprintStrengthDeposit);
        terms = findViewById(R.id.terms);

        iv_fingerprint_BIG.setEnabled(false);
        typedValue = new TypedValue();
        theme = this.getTheme();
        session = new Session(AadharpayActivity.this);
        notificationHelper = new NotificationHelper(this);


        /** to enable the amount field, SdkConstants.editable = true.
         *  by default value is false
         *  **/
        et_enteredAmount.setEnabled(SdkConstants.editable);
        et_enteredAmount.setText(SdkConstants.transactionAmount);

        sp = getSharedPreferences(MATM_PREF, Context.MODE_PRIVATE);

        /**onFailed scenario data should not be reset*/
        if (getIntent().getStringExtra("transactionStatus") != null && getIntent().getStringExtra("transactionStatus").equals("FAILED")) {
            et_aadharNumber.setText(makePrettyString(SdkConstants.AADHAAR_NUMBER));
            et_bankSpinner.setText(SdkConstants.BANK_NAME);
            et_mobileNumber.setText(SdkConstants.MOBILENUMBER);
            bankIINNumber = SdkConstants.IIN_NUMBER;
            bankName_ = SdkConstants.BANK_NAME;
            aadharNumberMain = SdkConstants.AADHAAR_NUMBER;

            iv_fingerprint_BIG.setEnabled(true);
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            iv_fingerprint_BIG.setColorFilter(color);

        }

        getRDServiceClass();// to get the driver Activity from SDK Constants
        getPublicIp();// To get the public IP

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        /**  SdkConstants.isSL is required to call the api so if the sdk is used by our core app then it should be false,
         * or else if the sdk is used by SDK client then it should be true*/
        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            SdkConstants.isSL = false; //As this is a double layer application
        } else {

           /* String prefUser = sp.getString(MATM_USER, "");
            if (prefUser.equals("") || !prefUser.equals(SdkConstants.loginID)){
                //fetchToken
                getSdkToken();
            } else {
                String oldDate = sp.getString(MATM_TIME, "");
                Date newDate = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                if (expired(oldDate, formatter.format(newDate))){
                    getSdkToken();
                } else {
                    SdkConstants.isSL = true;//As this is a single layer application
                    SdkConstants.token = sp.getString(MATM_TOKEN, "");
                    session.setUserToken(SdkConstants.token);
                    session.setUsername(SdkConstants.loginID);
                }
            }*/

            SdkConstants.isSL = true;//As this is a single layer application
            getUserAuthToken(); // To generate token for sdk users
        }

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        iv_virtualID.setBackgroundResource(R.drawable.ic_language);
        tv_underVartualID.setTextColor(getResources().getColor(R.color.grey));
        iv_aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);


        /**EditText aadhaarNumber validation**/
        et_aadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mKeyListenerSet) {
                    et_aadharNumber.setOnKeyListener((v, keyCode, event) -> {
                        try {
                            mWannaDeleteHyphen = (keyCode == KeyEvent.KEYCODE_DEL
                                    && et_aadharNumber.getSelectionEnd() - et_aadharNumber.getSelectionStart() <= 1
                                    && et_aadharNumber.getSelectionStart() > 0
                                    && et_aadharNumber.getText().toString().charAt(et_aadharNumber.getSelectionEnd() - 1) == '-');
                        } catch (IndexOutOfBoundsException e) {
                            // never to happen because of checks
                        }
                        return false;
                    });
                    mKeyListenerSet = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mInside) // to avoid recursive calls
                    return;
                mInside = true;

                int currentPos = et_aadharNumber.getSelectionStart();
                String string = et_aadharNumber.getText().toString().toUpperCase();
                String newString = makePrettyString(string);

                if (count == 14) {
                    iv_fingerprint_BIG.setEnabled(true);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    iv_fingerprint_BIG.setColorFilter(color);
                }

                et_aadharNumber.setText(newString);
                try {
                    et_aadharNumber.setSelection(getCursorPos(string, newString, currentPos, mWannaDeleteHyphen));
                } catch (IndexOutOfBoundsException e) {
                    et_aadharNumber.setSelection(et_aadharNumber.length());
                }
                mWannaDeleteHyphen = false;
                mInside = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    et_aadharNumber.setError(getResources().getString(R.string.aadhaarnumber));
                }
                if (s.length() > 0) {
                    et_aadharNumber.setError(null);
                    String aadharNo = et_aadharNumber.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        aadharNumberMain = aadharNo;
                        if (aadharNumberMain.length() >= 12) {
                            iv_fingerprint_BIG.setEnabled(true);
                            //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            iv_fingerprint_BIG.setColorFilter(color);
                            et_aadharNumber.clearFocus();
                            et_mobileNumber.requestFocus();
                            makeAadharNonReadable();
                        }
                    }
                    if (!Util.validateAadharNumber(aadharNumberMain)) {
                        et_aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                    }
                }
            }
        });


        /**EditText MobileNumber validation*/
        et_mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0 || s.length() < 10) {
                    et_mobileNumber.setError(null);
                    String x = s.toString();
                    if (x.startsWith("0") || !Util.isValidMobile(et_mobileNumber.getText().toString().trim())) {
                        et_mobileNumber.setError(getResources().getString(R.string.mobilevaliderror));
                    }
                } else {

                }
            }
        });


        /**BankSPinner apicall*/
        et_bankSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AadharpayActivity.this, BankNameListActivity.class);
                startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_AADHAARPAY_CODE);
            }
        });

        /**EditText amount validation*/
        et_enteredAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    et_enteredAmount.setError(getResources().getString(R.string.amount_error));
                }
                if (s.length() > 0) {
                    et_enteredAmount.setError(null);
                }
            }
        });

/** This event is a default.
 * when someone initiate the transaction via AadhaarNumber */
        iv_aadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_aadharNumber.setVisibility(View.VISIBLE);
                et_virtualID.setVisibility(View.GONE);
                iv_virtualID.setEnabled(true);
                iv_aadhaar.setEnabled(false);
                iv_virtualID.setBackgroundResource(R.drawable.ic_language);
                tv_underVartualID.setTextColor(getResources().getColor(R.color.grey));
                adharbool = true;
                virtualbool = false;
                iv_aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                tv_underAdhaarIV.setTextColor(color);

            }
        });

        /** This event is for if someone wants to do transaction via VID instate of AadhaarNumber*/
        iv_virtualID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_aadharNumber.setVisibility(View.GONE);
                et_virtualID.setVisibility(View.VISIBLE);
                iv_virtualID.setEnabled(false);
                iv_aadhaar.setEnabled(true);
                virtualbool = true;
                adharbool = false;
                iv_virtualID.setBackgroundResource(R.drawable.ic_language_blue);
                //virtualidText.setTextColor(getResources().getColor(R.color.colorPrimary));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                tv_underVartualID.setTextColor(color);
                iv_aadhaar.setBackground(getResources().getDrawable(R.drawable.ic_fingerprint_grey));
                tv_underAdhaarIV.setTextColor(getResources().getColor(R.color.grey));
            }
        });


        /** e This event is for capturing the user fingerprint */
        iv_fingerprint_BIG.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                hideKeyboard();
                iv_fingerprint_BIG.setEnabled(false);
                //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner_grey));
                iv_fingerprint_BIG.setColorFilter(R.color.colorGrey);
                flagFromDriver = true;


                Intent launchIntent = new Intent(AadharpayActivity.this, driverActivity);
                launchIntent.putExtra("driverFlag", flagNameRdService);
                launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                launchIntent.putExtra("AadharNo", aadharNumberMain);
                startActivityForResult(launchIntent, 1);
            }
        });

        /** there is a terms and condition check in sdk (for now this is hidden) */
        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SdkConstants.firstCheck = true;
                showTermsDetails(AadharpayActivity.this);
            }
        });

        /** After clicking the submit button the api call and the transacion initiated here. with all the input validaions */
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String AadhaarNumber = et_aadharNumber.getText().toString();
                String AadhaarNumber = aadharNumberMain;
                String _mobileNumber = "";
                if (adharbool) {
//                    if (AadhaarNumber.contains("-")) {
//                        AadhaarNumber = AadhaarNumber.replaceAll("-", "").trim();
                    if (AadhaarNumber != null || !AadhaarNumber.matches("")) {
                        if (AadhaarNumber.length() == 12) {
                            if (Util.validateAadharNumber(AadhaarNumber)) {
//                                    aadharNumberMain = et_aadharNumber.getText().toString().trim();
                                if (!et_mobileNumber.getText().toString().isEmpty() && et_mobileNumber.getText().toString() != null && !et_mobileNumber.getText().toString().trim().matches("") && Util.isValidMobile(et_mobileNumber.getText().toString().trim())) {
                                    _mobileNumber = et_mobileNumber.getText().toString().trim();
                                    if (!et_bankSpinner.getText().toString().isEmpty() && et_bankSpinner.getText().toString() != null && !et_bankSpinner.getText().toString().trim().matches("")) {
                                        String _bankName = et_bankSpinner.getText().toString();
//                                        JSONObject respObj = null;

                                        if (flagFromDriver) {
                                            showAadhaarPayServiceChargesDialog(AadharpayActivity.this);

/*                                            try {
                                                showLoader();
                                                respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                                String aadhaarsha = Util.getSha256Hash(AadhaarNumber);
                                                String base64pidData = respObj.getString("base64pidData");
                                                aadharpayRequest = new AadharpayRequest(AadhaarNumber, bankIINNumber, "ANDROID", getLocation(), getPublicIp(), et_enteredAmount.getText().toString().trim(), _mobileNumber, SdkConstants.loginID, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, SdkConstants.isSL, bankName_.trim(), base64pidData, aadhaarsha);

                                                SdkConstants.AADHAAR_NUMBER = AadhaarNumber;
                                                SdkConstants.IIN_NUMBER = bankIINNumber;
                                                SdkConstants.MOBILENUMBER = et_mobileNumber.getText().toString().trim();
                                                SdkConstants.BANK_NAME = et_bankSpinner.getText().toString();

                                                if (SdkConstants.firstCheck) {
                                                    checkVPNstatusForTransaction(); // before transaction initiate it is checking for VPA connections
                                                } else {
                                                    Toast.makeText(AadharpayActivity.this, "Accept/Enable the Terms & Conditions for the further transaction", Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }*/
                                        } else {
                                            Toast.makeText(AadharpayActivity.this, "Please do Biometric Varification", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        et_bankSpinner.setError(getResources().getString(R.string.select_bank_error));
                                    }
                                } else {
                                    et_mobileNumber.setError(getResources().getString(R.string.mobileerror));
                                }
                            } else {
                                et_aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                            }
                        } else {
                            et_aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        }
                    } else {
                        et_aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                    }
                    // }
                } else if (virtualbool) {
                    String vartual_Id = et_virtualID.getText().toString();
                    if (vartual_Id.contains("-")) {
                        vartual_Id = vartual_Id.replaceAll("-", "").trim();
                    }
                    if (vartual_Id == null || vartual_Id.matches("")) {
                        et_virtualID.setError(getResources().getString(R.string.valid_vid_error));
                        return;
                    }
                    if (!Util.validateAadharNumber(vartual_Id)) {
                        et_virtualID.setError(getResources().getString(R.string.valid_aadhar_error));
                    }
                }


            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();

        /**This flag will be true when the finger print got successfully captured from driver activity.*/

        if (flagFromDriver) {
            if (SdkConstants.RECEIVE_DRIVER_DATA.isEmpty()) {
                // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                iv_fingerprint_BIG.setColorFilter(color);
                iv_fingerprint_BIG.setEnabled(true);
                btn_submit.setBackgroundResource(R.drawable.button_submit);
                btn_submit.setEnabled(false);
            } else if (aadharNumberMain.equalsIgnoreCase("") || aadharNumberMain.isEmpty()) {
                et_aadharNumber.setError("Enter Aadhar No.");
                fingerStrength();
            } else if (et_mobileNumber.getText().toString().isEmpty() || et_mobileNumber.getText().toString().equalsIgnoreCase("")) {
                et_mobileNumber.setError("Enter mobile no.");
                fingerStrength();
            } else if (et_bankSpinner.getText().toString().isEmpty() || et_bankSpinner.getText().toString().trim().equalsIgnoreCase("")) {
                et_bankSpinner.setError("Choose your bank.");
                fingerStrength();
            } else {
                fingerStrength();
                //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.F));
                iv_fingerprint_BIG.setColorFilter(R.color.colorGrey);
                iv_fingerprint_BIG.setEnabled(false);

            }

        }

    }

    /**
     * This method will invoked after successfully capturing the
     * finGerprint data to show the fingerprint strength on finger deposit bAR
     */
    public void fingerStrength() {
        hideLoader();
        try {
            JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
            String scoreStr = respObj.getString("pidata_qscore");

            if (scoreStr.contains(",")) {
                hideLoader();
                showAlert("Invalid Fingerprint Data");
            } else {
                btn_submit.setEnabled(true);
                btn_submit.setBackgroundResource(R.drawable.button_submit_blue);

                if (Float.parseFloat(scoreStr) <= 30) {
                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.red));
                    depositBar.setStartColor(getResources().getColor(R.color.red));
                    depositNote.setVisibility(View.VISIBLE);
                    fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
                } else if (Float.parseFloat(scoreStr) >= 30 && Float.parseFloat(scoreStr) <= 60) {

                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.yellow));
                    depositBar.setStartColor(getResources().getColor(R.color.yellow));
                    depositNote.setVisibility(View.VISIBLE);
                    fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
                } else {

                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.green));
                    depositBar.setStartColor(getResources().getColor(R.color.green));
                    depositNote.setVisibility(View.VISIBLE);
                    fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            showAlert("Invalid Fingerprint Data");
        }
    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void getUserAuthToken() {
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
                                    String userName = obj.getString("username");
                                    String userToken = obj.getString("usertoken");
                                    session.setUsername(userName);
                                    session.setUserToken(userToken);
                                    hideLoader();

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
                            showAlert(anError.getErrorDetail());

                        }

                    });
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }
    }

    private void getRDServiceClass() {
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;//getIntent().getStringExtra("activity");
        flagNameRdService = SdkConstants.MANUFACTURE_FLAG;//getIntent().getStringExtra("driverFlag");
        try {
            driverActivity = Class.forName(accessClassName).asSubclass(Activity.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getPublicIp() {
        String publicIP_ = "";
        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            publicIP_ = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            if (publicIP_.equalsIgnoreCase("0.0.0.0")) {
                publicIP_ = getLocalIpAddress();
                Log.e("TAG", "initView: getLocalIpAddress::" + publicIP_);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicIP_;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("TAG", "***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("TAG", ex.toString());
        }
        return null;
    }

    private void checkVPNstatusForTransaction() {
        AndroidNetworking.get("https://vpn.iserveu.online/vpn/telnet_checkVpn")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String status = jsonObject.getString("status");
                            if (status.equalsIgnoreCase("0")) {
                                CallAadharPayApi(); // this function is for aadhaarpay api call and initiate the transactions
//                                SdkConstants.firstCheck = false; // For now this is hidden to enable the terms check please remove comment from here
                            } else {
                                hideLoader();
                                showAlert("There is a VPN running in the background, You are not allowed to initiate the transactions !!");
                            }
                        } catch (JSONException e) {
                            hideLoader();
                            showAlert("Something went wrong please try again latter ..!!!");
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoader();
                        showAlert(anError.getErrorBody());
                    }
                });
    }


    /**
     * This function is for Aadhaarpay api call and initiate the transactions
     */
    private void CallAadharPayApi() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(aadharpayRequest); // to convert the Model class to JSONObject
            JSONObject jsonObject = new JSONObject(jsonString);// to convert the Model class to JSONObject
            System.out.println(jsonString);
            System.out.println(session.getUserToken());

            AndroidNetworking.post(Constants.LIVE_URL + "aadhaarPay")
                    .addHeaders("Authorization", session.getUserToken())
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {

                        @Override
                        public void onResponse(JSONObject response) {
                            hideLoader();
                            Log.e("TAG", "onResponse: " + response.toString());
                            try {
                                aadharpayResponse = new AadharpayResponse(
                                        response.getString("transactionStatus"),
                                        response.getInt("status"),
                                        response.getString("txId"),
                                        response.getString("errors"), et_enteredAmount.getText().toString().trim(), bankName_.trim(), et_mobileNumber.getText().toString().trim());

                                Gson gson = new Gson();
                                String myJson = gson.toJson(aadharpayResponse); // Converting Model class to JSON to intent to receipt activity

                                Intent intent = new Intent(AadharpayActivity.this, AadhaarpayReceiptActivity.class);
                                intent.putExtra("myjson", myJson);// Intending the Aadhaarpay response to receipt Activity
                                startActivity(intent);
                                finish();

                            } catch (JSONException e) {
                                showAlert("Something went wrong Please try again latter !!");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hideLoader();
                            Log.e("TAG", "onResponse: " + anError.getErrorBody());

                            if (anError.getErrorCode() == 400) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(anError.getErrorBody());
                                    if (!jsonObject1.equals("") || jsonObject1 != null) {
                                        String transactionStatus = jsonObject1.getString("transactionStatus");
                                        int status = jsonObject1.getInt("status");
                                        String txId = jsonObject1.getString("txId");
                                        String errors = jsonObject1.getString("errors");
                                        aadharpayResponse = new AadharpayResponse(transactionStatus, status, txId, errors, et_enteredAmount.getText().toString().trim(), bankName_.trim(), et_mobileNumber.getText().toString().trim());
                                        Intent intent2 = new Intent(AadharpayActivity.this, AadhaarpayReceiptActivity.class);
                                        startActivity(intent2);
                                        finish();
                                    } else {
                                        showAlert("Something went wrong , please try again.");
                                    }
                                } catch (JSONException e) {
                                    showAlert("Something went wrong , please try again.");
                                    e.printStackTrace();
                                }
                            } else {
                                showAlert("Something went wrong , please try again.");
                            }
                        }
                    });

        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public void showLoader() {
        loadingView.setMessage("Please wait..");
        loadingView.setCancelable(false);
        loadingView.show();
    }

    public void hideLoader() {
        try {
            if (!isFinishing()) {
                if (loadingView != null) {
                    loadingView.dismiss();
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * To convert the AadhaarNumber on a 4-4-4 Pattern
     */
    private String makePrettyString(String string) {
        String number = string.replaceAll("-", "");
        boolean isEndHyphen = string.endsWith("-") && (number.length() % 4 == 0);
        return number.replaceAll("(.{4}(?!$))", "$1-") + (isEndHyphen ? "-" : "");
    }


    private int getCursorPos(String oldString, String newString, int oldPos, boolean isDeleteHyphen) {
        int cursorPos = newString.length();
        if (oldPos != oldString.length()) {
            String stringWithMarker = oldString.substring(0, oldPos) + MARKER + oldString.substring(oldPos);

            cursorPos = (makePrettyString(stringWithMarker)).indexOf(MARKER);
            if (isDeleteHyphen)
                cursorPos -= 1;
        }
        return cursorPos;
    }

    public void showAlert(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AadharpayActivity.this);
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

    /**
     * To fetch the current lat long
     */
    public String getLocation() {
        String latLong = "";
        GpsTracker gpsTracker = new GpsTracker(AadharpayActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            latLong = latitude + "," + longitude;
        } else {
            gpsTracker.showSettingsAlert();
        }
        return latLong;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_AADHAARPAY_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                assert bankIINValue != null;
                et_bankSpinner.setText(bankIINValue.getBankName());
                bankName_ = bankIINValue.getBankName();
                bankIINNumber = bankIINValue.getIin();
                checkAadhaarPayValidation();
            }
            checkAadhaarPayValidation();
        }
    }

    private void checkAadhaarPayValidation() {
        // TODO Auto-generated method stub
        if (et_mobileNumber.getText() != null
                && !et_mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(et_mobileNumber.getText().toString().trim())
                && et_mobileNumber.getText().toString().length() == 10
                && et_bankSpinner.getText() != null
                && !et_bankSpinner.getText().toString().trim().matches("")
                && et_enteredAmount.getText() != null
                && !et_enteredAmount.getText().toString().trim().matches("")) {

            boolean status = false;
            if (adharbool) {
//                String aadharNo = et_aadharNumber.getText().toString();
//                if (aadharNo.contains("-")) {
//                    aadharNo = aadharNo.replaceAll("-", "").trim();
//                    status = Util.validateAadharNumber(aadharNo);
//                }
            } else if (virtualbool) {
                String aadharVid = et_virtualID.getText().toString();
                if (aadharVid.contains("-")) {
                    aadharVid = aadharVid.replaceAll("-", "").trim();
                    status = Util.validateAadharVID(aadharVid);
                }
            }
            if (status) {

            }
        } else {
            btn_submit.setEnabled(false);
            btn_submit.setBackground(getResources().getDrawable(R.drawable.button_submit));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    /**
     * Term and conditions  by @Subhashree
     */
    public void showTermsDetails(Activity activity) {
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.activity_terms_conditions);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


            TextView firstText = (TextView) dialog.findViewById(R.id.firststText);
            TextView secondText = (TextView) dialog.findViewById(R.id.secondstText);

            SwitchCompat switchCompat = (SwitchCompat) dialog.findViewById(R.id.swOnOff);

            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        firstText.setText(getResources().getString(R.string.hinditm1));
                        secondText.setText(getResources().getString(R.string.hinditm2));

                    } else {
                        firstText.setText(getResources().getString(R.string.term1));
                        secondText.setText(getResources().getString(R.string.term2));

                    }
                }
            });

            Button dialogBtn_close = (Button) dialog.findViewById(R.id.close_Btn);
            dialogBtn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.cancel();


                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AadharpayActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void makeAadharNonReadable() {
        String maskAadhaar = "XXXX XXXX " + aadharNumberMain.substring(8);
        et_aadharNumber.setText(maskAadhaar);
    }

    private void showAadhaarPayServiceChargesDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.aadhaarpay_service_charges_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

//        String enteredAmount=SdkConstants.transactionAmount;
        String enteredAmount = et_enteredAmount.getText().toString();
        TextView tvActualEnteredAmt = (TextView) dialog.findViewById(R.id.tvActualEnteredAmt);
        tvActualEnteredAmt.setText(enteredAmount);

        int amount = Integer.valueOf(enteredAmount);


        TextView text = (TextView) dialog.findViewById(R.id.tvServiceChargesAmt);
        text.setText(getServiceCharge(amount));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject respObj = null;
                try {
                    showLoader();
                    respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);

                    String aadhaarsha = Util.getSha256Hash(aadharNumberMain);
                    String base64pidData = respObj.getString("base64pidData");
                    aadharpayRequest = new AadharpayRequest(aadharNumberMain, bankIINNumber, "ANDROID", getLocation(), getPublicIp(), et_enteredAmount.getText().toString().trim(), et_mobileNumber.getText().toString().trim(), SdkConstants.loginID, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, SdkConstants.isSL, bankName_.trim(), base64pidData, aadhaarsha);

                    SdkConstants.AADHAAR_NUMBER = aadharNumberMain;
                    SdkConstants.IIN_NUMBER = bankIINNumber;
                    SdkConstants.MOBILENUMBER = et_mobileNumber.getText().toString().trim();
                    SdkConstants.BANK_NAME = et_bankSpinner.getText().toString();

                    if (SdkConstants.firstCheck) {
                        checkVPNstatusForTransaction(); // before transaction initiate it is checking for VPA connections
                    } else {
                        Toast.makeText(AadharpayActivity.this, "Accept/Enable the Terms & Conditions for the further transaction", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    public String getServiceCharge(int Amount) {
        String servChrg = "0";
        if (SdkConstants.SLAB_X.equals("") && SdkConstants.SLAB_Y.equals("")) {
            SdkConstants.SLAB_X = "0.60";
            SdkConstants.SLAB_Y = "0.3";
        }
        if (Amount >= 100 && Amount <= 250)
            servChrg = SdkConstants.SLAB_X;
        else if (Amount > 250) {
            double slabY = Double.valueOf(SdkConstants.SLAB_Y);
            servChrg = new DecimalFormat("##.##").format((Amount / 100.0f) * slabY);
        }
        return servChrg;
    }

    private boolean expired(String genDate, String currentDate) {
        boolean exp = true;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date d1 = sdf.parse(genDate);
            Date d2 = sdf.parse(currentDate);
            long difference = d2.getTime() - d1.getTime();
            long diffHours = (difference / (1000 * 60 * 60));
            if (diffHours <= 672) {
                exp = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return exp;
    }

    private void getSdkToken() {
        loadingView.show();
        String url = "https://coreuat-zwqcqy3qmq-el.a.run.app/api/getAuthenticateData";
        JSONObject obj = new JSONObject();
        try {
            obj.put("encryptedData", SdkConstants.encryptedData);
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
                                    String userName = obj.getString("username");
                                    String userToken = obj.getString("usertoken");
                                    session.setUsername(userName);
                                    session.setUserToken(userToken);

                                    Date d = new Date();
                                    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(MATM_USER, userToken);
                                    editor.putString(MATM_TOKEN, SdkConstants.loginID);
                                    editor.putString(MATM_TIME, formatter.format(d));
                                    editor.apply();

                                    hideLoader();

                                } else {
                                    showAlert(status);
                                    hideLoader();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                loadingView.dismiss();
                                showAlert("Invalid Encrypted Data");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            loadingView.dismiss();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
