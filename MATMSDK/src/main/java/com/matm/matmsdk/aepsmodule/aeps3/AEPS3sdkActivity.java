package com.matm.matmsdk.aepsmodule.aeps3;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.GpsTracker;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAEPS3RequestModel;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameListActivity;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameModel;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.notification.NotificationHelper;
import com.moos.library.HorizontalProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import isumatm.androidsdk.equitas.R;

/**
 * @author Rashmi Ranjan
 * @date 4th Jan
 * With different attributes used in API
 */
public class AEPS3sdkActivity extends AppCompatActivity {

    EditText et_aadharNumber, et_aadharVirtualID, et_mobileNumber, et_bankSpinner, et_enteredAmount;
    ImageView iv_fingerprint, iv_aadhaar, iv_virtualID;
    TextView tv_fingerprintStrengthDeposit, tv_cashWithdrawalHeadText, tv_balanceEnquiryHeadText, tv_miniStatementHeadText, tv_aadhaarPayHeadText,
            tv_virtualidText, tv_aadharText, depositNote;
    Button btn_submitButton;
    Toolbar toolbar;

    public FirebaseMessaging firebaseMessaging;
    String latLong = "", bankIINNumber = "", flagNameRdService = "",
            balanaceInqueryAadharNo = "", vid = "", uid = "", balanceaadharNo = "",
            balanceaadharVid = "", bankName_ = "", MobileNumber;

    ProgressDialog loadingView;
    private HorizontalProgressView depositBar;

    Class driverActivity;
    Session session;

    Resources.Theme theme;
    TypedValue typedValue;

    final static String MARKER = "|"; // filtered in layout not to be in the string
    int i = 5;

    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    boolean mInside = false;
    Boolean flagFromDriver = false;
    Boolean adharbool = true;
    Boolean virtualbool = false;

    String publicIP_;


    BalanceEnquiryAEPS3RequestModel balanceEnquiryAEPS3RequestModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this);
        setContentView(R.layout.activity_aeps3_home);

        initView();


        NotificationHelper notificationHelper = new NotificationHelper(this);

        // publicIP_ = getPublicIPAddress();


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            SdkConstants.isSL = false;
        } else {
            SdkConstants.isSL = true;
            getUserAuthToken();
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        iv_fingerprint.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                showLoader();
                iv_fingerprint.setEnabled(false);
                iv_fingerprint.setColorFilter(R.color.colorGrey);
                flagFromDriver = true;

                Intent launchIntent = new Intent(AEPS3sdkActivity.this, driverActivity);
                launchIntent.putExtra("driverFlag", flagNameRdService);
                launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                launchIntent.putExtra("AadharNo", balanaceInqueryAadharNo);
                startActivityForResult(launchIntent, 1);
            }
        });

        iv_virtualID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Will implement when VID will used*/
                et_aadharNumber.setVisibility(View.GONE);
                et_aadharVirtualID.setVisibility(View.VISIBLE);
                iv_virtualID.setEnabled(false);
                iv_aadhaar.setEnabled(true);
                virtualbool = true;
                adharbool = false;
                iv_virtualID.setBackgroundResource(R.drawable.ic_language_blue);
                //virtualidText.setTextColor(getResources().getColor(R.color.colorPrimary));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                tv_virtualidText.setTextColor(color);
                iv_aadhaar.setBackground(getResources().getDrawable(R.drawable.ic_fingerprint_grey));
                tv_aadharText.setTextColor(getResources().getColor(R.color.grey));
            }
        });

        iv_aadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_aadharNumber.setVisibility(View.VISIBLE);
                et_aadharVirtualID.setVisibility(View.GONE);
                iv_virtualID.setEnabled(true);
                iv_aadhaar.setEnabled(false);
                iv_virtualID.setBackgroundResource(R.drawable.ic_language);
                tv_virtualidText.setTextColor(getResources().getColor(R.color.grey));
                adharbool = true;
                virtualbool = false;
                iv_aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                tv_aadharText.setTextColor(color);
                //  aadharText.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });


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

        et_aadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mKeyListenerSet) {
                    et_aadharNumber.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            try {
                                mWannaDeleteHyphen = (keyCode == KeyEvent.KEYCODE_DEL
                                        && et_aadharNumber.getSelectionEnd() - et_aadharNumber.getSelectionStart() <= 1
                                        && et_aadharNumber.getSelectionStart() > 0
                                        && et_aadharNumber.getText().toString().charAt(et_aadharNumber.getSelectionEnd() - 1) == '-');
                            } catch (IndexOutOfBoundsException e) {
                                // never to happen because of checks
                            }
                            return false;
                        }
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

                    iv_fingerprint.setEnabled(true);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    iv_fingerprint.setColorFilter(color);
                    btn_submitButton.setEnabled(true);
                    btn_submitButton.setBackground(getResources().getDrawable(R.drawable.button_submit_blue));
                } else {

                }

                et_aadharNumber.setText(newString);
                try {
                    et_aadharNumber.setSelection(getCursorPos(string, newString, currentPos, mWannaDeleteHyphen));
                } catch (IndexOutOfBoundsException e) {
                    et_aadharNumber.setSelection(et_aadharNumber.length()); // last resort never to happen
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
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            iv_fingerprint.setEnabled(true);
                            //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            iv_fingerprint.setColorFilter(color);
                            et_aadharNumber.clearFocus();
                            et_mobileNumber.requestFocus();
                        }
                    }

                    if (Util.validateAadharNumber(aadharNo) == false) {
                        et_aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                    }
                }
            }

        });


        et_aadharVirtualID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mKeyListenerSet) {
                    et_aadharVirtualID.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            try {
                                mWannaDeleteHyphen = (keyCode == KeyEvent.KEYCODE_DEL
                                        && et_aadharVirtualID.getSelectionEnd() - et_aadharVirtualID.getSelectionStart() <= 1
                                        && et_aadharVirtualID.getSelectionStart() > 0
                                        && et_aadharVirtualID.getText().toString().charAt(et_aadharVirtualID.getSelectionEnd() - 1) == '-');
                            } catch (IndexOutOfBoundsException e) {
                                // never to happen because of checks
                            }
                            return false;
                        }
                    });
                    mKeyListenerSet = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mInside) // to avoid recursive calls
                    return;
                mInside = true;
                int currentPos = et_aadharVirtualID.getSelectionStart();
                String string = et_aadharVirtualID.getText().toString().toUpperCase();
                String newString = makePrettyString(string);
                if (count == 19) {
                    iv_fingerprint.setEnabled(true);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    iv_fingerprint.setColorFilter(color);
                    btn_submitButton.setEnabled(true);
                    btn_submitButton.setBackgroundResource(R.drawable.button_submit_blue);

                }
                et_aadharVirtualID.setText(newString);
                try {
                    et_aadharVirtualID.setSelection(getCursorPos(string, newString, currentPos, mWannaDeleteHyphen));
                } catch (IndexOutOfBoundsException e) {
                    et_aadharVirtualID.setSelection(et_aadharVirtualID.length()); // last resort never to happen
                }

                mWannaDeleteHyphen = false;
                mInside = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    et_aadharVirtualID.setError(getResources().getString(R.string.aadhaarVID));
                }
                if (s.length() > 0) {
                    et_aadharVirtualID.setError(null);
                    String aadharNo = et_aadharVirtualID.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            iv_fingerprint.setEnabled(true);
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            iv_fingerprint.setColorFilter(color);
                            et_aadharVirtualID.clearFocus();
                            et_mobileNumber.requestFocus();

                            //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                        }
                    }
                    if (!Util.validateAadharVID(aadharNo)) {
                        et_aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar__uid_error));
                    }
                }
            }

        });

        et_bankSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AEPS3sdkActivity.this, BankNameListActivity.class);

                if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE);
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE);
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE);
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.adhaarPay)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE);
                }
            }
        });

        et_mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_submitButton.setEnabled(true);
                btn_submitButton.setBackgroundResource(R.drawable.button_submit_blue);
                /*theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                submitButton.setBackgroundColor(color);*/
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
                    btn_submitButton.setEnabled(true);
                    btn_submitButton.setBackgroundResource(R.drawable.button_submit_blue);
                }
            }
        });


        btn_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showLoader();

                balanceaadharNo = et_aadharNumber.getText().toString();
                if (adharbool) {
                    if (balanceaadharNo.contains("-")) {
                        balanceaadharNo = balanceaadharNo.replaceAll("-", "").trim();
                    }
                    if (balanceaadharNo == null || balanceaadharNo.matches("")) {
                        et_aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                    if (!Util.validateAadharNumber(balanceaadharNo)) {
                        et_aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                    if (et_aadharNumber.getText().toString().length() < 14) {
                        et_aadharNumber.setError("Enter valid aadhaar no.");
                        return;
                    }
                    if (!flagFromDriver) {
                        Toast.makeText(AEPS3sdkActivity.this, "Please do Biometric Verification", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else if (virtualbool) {
                    balanceaadharVid = et_aadharVirtualID.getText().toString().trim();
                    if (balanceaadharVid.contains("-")) {
                        balanceaadharVid = balanceaadharVid.replaceAll("-", "").trim();
                    }
                    if (balanceaadharVid == null || balanceaadharVid.matches("")) {
                        et_aadharVirtualID.setError(getResources().getString(R.string.valid_vid_error));
                        return;
                    }
                    if (!Util.validateAadharNumber(balanceaadharVid)) {
                        et_aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }

                }
                if (et_mobileNumber.getText() == null || et_mobileNumber.getText().toString().trim().matches("") || !Util.isValidMobile(et_mobileNumber.getText().toString().trim())) {
                    et_mobileNumber.setError(getResources().getString(R.string.mobileerror));
                    return;
                }
                MobileNumber = et_mobileNumber.getText().toString().trim();
                if (!MobileNumber.contains(" ") && MobileNumber.length() == 10) {
                } else {
                    et_mobileNumber.setError(getResources().getString(R.string.mobileerror));
                    return;
                }
                if (et_bankSpinner.getText() == null || et_bankSpinner.getText().toString().trim().matches("")) {
                    et_bankSpinner.setError(getResources().getString(R.string.select_bank_error));
                    return;
                }
                if (!flagFromDriver) {
                    Toast.makeText(AEPS3sdkActivity.this, "Please do Biomatric Varification", Toast.LENGTH_LONG).show();
                    return;
                } else {

                    try {
                        JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                        String scoreStr = respObj.getString("pidata_qscore");

                        if (Float.parseFloat(scoreStr) <= 40) {
                            showAlert("Bad Fingerprint Strength, Please try Again !");
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                    showLoader();

                    try {
                        JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);

                        String base64pidData = respObj.getString("base64pidData");

                        // Log.e("TAG", "onClick:++ " + base64pidData);

                        checkVPNstatusForTransaction("Balance", balanaceInqueryAadharNo, bankIINNumber, latLong, bankName_, base64pidData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
                    showLoader();
                    try {

                        JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);

                        String base64pidData = respObj.getString("base64pidData");

                        checkVPNstatusForTransaction("MiniStatement", balanaceInqueryAadharNo, bankIINNumber, latLong, bankName_, base64pidData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
                    showLoader();
                    if (et_enteredAmount.getText() == null || et_enteredAmount.getText().toString().trim().matches("")) {
                        et_enteredAmount.setError(getResources().getString(R.string.amount_error));
                        return;
                    }
                    try {
                        JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);

                        String base64pidData = respObj.getString("base64pidData");

                        checkVPNstatusForTransaction("Cash", balanaceInqueryAadharNo, bankIINNumber, latLong, bankName_, base64pidData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.adhaarPay)) {
                    showLoader();
                    if (et_enteredAmount.getText() == null || et_enteredAmount.getText().toString().trim().matches("")) {
                        et_enteredAmount.setError(getResources().getString(R.string.amount_error));
                        return;
                    }

                    JSONObject respObj = null;
                    try {
                        respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);

                        String base64pidData = respObj.getString("base64pidData");

                        checkVPNstatusForTransaction("aadhaarPay", balanaceInqueryAadharNo, bankIINNumber, latLong, bankName_, base64pidData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    private void aadhaarPayAPIcall(String aadhaarNumber, String bankIINNumber, String latLong, String bankName, String base64pidData) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aadharNo", aadhaarNumber);
        jsonObject.put("iin", bankIINNumber);
        jsonObject.put("apiUser", "ANDROID");
        jsonObject.put("latLong", latLong);
        jsonObject.put("ipAddress", publicIP_);
        jsonObject.put("amount", et_enteredAmount.getText().toString().trim());
        jsonObject.put("mobileNumber", et_mobileNumber.getText().toString().trim());
        jsonObject.put("retailer", "aepsTestR");
        jsonObject.put("paramA", "");
        jsonObject.put("paramB", "");
        jsonObject.put("paramC", "");
        jsonObject.put("isSL", false);
        jsonObject.put("bankName", bankName.trim());
        jsonObject.put("pidData", base64pidData);

        Log.e("TAG", "aadhaarPayAPIcall: " + jsonObject);


        AndroidNetworking.post(Constants.LIVE_URL + "aadhaarPay")
                .addHeaders("Authorization", session.getUserToken())
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoader();
//216.187.190.143
                        Log.e("TAG", "aadhaarPayAPIcall onResponse: " + response.toString());

                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            String Success_msg = jsonObject.getString("transactionStatus");
                            String transactionID = jsonObject.optString("txId");
                            finish();
                            if (!Success_msg.equals("")) {

                                String Amount = et_enteredAmount.getText().toString().trim();


                                //  FirebaseCM();
                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);
                                intent.putExtra("TRANSACTION_STATUS", Success_msg);
                                intent.putExtra("TRANSACTION_ID", transactionID);
                                intent.putExtra("API_STATUS", "1");
                                intent.putExtra("TRANSACTION_TYPE", "AADHAARPAY");
                                intent.putExtra("BANKNAME", bankName_);
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("TXNAMOUNT", et_enteredAmount.getText().toString().trim());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(AEPS3sdkActivity.this, "Something Went Wrong..!!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoader();
                        if (anError.getErrorBody() == null) {
                            showAlert("Sorry, something went wrong. Please try again after sometimes.");
                        } else {
                            try {
                                String Amount = et_enteredAmount.getText().toString().trim();
                                JSONObject jsonObject1 = new JSONObject(anError.getErrorBody().toString());
                                Log.e("AEPS3", "onError: " + jsonObject1);
                                String transactionStatus = jsonObject1.getString("transactionStatus");
                                String txn_status = jsonObject1.getString("status");
                                String txId = jsonObject1.getString("txId");
                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);
                                intent.putExtra("TRANSACTION_STATUS_ERROR", transactionStatus);
                                intent.putExtra("API_STATUS", "0");
                                intent.putExtra("TRANSACTION_ID_ERROR", txId);
                                intent.putExtra("TRANSACTION_TYPE_ERROR", "AADHAARPAY");
                                intent.putExtra("BANKNAME", bankName_.trim());
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("TXNAMOUNT", Amount);
                                startActivity(intent);
                                finish();
                                Log.e("TAG", "onError111: " + transactionStatus + "," + txn_status + "," + txId);

                                //{"transactionStatus":"Amount range should be between Rs.1\/- to Rs.10000\/-","status":-1,"txId":"0000","errors":null}

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void cashWithdrawalApiCall(String aadhaarNumber, String bankIINNumber, String latLong, String bankName, String base64pidData) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aadharNo", aadhaarNumber);
        jsonObject.put("iin", bankIINNumber);
        jsonObject.put("apiUser", "ANDROID");
        jsonObject.put("latLong", latLong);
        jsonObject.put("ipAddress", publicIP_); //"192.168.43.110"
        jsonObject.put("amount", et_enteredAmount.getText().toString().trim());
        jsonObject.put("mobileNumber", et_mobileNumber.getText().toString().trim());
        jsonObject.put("retailer", "aepsTestR");
        jsonObject.put("paramA", "");
        jsonObject.put("paramB", "");
        jsonObject.put("paramC", "");
        jsonObject.put("isSL", false);
        jsonObject.put("bankName", bankName.trim());
        jsonObject.put("pidData", base64pidData);


        Log.e("TAG", "cashWithdrawalApiCall: " + jsonObject.toString());

        AndroidNetworking.post(Constants.LIVE_URL + "cashWithdrawal")
                .addHeaders("Authorization", session.getUserToken())
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoader();

                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            String Success_msg = jsonObject.optString("transactionStatus");
                            String transactionID = jsonObject.optString("txId");
                            finish();
                            if (!Success_msg.equals("")) {

                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);
                                intent.putExtra("TRANSACTION_STATUS", Success_msg);
                                intent.putExtra("TRANSACTION_ID", transactionID);
                                intent.putExtra("API_STATUS", "1");
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("TRANSACTION_TYPE", "CASHWITHDRAWAL");
                                intent.putExtra("BANKNAME", bankName_.trim());
                                intent.putExtra("TXNAMOUNT", et_enteredAmount.getText().toString().trim());
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(AEPS3sdkActivity.this, "Something Went Wrong..!!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoader();

                        Log.e("TAG", "onError: " + anError.getErrorBody());
                        if (anError.getErrorBody() == null) {
                            showAlert("Sorry, something went wrong. Please try again after sometimes.");
                        } else {
                            try {
                                JSONObject jsonObject1 = new JSONObject(anError.getErrorBody().toString());
                                String transactionStatus = jsonObject1.getString("transactionStatus");
                                String txn_status = jsonObject1.getString("status");
                                String txId = jsonObject1.getString("txId");
                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);

                                intent.putExtra("TRANSACTION_STATUS_ERROR", transactionStatus);
                                intent.putExtra("API_STATUS", "0");
                                intent.putExtra("TRANSACTION_ID_ERROR", txId);
                                intent.putExtra("TRANSACTION_TYPE_ERROR", "CASHWITHDRAWAL");
                                intent.putExtra("BANKNAME", bankName_.trim());
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("TXNAMOUNT", et_enteredAmount.getText().toString().trim());
                                startActivity(intent);
                                Log.e("TAG", "onError111: " + transactionStatus + "," + txn_status + "," + txId);
                                finish();

                                //{"transactionStatus":"Amount range should be between Rs.1\/- to Rs.10000\/-","status":-1,"txId":"0000","errors":null}

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    private void ministatementAPiCall(String aadhaarNumber, String bankIINNumber, String latLong, String bankName, String base64pidData) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aadharNo", aadhaarNumber);
        jsonObject.put("iin", bankIINNumber);

        jsonObject.put("apiUser", "ANDROID");
        jsonObject.put("latLong", latLong);
        jsonObject.put("ipAddress", publicIP_);
        jsonObject.put("mobileNumber", et_mobileNumber.getText().toString().trim());
        jsonObject.put("retailer", "aepsTestR");
        jsonObject.put("paramA", "");
        jsonObject.put("paramB", "");
        jsonObject.put("paramC", "");
        jsonObject.put("isSL", false);
        jsonObject.put("bankName", bankName.trim());
        jsonObject.put("pidData", base64pidData);


        Log.e("TAG", "ministatementAPiCall: " + jsonObject.toString());

        AndroidNetworking.post(Constants.LIVE_URL + "miniStatement")
                .addHeaders("Authorization", session.getUserToken())
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoader();
                        Log.e("TAG", "MinistatementAPicall onResponse: " + response.toString());
                        //{"transactionStatus":"Transaction request recieved","status":0,"txId":"791321538874843136","errors":null}

                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            String Success_msg = jsonObject.getString("transactionStatus");
                            String transactionID = jsonObject.optString("txId");
                            finish();
                            if (!Success_msg.equals("")) {
                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);
                                intent.putExtra("TRANSACTION_STATUS", Success_msg);
                                intent.putExtra("TRANSACTION_ID", transactionID);
                                intent.putExtra("API_STATUS", "1");
                                intent.putExtra("TRANSACTION_TYPE", "MINISTATEMENT");
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("BANKNAME", bankName_.trim());
                                intent.putExtra("TXNAMOUNT", et_enteredAmount.getText().toString().trim());
                                startActivity(intent);
                                finish();
                            } else {
                                finish();
                                Toast.makeText(AEPS3sdkActivity.this, "Something Went Wrong..!!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoader();
                        if (anError.getErrorBody() == null) {
                            showAlert("Sorry, something went wrong. Please try again after sometimes.");
                        } else {
                            try {
                                JSONObject jsonObject1 = new JSONObject(anError.getErrorBody().toString());
                                String transactionStatus = jsonObject1.getString("transactionStatus");
                                String txn_status = jsonObject1.getString("status");
                                String txId = jsonObject1.getString("txId");
                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);
                                intent.putExtra("TRANSACTION_STATUS_ERROR", transactionStatus);
                                intent.putExtra("API_STATUS", "0");
                                intent.putExtra("TRANSACTION_ID_ERROR", txId);
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("TRANSACTION_TYPE_ERROR", "ministatement");
                                intent.putExtra("BANKNAME", bankName_.trim());
                                intent.putExtra("TXNAMOUNT", et_enteredAmount.getText().toString().trim());
                                startActivity(intent);
                                finish();
                                Log.e("TAG", "onError111: " + transactionStatus + "," + txn_status + "," + txId);

                                //{"transactionStatus":"Amount range should be between Rs.1\/- to Rs.10000\/-","status":-1,"txId":"0000","errors":null}

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });


    }

    private void balanceEnquiryAPicall(String aadhaarNumber, String bankIINNumber, String latLong, String bankName, String base64pidData) throws JSONException {


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aadharNo", aadhaarNumber);
        jsonObject.put("iin", bankIINNumber);
        jsonObject.put("apiUser", "ANDROID");
        jsonObject.put("latLong", latLong);
        jsonObject.put("ipAddress", publicIP_);
        jsonObject.put("amount", "");
        jsonObject.put("mobileNumber", MobileNumber);
        jsonObject.put("retailer", "aepsTestR");
        jsonObject.put("paramA", "");
        jsonObject.put("paramB", "");
        jsonObject.put("paramC", "");
        jsonObject.put("isSL", false);
        jsonObject.put("bankName", bankName.trim());
        jsonObject.put("pidData", base64pidData);


        Log.e("TAG", "balanceEnquiryAPicall: " + jsonObject.toString());

        AndroidNetworking.post(Constants.LIVE_URL + "balanceEnquiry")
                .addHeaders("Authorization", session.getUserToken())
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoader();
                        Log.e("TAG", "balanceEnquiryAPicall onResponse: " + response.toString());
                        //{"transactionStatus":"Transaction request recieved","status":0,"txId":"791321538874843136","errors":null}
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            String Success_msg = jsonObject.getString("transactionStatus");
                            String transactionID = jsonObject.optString("txId");
                            finish();

                            if (!Success_msg.equals("")) {
                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);
                                intent.putExtra("TRANSACTION_STATUS", Success_msg);
                                intent.putExtra("TRANSACTION_ID", transactionID);
                                intent.putExtra("API_STATUS", "1");
                                intent.putExtra("TRANSACTION_TYPE", "BALANCE ENQUIRY");
                                intent.putExtra("BANKNAME", bankName_.trim());
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("TXNAMOUNT", et_enteredAmount.getText().toString().trim());
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(AEPS3sdkActivity.this, "Something Went Wrong..!!", Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoader();
                        if (anError.getErrorBody() == null) {
                            showAlert("Sorry, something went wrong. Please try again after sometimes.");
                        } else {
                            try {

                                Log.e("TAG", "onError: " + anError.getErrorDetail());

                                Toast.makeText(AEPS3sdkActivity.this, anError.getErrorDetail().toString(), Toast.LENGTH_LONG).show();
                                JSONObject jsonObject1 = new JSONObject(anError.getErrorBody().toString());

                                String transactionStatus = jsonObject1.getString("transactionStatus");
                                String txn_status = jsonObject1.getString("status");
                                String txId = jsonObject1.getString("txId");

                                Intent intent = new Intent(AEPS3sdkActivity.this, AEPS3_Transaction_Status_Activity.class);
                                intent.putExtra("TRANSACTION_STATUS_ERROR", transactionStatus);
                                intent.putExtra("API_STATUS", "0");
                                intent.putExtra("TRANSACTION_ID_ERROR", txId);
                                intent.putExtra("TRANSACTION_TYPE_ERROR", "BALANCE ENQUIRY");
                                intent.putExtra("BANKNAME", bankName_.trim());
                                intent.putExtra("MOBILE_NUMBER", et_mobileNumber.getText().toString());
                                intent.putExtra("TXNAMOUNT", et_enteredAmount.getText().toString().trim());
                                startActivity(intent);
                                finish();
                                Log.e("TAG", "onError111: " + transactionStatus + "," + txn_status + "," + txId);

                                //{"transactionStatus":"Amount range should be between Rs.1\/- to Rs.10000\/-","status":-1,"txId":"0000","errors":null}

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

    }


    private void setLoaderForOneMinutes(String Success_msg, String transactionID) {

        ProgressDialog TempDialog = new ProgressDialog(this);
        TempDialog.setContentView(R.layout.custom_progressbar);
        TempDialog.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
        TempDialog.setMessage("Please wait...");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(i);
        TempDialog.show();

        CountDownTimer CDT = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                TempDialog.setMessage("Please wait..");
                i--;
            }

            public void onFinish() {
                TempDialog.dismiss();


                // Log.e("TAG", "onResponse:  onFinish of loader,Constants.THE_FIREBASE_DATA::::" + Constants.THE_FIREBASE_DATA);
                // System.out.println(Constants.THE_FIREBASE_DATA);


            }
        }.start();
    }


    private void checkVPNstatusForTransaction(final String transaction_type, String aadhaarNumber, String bankIINNumber, String latLong, String bankName, String base64pidData) {


        AndroidNetworking.get("https://vpn.iserveu.online/vpn/telnet_checkVpn")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // stopProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            String statusDesc = obj.getString("statusDesc");

                            if (status.equalsIgnoreCase("0")) {

                                if (transaction_type.equalsIgnoreCase("Cash")) {

                                    cashWithdrawalApiCall(aadhaarNumber, bankIINNumber, latLong, bankName, base64pidData);

                                } else if (transaction_type.equalsIgnoreCase("Balance")) {

                                    balanceEnquiryAPicall(aadhaarNumber, bankIINNumber, latLong, bankName, base64pidData);

                                } else if (transaction_type.equalsIgnoreCase("MiniStatement")) {

                                    ministatementAPiCall(aadhaarNumber, bankIINNumber, latLong, bankName, base64pidData);

                                } else if (transaction_type.equalsIgnoreCase("aadhaarPay")) {

                                    aadhaarPayAPIcall(aadhaarNumber, bankIINNumber, latLong, bankName, base64pidData);

                                } else {


                                    showAlert("Sorry, something went wrong. Please try again after sometimes.");
                                }
                            }

//
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoader();
                            showAlert("Sorry, something went wrong. Please try again after sometimes.");

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.getErrorBody();
                        hideLoader();

                        showAlert("Sorry, something went wrong. Please try again after sometimes.");
                    }
                });
    }

    private void showUserOnboardStatus(final String statusDesc) {


        AlertDialog.Builder builder1 = new AlertDialog.Builder(AEPS3sdkActivity.this);
        builder1.setMessage(statusDesc);
        builder1.setTitle("Alert");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

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
                            showAlert("Sorry, something went wrong. Please try again after sometimes.");

                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Sorry, something went wrong. Please try again after sometimes.");
        }
    }

    public void showAlert(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AEPS3sdkActivity.this);
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

    private String makePrettyString(String string) {
        String number = string.replaceAll("-", "");
        boolean isEndHyphen = string.endsWith("-") && (number.length() % 4 == 0);
        return number.replaceAll("(.{4}(?!$))", "$1-") + (isEndHyphen ? "-" : "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                assert bankIINValue != null;
                et_bankSpinner.setText(bankIINValue.getBankName());
                bankName_ = bankIINValue.getBankName();
                bankIINNumber = bankIINValue.getIin();
                checkBalanceEnquiryValidation();
            }
            checkBalanceEnquiryValidation();
        } else if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                assert bankIINValue != null;
                et_bankSpinner.setText(bankIINValue.getBankName());
                bankIINNumber = bankIINValue.getIin();
                bankName_ = bankIINValue.getBankName();
                checkWithdrawalValidation();

            }
            checkWithdrawalValidation();
        } else if (requestCode == SdkConstants.REQUEST_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                Intent respIntent = new Intent();
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                assert bankIINValue != null;
                bankName_ = bankIINValue.getBankName();
                respIntent.putExtra(SdkConstants.responseData, SdkConstants.transactionResponse);
                setResult(Activity.RESULT_OK, respIntent);
                finish();

            }
            checkWithdrawalValidation();
        } else if (requestCode == 1) {
            hideLoader();

        }

    }

    private void checkBalanceEnquiryValidation() {
        // TODO Auto-generated method stub
        if (et_mobileNumber.getText() != null && !et_mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(et_mobileNumber.getText().toString().trim()) == true && et_bankSpinner.getText() != null
                && !et_bankSpinner.getText().toString().trim().matches("")) {

            boolean status = false;
            if (adharbool) {
                String aadharNo = et_aadharNumber.getText().toString();
                if (aadharNo.contains("-")) {
                    aadharNo = aadharNo.replaceAll("-", "").trim();
                    status = Util.validateAadharNumber(aadharNo);
                }
            } else if (virtualbool) {
                String aadharVid = et_aadharVirtualID.getText().toString();
                if (aadharVid.contains("-")) {
                    aadharVid = aadharVid.replaceAll("-", "").trim();
                    status = Util.validateAadharVID(aadharVid);
                }
            }
            if (status) {

            }
        } else {
            btn_submitButton.setEnabled(false);
            btn_submitButton.setBackgroundResource(R.drawable.button_submit);
        }

    }


    private void checkWithdrawalValidation() {
        // TODO Auto-generated method stub
        if (et_mobileNumber.getText() != null
                && !et_mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(et_mobileNumber.getText().toString().trim()) == true
                && et_mobileNumber.getText().toString().length() == 10
                && et_bankSpinner.getText() != null
                && !et_bankSpinner.getText().toString().trim().matches("")
                && et_enteredAmount.getText() != null
                && !et_enteredAmount.getText().toString().trim().matches("")) {

            boolean status = false;
            if (adharbool == true) {
                String aadharNo = et_aadharNumber.getText().toString();
                if (aadharNo.contains("-")) {
                    aadharNo = aadharNo.replaceAll("-", "").trim();
                    status = Util.validateAadharNumber(aadharNo);
                }
            } else if (virtualbool == true) {
                String aadharVid = et_aadharVirtualID.getText().toString();
                if (aadharVid.contains("-")) {
                    aadharVid = aadharVid.replaceAll("-", "").trim();
                    status = Util.validateAadharVID(aadharVid);
                }
            }
            if (status) {

            }
        } else {
            btn_submitButton.setEnabled(false);
            btn_submitButton.setBackgroundResource(R.drawable.button_submit);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
        if (flagFromDriver) {
            if (SdkConstants.RECEIVE_DRIVER_DATA.isEmpty() || SdkConstants.RECEIVE_DRIVER_DATA.equalsIgnoreCase("")) {

                // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                iv_fingerprint.setColorFilter(color);
                iv_fingerprint.setEnabled(true);
                btn_submitButton.setBackgroundResource(R.drawable.button_submit);
                btn_submitButton.setEnabled(false);
            } else if (balanaceInqueryAadharNo.equalsIgnoreCase("") || balanaceInqueryAadharNo.isEmpty()) {
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
                iv_fingerprint.setColorFilter(R.color.colorGrey);
                iv_fingerprint.setEnabled(false);
                btn_submitButton.setEnabled(true);
                btn_submitButton.setBackgroundResource(R.drawable.button_submit_blue);
            }

        }

    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void fingerStrength() {
        try {
            JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
            String scoreStr = respObj.getString("pidata_qscore");


            if (Float.parseFloat(scoreStr) <= 40) {
                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.red));
                depositBar.setStartColor(getResources().getColor(R.color.red));
                depositNote.setVisibility(View.VISIBLE);
                tv_fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            } /*else if (Float.parseFloat(scoreStr) >= 30 && Float.parseFloat(scoreStr) <= 60) {

                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.yellow));
                depositBar.setStartColor(getResources().getColor(R.color.yellow));
                depositNote.setVisibility(View.VISIBLE);
                tv_fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            }*/ else {

                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.green));
                depositBar.setStartColor(getResources().getColor(R.color.green));
                depositNote.setVisibility(View.VISIBLE);
                tv_fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getLocation() {
        GpsTracker gpsTracker = new GpsTracker(AEPS3sdkActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            latLong = latitude + "," + longitude;
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void initView() {
        et_aadharNumber = findViewById(R.id.et_aadharNumber);
        et_aadharVirtualID = findViewById(R.id.et_aadharVirtualID);
        et_mobileNumber = findViewById(R.id.et_mobileNumber);
        et_bankSpinner = findViewById(R.id.et_bankSpinner);
        et_enteredAmount = findViewById(R.id.et_enteredAmount);

        iv_fingerprint = findViewById(R.id.iv_fingerprint);
        iv_fingerprint.setEnabled(false);
        iv_aadhaar = findViewById(R.id.iv_aadhaar);
        iv_virtualID = findViewById(R.id.iv_virtualID);

        tv_fingerprintStrengthDeposit = findViewById(R.id.tv_fingerprintStrengthDeposit);
        tv_cashWithdrawalHeadText = findViewById(R.id.tv_cashWithdrawalHeadText);
        tv_balanceEnquiryHeadText = findViewById(R.id.tv_balanceEnquiryHeadText);
        tv_miniStatementHeadText = findViewById(R.id.tv_miniStatementHeadText);
        tv_aadhaarPayHeadText = findViewById(R.id.tv_aadhaarPayHeadText);

        tv_virtualidText = findViewById(R.id.tv_virtualidText);
        tv_aadharText = findViewById(R.id.tv_aadharText);

        btn_submitButton = findViewById(R.id.btn_submitButton);
        depositBar = findViewById(R.id.depositBar);
        depositNote = findViewById(R.id.depositNote);
        depositNote.setVisibility(View.GONE);
        depositBar.setVisibility(View.GONE);
        toolbar = findViewById(R.id.toolbar);

        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
            tv_cashWithdrawalHeadText.setVisibility(View.GONE);
            tv_miniStatementHeadText.setVisibility(View.GONE);
            tv_aadhaarPayHeadText.setVisibility(View.GONE);
            tv_balanceEnquiryHeadText.setVisibility(View.VISIBLE);
            et_enteredAmount.setVisibility(View.GONE);

        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
            tv_cashWithdrawalHeadText.setVisibility(View.GONE);
            tv_miniStatementHeadText.setVisibility(View.VISIBLE);
            tv_aadhaarPayHeadText.setVisibility(View.GONE);
            tv_balanceEnquiryHeadText.setVisibility(View.GONE);
            et_enteredAmount.setVisibility(View.GONE);


        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
            tv_cashWithdrawalHeadText.setVisibility(View.VISIBLE);
            tv_miniStatementHeadText.setVisibility(View.GONE);
            tv_aadhaarPayHeadText.setVisibility(View.GONE);
            tv_balanceEnquiryHeadText.setVisibility(View.GONE);
            et_enteredAmount.setText(SdkConstants.transactionAmount);

        } else {
            tv_cashWithdrawalHeadText.setVisibility(View.GONE);
            tv_miniStatementHeadText.setVisibility(View.GONE);
            tv_aadhaarPayHeadText.setVisibility(View.VISIBLE);
            tv_balanceEnquiryHeadText.setVisibility(View.GONE);
            et_enteredAmount.setText(SdkConstants.transactionAmount);
        }

        et_enteredAmount.setEnabled(SdkConstants.editable);

        iv_virtualID.setBackgroundResource(R.drawable.ic_language);
        tv_virtualidText.setTextColor(getResources().getColor(R.color.grey));
        iv_aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);

        typedValue = new TypedValue();
        theme = this.getTheme();
        session = new Session(AEPS3sdkActivity.this);
        getLocation();
        getRDServiceClass();

        NotificationHelper notificationHelper = new NotificationHelper(this);
        SdkConstants.RECEIVE_DRIVER_DATA = "";

        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            publicIP_ = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            Log.e("TAG", "initView: publicIP_:::" + publicIP_);

            if (publicIP_.equalsIgnoreCase("0.0.0.0")) {

                publicIP_ = getLocalIpAddress();
                Log.e("TAG", "initView: getLocalIpAddress::" + publicIP_);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getRDServiceClass() {
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;
        flagNameRdService = SdkConstants.MANUFACTURE_FLAG;

        try {
            driverActivity = Class.forName(accessClassName).asSubclass(Activity.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showLoader() {
        if (loadingView == null) {
            loadingView = new ProgressDialog(AEPS3sdkActivity.this);
            loadingView.setCancelable(false);
            loadingView.setMessage("Please Wait..");
        }
        loadingView.show();
    }

    public void hideLoader() {
        if (loadingView != null) {
            loadingView.dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}

