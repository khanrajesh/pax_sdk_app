package com.matm.matmsdk.aepsmodule.aadharpay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
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
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.FileUtils;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.aadharpay.Model.AadharpayResponse;
import com.matm.matmsdk.aepsmodule.aadharpay.Model.FCMNotificationData;
import com.matm.matmsdk.aepsmodule.transactionstatus.DrawPdfAadhaarPayActivity;
import com.matm.matmsdk.aepsmodule.transactionstatus.DrawPdfAeps1Activity;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusAeps2Activity;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusNewActivity;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.aepsmodule.utils.GetPosConnectedPrinter;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.notification.service.MyFirebaseMessagingService;
import com.matm.matmsdk.permission.PermissionsActivity;
import com.matm.matmsdk.permission.PermissionsChecker;
import com.matm.matmsdk.readfile.PreviewPDFActivity;
import com.matm.matmsdk.transaction_report.print.GetConnectToPrinter;
import com.matm.matmsdk.vriddhi.AEMPrinter;
import com.matm.matmsdk.vriddhi.AEMScrybeDevice;
import com.matm.matmsdk.vriddhi.CardReader;
import com.matm.matmsdk.vriddhi.IAemCardScanner;
import com.matm.matmsdk.vriddhi.IAemScrybe;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import isumatm.androidsdk.equitas.R;

import static com.matm.matmsdk.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.matm.matmsdk.permission.PermissionsChecker.REQUIRED_PERMISSION;

public class AadhaarpayReceiptActivity extends AppCompatActivity implements IAemCardScanner, IAemScrybe {

    public ImageView iv_status_icon, iv_pending, sendButton;
    public TextView tv_transactionAmount, tv_bankName, tv_dateTime, tv_txnID, tv_viewDetails, tv_progressViewMessage, tv_transactionStatusDecs;
    public Button btn_print, btn_download, btn_close, btn_updateStatus, btn_ok;
    public LinearLayout ll_progressBar_container;
    public RelativeLayout rl_mainLayout;
    public ProgressBar pb_bigProgressBar;
    public CountDownTimer countDownTimer;
    public boolean onceDataLoaded = false;
    public String finalTxnID = "N/A", aadhaarNumber_dialog = "N/A", rrNumber_dialog = "N/A", transactionType_dialog = "N/A", balanceAmt_dialog = "N/A", _statusMain = "";
    LinearLayout mobileEditLayout, mobileTextLayout;
    EditText editTextMobile;
    CheckBox mobileCheckBox;
    ProgressDialog progressDialog;
    AadharpayResponse aadharpayResponse;
    FCMNotificationData fcmNotificationData;
    PermissionsChecker checker;
    BluetoothDevice bluetoothDevice;
    BluetoothAdapter bluetoothAdapter;
    String txnIdForPdf;
    private String filePath = "";

//For Vriddhi
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this); // This is for Custom theme
        setContentView(R.layout.activity_aadhaarpay_receipt);

        initilizationOfviews(); // This is for view initialization

        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
        checker = new PermissionsChecker(AadhaarpayReceiptActivity.this); // To allow all the permission for print and download

        Date date = Calendar.getInstance().getTime();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        m_AemScrybeDevice = new AEMScrybeDevice(AadhaarpayReceiptActivity.this);
        printerList = new ArrayList<String>();
        creditData = new String();

        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = formatter.format(date);

        System.out.println("Today : " + currentDateandTime);

        /** collect the data from previous activity*/
        Gson gson = new Gson();

//        if (!Constants.isDataLoadedForAadharPay) {
        if (getIntent() != null && getIntent().hasExtra("myjson")) {
            if (getIntent().getStringExtra("myjson") != null) {
                try {
                    aadharpayResponse = gson.fromJson(getIntent().getStringExtra("myjson"), AadharpayResponse.class); // Convert the JSON to Model
                    int status = 1;
                    String txnID = "N/A";

                    if (aadharpayResponse != null) {
                        status = aadharpayResponse.getStatus();
                        txnID = aadharpayResponse.getTxId();
                    } else {
                        ll_progressBar_container.setVisibility(View.GONE);
                        rl_mainLayout.setVisibility(View.VISIBLE);
                        setDataToView("FAILED", "N/A", "N/A", "This transaction got failed, Please check the report..", "N/A");
                    }

                    /*If the status is 0 then it is searching for response in Notification*/
                    if (status == 0) {
                        showLoaderForMaxOneMinutes();
                    } else {
                        ll_progressBar_container.setVisibility(View.GONE);
                        rl_mainLayout.setVisibility(View.VISIBLE);
                        setDataToView("FAILED", aadharpayResponse.getTxId(), aadharpayResponse.getTransactionAmount(), aadharpayResponse.getTransactionStatus(), aadharpayResponse.getBankName());
                    }

                    finalTxnID = txnID;

                } catch (Exception e) {
                    e.printStackTrace();
                    ll_progressBar_container.setVisibility(View.GONE);
                    rl_mainLayout.setVisibility(View.VISIBLE);
                    setDataToView("FAILED", "N/A", "N/A", "This transaction got failed, Please check the report..", "N/A");
                }

            } else {
                ll_progressBar_container.setVisibility(View.GONE);
                rl_mainLayout.setVisibility(View.VISIBLE);
                setDataToView("FAILED", "N/A", "N/A", "This transaction got failed, Please check the report..", "N/A");

            }
        } else {
            ll_progressBar_container.setVisibility(View.GONE);
            rl_mainLayout.setVisibility(View.VISIBLE);
            setDataToView("FAILED", "N/A", "N/A", "This transaction got failed, Please check the report..", "N/A");
        }
        //}



        /** this event is for fetch the transaction via api{If the notification is not received }*/
        btn_updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFetchdataAPI(finalTxnID);
            }
        });

        /** this will */
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionDetails(AadhaarpayReceiptActivity.this, aadhaarNumber_dialog, rrNumber_dialog, transactionType_dialog, balanceAmt_dialog);
            }
        });


        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(AadhaarpayReceiptActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    Date date = new Date();
                    long timeMilli = date.getTime();
                    System.out.println("Time in milliseconds using Date class: " + String.valueOf(timeMilli));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        createPdf(FileUtils.commonDocumentDirPath("PDF") + String.valueOf(timeMilli) + "Order_Receipt.pdf");
                    }else {
                        createPdf(FileUtils.getAppPath(getApplicationContext()) + String.valueOf(timeMilli) + "Order_Receipt.pdf");
                    }
                }
            }
        });

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String deviceModel = android.os.Build.MODEL;
                if(deviceModel.equalsIgnoreCase("A910")) {

                    getPrintData(tv_txnID.getText().toString(), aadhaarNumber_dialog,tv_dateTime.getText().toString(),tv_bankName.getText().toString(),
                            rrNumber_dialog, transactionType_dialog );

                } else {
                    registerForContextMenu(btn_print);
                    if (bluetoothAdapter == null) {
                        Toast.makeText(AadhaarpayReceiptActivity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
                    } else {
                        if (bluetoothAdapter.isEnabled()) {
                            if (GetPosConnectedPrinter.aemPrinter == null) {
                                printerList = m_AemScrybeDevice.getPairedPrinters();
                                if (printerList.size() > 0) {
                                    openContextMenu(v);
                                } else {
                                    showAlert("No Paired Printers found");
                                }
                            } else {
                                m_AemPrinter = GetPosConnectedPrinter.aemPrinter;

                                if (balanceAmt_dialog.equals("") || balanceAmt_dialog.equalsIgnoreCase("N/A")) {
                                    balanceAmt_dialog = "N/A";
                                }else{
                                    balanceAmt_dialog = "Rs." + balanceAmt_dialog;
                                }
                                callBluetoothFunction(tv_txnID.getText().toString(), tv_dateTime.getText().toString(),
                                        tv_bankName.getText().toString(), rrNumber_dialog,
                                        tv_transactionAmount.getText().toString(), balanceAmt_dialog,
                                        transactionType_dialog, aadhaarNumber_dialog, _statusMain, v);

                            }
                        } else {
                            GetPosConnectedPrinter.aemPrinter = null;
                            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(turnOn, 0);
                        }
                    }
                }
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

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void callBluetoothFunction(String _txnId, String _date, String _bankName, String _rrNumber, String _transactionAmount, String _balanceAmount, String _transactionType, String _aadhaarNumber, String _txnStatus,View view) {

        try {
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_CENTER);
            m_AemPrinter.setFontType(AEMPrinter.FONT_NORMAL);
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(SdkConstants.SHOP_NAME);
            m_AemPrinter.print("\n");
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_CENTER);
            m_AemPrinter.print("-----Transaction Report-----\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(_txnStatus);
            m_AemPrinter.print("\n\n");
            m_AemPrinter.print(_txnId);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Aadhaar Number: " + _aadhaarNumber);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Date/Time: " + _date);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Bank Name.: " + _bankName);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("RRN No.: " + _rrNumber);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Balance Amount: " + _balanceAmount);
            m_AemPrinter.print("\n");
            m_AemPrinter.print( _transactionAmount);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("TransactionType: " + _transactionType);
            m_AemPrinter.print("\n\n");
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            m_AemPrinter.POS_FontThreeInchRIGHT();
            m_AemPrinter.print("Thank you \n");
            m_AemPrinter.POS_FontThreeInchRIGHT();
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

    }

    private void createPdf(String s) {
        filePath = s;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            //This is for android 11 changes
//            if (checkPermission()) {
//                createPdfGenericMethod(s);
//            } else {
//                requestPermission();
//            }
//        } else {
            createPdfGenericMethod(s);
//        }
    }

//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
//                startActivityForResult(intent, 2296);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, 2296);
//            }
//        }
//    }

//    private boolean checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        }
//        return false;
//    }

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

    private void createPdfGenericMethod(String s) {
        {
            if (new File(s).exists()) {
                new File(s).delete();
            }
            try {
                /**
                 * Creating Document
                 */
                Document document = new Document();

                // Location to save
                PdfWriter.getInstance(document, new FileOutputStream(s));

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
                document.add(new Paragraph("\n"));
                Font mOrderShopTitleFont = new Font(urName, 25.0f, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderShopTitleChunk = new Chunk("Receipt", mOrderShopTitleFont);
                Paragraph mOrderShopTitleParagraph = new Paragraph(mOrderShopTitleChunk);
                mOrderShopTitleParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mOrderShopTitleParagraph);
                Font mOrderDetailsTitleFont11;
                if (_statusMain.equalsIgnoreCase("FAILED")) {
                    mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.RED);

                } else if (_statusMain.equalsIgnoreCase("SUCCESS")) {
                    mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.GREEN);

                } else {
                    mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.YELLOW);
                }

                Chunk mOrderDetailsTitleChunk1 = new Chunk(_statusMain, mOrderDetailsTitleFont11);
                Paragraph mOrderDetailsTitleParagraph1 = new Paragraph(mOrderDetailsTitleChunk1);
                mOrderDetailsTitleParagraph1.setAlignment(Element.ALIGN_CENTER);
                document.add(mOrderDetailsTitleParagraph1);
                document.add(new Paragraph("\n"));


                Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
                Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);

                Paragraph p = new Paragraph();
                p.add(new Chunk("Date/Time : ", mOrderDateFont));
                p.add(new Chunk(tv_dateTime.getText().toString().trim(), mOrderDateValueFont));
                document.add(p);
                document.add(new Paragraph("\n\n"));

                Paragraph p1 = new Paragraph();
                p1.add(new Chunk("Operation Performed : ", mOrderDateFont));
                p1.add(new Chunk("AadhaarPay", mOrderDateValueFont));
                document.add(p1);
                document.add(new Paragraph("\n"));
                Font mOrderDetailsFont = new Font(urName, 30.0f, Font.BOLD, mColorAccent);
                Chunk mOrderDetailsChunk = new Chunk("Transaction Details", mOrderDetailsFont);
                Paragraph mOrderDetailsParagraph = new Paragraph(mOrderDetailsChunk);
                mOrderDetailsParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mOrderDetailsParagraph);

                document.add(new Paragraph("\n"));


                // Fields of Order Details...
                // Adding Chunks for Title and value
                Font mOrderIdFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderIdChunk = new Chunk("Transaction ID: " + txnIdForPdf, mOrderIdFont);
                Paragraph mOrderTxnParagraph = new Paragraph(mOrderIdChunk);
                document.add(mOrderTxnParagraph);
                Chunk mOrderIdValueChunk = new Chunk("Aadhar Number: " + aadhaarNumber_dialog, mOrderIdFont);
                Paragraph mOrderaadharParagraph = new Paragraph(mOrderIdValueChunk);
                document.add(mOrderaadharParagraph);
                Chunk mBankNameChunk = new Chunk("Bank Name: " + tv_bankName.getText().toString().trim(), mOrderIdFont);
                Paragraph mBankNameParagraph = new Paragraph(mBankNameChunk);
                document.add(mBankNameParagraph);
                Chunk mOrderrrnChunk = new Chunk("RRN: " + rrNumber_dialog, mOrderIdFont);
                Paragraph mOrderrnParagraph = new Paragraph(mOrderrrnChunk);
                document.add(mOrderrnParagraph);

                if (balanceAmt_dialog.equals("") || balanceAmt_dialog.equalsIgnoreCase("N/A")) {
                    balanceAmt_dialog = "N/A";
                }else{
                    balanceAmt_dialog = "Rs." + balanceAmt_dialog;
                }

                Chunk mOrderbalanceChunk = new Chunk("Balance Amount: " + balanceAmt_dialog, mOrderIdFont);
                Paragraph mOrderbalanceParagraph = new Paragraph(mOrderbalanceChunk);
                document.add(mOrderbalanceParagraph);
//                Chunk mOrdertxnAmtChunk = new Chunk("Transaction Amount: " + tv_transactionAmount.getText().toString().substring(12), mOrderIdFont);
                Chunk mOrdertxnAmtChunk = new Chunk( tv_transactionAmount.getText().toString(), mOrderIdFont);
                Paragraph mOrdertxnAmtParagraph = new Paragraph(mOrdertxnAmtChunk);
                document.add(mOrdertxnAmtParagraph);
                Chunk mOrdertxnTypeChunk = new Chunk("Transaction Type: " + transactionType_dialog, mOrderIdFont);
                Paragraph mOrdertxnTypeParagraph = new Paragraph(mOrdertxnTypeChunk);
                document.add(mOrdertxnTypeParagraph);
                document.add(new Paragraph(""));
                document.add(new Paragraph(""));

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

                Toast.makeText(AadhaarpayReceiptActivity.this, "PDF saved in the internal storage", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AadhaarpayReceiptActivity.this, PreviewPDFActivity.class);
                intent.putExtra("filePath", s);
                startActivity(intent);


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    Intent intent = new Intent(AadhaarpayReceiptActivity.this, DrawPdfAadhaarPayActivity.class);
//                    intent.putExtra("shop_name", SdkConstants.SHOP_NAME);
//                    intent.putExtra("status", _statusMain);
//                    intent.putExtra("dt_time", tv_dateTime.getText().toString().trim());
//                    intent.putExtra("tran_id", txnIdForPdf);
//                    intent.putExtra("aadhar", aadhaarNumber_dialog);
//                    intent.putExtra("bank_name", tv_bankName.getText().toString().trim());
//                    intent.putExtra("rrn", rrNumber_dialog);
//                    intent.putExtra("balance_amount", balanceAmt_dialog);
//                    intent.putExtra("txn_amount", tv_transactionAmount.getText().toString().substring(12));
//                    intent.putExtra("txn_type", "AadhaarPay");
//                    intent.putExtra("brand_name", SdkConstants.BRAND_NAME);
//                    startActivity(intent);
//                }else {
//                    Toast.makeText(AadhaarpayReceiptActivity.this, "PDF saved in the internal storage", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(AadhaarpayReceiptActivity.this, PreviewPDFActivity.class);
//                    intent.putExtra("filePath", s);
//                    startActivity(intent);
//                }



            } catch (IOException | DocumentException ie) {
                Log.e("createPdf: Error ", "" + ie.getLocalizedMessage());
            } catch (ActivityNotFoundException ae) {
                Toast.makeText(AadhaarpayReceiptActivity.this, "No application found to open this file.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void showTransactionDetails(Activity activity, String aadhaarNumber, String rrNumber, String transactionType, String balanceAmt) {
        try {
            final Dialog dialog = new Dialog(activity);
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

            if (aadhaarNumber.equals("") || aadhaarNumber == null) {
                aadhar_number.setText("N/A");
            } else {
                aadhar_number.setText(aadhaarNumber);
            }

            if (rrNumber.equals("") || rrNumber == null) {
                rref_num.setText("N/A");
            } else {
                rref_num.setText(rrNumber);
            }

            if (balanceAmt.equals("") || balanceAmt.equalsIgnoreCase("N/A")) {
                card_transaction_amount.setText("N/A");
            } else {
                card_transaction_amount.setText("Rs." + balanceAmt);
            }

            card_transaction_type.setText(transactionType);


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

    private void initilizationOfviews() {
        iv_status_icon = findViewById(R.id.status_icon);
        tv_transactionAmount = findViewById(R.id.card_amount);
        tv_transactionStatusDecs = findViewById(R.id.balanceText);

        tv_bankName = findViewById(R.id.bank_name);
        tv_dateTime = findViewById(R.id.date_time);
        tv_txnID = findViewById(R.id.txnID);
        tv_viewDetails = findViewById(R.id.txndetails);
        tv_progressViewMessage = findViewById(R.id.pb_aeps3_tv);
        btn_print = findViewById(R.id.printBtn);
        btn_download = findViewById(R.id.downloadBtn);
        btn_close = findViewById(R.id.closeBtn);
        btn_updateStatus = findViewById(R.id.btn_updateStatus);
        btn_ok = findViewById(R.id.btn_ok);
        ll_progressBar_container = findViewById(R.id.progress_container);
        rl_mainLayout = findViewById(R.id.rl_main);
        pb_bigProgressBar = findViewById(R.id.big_pb);
        iv_pending = findViewById(R.id.iv_aeps3_pending);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*for sms feature*/
        mobileCheckBox = findViewById(R.id.mobileCheckBox);
        mobileEditLayout = findViewById(R.id.mobileEditLayout);
        mobileTextLayout = findViewById(R.id.mobileTextLayout);
        editTextMobile = findViewById(R.id.editTextMobile);
        sendButton = findViewById(R.id.sendButton);

    }

    private void callFetchdataAPI(String transactionID) {
        rl_mainLayout.setVisibility(View.GONE);
        ll_progressBar_container.setVisibility(View.VISIBLE);
        btn_updateStatus.setVisibility(View.GONE);
        pb_bigProgressBar.setVisibility(View.VISIBLE);
        iv_pending.setVisibility(View.GONE);
        tv_progressViewMessage.setText("Processing...");

        String url = Constants.FETCH_TRANSACTION_DETAILS + "/" + transactionID;

        AndroidNetworking.post(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!response.toString().equals("") || response.toString() != null) {
                            TransitionManager.beginDelayedTransition(rl_mainLayout);
                            rl_mainLayout.setVisibility(View.VISIBLE);
                            ll_progressBar_container.setVisibility(View.GONE);
                            btn_ok.setVisibility(View.GONE);
                            Log.e("TAG", "onResponse: " + response.toString());
                            try {
                                Log.e("TAG", "onResponse: " + response.toString());
                                aadhaarNumber_dialog = response.getString("origin_identifier");
                                rrNumber_dialog = response.getString("apiTid");
                                transactionType_dialog = "AadhaarPay";
                                balanceAmt_dialog = response.getString("balance");
                                _statusMain = response.getString("status");
                                Log.e("TAG", "FetchAPI " + _statusMain);
                                setDataToView(_statusMain, response.getString("txId"), aadharpayResponse.getTransactionAmount(), response.getString("apiComment"), aadharpayResponse.getBankName());

                            } catch (JSONException e) {
                                Log.e("TAG", "onResponse: " + e.toString());
                                showAlert("Something went Wrong, please check the Report for Transaction details or Contact to support@iservu.in");
                                e.printStackTrace();
                            }
                        } else {
                            showAlert("Something went Wrong, please check the Report for Transaction details or Contact to support@iservu.in");
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("TAG", "onError: " + anError.getErrorBody());
                        TransitionManager.beginDelayedTransition(rl_mainLayout);
                        rl_mainLayout.setVisibility(View.GONE);
                        pb_bigProgressBar.setVisibility(View.GONE);
                        ll_progressBar_container.setVisibility(View.VISIBLE);
                        btn_updateStatus.setVisibility(View.VISIBLE);
                        iv_pending.setVisibility(View.VISIBLE);
                        tv_progressViewMessage.setText("Pending...");
                        btn_ok.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * this function will show one loader for 50 sec.
     * it will stop when the transaction response will receive via Firebase Notification.
     * if the notifiaction will not receive then it will show a FETCH button to fetch the transaction details.
     */
    private void showLoaderForMaxOneMinutes() {

        ll_progressBar_container.setVisibility(View.VISIBLE);
        rl_mainLayout.setVisibility(View.GONE);
        pb_bigProgressBar.setVisibility(View.VISIBLE);
        iv_pending.setVisibility(View.GONE);
        btn_updateStatus.setVisibility(View.GONE);

        countDownTimer = new CountDownTimer(50000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (Constants.NOTIFICATION_RECEIVED) { // this check will be true when the notification will receive
                    Gson gson = new Gson();
                    fcmNotificationData = gson.fromJson(Constants.THE_FIREBASE_DATA, FCMNotificationData.class);
                    Log.e("TAG", "onTick: " + fcmNotificationData.toString());
                    if (fcmNotificationData.getTxId().length() != 0 && aadharpayResponse.getTxId().length() != 0) {
                        if (aadharpayResponse.getTxId().equals(fcmNotificationData.getTxId())) {// To check the txn ID from FCM and Previous Activity
                            Log.e("TAG", "onTick: 2" + fcmNotificationData.toString());
                            rl_mainLayout.setVisibility(View.VISIBLE);
                            ll_progressBar_container.setVisibility(View.GONE);
                            btn_updateStatus.setVisibility(View.GONE);
                            onFinish();
                            cancel();
                            if (!onceDataLoaded) { // To not over load/ Update in live
                                aadhaarNumber_dialog = fcmNotificationData.getAadhaarNumber();
                                rrNumber_dialog = fcmNotificationData.getRrn();
                                transactionType_dialog = "AadhaarPay";
                                balanceAmt_dialog = fcmNotificationData.getBalanceAmount();
                                _statusMain = fcmNotificationData.getStatus();
                                Log.e("TAG", "onTick:3 " + _statusMain);
                                setDataToView(_statusMain, fcmNotificationData.getTxId(), aadharpayResponse.getTransactionAmount(), fcmNotificationData.getStatusDesc(), aadharpayResponse.getBankName());
                                onceDataLoaded = true;
                            }
                        }
                    }
                    Constants.NOTIFICATION_RECEIVED = false;
                }
            }

            public void onFinish() {
                if (!Constants.NOTIFICATION_RECEIVED || !fcmNotificationData.getTxId().equals(aadharpayResponse.getTxId())) {
                    tv_progressViewMessage.setText("Pending...");
                    pb_bigProgressBar.setVisibility(View.GONE);
                    iv_pending.setVisibility(View.VISIBLE);
                    btn_updateStatus.setVisibility(View.VISIBLE);
                }
                cancel();
            }
        }.start();

    }

    public void showAlert(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AadhaarpayReceiptActivity.this);
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

    @SuppressLint("SetTextI18n")
    public void setDataToView(String status, String txnID, String amount, String statusDesc, String bankName) {

//        Constants.isDataLoadedForAadharPay = true;
        Log.e("TAG", "The main status " + status);
        _statusMain = status;

        txnIdForPdf = txnID;

        if (status.equals("SUCCESS")) {
            iv_status_icon.setImageResource(R.drawable.hero_success);
            editTextMobile.setText(aadharpayResponse.getMobileNumber());
            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                mobileTextLayout.setVisibility(View.GONE);
            }
        } else if (status.equals("FAILED")) {
            iv_status_icon.setImageResource(R.drawable.hero_failure);
        } else {
            iv_status_icon.setImageResource(R.drawable.pending);
        }

        if (amount.equals("") || amount.equals(null)) {
            tv_transactionAmount.setText("Txn Amount: Rs." + "N/A");
        } else {
            tv_transactionAmount.setText("Txn Amount: Rs." + amount);
        }

        if (statusDesc.equals("") || statusDesc == null) {
            tv_transactionStatusDecs.setText("N/A");
        } else {
            tv_transactionStatusDecs.setText(statusDesc);
        }

        if (bankName.equals("") || bankName == null) {
            tv_bankName.setText("N/A");
        } else {
            tv_bankName.setText(bankName);
        }

        Date date = Calendar.getInstance().getTime();

        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = formatter.format(date);
        tv_dateTime.setText(currentDateandTime);


        if (txnID.equals("") || txnID.equals(null)) {
            tv_txnID.setText("Transaction ID : " + " N/A");
        } else {
            tv_txnID.setText("Transaction ID : " + txnID);
        }

    }



    /*sms feature methods*/

    public void mobileNumberSMS() {

        if (balanceAmt_dialog.equals("") || balanceAmt_dialog == null) {
            balanceAmt_dialog = "N/A";
        }

        String msgValue = "Thanks for visiting " + SdkConstants.SHOP_NAME + ". Current balance for " + tv_bankName.getText().toString().trim() + " account seeded with aadhaar " + aadhaarNumber_dialog + " is Rs " + balanceAmt_dialog + ". Dated " + tv_dateTime.getText().toString().trim() + ".  Thanks ITPL";

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
                                    Toast.makeText(AadhaarpayReceiptActivity.this, "Message Sent Successfully . ", Toast.LENGTH_SHORT).show();

                                } else {

                                    hideLoader();
                                    Toast.makeText(AadhaarpayReceiptActivity.this, msg, Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(AadhaarpayReceiptActivity.this, "Wallet balance not available", Toast.LENGTH_SHORT).show();


                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoader() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(AadhaarpayReceiptActivity.this);
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

    @Override
    public void onBackPressed() {
        try {
            if (_statusMain.equals("FAILED")) {
                Intent intent = new Intent(this, AadharpayActivity.class);
                intent.putExtra("transactionStatus", "FAILED");
                startActivity(intent);
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Toast.makeText(AadhaarpayReceiptActivity.this, "Connected with " + printerName, Toast.LENGTH_SHORT).show();



        } catch (IOException e) {
            if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(AadhaarpayReceiptActivity.this, "Not Connected\n" + printerName + " is unreachable or off otherwise it is connected with other device", Toast.LENGTH_SHORT).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(AadhaarpayReceiptActivity.this, "the device is already connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AadhaarpayReceiptActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    CardReader.MSRCardData creditDetails;

    public void onScanMSR(final String buffer, CardReader.CARD_TRACK cardTrack) {
        cardTrackType = cardTrack;
        creditData = buffer;
        AadhaarpayReceiptActivity.this.runOnUiThread(new Runnable() {
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

        AadhaarpayReceiptActivity.this.runOnUiThread(new Runnable() {
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
            final String[][] formattedData = {strData.split("&", 3)};
            responseString = formattedData[0][2];
            responseArray[0] = responseString.replace("^", "");
            Log.e("Response Array", responseArray[0]);
            AadhaarpayReceiptActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    replacedData = tempdata.replace("|", "&");
                    formattedData[0] = replacedData.split("&", 3);
                    response = formattedData[0][2];
                    if (response.contains("BAT")) {
                    }
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
            final String[][] formattedData = {strData.split("&", 3)};
            responseString = formattedData[0][2];
            responseArray[0] = responseString.replace("^", "");
            Log.e("Response Array", responseArray[0]);
            AadhaarpayReceiptActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    replacedData = tempdata.replace("|", "&");
                    formattedData[0] = replacedData.split("&", 3);
                    response = formattedData[0][2];
                    if (response.contains("BAT")) {
                    }
                }
            });
        }
    }

    public String printerStatus() throws IOException {
        String data = new String(printerStatus);
        m_AemPrinter.print(data);
        return data;
    }


    private void getPrintData(final String txnId,final String adharcard, final String date, final String bankname,
                              final String referenceNo, final String type) {
        new Thread(new Runnable() {
            public void run() {
                GetConnectToPrinter.getInstance().init();

                //Shop Name Set
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_24_48,
                        EFontTypeExtCode.FONT_24_48);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("60"));
                GetConnectToPrinter.getInstance().setInvert(false);
                GetConnectToPrinter.getInstance().printStr(SdkConstants.SHOP_NAME, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("20"));

                //Transaction Details Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().printStr(getString(R.string.txn_report_txt),null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("20"));

                //status Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("110"));
                GetConnectToPrinter.getInstance().printStr(_statusMain,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("30"));

                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().spaceSet((byte) 0,(byte) 0);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("0"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.transaction_id_txt)+txnId,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.date_time_txt)+date,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.rrn_txt)+referenceNo,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr("BankName: "+bankname,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr("Aadhar Number :"+adharcard,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr("Balance Amount: Rs."+balanceAmt_dialog,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr("TransactionType"+type,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));

                //Transaction Amount
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().spaceSet((byte) 0,(byte) 0);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("0"));
                GetConnectToPrinter.getInstance().printStr("Transaction Amount: Rs."+tv_transactionAmount.getText().toString(),null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("25"));


                //Thank You Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                String thankYouString = getString(R.string.thanks_txt);
                if (thankYouString != null && thankYouString.length() > 0)
                    GetConnectToPrinter.getInstance().printStr(thankYouString,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("15"));

                //Partner(Admin, MD etc.) Name Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_32);
                String brandName = SdkConstants.BRAND_NAME;
                if (brandName != null && brandName.length() > 0)
                    GetConnectToPrinter.getInstance().printStr(brandName,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("100"));

                GetConnectToPrinter.getInstance().start();
            }
        }).start();
    }

    private void getConnection(View view){
        registerForContextMenu(btn_print);
        if (bluetoothAdapter == null) {
            Toast.makeText(AadhaarpayReceiptActivity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
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
                    callBluetoothFunction(tv_txnID.getText().toString(), tv_dateTime.getText().toString(),
                            tv_bankName.getText().toString(), rrNumber_dialog,
                            tv_transactionAmount.getText().toString(), balanceAmt_dialog,
                            transactionType_dialog, aadhaarNumber_dialog, _statusMain, view);

                }
            } else {
                GetPosConnectedPrinter.aemPrinter = null;
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }
        }
    }



}