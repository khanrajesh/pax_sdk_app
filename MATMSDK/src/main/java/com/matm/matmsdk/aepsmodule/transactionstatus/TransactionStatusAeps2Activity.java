package com.matm.matmsdk.aepsmodule.transactionstatus;


import static com.matm.matmsdk.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.matm.matmsdk.permission.PermissionsChecker.REQUIRED_PERMISSION;

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
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.matm.matmsdk.aepsmodule.AEPS2HomeActivity;
import com.matm.matmsdk.aepsmodule.utils.GetPosConnectedPrinter;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.bluetoothprinter.BluetoothDeviceList;
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

public class TransactionStatusAeps2Activity extends AppCompatActivity implements IAemCardScanner, IAemScrybe {

    public static final String TAG = TransactionStatusAeps2Activity.class.getSimpleName();
    private static BluetoothSocket btsocket;
    ImageView status_icon, sendButton;
    ImageButton backBtn;
    TextView balanceText, card_amount, bank_name, date_time, txnID;
    EditText editTextMobile;
    Button txndetails;
    CheckBox mobileCheckBox;
    Button printBtn, downloadBtn, closeBtn;
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
        if (SdkConstants.aepsStatusLayout == 0) {
            setContentView(R.layout.activity_transaction_status_aeps1);
        } else {
            setContentView(SdkConstants.aepsStatusLayout);
        }
        getToneGenerator();

        status_icon = findViewById(R.id.status_icon);
        sendButton = findViewById(R.id.sendButton);
        balanceText = findViewById(R.id.balanceText);
        card_amount = findViewById(R.id.card_amount);
        bank_name = findViewById(R.id.bank_name);
        date_time = findViewById(R.id.date_time);
        mobileTextLayout = findViewById(R.id.mobileTextLayout);
        txnID = findViewById(R.id.txnID);
        txndetails = findViewById(R.id.txndetailsBtn);
        mobileCheckBox = findViewById(R.id.mobileCheckBox);
        printBtn = findViewById(R.id.printBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        closeBtn = findViewById(R.id.closeBtn);
        backBtn = findViewById(R.id.backBtn);
        editTextMobile = findViewById(R.id.editTextMobile);
        mobileEditLayout = findViewById(R.id.mobileEditLayout);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Runtime permission request required if Android permission >= Marshmallow
        checker = new PermissionsChecker(this);
        mContext = getApplicationContext();
        mobile = getIntent().getStringExtra("MOBILE_NUMBER");
        editTextMobile.setText(mobile);

        m_AemScrybeDevice = new AEMScrybeDevice(TransactionStatusAeps2Activity.this);
        printerList = new ArrayList<String>();
        creditData = new String();


        session = new Session(TransactionStatusAeps2Activity.this);
        TransactionStatusModel transactionStatusModel = (TransactionStatusModel) getIntent().getSerializableExtra(SdkConstants.TRANSACTION_STATUS_KEY);

        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = formatter.format(date);
        date_time.setText(currentDateandTime);

        if (getIntent().getSerializableExtra(SdkConstants.TRANSACTION_STATUS_KEY) == null) {
            status_icon.setImageResource(R.drawable.hero_failure);
            balanceText.setText("Failed");
            statusTxt = "Failed";

        } else {

            if (transactionStatusModel.getStatus() != null) {
                if (transactionStatusModel.getStatus().equalsIgnoreCase("0")) {
                    aadharCard = transactionStatusModel.getAadharCard();

                    if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                        mobileTextLayout.setVisibility(View.GONE);
                    }

                    statusTxt = "Success";
                    if (transactionStatusModel.getAadharCard() == null) {
                        aadharCard = "N/A";
                    } else {
                        if (transactionStatusModel.getAadharCard().equalsIgnoreCase("")) {
                            aadharCard = "N/A";
                        } else {
                            StringBuffer buf = new StringBuffer("XXXX-XXXX-"+aadharCard.substring(8));
//                            buf.replace(0, 8, "XXXXXXXX");
                            System.out.println(buf.length());
                            aadharCard = buf.toString();
                        }
                    }

                    if (transactionStatusModel.getTxnID() != null && !transactionStatusModel.getTxnID().matches("")) {
                        txnid = transactionStatusModel.getTxnID();
                    }
                    if (transactionStatusModel.getBankName() != null && !transactionStatusModel.getBankName().matches("")) {
                        bankName = transactionStatusModel.getBankName();
                    }

                    if (transactionStatusModel.getReferenceNo() != null && !transactionStatusModel.getReferenceNo().matches("")) {
                        referenceNo = transactionStatusModel.getReferenceNo();
                    }

                    if (transactionStatusModel.getBalanceAmount() != null && !transactionStatusModel.getBalanceAmount().matches("")) {
                        balance = transactionStatusModel.getBalanceAmount();
                        if (balance.contains(":")) {
                            String[] separated = balance.split(":");
                            balance = separated[1].trim();
                        }
                    }

                    if (transactionStatusModel.getTransactionAmount() != null && !transactionStatusModel.getTransactionAmount().matches("")) {
                        amount = transactionStatusModel.getTransactionAmount();
                    }
                    if (transactionStatusModel.getTransactionType() != null && !transactionStatusModel.getTransactionType().matches("")) {
                        transactionType = transactionStatusModel.getTransactionType();
                    }
                    if (transactionStatusModel.getTransactionType().equalsIgnoreCase("Cash Withdrawal")) {

                        txnID.setText("Transaction ID: " + txnid);
                        bank_name.setText(bankName);
                        card_amount.setText("Txn Amt: Rs. " + amount);

                        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                            mobileTextLayout.setVisibility(View.GONE);
                        }
                    } else if (transactionStatusModel.getTransactionType().equalsIgnoreCase("Balance Enquery") || transactionStatusModel.getTransactionType().equalsIgnoreCase("Balance Enquiry")) {

                        txnID.setText("Transaction ID: " + txnid);
                        bank_name.setText(bankName);
                        card_amount.setText("Balance Amount: Rs. " + balance);

                        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                            mobileTextLayout.setVisibility(View.GONE);
                        }
                    }

                } else {
                    aadharCard = transactionStatusModel.getAadharCard();
                    if (transactionStatusModel.getAadharCard() == null) {
                        aadharCard = "N/A";
                    } else {
                        if (transactionStatusModel.getAadharCard().equalsIgnoreCase("")) {
                            aadharCard = "N/A";
                        } else {
                            StringBuffer buf = new StringBuffer("XXXX-XXXX-"+aadharCard.substring(8));
//                            buf.replace(0, 8, "XXXXXXXX");
                            System.out.println(buf.length());
                            aadharCard = buf.toString();
                        }
                    }

                    if (transactionStatusModel.getTxnID() != null && !transactionStatusModel.getTxnID().matches("")) {
                        txnid = transactionStatusModel.getTxnID();
                    }

                    if (transactionStatusModel.getBankName() != null && !transactionStatusModel.getBankName().matches("")) {
                        bankName = transactionStatusModel.getBankName();
                    }

                    if (transactionStatusModel.getReferenceNo() != null && !transactionStatusModel.getReferenceNo().matches("")) {
                        referenceNo = transactionStatusModel.getReferenceNo();
                    }
                    if (transactionStatusModel.getTransactionType() != null && !transactionStatusModel.getTransactionType().matches("")) {
                        transactionType = transactionStatusModel.getTransactionType();
                    }


                    if (transactionStatusModel.getBalanceAmount() != null && !transactionStatusModel.getBalanceAmount().matches("")) {
                        balance = transactionStatusModel.getBalanceAmount();
                        if (balance.contains(":")) {
                            String[] separated = balance.split(":");
                            balance = separated[1].trim();
                        }
                    }

                    if (transactionStatusModel.getTransactionAmount() != null && !transactionStatusModel.getTransactionAmount().matches("")) {
                        amount = transactionStatusModel.getTransactionAmount();
                    }
                    status_icon.setImageResource(R.drawable.hero_failure);
                    balanceText.setText(transactionStatusModel.getApiComment());
                    statusTxt = "Failed";


                    if (transactionStatusModel.getTransactionType().equalsIgnoreCase("Cash Withdrawal")) {
                        txnID.setText("Transaction ID: " + txnid);
                        bank_name.setText(bankName);
                        card_amount.setText("Txn Amt: Rs. " + amount);


                    } else if (transactionStatusModel.getTransactionType().equalsIgnoreCase("Balance Enquery") || transactionStatusModel.getTransactionType().equalsIgnoreCase("Balance Enquiry")) {
                        txnID.setText("Transaction ID: " + txnid);
                        bank_name.setText(bankName);
                        card_amount.setText("Balance Amount: Rs. " + balance);

                    }
                }


            } else {
                aadharCard = transactionStatusModel.getAadharCard();
                if (transactionStatusModel.getAadharCard() == null) {
                    aadharCard = "N/A";
                } else {
                    if (transactionStatusModel.getAadharCard().equalsIgnoreCase("")) {
                        aadharCard = "N/A";
                    } else {
                        StringBuffer buf = new StringBuffer("XXXX-XXXX-"+aadharCard.substring(8));
//                        buf.replace(0, 8, "XXXXXXXX");
                        System.out.println(buf.length());
                        aadharCard = buf.toString();
                    }
                }

                if (transactionStatusModel.getTxnID() != null && !transactionStatusModel.getTxnID().matches("")) {
                    txnid = transactionStatusModel.getTxnID();
                }

                if (transactionStatusModel.getBankName() != null && !transactionStatusModel.getBankName().matches("")) {
                    bankName = transactionStatusModel.getBankName();
                }

                if (transactionStatusModel.getReferenceNo() != null && !transactionStatusModel.getReferenceNo().matches("")) {
                    referenceNo = transactionStatusModel.getReferenceNo();
                }
                if (transactionStatusModel.getTransactionType() != null && !transactionStatusModel.getTransactionType().matches("")) {
                    transactionType = transactionStatusModel.getTransactionType();
                }


                if (transactionStatusModel.getBalanceAmount() != null && !transactionStatusModel.getBalanceAmount().matches("")) {
                    balance = transactionStatusModel.getBalanceAmount();
                    if (balance.contains(":")) {
                        String[] separated = balance.split(":");
                        balance = separated[1].trim();
                    }
                }

                if (transactionStatusModel.getTransactionAmount() != null && !transactionStatusModel.getTransactionAmount().matches("")) {
                    amount = transactionStatusModel.getTransactionAmount();
                }
                status_icon.setImageResource(R.drawable.hero_failure);
                balanceText.setText(transactionStatusModel.getApiComment());
                statusTxt = "Failed";


                if (transactionStatusModel.getTransactionType().equalsIgnoreCase("Cash Withdrawal")) {
                    txnID.setText("Transaction ID: " + txnid);
                    bank_name.setText(bankName);
                    card_amount.setText("Txn Amt: Rs. " + amount);


                } else if (transactionStatusModel.getTransactionType().equalsIgnoreCase("Balance Enquery") || transactionStatusModel.getTransactionType().equalsIgnoreCase("Balance Enquiry")) {
                    txnID.setText("Transaction ID: " + txnid);
                    bank_name.setText(bankName);
                    card_amount.setText("Balance Amount: Rs. " + balance);

                }

            }


        }

        txndetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionDetails(TransactionStatusAeps2Activity.this);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent mainActivity = new Intent(TransactionStatusAeps2Activity.this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(TransactionStatusAeps2Activity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    Date date = new Date();
                    long timeMilli = date.getTime();
                    System.out.println("Time in milliseconds using Date class: " + String.valueOf(timeMilli));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        createPdf(FileUtils.commonDocumentDirPath("PDF") + String.valueOf(timeMilli) + "Order_Receipt.pdf");
                    }else {
                        createPdf(FileUtils.getAppPath(mContext) + String.valueOf(timeMilli) + "Order_Receipt.pdf");
                    }
                }
            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceModel = android.os.Build.MODEL;
                if(deviceModel.equalsIgnoreCase("A910")) {

                    getPrintData(txnID.getText().toString(), aadharCard,date_time.getText().toString(),bank_name.getText().toString(),referenceNo, transactionType );



                } else {
                    registerForContextMenu(printBtn);
                    if (bluetoothAdapter == null) {
                        Toast.makeText(TransactionStatusAeps2Activity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
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
                                callBluetoothFunction(txnID.getText().toString(), aadharCard, date_time.getText().toString(), bank_name.getText().toString(), referenceNo, transactionType,v);

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
    private void createPdf(String s) {
        filePath = s;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            //This is for android 11 changes
//            if (checkPermission()) {
                createPdfGenericMethod(s);
//            } else {
//                requestPermission();
//            }
//        } else {
//            createPdfGenericMethod(s);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = BluetoothDeviceList.getSocket();
            if (btsocket != null) {
                // printText(message.getText().toString());
                System.out.println("Connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    createPdfGenericMethod(filePath);
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }else if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
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
            if (statusTxt.equalsIgnoreCase("Failed")) {
                mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.RED);

            } else {
                mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.GREEN);
            }

            Chunk mOrderDetailsTitleChunk1 = new Chunk(statusTxt, mOrderDetailsTitleFont11);
            Paragraph mOrderDetailsTitleParagraph1 = new Paragraph(mOrderDetailsTitleChunk1);
            mOrderDetailsTitleParagraph1.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph1);
            document.add(new Paragraph("\n"));


            Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);

            Paragraph p = new Paragraph();
            p.add(new Chunk("Date/Time : ", mOrderDateFont));
            p.add(new Chunk(date_time.getText().toString().trim(), mOrderDateValueFont));
            document.add(p);
            document.add(new Paragraph("\n"));

            Paragraph p1 = new Paragraph();
            p1.add(new Chunk("Operation Performed : ", mOrderDateFont));
            p1.add(new Chunk("AePS 2", mOrderDateValueFont));
            document.add(p1);


            document.add(new Paragraph("\n"));


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
            Chunk mOrderIdChunk = new Chunk("Transaction ID: " + txnid, mOrderIdFont);
            Paragraph mOrderTxnParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderTxnParagraph);
            Chunk mOrderIdValueChunk = new Chunk("Aadhaar Number: " + aadharCard, mOrderIdFont);
            Paragraph mOrderaadharParagraph = new Paragraph(mOrderIdValueChunk);
            document.add(mOrderaadharParagraph);
            Chunk mBankNameChunk = new Chunk("Bank Name: " + bank_name.getText().toString().trim(), mOrderIdFont);
            Paragraph mBankNameParagraph = new Paragraph(mBankNameChunk);
            document.add(mBankNameParagraph);
            Chunk mOrderrrnChunk = new Chunk("RRN: " + referenceNo, mOrderIdFont);
            Paragraph mOrderrnParagraph = new Paragraph(mOrderrrnChunk);
            document.add(mOrderrnParagraph);
            Chunk mOrderbalanceChunk = new Chunk("Balance Amount: Rs." + balance, mOrderIdFont);
            Paragraph mOrderbalanceParagraph = new Paragraph(mOrderbalanceChunk);
            document.add(mOrderbalanceParagraph);
            Chunk mOrdertxnAmtChunk = new Chunk("Transaction Amount: Rs." + amount, mOrderIdFont);
            Paragraph mOrdertxnAmtParagraph = new Paragraph(mOrdertxnAmtChunk);
            document.add(mOrdertxnAmtParagraph);
            Chunk mOrdertxnTypeChunk = new Chunk("Transaction Type: " + transactionType, mOrderIdFont);
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

            Toast.makeText(mContext, "PDF saved in the internal storage", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TransactionStatusAeps2Activity.this, PreviewPDFActivity.class);
            intent.putExtra("filePath", dest);
            startActivity(intent);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                Intent intent = new Intent(TransactionStatusAeps2Activity.this, DrawPDFAeps2Activity.class);
//                intent.putExtra("shop_name", SdkConstants.SHOP_NAME);
//                intent.putExtra("status", statusTxt);
//                intent.putExtra("dt_time", date_time.getText().toString().trim());
//                intent.putExtra("tran_id", txnid);
//                intent.putExtra("aadhar", aadharCard);
//                intent.putExtra("bank_name", bank_name.getText().toString().trim());
//                intent.putExtra("rrn", referenceNo);
//                intent.putExtra("balance_amount", balance);
//                intent.putExtra("txn_amount", amount);
//                intent.putExtra("txn_type", transactionType);
//                intent.putExtra("brand_name", SdkConstants.BRAND_NAME);
//                startActivity(intent);
//            }else {
//                Intent intent = new Intent(TransactionStatusAeps2Activity.this, PreviewPDFActivity.class);
//                intent.putExtra("filePath", dest);
//                startActivity(intent);
//            }
        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", "" + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        }
    }*/

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            btsocket = BluetoothDeviceList.getSocket();
            if (btsocket != null) {
                // printText(message.getText().toString());
                System.out.println("Connected");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        }
    }*/



    private void showBrandSetAlert() {
        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(TransactionStatusAeps2Activity.this);
            builder1.setMessage("Unable to download/print the receipt. Please contact admin.");
            builder1.setTitle("Warning!!!");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "GOT IT",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {

        }
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
                GetConnectToPrinter.getInstance().printStr(statusTxt,null);
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
                GetConnectToPrinter.getInstance().printStr("Balance Amount: Rs."+balance,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr("TransactionType"+type,null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));

                //Transaction Amount
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().spaceSet((byte) 0,(byte) 0);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("0"));
                GetConnectToPrinter.getInstance().printStr("Transaction Amount: Rs."+amount,null);
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



    private void callBluetoothFunction(final String txnId, final String aadharNo, final String date, final String bank_name, final String reffNo, final String type, View view) {

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
            m_AemPrinter.print("\n\n");
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            m_AemPrinter.POS_FontThreeInchRIGHT();
            m_AemPrinter.print("Thank you \n");
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_CENTER);
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_RIGHT);
            m_AemPrinter.POS_FontThreeInchRIGHT();
            m_AemPrinter.print(SdkConstants.BRAND_NAME);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            data = printerStatus();
            m_AemPrinter.print(data);
            m_AemPrinter.print("\n");
        } catch (IOException e) {
//            e.printStackTrace();
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
//                mPrinter.printText("Aadhaar Number: " + aadharNo);
//                mPrinter.addNewLine();
//                mPrinter.printText("Date/Time: " + date);
//                mPrinter.addNewLine();
//                mPrinter.printText("Bank Name.: " + bank_name);
//                mPrinter.addNewLine();
//                mPrinter.printText("RRN: " + reffNo);
//                mPrinter.addNewLine();
//                mPrinter.printText("Balance Amount: Rs." + balance);
//                mPrinter.addNewLine();
//                mPrinter.printText("Transaction Amount: Rs." + amount);
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
//                hideLoader();
//                Log.d(TAG, "onConnected: Print Finished SuccessFully .....");
//            }
//            @Override
//            public void onFailed() {
//                Log.d(TAG, "Connection failed");
//                hideLoader();
//                Toast.makeText(TransactionStatusAeps2Activity.this, "Please switch on bluetooth printer", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public void mobileNumberSMS() {

        String msgValue = "Thanks for visiting " + SdkConstants.SHOP_NAME + ". Current balance for " + bankName + " account seeded with balance is Rs " + balance + ". Dated " + date_time.getText().toString() + ".  Thanks ITPL";

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

                                    Toast.makeText(TransactionStatusAeps2Activity.this, "Message Sent Successfully . ", Toast.LENGTH_SHORT).show();

                                } else {

                                    hideLoader();
                                    Toast.makeText(TransactionStatusAeps2Activity.this, msg, Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(TransactionStatusAeps2Activity.this, "Wallet balance not available", Toast.LENGTH_SHORT).show();


                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoader() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(TransactionStatusAeps2Activity.this);
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
            aadhar_number.setText(aadharCard);
            rref_num.setText(referenceNo);
            card_transaction_type.setText(transactionType);


            if (transactionType.equalsIgnoreCase("Cash Withdrawal")) {

                card_transaction_amount.setText(balance);

            } else if (transactionType.equalsIgnoreCase("Balance Enquery") || transactionType.equalsIgnoreCase("Balance Enquiry")) {

                balanceAmtID.setText("Transaction Amount");
                card_transaction_amount.setText(amount);


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

    public void getToneGenerator() {

   /*     ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150);
        toneGen1.release();
*/

        ToneGenerator dtmfGenerator = new ToneGenerator(0, ToneGenerator.MAX_VOLUME);
        dtmfGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 1000); // all types of tones are available...
        dtmfGenerator.stopTone();


    }


    @Override
    public void onBackPressed() {

        try {
            if (statusTxt.equalsIgnoreCase("Failed")) {
                Intent intent = new Intent(TransactionStatusAeps2Activity.this, AEPS2HomeActivity.class);
                intent.putExtra("FAILEDVALUE", "FAILEDDATA");
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showAlert(String alertMsg) {
        android.app.AlertDialog.Builder alertBox = new android.app.AlertDialog.Builder(TransactionStatusAeps2Activity.this);
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
            Toast.makeText(TransactionStatusAeps2Activity.this, "Connected with " + printerName, Toast.LENGTH_SHORT).show();

            //            String data=new String(batteryStatusCommand);
//            m_AemPrinter.print(data);
            //  m_cardReader.readMSR();


        } catch (IOException e) {
            if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(TransactionStatusAeps2Activity.this, "Not Connected\n" + printerName + " is unreachable or off otherwise it is connected with other device", Toast.LENGTH_SHORT).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(TransactionStatusAeps2Activity.this, "the device is already connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TransactionStatusAeps2Activity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    CardReader.MSRCardData creditDetails;

    public void onScanMSR(final String buffer, CardReader.CARD_TRACK cardTrack) {
        cardTrackType = cardTrack;
        creditData = buffer;
        TransactionStatusAeps2Activity.this.runOnUiThread(new Runnable() {
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

        TransactionStatusAeps2Activity.this.runOnUiThread(new Runnable() {
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
            TransactionStatusAeps2Activity.this.runOnUiThread(new Runnable() {
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
            TransactionStatusAeps2Activity.this.runOnUiThread(new Runnable() {
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

    public String printerStatus() throws IOException {
        String data = new String(printerStatus);
        m_AemPrinter.print(data);
        return data;
    }
    private void getConnection(View view){
        GetPosConnectedPrinter.aemPrinter = null;
        registerForContextMenu(printBtn);
        if (bluetoothAdapter == null) {
            Toast.makeText(TransactionStatusAeps2Activity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
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
                    callBluetoothFunction(txnID.getText().toString(), aadharCard, date_time.getText().toString(), bank_name.getText().toString(), referenceNo, transactionType,view);

                }
            } else {
                GetPosConnectedPrinter.aemPrinter = null;
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }
        }
    }
}
