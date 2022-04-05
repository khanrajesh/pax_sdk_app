package com.matm.matmsdk.aepsmodule.unifiedaeps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.GpsTracker;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.AEPS2HomeActivity;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameListActivity;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameModel;
import com.matm.matmsdk.aepsmodule.ministatement.StatementList_Adapter;
import com.matm.matmsdk.aepsmodule.ministatement.TransactionList;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusAeps2Activity;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusModel;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.notification.NotificationHelper;
import com.moos.library.HorizontalProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import isumatm.androidsdk.equitas.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnifiedAepsActivity extends AppCompatActivity implements UnifiedAepsContract.View {
    final static String MARKER = "|"; // filtered in layout not to be in the string
    Boolean adharbool = true;
    Boolean virtualbool = false;
    AppCompatCheckBox terms;
    Session session;
    UnifiedAepsRequestModel unifiedAepsRequestModel;
    String bankIINNumber = "";
    ProgressDialog loadingView;
    String flagNameRdService = "";
    Class driverActivity;
    String balanaceInqueryAadharNo = "";
    Boolean flagFromDriver = false;
    String vid = "", uid = "";
    TextView virtualidText, aadharText;
    boolean mInside = false;
    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    String latLong = "";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    TypedValue typedValue;
    Resources.Theme theme;
    private EditText aadharNumber, aadharVirtualID;
    private TextView balanceEnquiryExpandButton, cashWithdrawalButton, fingerprintStrengthDeposit, depositNote;
    private EditText mobileNumber, bankspinner, amountEnter;
    private ImageView fingerprint, virtualID, aadhaar;
    private HorizontalProgressView depositBar;
    private Button submitButton;
    private GpsTracker gpsTracker;
    private int gatewayPriority = 0;
    //notification 22 july
    private NotificationHelper notificationHelper;
    private UnifiedAepsPresenter unifiedAepsPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * For CustomThemes
         * @Author - RashmiRanjan
         * */
        new CustomThemes(this);
        // setTheme(R.style.MediumSlateBlue);
        if (SdkConstants.dashboardLayout != 0) {
            setContentView(SdkConstants.dashboardLayout);
        } else {
            setContentView(R.layout.activity_unified_aeps);
        }
        session = new Session(UnifiedAepsActivity.this);
        getRDServiceClass();
        typedValue = new TypedValue();
        theme = this.getTheme();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLocation();
        //notification 22 july
        notificationHelper = new NotificationHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SdkConstants.RECEIVE_DRIVER_DATA = "";
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fingerprintStrengthDeposit = findViewById(R.id.fingerprintStrengthDeposit);
        depositNote = findViewById(R.id.depositNote);
        depositNote.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
        aadharVirtualID = (EditText) findViewById(R.id.aadharVirtualID);
        virtualID = findViewById(R.id.virtualID);
        aadhaar = findViewById(R.id.aadhaar);
        virtualidText = findViewById(R.id.virtualidText);
        aadharText = findViewById(R.id.aadharText);
        aadharNumber = findViewById(R.id.aadharNumber);
        mobileNumber = findViewById(R.id.mobileNumber);
        bankspinner = findViewById(R.id.bankspinner);
        amountEnter = findViewById(R.id.amountEnter);
        fingerprint = findViewById(R.id.fingerprint);
        fingerprint.setEnabled(false);
        fingerprint.setClickable(false);
        submitButton = findViewById(R.id.submitButton);
        depositBar = findViewById(R.id.depositBar);
        depositBar.setVisibility(View.GONE);
        terms = findViewById(R.id.terms);
        cashWithdrawalButton = findViewById(R.id.cashWithdrawalButton);
        balanceEnquiryExpandButton = findViewById(R.id.balanceEnquiryExpandButton);
        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            SdkConstants.isSL = false;
        } else {
            SdkConstants.isSL = true;
            getUserAuthToken();
            terms.setVisibility(View.GONE);
        }
        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
            balanceEnquiryExpandButton.setVisibility(View.VISIBLE);
            cashWithdrawalButton.setVisibility(View.GONE);
            amountEnter.setVisibility(View.GONE);
        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
            balanceEnquiryExpandButton.setVisibility(View.VISIBLE);
            balanceEnquiryExpandButton.setText(getResources().getString(R.string.mini_statement));
            cashWithdrawalButton.setVisibility(View.GONE);
            amountEnter.setVisibility(View.GONE);
        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
            balanceEnquiryExpandButton.setVisibility(View.GONE);
            cashWithdrawalButton.setVisibility(View.VISIBLE);
            amountEnter.setText(SdkConstants.transactionAmount);
        }
        amountEnter.setEnabled(SdkConstants.editable);
        virtualID.setBackgroundResource(R.drawable.ic_language);
        virtualidText.setTextColor(getResources().getColor(R.color.grey));
        aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
        bankspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showLoader();
                mobileNumber.clearFocus();
                Intent in = new Intent(UnifiedAepsActivity.this, BankNameListActivity.class);
                if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE);
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE);
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE);
                }
            }
        });
        fingerprint.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    showLoader();
                    fingerprint.setEnabled(false);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner_grey));
                    fingerprint.setColorFilter(R.color.colorGrey);
                    flagFromDriver = true;
                    Intent launchIntent = new Intent(UnifiedAepsActivity.this, driverActivity);
                    launchIntent.putExtra("driverFlag", flagNameRdService);
                    launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                    launchIntent.putExtra("AadharNo", balanaceInqueryAadharNo);
                    startActivityForResult(launchIntent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        aadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mKeyListenerSet) {
                    aadharNumber.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            try {
                                mWannaDeleteHyphen = (keyCode == KeyEvent.KEYCODE_DEL
                                        && aadharNumber.getSelectionEnd() - aadharNumber.getSelectionStart() <= 1
                                        && aadharNumber.getSelectionStart() > 0
                                        && aadharNumber.getText().toString().charAt(aadharNumber.getSelectionEnd() - 1) == '-');
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
                int currentPos = aadharNumber.getSelectionStart();
                String string = aadharNumber.getText().toString().toUpperCase();
                String newString = makePrettyString(string);
                if (count == 14) {
                    fingerprint.setEnabled(true);
                    fingerprint.setClickable(true);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    fingerprint.setColorFilter(color);
                } else {
                }
                aadharNumber.setText(newString);
                try {
                    aadharNumber.setSelection(getCursorPos(string, newString, currentPos, mWannaDeleteHyphen));
                } catch (IndexOutOfBoundsException e) {
                    aadharNumber.setSelection(aadharNumber.length()); // last resort never to happen
                }
                mWannaDeleteHyphen = false;
                mInside = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    aadharNumber.setError(getResources().getString(R.string.aadhaarnumber));
                }
                if (s.length() > 0) {
                    aadharNumber.setError(null);
                    String aadharNo = aadharNumber.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            fingerprint.setEnabled(true);
                            fingerprint.setClickable(true);
                            //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            fingerprint.setColorFilter(color);
                            aadharNumber.clearFocus();
                            mobileNumber.requestFocus();
                        }
                    }
                    if (Util.validateAadharNumber(aadharNo) == false) {
                        aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                    }
                }
            }
        });
        aadharVirtualID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mKeyListenerSet) {
                    aadharVirtualID.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            try {
                                mWannaDeleteHyphen = (keyCode == KeyEvent.KEYCODE_DEL
                                        && aadharVirtualID.getSelectionEnd() - aadharVirtualID.getSelectionStart() <= 1
                                        && aadharVirtualID.getSelectionStart() > 0
                                        && aadharVirtualID.getText().toString().charAt(aadharVirtualID.getSelectionEnd() - 1) == '-');
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
                int currentPos = aadharVirtualID.getSelectionStart();
                String string = aadharVirtualID.getText().toString().toUpperCase();
                String newString = makePrettyString(string);
                if (count == 19) {
                    fingerprint.setEnabled(true);
                    fingerprint.setClickable(true);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    fingerprint.setColorFilter(color);
                }
                aadharVirtualID.setText(newString);
                try {
                    aadharVirtualID.setSelection(getCursorPos(string, newString, currentPos, mWannaDeleteHyphen));
                } catch (IndexOutOfBoundsException e) {
                    aadharVirtualID.setSelection(aadharVirtualID.length()); // last resort never to happen
                }
                mWannaDeleteHyphen = false;
                mInside = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    aadharVirtualID.setError(getResources().getString(R.string.aadhaarVID));
                }
                if (s.length() > 0) {
                    aadharVirtualID.setError(null);
                    String aadharNo = aadharVirtualID.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            fingerprint.setEnabled(true);
                            fingerprint.setClickable(true);
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            fingerprint.setColorFilter(color);
                            aadharVirtualID.clearFocus();
                            mobileNumber.requestFocus();
                            //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                        }
                    }
                    if (Util.validateAadharVID(aadharNo) == false) {
                        aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar__uid_error));
                    }
                }
            }

        });
        bankspinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    bankspinner.setError(getResources().getString(R.string.select_bank_error));
                    mobileNumber.clearFocus();
                }
                if (s.length() > 0) {
                    bankspinner.setError(null);
                }
            }
        });
        amountEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    amountEnter.setError(getResources().getString(R.string.amount_error));
                }
                if (s.length() > 0) {
                    amountEnter.setError(null);
                }
            }
        });
        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 || s.length() < 10) {
                    mobileNumber.setError(null);
                    String x = s.toString();
                    if (x.startsWith("0") || Util.isValidMobile(mobileNumber.getText().toString().trim()) == false) {
                        mobileNumber.setError(getResources().getString(R.string.mobilevaliderror));
                    }
                }
            }
        });
        virtualID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Will implement when VID will used*/
                aadharNumber.setVisibility(View.GONE);
                aadharVirtualID.setVisibility(View.VISIBLE);
                virtualID.setEnabled(false);
                aadhaar.setEnabled(true);
                virtualbool = true;
                adharbool = false;
                virtualID.setBackgroundResource(R.drawable.ic_language_blue);
                //virtualidText.setTextColor(getResources().getColor(R.color.colorPrimary));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                virtualidText.setTextColor(color);
                aadhaar.setBackground(getResources().getDrawable(R.drawable.ic_fingerprint_grey));
                aadharText.setTextColor(getResources().getColor(R.color.grey));
            }
        });
        aadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharNumber.setVisibility(View.VISIBLE);
                aadharVirtualID.setVisibility(View.GONE);
                virtualID.setEnabled(true);
                aadhaar.setEnabled(false);
                virtualID.setBackgroundResource(R.drawable.ic_language);
                virtualidText.setTextColor(getResources().getColor(R.color.grey));
                adharbool = true;
                virtualbool = false;
                aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                aadharText.setTextColor(color);
                //  aadharText.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SdkConstants.firstCheck = true;
                showTermsDetails(UnifiedAepsActivity.this);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showLoader();
                String balanceaadharNo = "";
                String balanceaadharVid = "";
                balanceaadharNo = aadharNumber.getText().toString();
                if (adharbool == true) {
                    if (balanceaadharNo.contains("-")) {
                        balanceaadharNo = balanceaadharNo.replaceAll("-", "").trim();
                    }
                    if (balanceaadharNo == null || balanceaadharNo.matches("")) {
                        aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                    if (Util.validateAadharNumber(balanceaadharNo) == false) {
                        aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                    if (aadharNumber.getText().toString().length() < 14) {
                        aadharNumber.setError("Enter valid aadhaar no.");
                        return;
                    }
                } else if (virtualbool == true) {
                    balanceaadharVid = aadharVirtualID.getText().toString().trim();
                    if (balanceaadharVid.contains("-")) {
                        balanceaadharVid = balanceaadharVid.replaceAll("-", "").trim();
                    }
                    if (balanceaadharVid == null || balanceaadharVid.matches("")) {
                        aadharVirtualID.setError(getResources().getString(R.string.valid_vid_error));
                        return;
                    }
                    if (Util.validateAadharNumber(balanceaadharVid) == false) {
                        aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                }
                if (!flagFromDriver) {
                    Toast.makeText(UnifiedAepsActivity.this, "Please do Biometric Verification", Toast.LENGTH_LONG).show();
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
                if (mobileNumber.getText() == null || mobileNumber.getText().toString().trim().matches("") || Util.isValidMobile(mobileNumber.getText().toString().trim()) == false) {
                    mobileNumber.setError(getResources().getString(R.string.mobileerror));
                    return;
                }
                SdkConstants.AADHAAR_NUMBER = aadharNumber.getText().toString().trim();
                SdkConstants.MOBILENUMBER = mobileNumber.getText().toString().trim();
                String panaaadhaar = mobileNumber.getText().toString().trim();
                if (!panaaadhaar.contains(" ") && panaaadhaar.length() == 10) {
                } else {
                    mobileNumber.setError(getResources().getString(R.string.mobileerror));
                    return;
                }
                if (bankspinner.getText() == null || bankspinner.getText().toString().trim().matches("")) {
                    bankspinner.setError(getResources().getString(R.string.select_bank_error));
                    return;
                }
                SdkConstants.BANK_NAME = bankspinner.getText().toString().trim();
                if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                    if (SdkConstants.firstCheck) {
                        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                            showLoader();
                            try {
                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                String pidData = respObj.getString("base64pidData");
                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("", balanaceInqueryAadharNo,
                                        bankIINNumber, mobileNumber.getText().toString().trim(), "ANDROD", bankspinner.getText().toString(), pidData, latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
                            showLoader();
                            try {
                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                String pidData = respObj.getString("base64pidData");
                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("", balanaceInqueryAadharNo,
                                        bankIINNumber, mobileNumber.getText().toString().trim(), "ANDROD", bankspinner.getText().toString(), pidData.toString().trim(), latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showLoader();
                            if (amountEnter.getText() == null || amountEnter.getText().toString().trim().matches("")) {
                                amountEnter.setError(getResources().getString(R.string.amount_error));
                                return;
                            }
                            try {
                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                String pidData = respObj.getString("base64pidData");
                                unifiedAepsRequestModel = new UnifiedAepsRequestModel(amountEnter.getText().toString().trim(), balanaceInqueryAadharNo,
                                        bankIINNumber, mobileNumber.getText().toString().trim(), "ANDROD", bankspinner.getText().toString(), pidData, latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, cashWithdrawalButton.getText().toString(), gatewayPriority);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(UnifiedAepsActivity.this, "Accept/Enable the Terms & Conditions for the further transaction", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if ((getIntent().getStringExtra("FAILEDVALUE") != null) && getIntent().getStringExtra("FAILEDVALUE").equalsIgnoreCase("FAILEDDATA")) {
            aadharNumber.setText(makePrettyString(SdkConstants.AADHAAR_NUMBER));
            bankspinner.setText(SdkConstants.BANK_NAME);
            mobileNumber.setText(SdkConstants.MOBILENUMBER);
            bankIINNumber = SdkConstants.bankIIN;
            aadharNumber.setEnabled(false);
            aadharNumber.setTextColor(getResources().getColor(R.color.grey));
            mobileNumber.setEnabled(false);
            mobileNumber.clearFocus();
            bankspinner.setEnabled(false);
            fingerprint.setEnabled(true);
            fingerprint.setClickable(true);
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            fingerprint.setColorFilter(color);
            submitButton.setBackground(getResources().getDrawable(R.drawable.button_submit));
            gatewayPriority = gatewayPriority + 1;
        }
    }
    private void getRDServiceClass() {
        //String accessClassName =  getIntent().getStringExtra("activity");
        //flagNameRdService = getIntent().getStringExtra("driverFlag");
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;//getIntent().getStringExtra("activity");
        flagNameRdService = SdkConstants.MANUFACTURE_FLAG;//getIntent().getStringExtra("driverFlag");
        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            driverActivity = targetActivity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void getLocation() {
        gpsTracker = new GpsTracker(UnifiedAepsActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            latLong = latitude + "," + longitude;
        } else {
            gpsTracker.showSettingsAlert();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                bankspinner.setText(bankIINValue.getBankName());
                bankIINNumber = bankIINValue.getIin();
                SdkConstants.BANK_NAME = bankIINValue.getBankName();
                SdkConstants.bankIIN = bankIINNumber;
                checkBalanceEnquiryValidation();
            }
            checkBalanceEnquiryValidation();
        } else if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                bankspinner.setText(bankIINValue.getBankName());
                bankIINNumber = bankIINValue.getIin();
                SdkConstants.bankIIN = bankIINNumber;
                SdkConstants.BANK_NAME = bankIINValue.getBankName();
                checkWithdrawalValidation();
            }
            checkWithdrawalValidation();
        } else if (requestCode == SdkConstants.REQUEST_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                Intent respIntent = new Intent();
                respIntent.putExtra(SdkConstants.responseData, SdkConstants.transactionResponse);
                setResult(Activity.RESULT_OK, respIntent);
                finish();
            }
            checkWithdrawalValidation();
        } else if (requestCode == 1) {
            hideLoader();
        }
    }
    @Override
    public void checkUnifiedResponseStatus(String status, String message, UnifiedTxnStatusModel miniStatementResponseModel) {
        if (status.equalsIgnoreCase("SUCCESS")) {
            //  statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsTransactionActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        } else {
            //  statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsTransactionActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        }
        finish();
    }
    @Override
    public void checkMSunifiedStatus(String status, String message, UnifiedTxnStatusModel miniStatementResponseModel) {
        if (status.equalsIgnoreCase("SUCCESS")) {
            Gson gson = new Gson();
            session.setFreshnessFactor("");
            //  statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsMiniStatementActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        } else {
            session.setFreshnessFactor("");
//           statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsTransactionActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        }
        finish();
    }
    @Override
    public void showLoader() {
        try {
            if (loadingView == null) {
                loadingView = new ProgressDialog(UnifiedAepsActivity.this);
                loadingView.setCancelable(false);
                loadingView.setMessage("Please Wait..");
            }
            loadingView.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void hideLoader() {
        try {
            if (loadingView != null) {
                loadingView.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkBalanceEnquiryValidation() {
        // TODO Auto-generated method stub
        if (mobileNumber.getText() != null && !mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(mobileNumber.getText().toString().trim()) == true && bankspinner.getText() != null
                && !bankspinner.getText().toString().trim().matches("")) {
            boolean status = false;
            if (adharbool == true) {
                String aadharNo = aadharNumber.getText().toString();
                if (aadharNo.contains("-")) {
                    aadharNo = aadharNo.replaceAll("-", "").trim();
                    status = Util.validateAadharNumber(aadharNo);
                }
            } else if (virtualbool == true) {
                String aadharVid = aadharVirtualID.getText().toString();
                if (aadharVid.contains("-")) {
                    aadharVid = aadharVid.replaceAll("-", "").trim();
                    status = Util.validateAadharVID(aadharVid);
                }
            }
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackground(getResources().getDrawable(R.drawable.button_submit));
        }
    }
    private void checkWithdrawalValidation() {
        // TODO Auto-generated method stub
        if (mobileNumber.getText() != null
                && !mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(mobileNumber.getText().toString().trim()) == true
                && mobileNumber.getText().toString().length() == 10
                && bankspinner.getText() != null
                && !bankspinner.getText().toString().trim().matches("")
                && amountEnter.getText() != null
                && !amountEnter.getText().toString().trim().matches("")) {
            boolean status = false;
            if (adharbool == true) {
                String aadharNo = aadharNumber.getText().toString();
                if (aadharNo.contains("-")) {
                    aadharNo = aadharNo.replaceAll("-", "").trim();
                    status = Util.validateAadharNumber(aadharNo);
                }
            } else if (virtualbool == true) {
                String aadharVid = aadharVirtualID.getText().toString();
                if (aadharVid.contains("-")) {
                    aadharVid = aadharVid.replaceAll("-", "").trim();
                    status = Util.validateAadharVID(aadharVid);
                }
            }
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackground(getResources().getDrawable(R.drawable.button_submit));
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
                fingerprint.setColorFilter(color);
                fingerprint.setEnabled(true);
                fingerprint.setClickable(true);
                submitButton.setBackgroundResource(R.drawable.button_submit);
                submitButton.setEnabled(false);
            } else if (balanaceInqueryAadharNo.equalsIgnoreCase("") || balanaceInqueryAadharNo.isEmpty()) {
                aadharNumber.setError("Enter Aadhar No.");
                fingerStrength();
            } else if (mobileNumber.getText().toString().isEmpty() || mobileNumber.getText().toString().equalsIgnoreCase("")) {
                mobileNumber.setError("Enter mobile no.");
                fingerStrength();
            } else if (bankspinner.getText().toString().isEmpty() || bankspinner.getText().toString().trim().equalsIgnoreCase("")) {
                bankspinner.setError("Choose your bank.");
                fingerStrength();
            } else {
                fingerStrength();
                //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.F));
                fingerprint.setColorFilter(R.color.colorGrey);
                fingerprint.setEnabled(false);
            }
        }
    }
    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    //Finger Strength
    public void fingerStrength() {
        try {
            JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);

            String scoreStr = respObj.getString("pidata_qscore");
            if (scoreStr.contains(",")) {
                hideLoader();
                showAlert("Invalid Fingerprint Data");
            } else {

                submitButton.setEnabled(true);
                submitButton.setBackgroundResource(R.drawable.button_submit_blue);

                if (Float.parseFloat(scoreStr) <= 40) {
                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.red));
                    depositBar.setStartColor(getResources().getColor(R.color.red));
                    depositNote.setVisibility(View.VISIBLE);
                    fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
                } /*else if (Float.parseFloat(scoreStr) >= 30 && Float.parseFloat(scoreStr) <= 60) {

                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.yellow));
                depositBar.setStartColor(getResources().getColor(R.color.yellow));
                depositNote.setVisibility(View.VISIBLE);
                fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            }*/ else {

                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.green));
                    depositBar.setStartColor(getResources().getColor(R.color.green));
                    depositNote.setVisibility(View.VISIBLE);
                    fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Invalid Fingerprint Data");
        }
    }
    public void releaseData() {
        amountEnter.setText(null);
        amountEnter.setError(null);
        aadharNumber.setText(null);
        aadharNumber.setError(null);
        mobileNumber.setText(null);
        mobileNumber.setError(null);
        bankspinner.setText(null);
        bankspinner.setError(null);
        bankIINNumber = "";
        unifiedAepsRequestModel = null;
        depositBar.setVisibility(View.GONE);
        depositNote.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
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
                                    SdkConstants.userNameFromCoreApp = userName;
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
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showUserOnboardStatus(final String statusDesc) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UnifiedAepsActivity.this);
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
    public void showAlert(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(UnifiedAepsActivity.this);
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
    public void statusNotification(String title, String body, Class intnetClass, TransactionStatusModel raw) {
        if (SdkConstants.showNotification) {
            NotificationCompat.Builder builder = notificationHelper.createTransactionStatusNotif(this,
                    title,
                    body,
                    intnetClass,
                    raw);
            if (builder != null) {
//                notificationHelper.create(0, builder);
            }
        }
    }
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
            Toast.makeText(UnifiedAepsActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}