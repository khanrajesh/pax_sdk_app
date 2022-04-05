package com.matm.matmsdk.aepsmodule.transactionstatus;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

import isumatm.androidsdk.equitas.R;

public class DrawPDFAeps2Activity extends AppCompatActivity {

    TextView shopNamee,receipt,txnStatus,dateTime,operPerform,txnId,aadhaarNo,bankNamee,rrnNo,blncAmt,brandNamee,txnAmt,txnTypee;

    private String shopName ="";
    private String status = "";
    private String dtTime = "";
    private String trxnId = "";
    private String aadhar = "";
    private String bankName = "";
    private String rrn = "";
    private String balanceAmt = "";
    private String txnAmount = "";
    private String txnType = "";
    private String brandName = "";
    private String operationPerformed = "AePS 2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_p_d_f);
        shopNamee=findViewById(R.id.tvShopName);
        receipt=findViewById(R.id.tvReceipt);
        txnStatus=findViewById(R.id.tvStatus);
        dateTime=findViewById(R.id.tvDate);
        operPerform=findViewById(R.id.tvOpePerform);
        txnId=findViewById(R.id.tvTaxnId);
        aadhaarNo=findViewById(R.id.tvAadhaarNo);
        bankNamee=findViewById(R.id.tvBankName);
        rrnNo=findViewById(R.id.tvRrnNo);
        blncAmt=findViewById(R.id.tvBlncAmt);
        txnTypee=findViewById(R.id.tvTxnType);
        txnAmt=findViewById(R.id.tvTxnAmt);
        brandNamee=findViewById(R.id.tvBrandName);
        if(getIntent()!=null){
            if(getIntent().hasExtra("shop_name")){
                shopName = getIntent().getStringExtra("shop_name");
            }
            if(getIntent().hasExtra("status")){
                status = getIntent().getStringExtra("status");
            }
            if(getIntent().hasExtra("dt_time")){
                dtTime = getIntent().getStringExtra("dt_time");
            }
            if(getIntent().hasExtra("tran_id")){
                trxnId = getIntent().getStringExtra("tran_id");
            }
            if(getIntent().hasExtra("aadhar")){
                aadhar = getIntent().getStringExtra("aadhar");
            }
            if(getIntent().hasExtra("bank_name")){
                bankName = getIntent().getStringExtra("bank_name");
            }
            if(getIntent().hasExtra("rrn")){
                rrn = getIntent().getStringExtra("rrn");
            }
            if(getIntent().hasExtra("balance_amount")){
                balanceAmt = getIntent().getStringExtra("balance_amount");
            }
            if(getIntent().hasExtra("txn_amount")){
                txnAmount = getIntent().getStringExtra("txn_amount");
            }
            if(getIntent().hasExtra("txn_type")){
                txnType = getIntent().getStringExtra("txn_type");
            }
            if(getIntent().hasExtra("brand_name")){
                brandName = getIntent().getStringExtra("brand_name");
            }
        }

        if (status.equalsIgnoreCase("FAILED")) {
            txnStatus.setTextColor(Color.RED);
        } else {
            txnStatus.setTextColor(Color.GREEN);
        }
        shopNamee.setText(shopName);
        txnStatus.setText(status);
        dateTime.setText(dtTime);
        txnId.setText(trxnId);
        aadhaarNo.setText(aadhar);
        rrnNo.setText(rrn);
        blncAmt.setText(balanceAmt);
        txnAmt.setText(txnAmount);
        txnTypee.setText(txnType);
        brandNamee.setText(brandName);
    }
}

