package com.matm.matmsdk.posmorefun;


import static com.matm.matmsdk.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.matm.matmsdk.permission.PermissionsChecker.REQUIRED_PERMISSION;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import com.matm.matmsdk.FileUtils;
import com.matm.matmsdk.Service.BankResponse;
import com.matm.matmsdk.Utils.PAXScreen;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.permission.PermissionsActivity;
import com.matm.matmsdk.permission.PermissionsChecker;
import com.matm.matmsdk.readfile.PreviewPDFActivity;
import com.paxsz.easylink.api.EasyLinkSdkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import isumatm.androidsdk.equitas.R;

public class TransactionStatusPOSActivity extends AppCompatActivity {

    ImageView status_icon;
    ImageButton backBtn;
    private TextView rref_num;
    TextView balanceText,statusDescTxt, card_amount,  date_time, txnID,customer_mobile;
    Button txndetails;
    AppCompatButton downloadReceiptBtn,printReceiptBtn;
    Button closeBtn;
    ProgressDialog progressDialog;
    public EasyLinkSdkManager manager;
    BluetoothDevice bluetoothDevice;
    Context mContext;
    BluetoothAdapter B;
    String statusTxt;
    private int STORAGE_PERMISSION_CODE = 1;
    String transaction_type;
    String mobile;
    String prefNum = "NA", MID = "NA", TID = "NA", CARD_TYPE = "NA", Card_NUM = "NA", balanceAmt = "NA", transactionAmt = "NA", TRANSACTION_ID = "NA", TXN_ID = "NA";
    String transactionTypeCheck;
    PermissionsChecker checker;

    private String bankName = "";
    private String merchantName = "";
    private String location = "";
    private String date = "";
    private String midTid = "";
    private String batch = "";
    private String sale = "";
    private String card = "";
    private String cardType = "";
    private String rrn = "";
    private String cardHolderName = "";
    private String pin = "";
    private String  RESPONSE_CODE = "";

    private String applicationVersion = "";
    private String applicationCryptogram = "";
    private String txnStatus = "";
    private String dedicatedFileName = "";
    private String terminalVerResult = "";
    private String applPreferredName = "";

    //void flag
    private boolean isVoid = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_pos);
        manager = EasyLinkSdkManager.getInstance(this);
        mContext = getApplicationContext();

        checker = new PermissionsChecker(this);

        B = BluetoothAdapter.getDefaultAdapter();
        status_icon = findViewById(R.id.status_icon);
        balanceText = findViewById(R.id.balanceText);
        statusDescTxt = findViewById(R.id.statusDescTxt);
        card_amount = findViewById(R.id.card_amount);
        customer_mobile = findViewById(R.id.customer_mobile);
//        bank_name = findViewById(R.id.card_type);
        date_time = findViewById(R.id.date_time);
        txnID = findViewById(R.id.txnID);
//        txndetails = findViewById(R.id.txndetailsBtn);
        closeBtn = findViewById(R.id.closeBtn);
        backBtn = findViewById(R.id.backBtn);
        downloadReceiptBtn = findViewById(R.id.trans_success_view_receipt);
        printReceiptBtn = findViewById(R.id.trans_success_print_receipt);
        rref_num = findViewById(R.id.rref_num);

        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = formatter.format(date);
        date_time.setText("Date : " + currentDateandTime);
        transaction_type = getIntent().getStringExtra("TRANSACTION_TYPE");
        TRANSACTION_ID = getIntent().getStringExtra("TRANSACTION_ID");

        bankName = getIntent().getStringExtra("bank_name");

        String applName = getIntent().getStringExtra("APP_NAME");
        String aId = getIntent().getStringExtra("AID");
        prefNum = getIntent().getStringExtra("RRN_NO");
        MID = getIntent().getStringExtra("MID");
        TID = getIntent().getStringExtra("TID");
         TXN_ID = getIntent().getStringExtra("TXN_ID");
        String INVOICE = getIntent().getStringExtra("INVOICE");

        CARD_TYPE = getIntent().getStringExtra("CARD_TYPE");
        String APPR_CODE = getIntent().getStringExtra("APPR_CODE");
        Card_NUM = getIntent().getStringExtra("CARD_NUMBER");
        balanceAmt = getIntent().getStringExtra("AMOUNT");
        transactionAmt = getIntent().getStringExtra("TRANSACTION_AMOUNT");
//        balanceAmt = getDecimalString(balanceAmt);
         RESPONSE_CODE = getIntent().getStringExtra("RESPONSE_CODE");
      /*  if (RESPONSE_CODE != null && !RESPONSE_CODE.isEmpty()) {
            RESPONSE_CODE = RESPONSE_CODE.substring(2);
        }*/

        boolean isVoid = getIntent().getBooleanExtra("IsVoid", false);

        //new added for visa test cases
//        bankName = getIntent().getStringExtra("bank_name");
        merchantName = getIntent().getStringExtra("merchant_name");
        location = getIntent().getStringExtra("location");
        //date=getIntent().getStringExtra("date");
        midTid = getIntent().getStringExtra("mid_tid");
        batch = getIntent().getStringExtra("batch");
        sale = getIntent().getStringExtra("sale");
        card = getIntent().getStringExtra("card");
        cardType = getIntent().getStringExtra("card_type");
        rrn = getIntent().getStringExtra("rrn");
        cardHolderName = getIntent().getStringExtra("card_name");
        pin = getIntent().getStringExtra("pin");
        applicationVersion = getIntent().getStringExtra("application_version");
        applicationCryptogram = getIntent().getStringExtra("application_cryptogram");
        txnStatus = getIntent().getStringExtra("txn_status");
        dedicatedFileName = getIntent().getStringExtra("dedicated_file_name");
        terminalVerResult = getIntent().getStringExtra("terminal_result");
        applPreferredName = getIntent().getStringExtra("appl_preferred_name");

        rref_num.setText(prefNum);
        customer_mobile.setText(SdkConstants.CUSTOMER_MOB);

        if (balanceAmt != null) {
            if (balanceAmt.equalsIgnoreCase("0") || balanceAmt.equalsIgnoreCase("N/A") || balanceAmt.equalsIgnoreCase("NA")) {
                balanceAmt = "N/A";
            } else {
                balanceAmt = replaceWithZero(balanceAmt);
            }
        } else {
            balanceAmt = "NA";
        }


        if(!isVoid){
            if (transactionAmt != null && transactionAmt.length() > 0) {
                if (transactionAmt.equalsIgnoreCase("0") || transactionAmt.equalsIgnoreCase("N/A") || transactionAmt.equalsIgnoreCase("NA") || transactionAmt.equalsIgnoreCase("null") || transactionAmt == null) {
                    transactionAmt = "N/A";
                } else {
                    transactionAmt = replaceWithZero(transactionAmt);
                }
            } else {
                transactionAmt = "NA";
            }
        }



//        if (transactionAmt.equalsIgnoreCase("N/A")) {


        System.out.println(">>>----" + balanceAmt);

        if (Card_NUM != null && !Card_NUM.isEmpty()) {
            String[] splitAmount = Card_NUM.split("D");
            Card_NUM = splitAmount[0];

            String firstnum = Card_NUM.substring(0, 2);
            String middlenum = Card_NUM.substring(2, Card_NUM.length() - 2);
            String lastNum = Card_NUM.replace(firstnum + middlenum, "");

            System.out.println(">>>---" + firstnum);
            System.out.println(">>>---" + middlenum);
            System.out.println(">>>---" + lastNum);

        }

        if (transaction_type.equalsIgnoreCase("cash")) {
            transactionTypeCheck = "Cash Withdrawal";
            card_amount.setText("Txn Amt : Rs. " + transactionAmt);

        }else if(isVoid){
            transactionTypeCheck = "VOID";
            card_amount.setText("Balance Amount: Rs. " + balanceAmt);
        }
        else if(transaction_type.equalsIgnoreCase("POS")){
            card_amount.setText("Rs. " + transactionAmt);
        }
        else {
            transactionTypeCheck = "Balance Enquiry";
            card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            transactionAmt = "N/A";
        }

        // CARD_NUMBER =
        String flag = getIntent().getStringExtra("flag");
        if (flag.equalsIgnoreCase("failure")) {
            //hide download receipt button
            downloadReceiptBtn.setVisibility(View.GONE);
            printReceiptBtn.setVisibility(View.GONE);
            balanceText.setText("Transaction Unsuccessful");
            balanceText.setTextColor(getResources().getColor(R.color.textViewTxnPos));
            status_icon.setImageResource(R.drawable.failed);
            statusDescTxt.setVisibility(View.VISIBLE);

            statusTxt = "Failed";
            txnID.setText("ID : " + TRANSACTION_ID);

            if (RESPONSE_CODE != null && !RESPONSE_CODE.isEmpty() && !TRANSACTION_ID.isEmpty()) {
                BankResponse.showStatusMessage(manager, RESPONSE_CODE, statusDescTxt);
                PAXScreen.showFailure(manager);


//                bank_name.setText(CARD_TYPE);
//                balanceAmt = "N/A";

                if(prefNum == null || prefNum.equalsIgnoreCase("")){
                    rref_num.setText("RRN No. " + "NA");
                }else{
                    rref_num.setText("RRN No. " + prefNum);
                }


            }

            if (transaction_type.equalsIgnoreCase("cash")) {
                transactionTypeCheck = "Cash Withdrawal";
                card_amount.setText("Txn Amt : Rs. " + transactionAmt);
            }
            else if(isVoid){
                transactionTypeCheck = "VOID";
                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            }
            else {
                transactionTypeCheck = "Balance Enquiry";
                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            }

            String transactionType = "";
            if (SdkConstants.transactionType.equalsIgnoreCase("0")) {
                transactionType = "BalanceEnquiry Failled!! ";
            } else {
                transactionType = "CashWithdraw Failled!! ";
            }

            String str = "";
            str = balanceText.getText().toString();

            if (Card_NUM != null && card_amount != null && !TID.isEmpty()) {
                String responseData = generateJsonData(transactionType, str, prefNum, Card_NUM, card_amount.getText().toString(), TID);
                SdkConstants.responseData = responseData;
            }

        } else {
            //Show Success
            //show download receipt button
            downloadReceiptBtn.setVisibility(View.VISIBLE);
            printReceiptBtn.setVisibility(View.VISIBLE);
            balanceText.setVisibility(View.GONE);
            statusTxt = "Success";
            PAXScreen.showSuccess(manager);
            txnID.setText("Transaction ID : " + TRANSACTION_ID);
          //  bank_name.setText(CARD_TYPE);
            if (transaction_type.equalsIgnoreCase("cash")) {
                transactionTypeCheck = "Cash Withdrawal";
                card_amount.setText("Txn Amt : Rs. " + transactionAmt);
            } else if(isVoid){
                transactionTypeCheck = "VOID";
                card_amount.setText(transactionAmt);
            }

            else {
                transactionTypeCheck = "Balance Enquiry";
                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            }

            String transactionType = "";
            if (SdkConstants.transactionType.equalsIgnoreCase("0")) {
                transactionType = "BalanceEnquiry Successful!! ";
            } else {
                transactionType = "CashWithdraw Successful!! ";
            }

            String str = "";
            str = statusTxt;

            String responseData = generateJsonData(transactionType, str, prefNum, Card_NUM, transactionAmt, TID);
            SdkConstants.responseData = responseData;


        }

        if(isVoid){
            card_amount.setText(getIntent().getStringExtra("TRANSACTION_AMOUNT"));
        }


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

      /*  backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mainActivity = new Intent(TransactionStatusPOSActivity.this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });*/

      /*  txndetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionDetails(TransactionStatusPOSActivity.this);
            }
        });*/
        downloadReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(TransactionStatusPOSActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    Date date = new Date();
                    long timeMilli = date.getTime();
                    System.out.println("Time in milliseconds using Date class: " + String.valueOf(timeMilli));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        createPdf(FileUtils.commonDocumentDirPath("PDF") + timeMilli +"_"+getIntent().getStringExtra("RRN_NO") + ".pdf");
                    } else {
                        createPdf(FileUtils.getAppPath(mContext) + timeMilli + "_"+getIntent().getStringExtra("RRN_NO") + ".pdf");
                    }
                }


            }
        });


        printReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public String replaceWithZero(String s) {
        float amount = Integer.valueOf(s) / 100F;
        DecimalFormat formatter = new DecimalFormat("##,##,##,##0.00");
        return formatter.format(Double.parseDouble(String.valueOf(amount)));
    }

    public String generateJsonData(String status, String statusDesc, String rrn, String cardno, String bal, String terminalId) {
        String jdata = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("TransactionStatus", status);
            obj.put("StatusDescription", statusDesc);
            obj.put("RRN", rrn);
            obj.put("CardNumber", cardno);
            obj.put("Balance", bal);
            obj.put("TerminalID", terminalId);
            jdata = obj.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jdata;
    }


    public void showTransactionDetails(Activity activity) {
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.transaction_matm_details_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView aadhar_number = (TextView) dialog.findViewById(R.id.aadhar_number);
            TextView rref_num = (TextView) dialog.findViewById(R.id.rref_num);
            TextView card_transaction_type = (TextView) dialog.findViewById(R.id.card_transaction_type);
            TextView txnType = (TextView) dialog.findViewById(R.id.txnType);
            TextView card_number = (TextView) dialog.findViewById(R.id.card_number);
            TextView card_transaction_amount = (TextView) dialog.findViewById(R.id.card_transaction_amount);
            TextView balanceAmtId = (TextView) dialog.findViewById(R.id.balanceAmtID);
            aadhar_number.setText("488");//MID
            rref_num.setText(prefNum);
            card_transaction_type.setText(TID);
            card_number.setText(Card_NUM);

            if (transaction_type.equalsIgnoreCase("cash")) {
                card_transaction_amount.setText((balanceAmt));
                balanceAmt = card_transaction_amount.getText().toString();
                txnType.setText("Sale");//Cash Withdrawal
                balanceAmtId.setText("Balance Amount");
                transactionTypeCheck = txnType.getText().toString();
            } else {
                txnType.setText("Balance Enquiry");
                transactionAmt = "N/A";
                card_transaction_amount.setText(transactionAmt);
                transactionTypeCheck = txnType.getText().toString();
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

    private void createPdf(String filePath) {
        createPdfGenericMethod(filePath);
    }

    private void createPdfGenericMethod(String dest) {
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
            document.addCreationDate();
            document.addAuthor("");
            document.addCreator("");
            document.setMargins(0, 0, 50, 50);
            Rectangle rect = new Rectangle(577, 825, 18, 15);
            rect.enableBorderSide(1);
            rect.enableBorderSide(2);
            rect.enableBorderSide(4);
            rect.enableBorderSide(8);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            rect.setBorderColor(BaseColor.BLACK);
            //document.add(rect);

            /*commit git test*/
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 24.0f;
            float mValueFontSize = 26.0f;
            BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));



            Font mOrderDetailsTitleFont = new Font(urName, 30.0f, Font.NORMAL, BaseColor.BLACK);

            if(bankName != null && bankName.length() >0){
                Chunk mOrderDetailsTitleChunk = new Chunk(bankName, mOrderDetailsTitleFont);
                Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
                mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mOrderDetailsTitleParagraph);
                document.add(new Paragraph("\n"));
            }

            if(merchantName != null && merchantName.length() >0){
                Chunk mMerchantName = new Chunk(merchantName, mOrderDetailsTitleFont);
                Paragraph mMerchanParagraph = new Paragraph(mMerchantName);
                mMerchanParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mMerchanParagraph);
            }

            if(location != null && location.length() >0){
                Chunk mLocationName = new Chunk(location, mOrderDetailsTitleFont);
                Paragraph mLocationNameParagraph = new Paragraph(mLocationName);
                mLocationNameParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mLocationNameParagraph);
                document.add(new Paragraph("\n"));
            }



//            Chunk mCityName = new Chunk("City\n" , mOrderDetailsTitleFont11);
//            Paragraph mCityNameParagraph = new Paragraph(mCityName);
//            mCityNameParagraph.setAlignment(Element.ALIGN_CENTER);
//            document.add(mCityNameParagraph);

            Font mOrderFont = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);

            //Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Paragraph p = new Paragraph();
            p.add(new Chunk("Date/Time : ", mOrderFont));
            p.add(new Chunk(date_time.getText().toString().trim(), mOrderFont));
            document.add(p);


            if(midTid != null && midTid.length() > 0){
                Chunk mMidChunk = new Chunk(midTid, mOrderFont);
                Paragraph mMidParagraph = new Paragraph(mMidChunk);
                mMidParagraph.setAlignment(Element.ALIGN_LEFT);
                document.add(mMidParagraph);
            }


//            Chunk mTidChunk = new Chunk( "TID" + TID, mOrderIdFont);
//            Paragraph mTidParagraph = new Paragraph(mTidChunk);
//            mTidParagraph.setAlignment(Element.ALIGN_LEFT);
//            document.add(mTidParagraph);
            if(batch != null && batch.length() > 0){
                Chunk mBatchNoChunk = new Chunk(batch, mOrderFont);
                Paragraph mBatchNoParagraph = new Paragraph(mBatchNoChunk);
                mBatchNoParagraph.setAlignment(Element.ALIGN_LEFT);
                document.add(mBatchNoParagraph);
            }


//            Chunk mInvoiceNoChunk = new Chunk("Invoice No. " + TID, mOrderIdFont);
//            Paragraph mInVoiceNoParagraph = new Paragraph(mInvoiceNoChunk);
//            mInVoiceNoParagraph.setAlignment(Element.ALIGN_LEFT);
//            document.add(mInVoiceNoParagraph);

            if(sale != null && sale.length() > 0){
                Chunk mSaleChunk = new Chunk(sale, mOrderFont);
                Paragraph mSaleParagraph = new Paragraph(mSaleChunk);
                mSaleParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mSaleParagraph);
            }


            if(card != null && card.length() > 0){
                Chunk mCardNoChunk = new Chunk("Card No: " + card, mOrderFont);
                Paragraph mCardNoParagraph = new Paragraph(mCardNoChunk);
                mCardNoParagraph.setAlignment(Element.ALIGN_LEFT);
                document.add(mCardNoParagraph);
            }


            if(cardType != null && cardType.length() > 0){
                Chunk mCardTypeChunk = new Chunk(cardType, mOrderFont);
                Paragraph mCardTypeParagraph = new Paragraph(mCardTypeChunk);
                mCardTypeParagraph.setAlignment(Element.ALIGN_LEFT);
                document.add(mCardTypeParagraph);
            }



            if(rrn != null && rrn.length() > 0){
                Chunk mRrnNoChunk = new Chunk(rrn, mOrderFont);
                Paragraph mRrnNoParagraph = new Paragraph(mRrnNoChunk);
                mRrnNoParagraph.setAlignment(Element.ALIGN_LEFT);
                document.add(mRrnNoParagraph);
            }


            if (transactionAmt != null && transactionAmt.length() > 0) {

                if (getIntent().getBooleanExtra("IsVoid", false)) {
                    Chunk mOrdertxnAmtChunk = new Chunk(getIntent().getStringExtra("TRANSACTION_AMOUNT"), mOrderFont);
                    Paragraph mOrdertxnAmtParagraph = new Paragraph(mOrdertxnAmtChunk);
                    document.add(mOrdertxnAmtParagraph);
                }else{
                    Chunk mOrdertxnAmtChunk = new Chunk("Transaction Amount: " + transactionAmt, mOrderFont);
                    Paragraph mOrdertxnAmtParagraph = new Paragraph(mOrdertxnAmtChunk);
                    document.add(mOrdertxnAmtParagraph);
                }
            }


            if (applicationCryptogram != null && !applicationCryptogram.isEmpty()) {
                Chunk mTcMsgChunk = new Chunk("TC: " + applicationCryptogram, mOrderFont);
                Paragraph mTcMsgParagraph = new Paragraph(mTcMsgChunk);
                document.add(mTcMsgParagraph);
            }

            if(!dedicatedFileName.isEmpty()) {
                Chunk mAidMsgChunk = new Chunk("AID: " + dedicatedFileName, mOrderFont);
                Paragraph mAidMsgParagraph = new Paragraph(mAidMsgChunk);
                document.add(mAidMsgParagraph);
            }

            if(!terminalVerResult.isEmpty()) {
                Chunk mTvrMsgChunk = new Chunk("TVR: " + terminalVerResult, mOrderFont);
                Paragraph mTvrMsgParagraph = new Paragraph(mTvrMsgChunk);
                document.add(mTvrMsgParagraph);
            }

            if(!applPreferredName.isEmpty()) {
                Chunk mAppnameMsgChunk = new Chunk("App Name: " + applPreferredName, mOrderFont);
                Paragraph mAppnameMsgParagraph = new Paragraph(mAppnameMsgChunk);
                document.add(mAppnameMsgParagraph);
            }

            if(!txnStatus.isEmpty()) {
                Chunk mTsiMsgChunk = new Chunk("TSI: " + txnStatus, mOrderFont);
                Paragraph mTsiMsgParagraph = new Paragraph(mTsiMsgChunk);
                document.add(mTsiMsgParagraph);
            }

            Font mDetailsFont = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk pinMsg = new Chunk(pin, mDetailsFont);
            Paragraph mPinMsgParagraph = new Paragraph(pinMsg);
            mPinMsgParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mPinMsgParagraph);

            Font mCardHoldersFont = new Font(urName, 20.0f, Font.BOLD, BaseColor.BLACK);
            Chunk mCardHolderChunk = new Chunk(cardHolderName, mCardHoldersFont);
            Paragraph mCardHolderNameParagraph = new Paragraph(mCardHolderChunk);
            mCardHolderNameParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mCardHolderNameParagraph);

            Chunk agreeMsg = new Chunk("I AGREE TO PAY AS PER\nCARD ISSUER AGREEMENT", mDetailsFont);
            Paragraph mAgreeMsgParagraph = new Paragraph(agreeMsg);
            mAgreeMsgParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mAgreeMsgParagraph);

            Chunk customerMsg = new Chunk("***** Merchant Copy *****", mDetailsFont);
            Paragraph mCustomerMsgParagraph = new Paragraph(customerMsg);
            mCustomerMsgParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mCustomerMsgParagraph);
            Chunk powerByMsg = new Chunk("Powered by iServeU Ver 1.0.0", mDetailsFont);
            Paragraph mPowerByMsgParagraph = new Paragraph(powerByMsg);
            mPowerByMsgParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mPowerByMsgParagraph);

            document.close();
            Toast.makeText(mContext, "PDF saved in the internal storage", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TransactionStatusPOSActivity.this, PreviewPDFActivity.class);
            intent.putExtra("filePath", dest);
            startActivity(intent);

        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", "" + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }

    }


}