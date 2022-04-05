package com.matm.matmsdk.aepsmodule.transactionstatus;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import isumatm.androidsdk.equitas.R;

public class DrawPdfAeps1Activity extends AppCompatActivity {

    TextView tv_shopName, tv_receipt, tv_txnStatus, tv_dateTime, tv_operPerform, tv_txnId, tv_aadhaarNo, tv_bankName, tv_rrnNo, tv_blncAmt, tv_txnAmt, tv_txnType;
    private String shopName = "";
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
    private final String operationPerformed = "AePS 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pdf_aeps1);

        tv_shopName = findViewById(R.id.tvShopName);
        tv_receipt = findViewById(R.id.tvReceipt);
        tv_txnStatus = findViewById(R.id.tvStatus);
        tv_dateTime = findViewById(R.id.tvDate);
        tv_operPerform = findViewById(R.id.tvOpePerform);
        tv_txnId = findViewById(R.id.tvTaxnId);
        tv_aadhaarNo = findViewById(R.id.tvAadhaarNo);
        tv_bankName = findViewById(R.id.tvBankName);
        tv_rrnNo = findViewById(R.id.tvRrnNo);
        tv_blncAmt = findViewById(R.id.tvBlncAmt);
        tv_txnType = findViewById(R.id.tvTxnType);
        tv_txnAmt = findViewById(R.id.tvTxnAmt);


        if (getIntent() != null) {
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
            if (getIntent().hasExtra("aadhar")) {
                aadhar = getIntent().getStringExtra("aadhar");
            }
            if (getIntent().hasExtra("bank_name")) {
                bankName = getIntent().getStringExtra("bank_name");
            }
            if (getIntent().hasExtra("rrn")) {
                rrn = getIntent().getStringExtra("rrn");
            }
            if (getIntent().hasExtra("balance_amount")) {
                balanceAmt = getIntent().getStringExtra("balance_amount");
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
            tv_txnStatus.setTextColor(Color.RED);
        } else {
            tv_txnStatus.setTextColor(Color.GREEN);
        }


        tv_shopName.setText(shopName);
        tv_txnStatus.setText(status);
        tv_dateTime.setText(dtTime);
        tv_operPerform.setText(operationPerformed);
        tv_txnId.setText(trxnId);
        tv_aadhaarNo.setText(aadhar);
        tv_bankName.setText(bankName);
        tv_rrnNo.setText(rrn);
        tv_blncAmt.setText(balanceAmt);
        tv_txnType.setText(txnType);
        tv_txnAmt.setText(txnAmount);

    }
}