package com.matm.matmsdk.aepsmodule.transactionstatus;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.matm.matmsdk.Utils.SdkConstants;

import isumatm.androidsdk.equitas.R;

public class DrawPdfMatm2Activity extends AppCompatActivity {

    TextView shopNamee,txnStatus,dateTime,operPerform,txnId,midNo,terminalIdd,rrnNo,cardNo,blncAmt,brandNamee,txnTypee,txnAmt;

    private String shopName ="";
    private String status = "";
    private String dtTime = "";
    private String trxnId = "";
    private String mid = "";
    private String terminalId = "";
    private String rrn = "";
    private String cardNumber = "";
    private String balanceAmount = "";
    private String txnAmount = "";
    private String txnType = "";
    private String brandName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pdf_matm2);
        shopNamee=findViewById(R.id.tvShopName);
        txnStatus=findViewById(R.id.tvStatus);
        dateTime=findViewById(R.id.tvDate);
        operPerform=findViewById(R.id.tvOpePerform);
        txnId=findViewById(R.id.tvTaxnId);
        midNo=findViewById(R.id.tvMidNo);
        terminalIdd=findViewById(R.id.tvTerminalId);
        rrnNo=findViewById(R.id.tvRrnNo);
        cardNo=findViewById(R.id.tvCardNo);
        blncAmt=findViewById(R.id.tvBlncAmt);
        txnTypee=findViewById(R.id.tvTxnType);
        txnAmt=findViewById(R.id.tvTxnAmt);
        brandNamee=findViewById(R.id.tvBrandName);

        if(getIntent()!=null) {
            if (getIntent().hasExtra("shop_name")) {
                shopName = getIntent().getStringExtra("shop_name");
            }
            if (getIntent().hasExtra("status")) {
                status = getIntent().getStringExtra("status");
            }
            if (getIntent().hasExtra("dt_time")) {
                dtTime = getIntent().getStringExtra("dt_time");
            }
            if (getIntent().hasExtra("tran_id")) {
                trxnId = getIntent().getStringExtra("tran_id");
            }
            if (getIntent().hasExtra("mid")) {
                mid = getIntent().getStringExtra("mid");
            }
            if (getIntent().hasExtra("terminal_id")) {
                terminalId = getIntent().getStringExtra("terminal_id");
            }
            if (getIntent().hasExtra("rrn")) {
                rrn = getIntent().getStringExtra("rrn");
            }
            if (getIntent().hasExtra("card_number")) {
                cardNumber = getIntent().getStringExtra("card_number");
            }
            if (getIntent().hasExtra("balance_amount")) {
                balanceAmount = getIntent().getStringExtra("balance_amount");
            }
            if (getIntent().hasExtra("txn_amount")) {
                txnAmount = getIntent().getStringExtra("txn_amount");
            }
            if (getIntent().hasExtra("txn_type")) {
                txnType = getIntent().getStringExtra("txn_type");
            }
            if (getIntent().hasExtra("brand_name")) {
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
        midNo.setText(mid);
        terminalIdd.setText(terminalId);
        cardNo.setText(cardNumber);
        txnId.setText(trxnId);
        rrnNo.setText(rrn);
        blncAmt.setText(balanceAmount);
        txnAmt.setText(txnAmount);
        txnTypee.setText(txnType);
        brandNamee.setText(brandName);
    }
}