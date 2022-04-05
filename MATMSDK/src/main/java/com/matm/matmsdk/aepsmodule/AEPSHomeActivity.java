package com.matm.matmsdk.aepsmodule;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.Utils.GetUserAuthToken;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryContract;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryPresenter;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryRequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryResponse;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameListActivity;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameModel;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CashWithDrawalContract;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CashWithdrawalPresenter;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CashWithdrawalRequestModel;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CashWithdrawalResponse;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusModel;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusNewActivity;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.notification.NotificationHelper;
import com.moos.library.HorizontalProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import isumatm.androidsdk.equitas.R;

public class AEPSHomeActivity extends AppCompatActivity implements BalanceEnquiryContract.View, CashWithDrawalContract.View {

    Boolean adharbool = true;
    Boolean virtualbool = false;
    EditText aadharNumber, aadharVirtualID;
    private TextView balanceEnquiryExpandButton, cashWithdrawalButton, fingerprintStrengthDeposit, depositNote;
    EditText mobileNumber, bankspinner, amountEnter;
    private ImageView fingerprint, virtualID, aadhaar;
    private HorizontalProgressView depositBar;
    private Button submitButton;
    TextView terms;

    private BalanceEnquiryPresenter balanceEnquiryPresenter;
    private CashWithdrawalPresenter cashWithdrawalPresenter;
    Session session;
    BalanceEnquiryRequestModel balanceEnquiryRequestModel;
    CashWithdrawalRequestModel cashWithdrawalRequestModel;

    String bankIINNumber = "";
    ProgressDialog loadingView;
    TextView virtualidText, aadharText;
    boolean mInside = false;
    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    final static String MARKER = "|"; // filtered in layout not to be in the string

    String flagNameRdService = "";
    Class driverActivity;
    String balanaceInqueryAadharNo = "";
    Boolean flagFromDriver = false;
    String vid = "", uid = "";
    Resources.Theme theme;
    TypedValue typedValue;

    private NotificationHelper notificationHelper;

    SharedPreferences sharedPreferences;
    public static final String MATM_PREF = "matmPref";
    public static final String MATM_USER = "userPref";
    public static final String MATM_TOKEN = "tokenPref";
    public static final String MATM_TIME = "timePref";
    private static final String pattern = "dd-MM-yyyy HH:mm:ss";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this); //For theme Customization

        /*For Layout customization */
        if (SdkConstants.dashboardLayout == 0) {
            setContentView(R.layout.activity_aeps_home);
        } else {
            setContentView(SdkConstants.dashboardLayout);
        }

//        if(SdkConstants.paramA.equals("")){
//            showAlert("ParamA is missing.\nContact to your admin");
//        }
        sharedPreferences = getSharedPreferences(MATM_PREF, Context.MODE_PRIVATE);


        session = new Session(AEPSHomeActivity.this);
        getRDServiceClass();
        SdkConstants.RECEIVE_DRIVER_DATA = "";
        //notification 22 july
        notificationHelper = new NotificationHelper(this);

        typedValue = new TypedValue();
        theme = this.getTheme();

        SdkConstants.RECEIVE_DRIVER_DATA = "";
        retriveUserList();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        aadharNumber = (EditText) findViewById(R.id.aadharNumber);
        aadharVirtualID = (EditText) findViewById(R.id.aadharVirtualID);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        bankspinner = (EditText) findViewById(R.id.bankspinner);
        amountEnter = (EditText) findViewById(R.id.amountEnter);
        virtualidText = findViewById(R.id.virtualidText);
        aadharText = findViewById(R.id.aadharText);
        fingerprint = findViewById(R.id.fingerprint);
        fingerprint.setEnabled(false);
        virtualID = findViewById(R.id.virtualID);
        aadhaar = findViewById(R.id.aadhaar);
        submitButton = findViewById(R.id.submitButton);
        depositBar = findViewById(R.id.depositBar);
        depositBar.setVisibility(View.GONE);
        terms = findViewById(R.id.terms);
        terms.setVisibility(View.GONE);
        balanceEnquiryExpandButton = findViewById(R.id.balanceEnquiryExpandButton);
        cashWithdrawalButton = findViewById(R.id.cashWithdrawalButton);

        virtualID.setBackgroundResource(R.drawable.ic_language);
        virtualidText.setTextColor(getResources().getColor(R.color.grey));

        aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
        // aadharText.setTextColor(getResources().getColor(R.color.light_blue));

        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
            balanceEnquiryExpandButton.setVisibility(View.VISIBLE);
            cashWithdrawalButton.setVisibility(View.GONE);
            amountEnter.setVisibility(View.GONE);
        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
            balanceEnquiryExpandButton.setVisibility(View.GONE);
            amountEnter.setText(SdkConstants.transactionAmount);
        }

        amountEnter.setEnabled(SdkConstants.editable);

        bankspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showLoader ();
                Intent in = new Intent(AEPSHomeActivity.this, BankNameListActivity.class);
                if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE);

                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE);

                }
            }
        });

        fingerprint.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                showLoader();
                fingerprint.setEnabled(false);
                // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner_grey));
                fingerprint.setColorFilter(R.color.colorGrey);

                flagFromDriver = true;
                Intent launchIntent = new Intent(AEPSHomeActivity.this, driverActivity);
                launchIntent.putExtra("driverFlag", flagNameRdService);
                launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                launchIntent.putExtra("AadharNo", balanaceInqueryAadharNo);
                startActivityForResult(launchIntent, 1);


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
                    // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    fingerprint.setColorFilter(color);

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
                            // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            fingerprint.setColorFilter(color);
                            aadharNumber.clearFocus();
                            mobileNumber.requestFocus();
                            makeAadharNonReadable();
                        }
                    }
                    if (Util.validateAadharNumber(balanaceInqueryAadharNo) == false) {
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

                Log.v("SUBHA", "count == " + count);
                int currentPos = aadharVirtualID.getSelectionStart();
                String string = aadharVirtualID.getText().toString().toUpperCase();
                String newString = makePrettyString(string);

                Log.v("SUBHA", "count == " + string.length());
                if (count == 19) {
                    fingerprint.setEnabled(true);
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
                            //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            fingerprint.setColorFilter(color);
                            aadharVirtualID.clearFocus();
                            mobileNumber.requestFocus();
                            makeVirtualIdAadharNonReadable();

                        }
                    }
                    if (Util.validateAadharVID(balanaceInqueryAadharNo) == false) {
                        aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar__uid_error));
                    }
                }
            }

        });


        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsDetails(AEPSHomeActivity.this);
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
                }
                if (s.length() > 0) {
                    bankspinner.setError(null);
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
                // virtualidText.setTextColor(getResources().getColor(R.color.light_blue));
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
                // aadharText.setTextColor(getResources().getColor(R.color.light_blue));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                aadharText.setTextColor(color);
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
                } else {

                }
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showLoader ();
                String withdrawalaadharNo = "";
                String balanceaadharVid = "";
                if (adharbool == true) {
//                    withdrawalaadharNo = aadharNumber.getText().toString();
//                    if (withdrawalaadharNo.contains("-")) {
//                        withdrawalaadharNo = withdrawalaadharNo.replaceAll("-", "").trim();
//                    }
//                    if (withdrawalaadharNo == null || withdrawalaadharNo.matches("")) {
//                        aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
//                        return;
//                    }
//                    if (Util.validateAadharNumber(withdrawalaadharNo) == false) {
//                        aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
//                        return;
//                    }
//                    if (aadharNumber.getText().toString().length() < 14) {
//                        aadharNumber.setError("Enter valid aadhaar no.");
//                        return;
//                    }

                } else if (virtualbool == true) {
//                    balanceaadharVid = balanaceInqueryAadharNo;
//                    if (balanceaadharVid.contains("-")) {
//                        balanceaadharVid = balanceaadharVid.replaceAll("-", "").trim();
//                    }
//                    if (balanceaadharVid == null || balanceaadharVid.matches("")) {
//                        aadharVirtualID.setError(getResources().getString(R.string.valid_vid_error));
//                        return;
//                    }
//                    if (Util.validateAadharVID(balanceaadharVid) == false) {
//                        aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar_error));
//                        return;
//                    }

                }
                if (!flagFromDriver) {
                    Toast.makeText(AEPSHomeActivity.this, "Please do Biometric Varification", Toast.LENGTH_LONG).show();
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
//                if (SdkConstants.firstCheck == true && SdkConstants.secondCheck == true) {
                if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                    showLoader();
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
                        balanceEnquiryRequestModel = new BalanceEnquiryRequestModel("", balanaceInqueryAadharNo, CI, DC, "", DPID, DATAVALUE, session.getFreshnessFactor(), HMAC, bankIINNumber, MC, MI, mobileNumber.getText().toString().trim(), "", RDSID, RDSVER, value, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC);
                        balanceEnquiryPresenter = new BalanceEnquiryPresenter(AEPSHomeActivity.this);
                        balanceEnquiryPresenter.performBalanceEnquiry(AEPSHomeActivity.this, SdkConstants.USER_NAME, SdkConstants.USER_TOKEN, balanceEnquiryRequestModel);
//                        SdkConstants.firstCheck = false;
//                        SdkConstants.secondCheck = false;
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

                        cashWithdrawalRequestModel = new CashWithdrawalRequestModel(amountEnter.getText().toString().trim(), balanaceInqueryAadharNo, CI, DC, "", DPID, DATAVALUE, session.getFreshnessFactor(), HMAC, bankIINNumber, MC, MI, mobileNumber.getText().toString().trim(), "WITHDRAW", RDSID, RDSVER, value, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC);
                        cashWithdrawalPresenter = new CashWithdrawalPresenter(AEPSHomeActivity.this);
                        cashWithdrawalPresenter.performCashWithdrawal(session.getUserName(), session.getUserToken(), cashWithdrawalRequestModel);
//                        SdkConstants.firstCheck = false;
//                        SdkConstants.secondCheck = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                /*}else {


                    showTermsDetails(AEPSHomeActivity.this);
                }
*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void getRDServiceClass() {
        // String accessClassName =  getIntent().getStringExtra("activity");
        // flagNameRdService = getIntent().getStringExtra("driverFlag");
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;//getIntent().getStringExtra("activity");
        flagNameRdService = SdkConstants.MANUFACTURE_FLAG;//getIntent().getStringExtra("driverFlag");


        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            driverActivity = targetActivity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //-----------RAJESH---------------
    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
        if (flagFromDriver) {
            if (SdkConstants.RECEIVE_DRIVER_DATA.isEmpty() || SdkConstants.RECEIVE_DRIVER_DATA.equalsIgnoreCase("")) {
                //  fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                fingerprint.setColorFilter(color);
                fingerprint.setEnabled(true);
                submitButton.setBackgroundResource(R.drawable.button_submit);
                submitButton.setEnabled(false);
                fingerStrength();
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
                // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner_grey));
                fingerprint.setColorFilter(R.color.colorGrey);
                fingerprint.setEnabled(false);
                submitButton.setEnabled(true);
                submitButton.setBackgroundResource(R.drawable.button_submit_blue);
            }

        }

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
                checkBalanceEnquiryValidation();

            }
            checkBalanceEnquiryValidation();

        } else if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_CASH_DEPOSIT_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                bankspinner.setText(bankIINValue.getBankName());
                bankIINNumber = bankIINValue.getIin();
            }
        } else if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                bankspinner.setText(bankIINValue.getBankName());
                bankIINNumber = bankIINValue.getIin();
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
            //Toast.makeText(this, "I am Here....", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void checkBalanceEnquiryStatus(String status, String message, BalanceEnquiryResponse balanceEnquiryResponse) {
        String aadhar = "";
        if (adharbool == true) {
//            aadhar = aadharNumber.getText().toString().trim();
            aadhar = balanaceInqueryAadharNo;
        } else if (virtualbool == true) {
//            aadhar = aadharVirtualID.getText().toString().trim();
            aadhar = balanaceInqueryAadharNo;
        }
        releaseData();
        TransactionStatusModel transactionStatusModel = new TransactionStatusModel();
        if (balanceEnquiryResponse != null) {
            transactionStatusModel.setAadharCard(aadhar);
            transactionStatusModel.setBankName(balanceEnquiryResponse.getBankName());
            transactionStatusModel.setBalanceAmount(balanceEnquiryResponse.getBalance());
            transactionStatusModel.setReferenceNo(balanceEnquiryResponse.getReferenceNo());
            transactionStatusModel.setTransactionType("Balance Enquiry");
            transactionStatusModel.setStatus(balanceEnquiryResponse.getStatus());
            transactionStatusModel.setApiComment(balanceEnquiryResponse.getApiComment());
            transactionStatusModel.setStatusDesc(balanceEnquiryResponse.getStatusDesc());
            transactionStatusModel.setTxnID(balanceEnquiryResponse.getTxId());
            session.setFreshnessFactor(balanceEnquiryResponse.getNextFreshnessFactor());

            Gson g = new Gson();
            String jsonString = g.toJson(transactionStatusModel);
            SdkConstants.transactionResponse = jsonString;//transactionStatusModel.toString().replace("TransactionStatusModel","");
            statusNotification("Success", "Balance Enquiry", TransactionStatusNewActivity.class, transactionStatusModel);
            Intent intent = new Intent(AEPSHomeActivity.this, TransactionStatusNewActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY,transactionStatusModel);
            finish();
            startActivity(intent);

        } else {
            transactionStatusModel = null;
            session.setFreshnessFactor(null);
            session.clear();
            showAlert(message);
        }

    }

    @Override
    public void checkBalanceEnquiryAEPS2(String status, String message, AepsResponse balanceEnquiryResponse) {

    }


    @Override
    public void checkStatementEnquiryAEPS2(String status, String message, JSONObject statementResponse) {

    }


    @Override
    public void checkCashWithdrawalStatus(String status, String message, CashWithdrawalResponse cashWithdrawalResponse) {
        String aadhar = "";
        if (adharbool == true) {
//            aadhar = aadharNumber.getText().toString().trim();
            aadhar = balanaceInqueryAadharNo;
        } else if (virtualbool == true) {
//            aadhar = aadharVirtualID.getText().toString().trim();
            aadhar = balanaceInqueryAadharNo;
        }
        String amount = amountEnter.getText().toString().trim();
        releaseData();
        TransactionStatusModel transactionStatusModel = new TransactionStatusModel();
        if (cashWithdrawalResponse != null) {
            transactionStatusModel.setAadharCard(aadhar);
            transactionStatusModel.setBankName(cashWithdrawalResponse.getBankName());
            transactionStatusModel.setBalanceAmount(cashWithdrawalResponse.getBalance());
            transactionStatusModel.setReferenceNo(cashWithdrawalResponse.getReferenceNo());
            transactionStatusModel.setTransactionAmount(amount);
            transactionStatusModel.setTransactionType("Cash Withdrawal");
            transactionStatusModel.setStatus(cashWithdrawalResponse.getStatus());
            transactionStatusModel.setApiComment(cashWithdrawalResponse.getApiComment());
            transactionStatusModel.setStatusDesc(cashWithdrawalResponse.getStatusDesc());
            transactionStatusModel.setTxnID(cashWithdrawalResponse.getTxId());
            session.setFreshnessFactor(cashWithdrawalResponse.getNextFreshnessFactor());

            Gson g = new Gson();
            String jsonString = g.toJson(transactionStatusModel);
            SdkConstants.transactionResponse = jsonString;//transactionStatusModel.toString().replace("TransactionStatusModel","");
            statusNotification("Success", "Transaction Amount "+cashWithdrawalResponse.getBalance(), TransactionStatusNewActivity.class, transactionStatusModel);
            Intent intent = new Intent(AEPSHomeActivity.this, TransactionStatusNewActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY,transactionStatusModel);
            //startActivityForResult (intent, SdkConstants.BALANCE_RELOAD);
            finish();
            startActivity(intent);
        }else{
            transactionStatusModel = null;
            session.setFreshnessFactor ( null );
            session.clear();
            showAlert(message);
        }

    }

    @Override
    public void checkCashWithdrawalAEPS2(String status, String message, AepsResponse cashWithdrawalResponse) {

    }

    @Override
    public void checkEmptyFields() {
        Toast.makeText(AEPSHomeActivity.this, "Kindly get Registered with AEPS to proceed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoader() {
        if (loadingView == null) {
            loadingView = new ProgressDialog(AEPSHomeActivity.this);
            loadingView.setCancelable(false);
            loadingView.setMessage("Please Wait..");
        }
        loadingView.show();
    }

    @Override
    public void hideLoader() {
        if (loadingView != null) {
            loadingView.dismiss();
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

    /*
     *  url for the sync of the data for the
     */

    private String getAuthURL(String UID) {
        String url = "http://developer.uidai.gov.in/auth/";
        url += "public/" + UID.charAt(0) + "/" + UID.charAt(1) + "/";
        url += "MG41KIrkk5moCkcO8w-2fc01-P7I5S-6X2-X7luVcDgZyOa2LXs3ELI"; //ASA
        return url;
    }

    private void checkBalanceEnquiryValidation() {
        // TODO Auto-generated method stub
        if (mobileNumber.getText() != null && !mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(mobileNumber.getText().toString().trim()) == true && bankspinner.getText() != null
                && !bankspinner.getText().toString().trim().matches("")) {

            boolean status = false;


            if (adharbool == true) {
                String aadharNo = balanaceInqueryAadharNo;
//                if (aadharNo.contains("-")) {
//                    aadharNo = aadharNo.replaceAll("-", "").trim();
//                    status = Util.validateAadharNumber(aadharNo);
//                }
            } else if (virtualbool == true) {
                String aadharVid = aadharVirtualID.getText().toString();
//                if (aadharVid.contains("-")) {
//                    aadharVid = aadharVid.replaceAll("-", "").trim();
//                    status = Util.validateAadharVID(aadharVid);
//                }
            }

            if (status) {
                //
            }
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackgroundResource(R.drawable.button_submit);
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

            if (adharbool) {
//                String aadharNo = aadharNumber.getText().toString();
//                if (aadharNo.contains("-")) {
//                    aadharNo = aadharNo.replaceAll("-", "").trim();
//                    status = Util.validateAadharNumber(aadharNo);
//                }
            } else if (virtualbool) {
//                String aadharVid = aadharVirtualID.getText().toString();
//                if (aadharVid.contains("-")) {
//                    aadharVid = aadharVid.replaceAll("-", "").trim();
//                    status = Util.validateAadharVID(aadharVid);
//                }
            }
            if (status) {
                //

            }
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackgroundResource(R.drawable.button_submit);
        }

    }


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


    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        balanceEnquiryRequestModel = null;
        cashWithdrawalRequestModel = null;

        depositBar.setVisibility(View.GONE);
        depositNote.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
    }

    public void showAlert(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AEPSHomeActivity.this);
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

    private void retriveUserList() {
        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);

        } else {
            if (SdkConstants.encryptedData.trim().length() != 0 && SdkConstants.paramA.trim().length() != 0 && SdkConstants.paramB.trim().length() != 0 && SdkConstants.transactionType.trim().length() != 0 && SdkConstants.loginID.trim().length() != 0) {
                /*String prefUser = sharedPreferences.getString(MATM_USER, "");
            if (prefUser.equals("") || !prefUser.equals(SdkConstants.loginID)){
                //fetchToken
                getSdkToken();
            } else {
                String oldDate = sharedPreferences.getString(MATM_TIME, "");
                Date newDate = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                if (expired(oldDate, formatter.format(newDate))){
                    getSdkToken();
                } else {
                    SdkConstants.isSL = true;//As this is a single layer application
                    SdkConstants.token = sharedPreferences.getString(MATM_TOKEN, "");
                    session.setUserToken(SdkConstants.token);
                    session.setUsername(SdkConstants.loginID);
                }
            }*/

                /*
                 * @author Subhashree
                 * Separated the Authentication
                 * */
                new GetUserAuthToken(AEPSHomeActivity.this);
                session.setUsername(SdkConstants.USER_NAME);
                session.setUserToken(SdkConstants.USER_TOKEN);
            } else {
                showAlert("Request parameters are missing. Please check and try again..");
            }
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


            AppCompatCheckBox firstText = (AppCompatCheckBox) dialog.findViewById(R.id.firststText);
            AppCompatCheckBox secondText = (AppCompatCheckBox) dialog.findViewById(R.id.secondstText);

            SwitchCompat switchCompat = (SwitchCompat) dialog.findViewById(R.id.swOnOff);

            firstText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    firstText.setChecked(true);
                    SdkConstants.secondCheck = true;
                }
            });

            secondText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    secondText.setChecked(true);
                    SdkConstants.firstCheck = true;
                }
            });


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

                    if (SdkConstants.firstCheck == true && SdkConstants.secondCheck == true) {
                        dialog.cancel();
                    }


                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AEPSHomeActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
    private void makeAadharNonReadable() {
        String maskAadhaar = "XXXX XXXX " + balanaceInqueryAadharNo.substring(8);
        aadharNumber.setText(maskAadhaar);
    }
    private void makeVirtualIdAadharNonReadable() {
        String maskVirtualAadhaarNo = "XXXX XXXX " + balanaceInqueryAadharNo.substring(8);
        aadharVirtualID.setText(maskVirtualAadhaarNo);
    }
    private boolean expired(String genDate, String currentDate){
        boolean exp = true;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date d1 = sdf.parse(genDate);
            Date d2 = sdf.parse(currentDate);
            long difference = d2.getTime() - d1.getTime();
            long diffHours = (difference / (1000 * 60 * 60));
            if (diffHours<=672){
                exp = false;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return exp;
    }
    private void getSdkToken(){
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
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(MATM_USER, userToken);
                                    editor.putString(MATM_TOKEN, SdkConstants.loginID);
                                    editor.putString(MATM_TIME, formatter.format(d));
                                    editor.apply();

                                    hideLoader();

                                } else {
                                    showAlert(status);
                                    hideLoader();
                                }

                            }
                            catch (JSONException e) {
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

