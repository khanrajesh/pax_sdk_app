package com.matm.matmsdk.aepsmodule;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.Utils.GetUserAuthToken;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryRequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryResponse;
import com.matm.matmsdk.aepsmodule.balanceenquiry.CoreBalanceEnquiryContract;
import com.matm.matmsdk.aepsmodule.balanceenquiry.CoreBalanceEnquiryPresenter;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameListActivity;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameModel;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CashWithdrawalRequestModel;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CashWithdrawalResponse;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CoreCashWithDrawalContract;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.CoreCashWithdrawalPresenter;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusModel;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusNewActivity;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.notification.NotificationHelper;
import com.moos.library.HorizontalProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import isumatm.androidsdk.equitas.R;


public class CoreAEPSHomeActivity extends AppCompatActivity implements CoreBalanceEnquiryContract.View, CoreCashWithDrawalContract.View {
    final static String MARKER = "|"; // filtered in layout not to be in the string
    Boolean adharbool = true;
    Boolean virtualbool = false;
    EditText aadharNumber, aadharVirtualID;
    RelativeLayout main_layout;
    AppCompatCheckBox terms;
    EditText mobileNumber, bankspinner, amountEnter;
    Session session;
    BalanceEnquiryRequestModel balanceEnquiryRequestModel;
    CashWithdrawalRequestModel cashWithdrawalRequestModel;
    String bankIINNumber = "";
    TextView virtualidText, aadharText;
    ProgressDialog loadingView;
    boolean mInside = false;
    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    String flagNameRdService = "";
    Class driverActivity;
    String balanaceInqueryAadharNo = "";
    Boolean flagFromDriver = false;
    String vid = "", uid = "";
    TypedValue typedValue;
    Resources.Theme theme;
    private TextView balanceEnquiryExpandButton, cashWithdrawalButton, fingerprintStrengthDeposit, depositNote;
    private ImageView fingerprint, virtualID, aadhaar;
    private HorizontalProgressView depositBar;
    private Button submitButton;
    private CoreBalanceEnquiryPresenter balanceEnquiryPresenter;
    private CoreCashWithdrawalPresenter cashWithdrawalPresenter;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this);
        if (SdkConstants.dashboardLayout == 0) {
            setContentView(R.layout.activity_aeps_home);
        } else {
            setContentView(SdkConstants.dashboardLayout);
        }

        getRDServiceClass();

        typedValue = new TypedValue();
        theme = this.getTheme();

        //notification 22 july
        notificationHelper = new NotificationHelper(this);

        session = new Session(CoreAEPSHomeActivity.this);
        session.setFreshnessFactor(SdkConstants.FRESHNESS_FACTOR);
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
        main_layout = findViewById(R.id.main_layout);
        SdkConstants.RECEIVE_DRIVER_DATA = "";
        fingerprintStrengthDeposit = findViewById(R.id.fingerprintStrengthDeposit);
        depositNote = findViewById(R.id.depositNote);
        depositNote.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
        aadharVirtualID = (EditText) findViewById(R.id.aadharVirtualID);
        aadharNumber = findViewById(R.id.aadharNumber);
        mobileNumber = findViewById(R.id.mobileNumber);
        bankspinner = findViewById(R.id.bankspinner);
        amountEnter = findViewById(R.id.amountEnter);
        fingerprint = findViewById(R.id.fingerprint);
        virtualidText = findViewById(R.id.virtualidText);
        aadharText = findViewById(R.id.aadharText);
        virtualID = findViewById(R.id.virtualID);
        aadhaar = findViewById(R.id.aadhaar);
        submitButton = findViewById(R.id.submitButton);
        depositBar = findViewById(R.id.depositBar);
        depositBar.setVisibility(View.GONE);
        fingerprint.setClickable(false);
        terms = findViewById(R.id.terms);

        cashWithdrawalButton = findViewById(R.id.cashWithdrawalButton);
        balanceEnquiryExpandButton = findViewById(R.id.balanceEnquiryExpandButton);

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
            if (amountEnter.getText() == null || amountEnter.getText().toString().trim().matches("")) {
                amountEnter.setEnabled(true);
            } else {
                amountEnter.setEnabled(false);
            }

        }


        bankspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showLoader();
                Intent in = new Intent(CoreAEPSHomeActivity.this, BankNameListActivity.class);
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
                //  fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner_grey));
                fingerprint.setColorFilter(R.color.colorGrey);

                flagFromDriver = true;
                Intent launchIntent = new Intent(CoreAEPSHomeActivity.this, driverActivity);
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
                    fingerprint.setClickable(true);
                    // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
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
                            //  fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            fingerprint.setColorFilter(color);
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

                Log.v("SUBHA", "count == " + count);
                int currentPos = aadharVirtualID.getSelectionStart();
                String string = aadharVirtualID.getText().toString().toUpperCase();
                String newString = makePrettyString(string);

                Log.v("SUBHA", "count == " + string.length());
                if (count == 19) {
                    fingerprint.setEnabled(true);
                    fingerprint.setClickable(true);
                    // fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
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
                            //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                            int color = typedValue.data;
                            fingerprint.setColorFilter(color);
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
                if (s.length() < 10) {
                    mobileNumber.setError(getResources().getString(R.string.mobileerror));
                }
                if (s.length() > 0) {
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
                //  virtualidText.setTextColor(getResources().getColor(R.color.light_blue));
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
                adharbool = true;
                virtualbool = false;
                virtualID.setBackgroundResource(R.drawable.ic_language);
                virtualidText.setTextColor(getResources().getColor(R.color.grey));
                aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                aadharText.setTextColor(color);
                //aadharText.setTextColor(getResources().getColor(R.color.light_blue));
            }
        });

        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SdkConstants.firstCheck = true;
                showTermsDetails(CoreAEPSHomeActivity.this);
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
                    if (Util.validateAadharVID(balanceaadharVid) == false) {
                        aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }

                }
                if (!flagFromDriver) {
                    Toast.makeText(CoreAEPSHomeActivity.this, "Please do Biometric Verification", Toast.LENGTH_LONG).show();
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
                if (SdkConstants.firstCheck == true) {
                    if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {

                        //========Rajesh
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

                            String aadhar = "";
                            if (adharbool == true) {
                                aadhar = aadharNumber.getText().toString().trim();
                            } else if (virtualbool == true) {
                                aadhar = aadharVirtualID.getText().toString().trim();
                            }

                            balanceEnquiryRequestModel = new BalanceEnquiryRequestModel("", balanaceInqueryAadharNo, CI, DC, "", DPID, DATAVALUE, session.getFreshnessFactor(), HMAC, bankIINNumber, MC, MI, mobileNumber.getText().toString().trim(), "", RDSID, RDSVER, value, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC);
                            balanceEnquiryPresenter = new CoreBalanceEnquiryPresenter(CoreAEPSHomeActivity.this, CoreAEPSHomeActivity.this);
                            balanceEnquiryPresenter.performBalanceEnquiry(aadhar, session.getUserToken(), balanceEnquiryRequestModel);
//                            SdkConstants.firstCheck = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        showLoader();
                        if (amountEnter.getText() == null || amountEnter.getText().toString().trim().matches("")) {
                            amountEnter.setError(getResources().getString(R.string.amount_error));
                            return;
                        }
                        //=======Rajesh
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

                            cashWithdrawalRequestModel = new CashWithdrawalRequestModel(amountEnter.getText().toString(), balanaceInqueryAadharNo, CI, DC, "", DPID, DATAVALUE, session.getFreshnessFactor(), HMAC, bankIINNumber, MC, MI, mobileNumber.getText().toString().trim(), "WITHDRAW", RDSID, RDSVER, value, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC);
                            cashWithdrawalPresenter = new CoreCashWithdrawalPresenter(CoreAEPSHomeActivity.this);
                            cashWithdrawalPresenter.performCashWithdrawal(session.getUserToken(), cashWithdrawalRequestModel);
//                            SdkConstants.firstCheck = false;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {


                    Toast.makeText(CoreAEPSHomeActivity.this, "Accept/Enable the Terms & Conditions for the further transaction", Toast.LENGTH_SHORT).show();
                }


            }
        });


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

    //-----------RAJESH---------------
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
                //  fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner_grey));
                fingerprint.setColorFilter(R.color.colorGrey);
                fingerprint.setEnabled(false);

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


    @Override
    public void checkBalanceEnquiryStatus(String status, String message, BalanceEnquiryResponse balanceEnquiryResponse) {
        String aadhar = "";
        if (adharbool == true) {
            aadhar = aadharNumber.getText().toString().trim();
        } else if (virtualbool == true) {
            aadhar = aadharVirtualID.getText().toString().trim();
        }
        showLoader();
        finish();
//        releaseData();
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
            SdkConstants.FRESHNESS_FACTOR = balanceEnquiryResponse.getNextFreshnessFactor();

            Gson g = new Gson();
            String jsonString = g.toJson(transactionStatusModel);
            SdkConstants.transactionResponse = jsonString;//transactionStatusModel.toString().replace("TransactionStatusModel","");
            statusNotification("Success", "Balance Enquiry", TransactionStatusNewActivity.class, transactionStatusModel);
            Intent intent = new Intent(CoreAEPSHomeActivity.this, TransactionStatusNewActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, transactionStatusModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            //startActivityForResult (intent, SdkConstants.BALANCE_RELOAD);
            hideLoader();
            startActivity(intent);
        } else {
            transactionStatusModel = null;
            session.setFreshnessFactor(null);
            SdkConstants.FRESHNESS_FACTOR = null;
            session.clear();
            showAlert(message);
        }

    }

    @Override
    public void checkBalanceEnquiryAEPS2(String status, String message, AepsResponse balanceEnquiryResponse) {

    }

    @Override
    public void checkCashWithdrawalStatus(String status, String message, CashWithdrawalResponse cashWithdrawalResponse) {
        String aadhar = "";

        if (adharbool == true) {
            aadhar = aadharNumber.getText().toString().trim();
        } else if (virtualbool == true) {
            aadhar = aadharVirtualID.getText().toString().trim();
        }
        String amount = amountEnter.getText().toString().trim();
        showLoader();
        finish();
        //releaseData();
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
            SdkConstants.FRESHNESS_FACTOR = cashWithdrawalResponse.getNextFreshnessFactor();

            Gson g = new Gson();
            String jsonString = g.toJson(transactionStatusModel);
            SdkConstants.transactionResponse = jsonString;//transactionStatusModel.toString().replace("TransactionStatusModel","");
            statusNotification("Success", "Balance Enquiry", TransactionStatusNewActivity.class, transactionStatusModel);
            Intent intent = new Intent(CoreAEPSHomeActivity.this, TransactionStatusNewActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, transactionStatusModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        } else {
            transactionStatusModel = null;
            session.setFreshnessFactor(null);
            SdkConstants.FRESHNESS_FACTOR = null;
            session.clear();
            showAlert(message);
        }

    }

    @Override
    public void checkCashWithdrawalAEPS2(String status, String message, AepsResponse cashWithdrawalResponse) {

    }

    @Override
    public void checkEmptyFields() {
        Toast.makeText(CoreAEPSHomeActivity.this, "Kindly get Registered with AEPS to proceed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoader() {
        try {
            if (loadingView == null) {
                loadingView = new ProgressDialog(CoreAEPSHomeActivity.this);
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

            if (status) {

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


            if (status) {

            }
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackgroundResource(R.drawable.button_submit);
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

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(CoreAEPSHomeActivity.this);
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

        }
    }


    private void retriveUserList() {
        final PackageManager packageManager = this.getPackageManager();
        Intent intent = getIntent();
        List<ResolveInfo> packages = packageManager.queryIntentActivities(intent, 0);

        String pkgName = "";
        for (ResolveInfo res : packages) {
            pkgName = res.activityInfo.packageName;
            Log.w("Package Name: ", pkgName);
        }


        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);

        } else {
            if (SdkConstants.encryptedData.trim().length() != 0 && SdkConstants.paramA.trim().length() != 0 && SdkConstants.paramB.trim().length() != 0 && SdkConstants.transactionType.trim().length() != 0 && SdkConstants.loginID.trim().length() != 0) {
                /*
                 * @author Subhashree
                 * Separated the Authentication
                 * */
                new GetUserAuthToken(CoreAEPSHomeActivity.this);
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
            Toast.makeText(CoreAEPSHomeActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}

