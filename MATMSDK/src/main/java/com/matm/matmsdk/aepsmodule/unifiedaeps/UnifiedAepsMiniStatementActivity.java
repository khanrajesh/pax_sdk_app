package com.matm.matmsdk.aepsmodule.unifiedaeps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.AEPS2HomeActivity;
import com.matm.matmsdk.aepsmodule.utils.Session;

import java.util.ArrayList;
import java.util.regex.Pattern;

import isumatm.androidsdk.equitas.R;

public class UnifiedAepsMiniStatementActivity extends AppCompatActivity {
    RelativeLayout failureLayout;
    LinearLayout successLayout;
    Button okButton, okSuccessButton;
    TextView statusDescTxt, failtxnID, aadhar_number, date_time, bank_name, card_transaction_type;
    TextView transaction_details_header_txt, aadhar_num_txt, account_balance_txt, transaction_id_txt, bank_name_txt;
    String aadharCardStr = "";
    String amount = "";
    Session session;
    ProgressDialog pd;
    RecyclerView statement_list;
    String statusTxt;
    private UnifiedTxnStatusModel transactionStatusModel;
    UnifiedStatementListAdapter statementList_adapter;
    public Pattern DATE_PATTERN1 = Pattern.compile(
            "^\\d{2}/\\d{2}/\\d{4}$");
    public Pattern DATE_PATTERN2 = Pattern.compile(
            "^\\d{2}/\\d{2}$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SdkConstants.statementLayout == 0) {
            setContentView(R.layout.activity_statement_transaction);
        } else {
            setContentView(SdkConstants.statementLayout);
        }
        session = new Session(UnifiedAepsMiniStatementActivity.this);
        pd = new ProgressDialog(UnifiedAepsMiniStatementActivity.this);
        statement_list = findViewById(R.id.statement_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UnifiedAepsMiniStatementActivity.this);
        statement_list.setLayoutManager(linearLayoutManager);
        statusDescTxt = findViewById(R.id.statusDescTxt);
        transaction_details_header_txt = findViewById(R.id.transaction_details_header_txt);
        failtxnID = findViewById(R.id.txnID);
        aadhar_number = findViewById(R.id.aadhar_number);
        date_time = findViewById(R.id.date_time);
        bank_name = findViewById(R.id.bank_name);
        card_transaction_type = findViewById(R.id.card_transaction_type);
        successLayout = findViewById(R.id.successLayout);
        failureLayout = findViewById(R.id.failureLayout);
        okButton = findViewById(R.id.okButton);
        okSuccessButton = findViewById(R.id.okSuccessButton);
        aadhar_num_txt = findViewById(R.id.aadhar_num_txt);
        account_balance_txt = findViewById(R.id.account_balance_txt);
        transaction_id_txt = findViewById(R.id.transaction_id_txt);
        bank_name_txt = findViewById(R.id.bank_name_txt);
        transactionStatusModel = getIntent().getParcelableExtra(SdkConstants.TRANSACTION_STATUS_KEY);
            failureLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.VISIBLE);
            aadhar_num_txt.setText(transactionStatusModel.getAadharCard());
            transaction_id_txt.setText(transactionStatusModel.getTxnID());
            bank_name_txt.setText(transactionStatusModel.getBankName());
            account_balance_txt.setText("Rs."+transactionStatusModel.getBalanceAmount());
                    try {
                        pd.show();
                        pd.setMessage("Loading..");
                        statementList_adapter = new UnifiedStatementListAdapter(UnifiedAepsMiniStatementActivity.this, transactionStatusModel.getMinistatement(),pd);
                        statement_list.setAdapter(statementList_adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        okSuccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respIntent = new Intent();
                setResult(Activity.RESULT_OK, respIntent);
                finish();
            }
        });
    }
    public String getRemarks(ArrayList<String> tempRemark) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tempRemark.size(); i++) {
            if (tempRemark.get(i).toString().length() != 0) {
                sb.append(" " + tempRemark.get(i));
            }
        }
        return sb.toString().trim();
    }
    @Override
    public void onBackPressed() {
        try{
            if (statusTxt.equalsIgnoreCase("FAILED")) {
                Intent intent = new Intent(UnifiedAepsMiniStatementActivity.this, AEPS2HomeActivity.class);
                intent.putExtra("FAILEDVALUE", "FAILEDDATA");
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
