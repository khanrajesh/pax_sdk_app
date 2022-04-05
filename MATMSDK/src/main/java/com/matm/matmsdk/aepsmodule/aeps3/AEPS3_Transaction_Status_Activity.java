package com.matm.matmsdk.aepsmodule.aeps3;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.matm.matmsdk.Bluetooth.BluetoothConnectorActivity;
import com.matm.matmsdk.Bluetooth.BluetoothPrinter;
import com.matm.matmsdk.Dashboard.MainActivity;
import com.matm.matmsdk.FileUtils;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.Utils.Session;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusNewActivity;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.aepsmodule.utils.GetPosConnectedPrinter;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.notification.service.MyFirebaseMessagingService;
import com.matm.matmsdk.permission.PermissionsActivity;
import com.matm.matmsdk.permission.PermissionsChecker;
import com.matm.matmsdk.readfile.PreviewPDFActivity;
import com.matm.matmsdk.vriddhi.AEMPrinter;
import com.matm.matmsdk.vriddhi.AEMScrybeDevice;
import com.matm.matmsdk.vriddhi.CardReader;
import com.matm.matmsdk.vriddhi.IAemCardScanner;
import com.matm.matmsdk.vriddhi.IAemScrybe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import isumatm.androidsdk.equitas.R;

import static com.matm.matmsdk.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.matm.matmsdk.permission.PermissionsChecker.REQUIRED_PERMISSION;

public class AEPS3_Transaction_Status_Activity extends AppCompatActivity implements IAemCardScanner, IAemScrybe {

    private static BluetoothSocket btsocket;
    ImageView status_icon, sendButton, iv_pending;
    ImageButton backBtn;
    TextView balanceText, card_amount, bank_name, date_time, txnID, txndetails, pb_aeps3_tv;
    String txnAmount_pdf, balanceAmount_pdf;
    LinearLayout progress_container;
    String balanceAmt;
    ProgressBar aeps3_pb;
    EditText editTextMobile;
    CheckBox mobileCheckBox;
    Button printBtn, downloadBtn, closeBtn, btn_ok, btn_updateStatus;
    LinearLayout mobileEditLayout, mobileTextLayout;
    ProgressDialog progressDialog;
    Session session;
    String balance = "N/A";
    BluetoothDevice bluetoothDevice;
    String amount = "N/A";
    String transactionType = "N/A";
    String referenceNo = "N/A";
    String bankName = "N/A";
    String aadharCard = "N/A";
    String txnid = "N/A";
    PermissionsChecker checker;
    Context mContext;
    BluetoothAdapter bluetoothAdapter;
    String statusTxt;
    String mobile;
    String txnId = "N/A", txn_type, txn_status, TRANSACTION_STATUS_ERROR, TRANSACTION_ID_ERROR = "N/A", TRANSACTION_TYPE_ERROR = "N/A", API_STATUS, TXNAMOUNT = "N/A", userBankName_; // From previous Activity
    String date_ = "N/A", amount_ = "N/A", txnId_ = "N/A", adhaar_ = "N/A", status_ = "N/A", transactionMode_ = "N/A";//from FCM
    RelativeLayout rl_main;
    LinearLayout progressContainer;
    ArrayList<MinistatemenTxnList> list = new ArrayList<>();
    CountDownTimer CDT;
    boolean loaded = false;
    HashMap<String, String> mapTxn;
    private int STORAGE_PERMISSION_CODE = 1;
    private String filePath = "";

    AEMScrybeDevice m_AemScrybeDevice;
    AEMPrinter m_AemPrinter = null;
    CardReader m_cardReader = null;
    CardReader.CARD_TRACK cardTrackType;
    String creditData, tempdata, replacedData, data;
    ArrayList<String> printerList;
    String responseString, response;
    int numChars;
    String[] responseArray = new String[1];
    char[] printerStatus = new char[]{0x1B, 0x7E, 0x42, 0x50, 0x7C, 0x47, 0x45, 0x54, 0x7C, 0x50, 0x52, 0x4E, 0x5F, 0x53, 0x54, 0x5E};
    String printerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps3_transaction_status_);
        initView();

        m_AemScrybeDevice = new AEMScrybeDevice(AEPS3_Transaction_Status_Activity.this);
        printerList = new ArrayList<String>();
        creditData = new String();

        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
    }

    private void initView() {
        status_icon = findViewById(R.id.status_icon);
        sendButton = findViewById(R.id.sendButton);
        balanceText = findViewById(R.id.balanceText);
        card_amount = findViewById(R.id.card_amount);
        bank_name = findViewById(R.id.bank_name);
        date_time = findViewById(R.id.date_time);
        txnID = findViewById(R.id.txnID);
        txndetails = findViewById(R.id.txndetails);
        mobileCheckBox = findViewById(R.id.mobileCheckBox);
        printBtn = findViewById(R.id.printBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        closeBtn = findViewById(R.id.closeBtn);
        backBtn = findViewById(R.id.backBtn);
        mobileTextLayout = findViewById(R.id.mobileTextLayout);
        editTextMobile = findViewById(R.id.editTextMobile);
        mobileEditLayout = findViewById(R.id.mobileEditLayout);
        // progress_container = findViewById(R.id.progress_container);
        aeps3_pb = findViewById(R.id.aeps3_pb);
        pb_aeps3_tv = findViewById(R.id.pb_aeps3_tv);
        btn_ok = findViewById(R.id.btn_ok);
        btn_updateStatus = findViewById(R.id.btn_updateStatus);
        rl_main = findViewById(R.id.ll_maiin);
        progressContainer = findViewById(R.id.progress_container);
        iv_pending = findViewById(R.id.iv_aeps3_pending);

        mapTxn = new HashMap<>();

        if (getIntent().getExtras() != null) {
            txn_status = getIntent().getStringExtra("TRANSACTION_STATUS");
            txnId = getIntent().getStringExtra("TRANSACTION_ID");
            txn_type = getIntent().getStringExtra("TRANSACTION_TYPE");
            TRANSACTION_STATUS_ERROR = getIntent().getStringExtra("TRANSACTION_STATUS_ERROR");
            TRANSACTION_ID_ERROR = getIntent().getStringExtra("TRANSACTION_ID_ERROR");
            TRANSACTION_TYPE_ERROR = getIntent().getStringExtra("TRANSACTION_TYPE_ERROR");
            Log.e("TAG", "initView:----------: " + TRANSACTION_TYPE_ERROR + "initView--------:" + txn_type);
            userBankName_ = getIntent().getStringExtra("BANKNAME");
            API_STATUS = getIntent().getStringExtra("API_STATUS");
            TXNAMOUNT = getIntent().getStringExtra("TXNAMOUNT");

            if (TXNAMOUNT.length() == 0 && TXNAMOUNT.equals("") && TXNAMOUNT.equals(null)) {
                TXNAMOUNT = "N/A";
            }

            Log.e("TAG", "initView:------------------- " + TXNAMOUNT);
            bank_name.setText(userBankName_);
        }
        if (API_STATUS.equals("1")) {
            setLoaderForOneMinutes();
        } else {
            TransitionManager.beginDelayedTransition(rl_main);
            progressContainer.setVisibility(View.GONE);
            rl_main.setVisibility(View.VISIBLE);
            if (TRANSACTION_TYPE_ERROR.equalsIgnoreCase("ministatement")) {
                Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                intent.putExtra("status", "Failed.");
                intent.putExtra("statusDec", TRANSACTION_STATUS_ERROR);
                intent.putExtra("bankName", userBankName_);
                intent.putExtra("Date", date_time.getText().toString());
                intent.putExtra("txnId", TRANSACTION_ID_ERROR);
                intent.putExtra("adhaar", aadharCard);
                intent.putExtra("transactionMode", "Mini Statement");
                intent.putExtra("list_data", (Serializable) list);
                startActivity(intent);
                finish();
            }
            status_icon.setImageResource(R.drawable.hero_failure);
            mobileTextLayout.setVisibility(View.GONE);
            statusTxt = "FAILED";
            txnId = TRANSACTION_ID_ERROR;
            txnID.setText("Txn ID : " + TRANSACTION_ID_ERROR);
            transactionType = TRANSACTION_TYPE_ERROR;
            bank_name.setText(userBankName_);
            TXNAMOUNT = "N/A";
            balanceAmt = "N/A";
            balanceText.setText(TRANSACTION_STATUS_ERROR);
        }
        btn_updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callFetchdataAPI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Runtime permission request required if Android permission >= Marshmallow
        checker = new PermissionsChecker(this);
        mContext = getApplicationContext();
        mobile = getIntent().getStringExtra("MOBILE_NUMBER");
        editTextMobile.setText(mobile);

        session = new Session(AEPS3_Transaction_Status_Activity.this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        date_time.setText(currentDateandTime);


        txndetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionDetails(AEPS3_Transaction_Status_Activity.this);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent mainActivity = new Intent(AEPS3_Transaction_Status_Activity.this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(AEPS3_Transaction_Status_Activity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    Date date = new Date();
                    long timeMilli = date.getTime();
                    System.out.println("Time in milliseconds using Date class: " + String.valueOf(timeMilli));
                    createPdf(FileUtils.getAppPath(mContext) + String.valueOf(timeMilli) + "Order_Receipt.pdf");
                }
            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForContextMenu(printBtn);
                if (bluetoothAdapter == null) {
                    Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
                } else {
                    if (bluetoothAdapter.isEnabled()) {
                        if (GetPosConnectedPrinter.aemPrinter == null) {
                            printerList = m_AemScrybeDevice.getPairedPrinters();
                            if (printerList.size() > 0) {
                                openContextMenu(view);
                            } else {
                                showAlert("No Paired Printers found");
                            }
                        } else {
                            m_AemPrinter = GetPosConnectedPrinter.aemPrinter;
                            callBluetoothFunction(txnId, aadharCard, date_time.getText().toString(), bank_name.getText().toString(), referenceNo, card_amount.getText().toString(), amount, transactionType, view);
                        }
                    } else {
                        GetPosConnectedPrinter.aemPrinter = null;
                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnOn, 0);
                    }
                }

//                if (SdkConstants.Bluetoothname.equalsIgnoreCase("ESIAF3996")) {
//                    SdkConstants.bluetoothDevice = null;
//                    SdkConstants.Bluetoothname = "null";
//                } else if (SdkConstants.Bluetoothname.contains("BPFS")) {
//                    SdkConstants.bluetoothDevice = null;
//                    SdkConstants.Bluetoothname = "null";
//                } else if (SdkConstants.Bluetoothname.contains("BTprinter")) {
//                    SdkConstants.bluetoothDevice = null;
//                    SdkConstants.Bluetoothname = "null";
//                } else {
//                    bluetoothDevice = SdkConstants.bluetoothDevice;
//                }
//
//                if (bluetoothDevice != null) {
//
//
//                    if (!B.isEnabled()) {
//                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(turnOn, 0);
//                        finish();
//                        Toast.makeText(getApplicationContext(), "Your Bluetooth is OFF .", Toast.LENGTH_LONG).show();
//                    } else {
////                            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_LONG).show();
//                        callBluetoothFunction(txnId, aadharCard, date_time.getText().toString(), bank_name.getText().toString(), referenceNo, card_amount.getText().toString(), amount, transactionType, bluetoothDevice);
//                    }
//
//                } else {
//
//                    Intent in = new Intent(AEPS3_Transaction_Status_Activity.this, BluetoothConnectorActivity.class);
//                    startActivity(in);
//                }

            }
        });


        mobileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mobileEditLayout.setVisibility(View.VISIBLE);
                } else {
                    mobileEditLayout.setVisibility(View.GONE);
                }
            }
        });

        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 10) {
                    editTextMobile.setError(getResources().getString(R.string.mobileerror));
                }
                if (s.length() > 0) {
                    editTextMobile.setError(null);
                    String x = s.toString();
                    if (x.startsWith("0") || Util.isValidMobile(editTextMobile.getText().toString().trim()) == false) {
                        editTextMobile.setError(getResources().getString(R.string.mobilevaliderror));
                    }
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextMobile.getText() == null || editTextMobile.getText().toString().trim().matches("") || Util.isValidMobile(editTextMobile.getText().toString().trim()) == false) {
                    editTextMobile.setError(getResources().getString(R.string.mobileerror));
                } else {
                    showLoader();
                    mobileNumberSMS();
                }

            }
        });

    }

    private void callFetchdataAPI() throws JSONException {
        TransitionManager.beginDelayedTransition(rl_main);
        rl_main.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);
        btn_updateStatus.setVisibility(View.GONE);
        aeps3_pb.setVisibility(View.VISIBLE);
        iv_pending.setVisibility(View.GONE);
        pb_aeps3_tv.setText("Processing...");


        String url = Constants.FETCH_TRANSACTION_DETAILS + "/" + txnId;

        AndroidNetworking.post(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String resp = response.toString();
                        Log.e("TAG", "onResponse: " + resp);

                        try {
                            String txnId_ = response.getString("txId");
                            String transactionMode_ = response.getString("transactionMode");
                            String aadhaar_ = response.getString("origin_identifier");
                            String date_ = response.getString("updatedDate");
                            String bankName_ = response.getString("bankName");
                            String status_ = response.getString("status");
                            String statusDec_ApiComment = response.getString("message");
                            String rrn_ = response.getString("apiTid");
                            String statusDec_ = response.getString("apiComment");

                            if (statusDec_ == null || statusDec_.equals("")) {
                                statusDec_ = statusDec_ApiComment;
                            }
                            if (transactionMode_.equalsIgnoreCase("AEPS_MINI_STATEMENT")) {


                                if (!status_.equals("FAILED")) {
                                    try {
                                        String amount_ = response.getString("balance");

                                        if (amount_ != null && amount_.length() != 0 && !amount_.equals("N/A")) {

                                            JSONObject newBalance = new JSONObject(amount_);
                                            String totalBalanceAmount = newBalance.getString("balance");
                                            String ministatementBalance = newBalance.getString("ministatement");
                                            JSONArray ministatement_balance = new JSONArray(ministatementBalance);

                                            Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                                            intent.putExtra("status", status_);
                                            intent.putExtra("statusDec", statusDec_);
                                            intent.putExtra("bankName", userBankName_);
                                            intent.putExtra("Date", date_time.getText().toString());
                                            intent.putExtra("txnId", txnId_);
                                            intent.putExtra("adhaar", aadhaar_);
                                            intent.putExtra("transactionMode", "Mini Statement");
                                            intent.putExtra("list_data", ministatement_balance.toString());
                                            intent.putExtra("amount", totalBalanceAmount);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                                            intent.putExtra("status", status_);
                                            intent.putExtra("statusDec", statusDec_);
                                            intent.putExtra("bankName", userBankName_);
                                            intent.putExtra("Date", date_time.getText().toString());
                                            intent.putExtra("txnId", txnId_);
                                            intent.putExtra("adhaar", aadhaar_);
                                            intent.putExtra("transactionMode", "Mini Statement");
                                            intent.putExtra("list_data", "");
                                            intent.putExtra("amount", "N/A");
                                            startActivity(intent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e("TAG", "onResponse:JSONException " + e);
                                        Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                                        intent.putExtra("status", status_);
                                        intent.putExtra("statusDec", statusDec_);
                                        intent.putExtra("bankName", userBankName_);
                                        intent.putExtra("Date", date_time.getText().toString());
                                        intent.putExtra("txnId", txnId_);
                                        intent.putExtra("adhaar", aadhaar_);
                                        intent.putExtra("transactionMode", "Mini Statement");
                                        intent.putExtra("list_data", "");
                                        intent.putExtra("amount", "N/A");
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                                    intent.putExtra("status", status_);
                                    intent.putExtra("statusDec", statusDec_);
                                    intent.putExtra("bankName", userBankName_);
                                    intent.putExtra("Date", date_time.getText().toString());
                                    intent.putExtra("txnId", txnId_);
                                    intent.putExtra("adhaar", aadhaar_);
                                    intent.putExtra("transactionMode", "Mini Statement");
                                    intent.putExtra("list_data", "");
                                    intent.putExtra("amount", "N/A");
                                    startActivity(intent);
                                    finish();
                                }
                            } else {

                                TransitionManager.beginDelayedTransition(rl_main);
                                rl_main.setVisibility(View.VISIBLE);
                                progressContainer.setVisibility(View.GONE);
                                btn_ok.setVisibility(View.GONE);

                                String amount1_ = response.getString("balance");

                                balanceAmt = amount1_;
                                if (status_.equalsIgnoreCase("SUCCESS")) {
                                    status_icon.setImageResource(R.drawable.hero_success);
                                    mobileTextLayout.setVisibility(View.VISIBLE);
                                } else {
                                    status_icon.setImageResource(R.drawable.hero_failure);
                                    mobileTextLayout.setVisibility(View.GONE);
                                }


                                if (!txnId_.equals("")) {
                                    txnID.setText("Txn ID : " + txnId_);
                                } else {
                                    txnID.setText("Txn ID : " + "N/A");
                                }

                                if (transactionMode_.equals("")) {

                                    transactionType = "N/A";
                                } else {
                                    if (transactionMode_.equalsIgnoreCase("AEPS_CASH_WITHDRAWAL")) {
                                        transactionType = "Cash Withdrawal";
                                        if (amount1_.equals("") || amount1_ == null) {
                                            card_amount.setText("N/A");
                                        } else {
                                            card_amount.setText("Txn Amount : Rs. " + TXNAMOUNT);
                                        }
                                    } else if (transactionMode_.equalsIgnoreCase("AEPS_BALANCE_ENQUIRY")) {
                                        transactionType = "Balance Enquiry";
                                        TXNAMOUNT = "N/A";
                                        if (amount1_.equals("") || amount1_ == null) {
                                            card_amount.setText("N/A");
                                        } else {
                                            card_amount.setText("Rs. " + amount1_);
                                        }
                                    } else {
                                        transactionType = "AadhaarPay";
                                        if (amount1_.equals("") || amount1_ == null) {
                                            card_amount.setText("N/A");
                                        } else {
                                            card_amount.setText("Txn Amount : Rs. " + TXNAMOUNT);
                                        }
                                    }

                                }
                                if (aadhaar_.equals("")) {
                                    aadharCard = "N/A";
                                } else {
                                    aadharCard = aadhaar_;
                                }
                                if (status_.equals("")) {
                                    statusTxt = "N/A";
                                } else {
                                    statusTxt = status_;
                                }

                                if (rrn_.equals("")) {
                                    referenceNo = "N/A";
                                } else {
                                    referenceNo = rrn_;
                                }


                                if (statusDec_.equalsIgnoreCase("")) {
                                    balanceText.setText("N/A");
                                } else {
                                    balanceText.setText(statusDec_);
                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        TransitionManager.beginDelayedTransition(rl_main);
                        rl_main.setVisibility(View.GONE);
                        aeps3_pb.setVisibility(View.GONE);
                        progressContainer.setVisibility(View.VISIBLE);
                        btn_updateStatus.setVisibility(View.VISIBLE);
                        iv_pending.setVisibility(View.VISIBLE);
                        pb_aeps3_tv.setText("Pending...");
                        btn_ok.setVisibility(View.VISIBLE);


                        Log.d("TAG", "onError: " + anError.getErrorDetail() + txnId);
                    }
                });
    }

    private void setLoaderForOneMinutes() {

        TransitionManager.beginDelayedTransition(rl_main);
        progressContainer.setVisibility(View.VISIBLE);
        rl_main.setVisibility(View.GONE);
        iv_pending.setVisibility(View.GONE);
        aeps3_pb.setVisibility(View.VISIBLE);
        btn_updateStatus.setVisibility(View.GONE);
        btn_ok.setVisibility(View.GONE);


        CDT = new CountDownTimer(50000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!Constants.THE_FIREBASE_DATA.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(Constants.THE_FIREBASE_DATA);
                        userBankName_ = jsonObject.getString("bankName");
                        date_ = jsonObject.getString("updatedDate");
                        amount_ = jsonObject.getString("balance");
                        balanceAmt = amount_;
                        txnId_ = jsonObject.getString("txId");
                        mapTxn.put(txnId_, Constants.THE_FIREBASE_DATA);
                        adhaar_ = jsonObject.getString("origin_identifier");
                        status_ = jsonObject.getString("status");
                        transactionMode_ = jsonObject.getString("transactionMode");
                        String statusDec_ApiComment = jsonObject.getString("message");
                        String rrn = jsonObject.getString("apiTid");
                        String statusDec_ = jsonObject.getString("apiComment");

                        Log.e("TAG", "onTick: the data from api : " + txnId + ",The data from fcm :" + txnId_);
                        Log.e("TAG", "onTick: The hashMap: " + mapTxn);

                        if (statusDec_ == null || statusDec_.equals("")) {
                            statusDec_ = statusDec_ApiComment;
                        }
                        if (transactionMode_.equalsIgnoreCase("AEPS_MINI_STATEMENT")) {
                            try {

                                if (txnId.equals(txnId_)) {
                                    onFinish();
                                    cancel();

                                    if (!loaded) {
                                        String acBalance = jsonObject.getString("account_balance");
                                        if (!acBalance.equals("")) {
                                            JSONArray jsonArray = new JSONArray(acBalance);
                                            Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                                            intent.putExtra("status", status_);
                                            intent.putExtra("statusDec", statusDec_);
                                            intent.putExtra("bankName", userBankName_);
                                            intent.putExtra("amount", amount_);
                                            intent.putExtra("Date", date_time.getText().toString());
                                            intent.putExtra("txnId", txnId_);
                                            intent.putExtra("adhaar", adhaar_);
                                            intent.putExtra("transactionMode", "Mini Statement");
                                            intent.putExtra("list_data", jsonArray.toString());
                                            startActivity(intent);
                                            finish();
                                            loaded = true;
                                        } else {
                                            Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                                            intent.putExtra("status", status_);
                                            intent.putExtra("statusDec", statusDec_);
                                            intent.putExtra("bankName", userBankName_);
                                            intent.putExtra("amount", amount_);
                                            intent.putExtra("Date", date_time.getText().toString());
                                            intent.putExtra("txnId", txnId_);
                                            intent.putExtra("adhaar", adhaar_);
                                            intent.putExtra("transactionMode", "Mini Statement");
                                            intent.putExtra("list_data", "");
                                            startActivity(intent);
                                            finish();
                                            loaded = true;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, AEPS3_TransactionReceipt_ministatement.class);
                                intent.putExtra("status", status_);
                                intent.putExtra("statusDec", statusDec_);
                                intent.putExtra("bankName", userBankName_);
                                intent.putExtra("amount", amount_);
                                intent.putExtra("Date", date_time.getText().toString());
                                intent.putExtra("txnId", txnId_);
                                intent.putExtra("adhaar", adhaar_);
                                intent.putExtra("transactionMode", "Mini Statement");
                                intent.putExtra("list_data", "");
                                startActivity(intent);
                                finish();
                                loaded = true;
                            }
                        } else {
                            if (txnId.equals(txnId_)) {
                                TransitionManager.beginDelayedTransition(rl_main);
                                rl_main.setVisibility(View.VISIBLE);
                                progressContainer.setVisibility(View.GONE);
                                btn_ok.setVisibility(View.GONE);
                                onFinish();
                                cancel();
                                if (!loaded) {

                                    if (status_.equalsIgnoreCase("SUCCESS")) {
                                        status_icon.setImageResource(R.drawable.hero_success);
                                        mobileTextLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        status_icon.setImageResource(R.drawable.hero_failure);
                                        mobileTextLayout.setVisibility(View.GONE);
                                    }

                                    if (txnId_.equals("")) {
                                        txnID.setText("Txn ID : " + "N/A");
                                    } else {
                                        txnID.setText("Txn ID : " + txnId_);
                                    }
                                    if (transactionMode_.equals("")) {
                                        transactionType = "N/A";
                                    } else {
                                        if (transactionMode_.equalsIgnoreCase("AEPS_CASH_WITHDRAWAL")) {
                                            transactionType = "Cash Withdrawal";
                                            if (amount_.equals("") || amount_ == null) {
                                                card_amount.setText("N/A");
                                            } else {
                                                card_amount.setText("Txn Amount : Rs. " + TXNAMOUNT);
                                            }

                                        } else if (transactionMode_.equalsIgnoreCase("AEPS_BALANCE_ENQUIRY")) {
                                            transactionType = "Balance Enquiry";
                                            TXNAMOUNT = "N/A";
                                            if (amount_.equals("") || amount_ == null) {
                                                card_amount.setText("N/A");
                                            } else {
                                                card_amount.setText("Rs. " + amount_);
                                            }
                                        } else {
                                            transactionType = "AadhaarPay";
                                            if (amount_.equals("") || amount_ == null) {
                                                card_amount.setText("N/A");
                                            } else {
                                                card_amount.setText("Txn Amount : Rs. " + TXNAMOUNT);
                                            }
                                        }
                                    }
                                    if (adhaar_.equalsIgnoreCase("")) {
                                        aadharCard = "N/A";
                                    } else {
                                        aadharCard = adhaar_;
                                    }
                                    if (rrn.equals("")) {
                                        referenceNo = "N/A";
                                    } else {
                                        referenceNo = rrn;
                                    }
                                    if (status_.equals("")) {
                                        statusTxt = "N/A";
                                    } else {
                                        statusTxt = status_;
                                    }

                               /* if (amount_.equalsIgnoreCase("")) {
                                    amount = "N/A";
                                } else {
                                    amount = TXNAMOUNT;
                                }*/
                                    if (statusDec_.equalsIgnoreCase("")) {
                                        balanceText.setText("N/A");
                                    } else {
                                        balanceText.setText(statusDec_);
                                    }
                                    loaded = true;
                                }
                            }/*else {
                                    pb_aeps3_tv.setText("Pending...");
                                    aeps3_pb.setVisibility(View.GONE);
                                    iv_pending.setVisibility(View.VISIBLE);
                                    btn_updateStatus.setVisibility(View.VISIBLE);
                                }*/
                        }
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("TAG", "onTick: i ");
            }

            public void onFinish() {
                if (Constants.THE_FIREBASE_DATA.equalsIgnoreCase("") || !txnId.equals(txnId_)) {
                    pb_aeps3_tv.setText("Pending...");
                    aeps3_pb.setVisibility(View.GONE);
                    iv_pending.setVisibility(View.VISIBLE);
                    btn_updateStatus.setVisibility(View.VISIBLE);
                }
                cancel();
            }
        }.start();

    }

    public void mobileNumberSMS() {

        String msgValue = "Thanks for visiting " + SdkConstants.SHOP_NAME + ". Current balance for " + bankName + " account seeded with aadhaar " + aadharCard + " is Rs " + balance + ". Dated " + date_time.getText().toString() + ".";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_name", SdkConstants.userNameFromCoreApp);
            jsonObject.put("MobileNumber", editTextMobile.getText().toString());
            jsonObject.put("smsFor", "transaction");
            jsonObject.put("message", msgValue);

            AndroidNetworking.post("https://wallet-deduct-sms-vn3k2k7q7q-uc.a.run.app/")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");
                                String msg = obj.optString("message");

                                if (status.equalsIgnoreCase("0")) {
                                    JSONObject results = obj.getJSONObject("results");

                                    String statusMsg = results.getString("status");
                                    String message = results.getString("message");
                                    hideLoader();

                                    Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Message Sent Successfully . ", Toast.LENGTH_SHORT).show();

                                } else {

                                    hideLoader();
                                    Toast.makeText(AEPS3_Transaction_Status_Activity.this, msg, Toast.LENGTH_SHORT).show();

                                }


                            } catch (JSONException e) {
                                hideLoader();
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.getErrorBody();
                            hideLoader();
                            Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Wallet balance not available", Toast.LENGTH_SHORT).show();


                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoader() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(AEPS3_Transaction_Status_Activity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait..");
        }
        progressDialog.show();
    }

    public void hideLoader() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showTransactionDetails(Activity activity) {
        try {
            final android.app.Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.transaction_aeps_details_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView aadhar_number = (TextView) dialog.findViewById(R.id.aadhar_number);
            TextView rref_num = (TextView) dialog.findViewById(R.id.rref_num);
            TextView card_transaction_type = (TextView) dialog.findViewById(R.id.card_transaction_type);
            TextView card_transaction_amount = (TextView) dialog.findViewById(R.id.card_transaction_amount);
            TextView balanceAmtID = (TextView) dialog.findViewById(R.id.balanceAmtID);
            aadhar_number.setText(aadharCard);
            rref_num.setText(referenceNo);
            card_transaction_type.setText(transactionType);

            if (transactionType.equals("Balance Enquiry") || transactionType.equals("BALANCE ENQUIRY")) {
                balanceAmtID.setText("Transaction Amount");
                card_transaction_amount.setText("N/A");
            } else {
                card_transaction_amount.setText(balanceAmt);
            }


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
        }

    }

    private void createPdf(String s) {
        filePath = s;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //This is for android 11 changes
            if (checkPermission()) {
                createPdfGenericMethod(s);
            } else {
                requestPermission();
            }
        } else {
            createPdfGenericMethod(s);
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    createPdfGenericMethod(filePath);
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void createPdfGenericMethod(String dest) {

        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            /**
             * Creating Document
             */
            Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A3);
            document.setMargins(0, 0, 50, 50);
            document.addCreationDate();
            document.addAuthor("");
            document.addCreator("");
            Rectangle rect = new Rectangle(577, 825, 18, 15);
            rect.enableBorderSide(1);
            rect.enableBorderSide(2);
            rect.enableBorderSide(4);
            rect.enableBorderSide(8);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            rect.setBorderColor(BaseColor.BLACK);
            document.add(rect);
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 24.0f;
            float mValueFontSize = 26.0f;

            /**
             * How to USE FONT....
             */
            BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            BaseFont bf = BaseFont.createFont(
                    BaseFont.TIMES_ROMAN,
                    BaseFont.CP1252,
                    BaseFont.EMBEDDED);
            Font font = new Font(bf, 30);
            Font font2 = new Font(bf, 26);

            Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk(SdkConstants.SHOP_NAME, mOrderDetailsTitleFont);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);

            Font mOrderShopTitleFont = new Font(urName, 25.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderShopTitleChunk = new Chunk("Receipt", mOrderShopTitleFont);
            Paragraph mOrderShopTitleParagraph = new Paragraph(mOrderShopTitleChunk);
            mOrderShopTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderShopTitleParagraph);
            Font mOrderDetailsTitleFont11;
            if (statusTxt.equalsIgnoreCase("FAILED")) {
                mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.RED);

            } else {
                mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.GREEN);
            }

            Chunk mOrderDetailsTitleChunk1 = new Chunk(statusTxt, mOrderDetailsTitleFont11);
            Paragraph mOrderDetailsTitleParagraph1 = new Paragraph(mOrderDetailsTitleChunk1);
            mOrderDetailsTitleParagraph1.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph1);
            document.add(new Paragraph("\n\n"));


            Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);

            Paragraph p = new Paragraph();
            p.add(new Chunk("Date/Time : ", mOrderDateFont));
            p.add(new Chunk(date_time.getText().toString().trim(), mOrderDateValueFont));
            document.add(p);
            document.add(new Paragraph("\n\n"));

            Paragraph p1 = new Paragraph();
            p1.add(new Chunk("Operation Performed : ", mOrderDateFont));
            p1.add(new Chunk("AePS 3", mOrderDateValueFont));
            document.add(p1);


            document.add(new Paragraph("\n\n"));


            Font mOrderDetailsFont = new Font(urName, 30.0f, Font.BOLD, mColorAccent);
            Chunk mOrderDetailsChunk = new Chunk("Transaction Details", mOrderDetailsFont);
            Paragraph mOrderDetailsParagraph = new Paragraph(mOrderDetailsChunk);
            mOrderDetailsParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsParagraph);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph("\n\n"));


            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdChunk = new Chunk("Transaction ID: " + txnId, mOrderIdFont);
            Paragraph mOrderTxnParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderTxnParagraph);
            Chunk mOrderIdValueChunk = new Chunk("Aadhaar Number: " + aadharCard, mOrderIdFont);
            Paragraph mOrderaadharParagraph = new Paragraph(mOrderIdValueChunk);
            document.add(mOrderaadharParagraph);
            Chunk mBankNameChunk = new Chunk("Bank Name: " + bank_name.getText().toString().trim(), mOrderIdFont);
            Paragraph mBankNameParagraph = new Paragraph(mBankNameChunk);
            document.add(mBankNameParagraph);
            Chunk mOrderrrnChunk = new Chunk("RRN No: " + referenceNo, mOrderIdFont);
            Paragraph mOrderrnParagraph = new Paragraph(mOrderrrnChunk);
            document.add(mOrderrnParagraph);
            Chunk mOrderbalanceChunk = new Chunk("Balance Amount: " + balanceAmt, mOrderIdFont);
            Paragraph mOrderbalanceParagraph = new Paragraph(mOrderbalanceChunk);
            document.add(mOrderbalanceParagraph);
            Chunk mOrdertxnAmtChunk = new Chunk("Transaction Amount: " + TXNAMOUNT, mOrderIdFont);
            Paragraph mOrdertxnAmtParagraph = new Paragraph(mOrdertxnAmtChunk);
            document.add(mOrdertxnAmtParagraph);
            Chunk mOrdertxnTypeChunk = new Chunk("Transaction Type: " + transactionType, mOrderIdFont);
            Paragraph mOrdertxnTypeParagraph = new Paragraph(mOrdertxnTypeChunk);
            document.add(mOrdertxnTypeParagraph);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));

            document.add(new Paragraph("\n\n"));

            Font mOrderAcNameFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderAcNameChunk = new Chunk("Thank You", mOrderAcNameFont);
            Paragraph mOrderAcNameParagraph = new Paragraph(mOrderAcNameChunk);
            mOrderAcNameParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderAcNameParagraph);
            Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk(SdkConstants.BRAND_NAME, mOrderAcNameValueFont);
            Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
            mOrderAcNameValueParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderAcNameValueParagraph);
            document.close();

            Toast.makeText(mContext, "PDF saved in the internal storage", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AEPS3_Transaction_Status_Activity.this, PreviewPDFActivity.class);
            intent.putExtra("filePath", dest);
            startActivity(intent);


        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", "" + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }


    private void callBluetoothFunction(final String txnId, final String aadharNo, final String date, final String bank_name, final String reffNo, final String amount, final String transactionAmt, final String type, View view) {

        try {
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_CENTER);
            m_AemPrinter.setFontType(AEMPrinter.FONT_NORMAL);
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            m_AemPrinter.print(SdkConstants.SHOP_NAME);
            m_AemPrinter.print("\n");
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_CENTER);
            m_AemPrinter.print("-----Transaction Report-----\n");
            m_AemPrinter.print(statusTxt);
            m_AemPrinter.print("\n\n");
            m_AemPrinter.print(txnId);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Aadhaar Number: " + aadharNo);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Date/Time: " + date);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Bank Name.: " + bank_name);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("RRN No.: " + reffNo);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Balance Amount: " + balance);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Transaction Amount: " + amount);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("TransactionType: " + type);
            m_AemPrinter.print("\n");
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            //   m_AemPrinter.print(d);
            m_AemPrinter.print("Thank you! \n\n\n");
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_RIGHT);
            m_AemPrinter.print(SdkConstants.BRAND_NAME);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            data = printerStatus();
            m_AemPrinter.print(data);
            m_AemPrinter.print("\n");
        } catch (IOException e) {
            try{
                getConnection(view);
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
//        final BluetoothPrinter mPrinter = new BluetoothPrinter(bluetoothDevice);
//        mPrinter.connectPrinter(new BluetoothPrinter.PrinterConnectListener() {
//
//            @Override
//            public void onConnected() {
//                mPrinter.addNewLine();
//                mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
//                mPrinter.setBold(true);
//                mPrinter.printText(SdkConstants.SHOP_NAME);
//                mPrinter.addNewLine();
//                mPrinter.addNewLine();
//                mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
//                mPrinter.printText("-----Transaction Report-----");
//                mPrinter.addNewLine();
//                mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
//                mPrinter.setBold(true);
//                mPrinter.printText(statusTxt);
//                mPrinter.addNewLine();
//                mPrinter.addNewLine();
//                mPrinter.printText(txnId);
//                mPrinter.addNewLine();
//                mPrinter.printText("Aadhaar Number: " + aadharCard);
//                mPrinter.addNewLine();
//                mPrinter.printText("Date/Time: " + date);
//                mPrinter.addNewLine();
//                mPrinter.printText("Bank Name.: " + bank_name);
//                mPrinter.addNewLine();
//                mPrinter.printText("RRN No.: " + referenceNo);
//                mPrinter.addNewLine();
//                mPrinter.printText("Balance Amount: " + balanceAmt);
//                mPrinter.addNewLine();
//                mPrinter.printText("Transaction Amount: " + TXNAMOUNT);
//                mPrinter.addNewLine();
//                mPrinter.printText("TransactionType: " + type);
//                mPrinter.addNewLine();
//                mPrinter.addNewLine();
//                mPrinter.setBold(true);
//                mPrinter.setAlign(BluetoothPrinter.ALIGN_RIGHT);
//                mPrinter.printText("Thank You");
//                mPrinter.addNewLine();
//                mPrinter.setAlign(BluetoothPrinter.ALIGN_RIGHT);
//                mPrinter.printText(SdkConstants.BRAND_NAME);
//                mPrinter.addNewLine();
//                mPrinter.addNewLine();
//                mPrinter.addNewLine();
//                mPrinter.printText("-----------------------------------");
//                mPrinter.addNewLine();
//                mPrinter.addNewLine();
//                mPrinter.finish();
//            }
//
//            @Override
//            public void onFailed() {
//                Log.d("BluetoothPrinter", "Conection failed");
////                finish();
//                Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Please switch on bluetooth printer", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    private void getConnection(View view){
        GetPosConnectedPrinter.aemPrinter = null;
        registerForContextMenu(printBtn);
        if (bluetoothAdapter == null) {
            Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (GetPosConnectedPrinter.aemPrinter == null) {
                    printerList = m_AemScrybeDevice.getPairedPrinters();
                    if (printerList.size() > 0) {
                        openContextMenu(view);
                    } else {
                        showAlert("No Paired Printers found");
                    }
                } else {
                    m_AemPrinter = GetPosConnectedPrinter.aemPrinter;
                    callBluetoothFunction(txnId, aadharCard, date_time.getText().toString(), bank_name.getText().toString(), referenceNo, card_amount.getText().toString(), amount, transactionType, view);

                }
            } else {
                GetPosConnectedPrinter.aemPrinter = null;
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }
        }
    }
    public String printerStatus() throws IOException {
        String data = new String(printerStatus);
        m_AemPrinter.print(data);
        return data;
    }
    public void showAlert(String alertMsg) {
        android.app.AlertDialog.Builder alertBox = new android.app.AlertDialog.Builder(AEPS3_Transaction_Status_Activity.this);
        alertBox.setMessage(alertMsg).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                return;
            }
        });

        android.app.AlertDialog alert = alertBox.create();
        alert.show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Printer to connect");

        for (int i = 0; i < printerList.size(); i++) {
            menu.add(0, v.getId(), 0, printerList.get(i));
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        printerName= item.getTitle().toString();
        try {
            m_AemScrybeDevice.connectToPrinter(printerName);
            m_cardReader = m_AemScrybeDevice.getCardReader(this);
            m_AemPrinter = m_AemScrybeDevice.getAemPrinter();
            GetPosConnectedPrinter.aemPrinter = m_AemPrinter;
            Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Connected with " + printerName, Toast.LENGTH_SHORT).show();

            //            String data=new String(batteryStatusCommand);
//            m_AemPrinter.print(data);
            //  m_cardReader.readMSR();


        } catch (IOException e) {
            if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Not Connected\n" + printerName + " is unreachable or off otherwise it is connected with other device", Toast.LENGTH_SHORT).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(AEPS3_Transaction_Status_Activity.this, "the device is already connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AEPS3_Transaction_Status_Activity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    CardReader.MSRCardData creditDetails;

    public void onScanMSR(final String buffer, CardReader.CARD_TRACK cardTrack) {
        cardTrackType = cardTrack;
        creditData = buffer;
        AEPS3_Transaction_Status_Activity.this.runOnUiThread(new Runnable() {
            public void run() {
//                editText.setText(buffer.toString());
            }
        });
    }

    public void onScanDLCard(final String buffer) {
        CardReader.DLCardData dlCardData = m_cardReader.decodeDLData(buffer);
        String name = "NAME:" + dlCardData.NAME + "\n";
        String SWD = "SWD Of: " + dlCardData.SWD_OF + "\n";
        String dob = "DOB: " + dlCardData.DOB + "\n";
        String dlNum = "DLNUM: " + dlCardData.DL_NUM + "\n";
        String issAuth = "ISS AUTH: " + dlCardData.ISS_AUTH + "\n";
        String doi = "DOI: " + dlCardData.DOI + "\n";
        String tp = "VALID TP: " + dlCardData.VALID_TP + "\n";
        String ntp = "VALID NTP: " + dlCardData.VALID_NTP + "\n";

        final String data = name + SWD + dob + dlNum + issAuth + doi + tp + ntp;

        runOnUiThread(new Runnable() {
            public void run() {
//                editText.setText(data);
            }
        });
    }

    public void onScanRCCard(final String buffer) {
        CardReader.RCCardData rcCardData = m_cardReader.decodeRCData(buffer);
        String regNum = "REG NUM: " + rcCardData.REG_NUM + "\n";
        String regName = "REG NAME: " + rcCardData.REG_NAME + "\n";
        String regUpto = "REG UPTO: " + rcCardData.REG_UPTO + "\n";

        final String data = regNum + regName + regUpto;

        runOnUiThread(new Runnable() {
            public void run() {
//                editText.setText(data);
            }
        });
    }

    @Override
    public void onScanRFD(final String buffer) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(buffer);
        String temp = "";
        try {
            temp = stringBuffer.deleteCharAt(8).toString();
        } catch (Exception e) {
            // TODO: handle exception
        }
        final String data = temp;

        AEPS3_Transaction_Status_Activity.this.runOnUiThread(new Runnable() {
            public void run() {
                //rfText.setText("RF ID:   " + data);
//                editText.setText("ID " + data);
                try {
                    m_AemPrinter.print(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void onDiscoveryComplete(ArrayList<String> aemPrinterList) {
        printerList = aemPrinterList;
        for (int i = 0; i < aemPrinterList.size(); i++) {
            String Device_Name = aemPrinterList.get(i);
            String status = m_AemScrybeDevice.pairPrinter(Device_Name);
            Log.e("STATUS", status);
        }
    }

    @Override
    public void onScanPacket(String buffer) {
        if (buffer.equals("PRINTEROK")) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(buffer);
            String temp = "";
            try {
                temp = stringBuffer.toString();
            } catch (Exception e) {
                // TODO: handle exception
            }
            tempdata = temp;
            final String strData = tempdata.replace("|", "&");
            //Log.e("BufferData",data);
            final String[][] formattedData = {strData.split("&", 3)};
            // Log.e("Response Data",formattedData[2]);
            responseString = formattedData[0][2];
            responseArray[0] = responseString.replace("^", "");
            Log.e("Response Array", responseArray[0]);
            AEPS3_Transaction_Status_Activity.this.runOnUiThread(new Runnable() {
                public void run() {
                    replacedData = tempdata.replace("|", "&");
                    formattedData[0] = replacedData.split("&", 3);
                    response = formattedData[0][2];
                    if (response.contains("BAT")) {
//                        txtBatteryStatus.setText(response.replace("^","").replace("BAT","")+"%");
                    }
//                    editText.setText(response.replace("^",""));
                }
            });

        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(buffer);
            String temp = "";
            try {
                temp = stringBuffer.toString();
            } catch (Exception e) {
                // TODO: handle exception
            }
            tempdata = temp;
            final String strData = tempdata.replace("|", "&");
            //Log.e("BufferData",data);
            final String[][] formattedData = {strData.split("&", 3)};
            // Log.e("Response Data",formattedData[2]);
            responseString = formattedData[0][2];
            responseArray[0] = responseString.replace("^", "");
            Log.e("Response Array", responseArray[0]);
            AEPS3_Transaction_Status_Activity.this.runOnUiThread(new Runnable() {
                public void run() {
                    replacedData = tempdata.replace("|", "&");
                    formattedData[0] = replacedData.split("&", 3);
                    response = formattedData[0][2];
                    if (response.contains("BAT")) {
//                        txtBatteryStatus.setText(response.replace("^","").replace("BAT","")+"%");
                    }
//                    editText.setText(response.replace("^",""));
                }
            });
        }
    }
}