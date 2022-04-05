package com.matm.matmsdk.aepsmodule.aeps3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matm.matmsdk.aepsmodule.ministatement.StatementList_Adapter;
import com.matm.matmsdk.aepsmodule.ministatement.StatementTransactionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import isumatm.androidsdk.equitas.R;

public class AEPS3_TransactionReceipt_ministatement extends AppCompatActivity {

    RelativeLayout failureLayout;
    LinearLayout successLayout;
    Button okButton, okSuccessButton;
    TextView statusDescTxt, failtxnID, aadhar_number, date_time, bank_name, card_transaction_type;
    TextView  aadhar_num_txt, account_balance_txt, transaction_id_txt, bank_name_txt;

    RecyclerView statement_list;
    MinistatementListAdapter ministatementListAdapter;

    String status= "N/A", statusDec= "N/A", bankName= "N/A", Date= "N/A", adhaar = "N/A", transactionMode= "N/A", txnId= "N/A", amount= "N/A";
    ArrayList<MinistatemenTxnList> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps3_transaction_receipt_ministatement);


        statement_list = findViewById(R.id.statement_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AEPS3_TransactionReceipt_ministatement.this);
        statement_list.setLayoutManager(linearLayoutManager);


        statusDescTxt = findViewById(R.id.statusDescTxt);
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


        if (getIntent().getExtras() != null) {
            status = getIntent().getStringExtra("status");
            statusDec = getIntent().getStringExtra("statusDec");
            bankName = getIntent().getStringExtra("bankName");
            Date = getIntent().getStringExtra("Date");
            adhaar = getIntent().getStringExtra("adhaar");
            transactionMode = getIntent().getStringExtra("transactionMode");
            txnId = getIntent().getStringExtra("txnId");
            amount = getIntent().getStringExtra("amount");

            Intent intent = getIntent();
            String jsonArray = intent.getStringExtra("list_data");

            try {
                JSONArray array = new JSONArray(jsonArray);
                if (array.length() != 0 || array.equals(null)) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject mjsonobject = array.getJSONObject(i);
                        String mini_date_ = mjsonobject.getString("Date");
                        String miniType_ = mjsonobject.getString("Type");
                        String miniDebitCredit_ = mjsonobject.getString("DebitCredit");
                        Double miniAmount_ = mjsonobject.getDouble("Amount");

                        list.add(new MinistatemenTxnList(mini_date_, miniDebitCredit_, miniType_, miniAmount_));

                        Log.e("TAG", "onFinish: " + list);
                    }
                }else {
                    failureLayout.setVisibility(View.VISIBLE);
                    successLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                failureLayout.setVisibility(View.VISIBLE);
                successLayout.setVisibility(View.GONE);
            }

        }

        if (status.equals("SUCCESS")) {
            failureLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.VISIBLE);


            statusDescTxt.setText(statusDec);
            transaction_id_txt.setText( txnId);
            aadhar_num_txt.setText(adhaar);
            date_time.setText(Date);
            bank_name.setText(bankName);
            account_balance_txt.setText(amount);
            card_transaction_type.setText("Mini Statement");
            ministatementListAdapter = new MinistatementListAdapter(list, AEPS3_TransactionReceipt_ministatement.this);
            statement_list.setAdapter(ministatementListAdapter);


        } else {
            failureLayout.setVisibility(View.VISIBLE);
            successLayout.setVisibility(View.GONE);
            account_balance_txt.setText(amount);
            statusDescTxt.setText(statusDec);
            failtxnID.setText("Txn ID: " + txnId);
            aadhar_number.setText("Aadhar Number: " + adhaar);
            date_time.setText(Date);
            bank_name.setText(bankName);
            card_transaction_type.setText("Mini Statement");
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respIntent = new Intent();
                setResult(Activity.RESULT_OK, respIntent);
                finish();
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

}
