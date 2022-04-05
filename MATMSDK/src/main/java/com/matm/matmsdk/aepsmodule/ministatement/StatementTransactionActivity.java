package com.matm.matmsdk.aepsmodule.ministatement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.AEPS2HomeActivity;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusAeps2Activity;
import com.matm.matmsdk.aepsmodule.utils.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

import isumatm.androidsdk.equitas.R;

public class StatementTransactionActivity extends AppCompatActivity {
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
    StatementList_Adapter statementList_adapter;
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

        session = new Session(StatementTransactionActivity.this);
        pd = new ProgressDialog(StatementTransactionActivity.this);
        statement_list = findViewById(R.id.statement_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StatementTransactionActivity.this);
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
        String repsonse = getIntent().getStringExtra(SdkConstants.TRANSACTION_STATUS_KEY);
        Gson gson = new Gson();
        StatementResponse statementResponse = gson.fromJson(repsonse, StatementResponse.class);
        if (repsonse.equalsIgnoreCase("500") || repsonse.equalsIgnoreCase("001")) {
            statusTxt = "Success";
            failureLayout.setVisibility(View.VISIBLE);
            successLayout.setVisibility(View.GONE);
//            statusDescTxt.setText(statementResponse.getApiComment());
            statusDescTxt.setText("Ministatement Status");
            failtxnID.setText("Txn ID: " + statementResponse.getTxId());
            aadhar_number.setText("Aadhar Number: " + statementResponse.getCustomerAadhaarNo());
            date_time.setText(statementResponse.getCreatedDate());
            bank_name.setText(statementResponse.getBankName());
            card_transaction_type.setText("Mini Statement");

        } else if (repsonse.equalsIgnoreCase("002")) {
            statusTxt = "Success";
            failureLayout.setVisibility(View.VISIBLE);
            successLayout.setVisibility(View.GONE);
//            statusDescTxt.setText(statementResponse.getApiComment());
            statusDescTxt.setText("Ministatement Status");
            //failtxnID.setText("Txn ID: "+statementResponse.getTxId());
            //aadhar_number.setText("Aadhar Number: "+statementResponse.getCustomerAadhaarNo());
            // date_time.setText(statementResponse.getCreatedDate());
            //bank_name.setText(statementResponse.getBankName());
            card_transaction_type.setText("Mini Statement");

        } else {

            failureLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.VISIBLE);
            aadhar_num_txt.setText(statementResponse.getCustomerAadhaarNo());
            transaction_id_txt.setText(statementResponse.getTxId());
            bank_name_txt.setText(statementResponse.getBankName());
            if (statementResponse.getMiniStatementString() != null) {
                if (statementResponse.getStatus().equalsIgnoreCase("-1")) {
                    statusTxt = "Success";
                    failureLayout.setVisibility(View.VISIBLE);
                    aadhar_num_txt.setText(statementResponse.getCustomerAadhaarNo());
                    transaction_id_txt.setText(statementResponse.getTxId());
                    successLayout.setVisibility(View.GONE);
//                    statusDescTxt.setText(statementResponse.getApiComment());
                    statusDescTxt.setText("Ministatement Status");
                    failtxnID.setText("Txn ID: " + statementResponse.getTxId());
                    aadhar_number.setText("Aadhar Number: " + statementResponse.getCustomerAadhaarNo());
                    date_time.setText(statementResponse.getCreatedDate());
                    bank_name.setText(statementResponse.getBankName());
                    card_transaction_type.setText("Mini Statement");
                } else {
                    try {
                        pd.show();
                        getMiniStatementDetails(statementResponse.getMiniStatementString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                transaction_details_header_txt.setText("No Transaction Details Found");
            }
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent respIntent = new Intent();
                setResult(Activity.RESULT_OK, respIntent);
                finish();*/
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

    /*
     * @Subhashree
     * Api changes for fixing the amount on proper format
     * Sripati's New Api
     */

    private void getMiniStatementDetails(String miniStatement) {
        JSONObject obj = new JSONObject();
        String url = "https://us-central1-iserveustaging.cloudfunctions.net/ministatement2/api/v1/miniStatementJSONConverter";
        try {

//            obj.put("statement", "00100207002003UID0050021000635019/01 BUP/F/87727413   D      10.0019/01 BUP/F/87660223   C      10.0019/01 BUP/F/87660119   D      10.0019/01 BUP/F/87624913   C      10.0019/01 BUP/F/87624798   D      10.0018/01 BUP/F/82926889   D      80.0018/01 BUP/F/78203294   D      50.0017/01 BUP/F/74122460   D     175.0017/01 BUP/F/74119868   C    1000.000000000000Balance:+0000000000069595");
//            obj.put("iin", "508505");

            obj.put("statement", miniStatement);
            obj.put("iin", SdkConstants.bankIIN);

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response.toString());
                                int status = Integer.parseInt(obj.getString("statusCode"));
                                if (status == 0) {
                                    JSONObject jsonData = obj.getJSONObject("data");
                                    String balance = jsonData.getString("totalBalance");
                                    JSONArray jsonArray = jsonData.getJSONArray("transactions");

                                    account_balance_txt.setText(" ₹ " + balance);
                                    if (jsonArray.length() != 0) {
                                        ArrayList<TransactionList> list = new ArrayList<>();

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                                            String date = jsonobject.getString("Date");
                                            String d_c = jsonobject.getString("D/C");
                                            String transaction_remark = jsonobject.getString("Transaction Remark");
                                            String amount = jsonobject.getString("Amount");
                                            list.add(new TransactionList(date, d_c, transaction_remark, amount));
                                        }
                                        pd.hide();
                                        statementList_adapter = new StatementList_Adapter(StatementTransactionActivity.this, list);
                                        statement_list.setAdapter(statementList_adapter);
                                    } else {
                                        statusTxt = "Success";
                                        pd.hide();
                                        transaction_details_header_txt.setText("No Transaction Details Found");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                statusTxt = "Success";
                                pd.hide();
                                transaction_details_header_txt.setText("No Transaction Details Found");

                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            statusTxt = "Success";
                            pd.hide();
                            transaction_details_header_txt.setText("No Transaction Details Found");


                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            pd.hide();
            statusTxt = "Success";
        }
    }


    public JSONObject getJSON(String response1) {
        JSONObject mainObject = new JSONObject();
        try {
            ArrayList<String> transactionSlots = new ArrayList<>();
            String a = response1.substring(28, 31);
            int totalTransactionRecord = Integer.valueOf((response1.substring(28, 31))) / 35;
            String transactionRecord = response1.substring(31, response1.length());
            String transactionBalance = transactionRecord;
            for (int i = 0; i < totalTransactionRecord - 1; i++) {
                String tempResponse = transactionBalance;
                tempResponse = tempResponse.substring(0, 35);
                transactionSlots.add(tempResponse);
                if (transactionBalance.length() >= 35) {
                    transactionBalance = transactionBalance.substring(35);
                }
            }
            if (transactionBalance.contains("Balance")) {
                transactionBalance = transactionBalance.replaceAll("Balance", "").trim();
            } else if (transactionBalance.contains("AVAIL BAL")) {
                transactionBalance = transactionBalance.replaceAll("AVAIL BAL", "").trim();
            }

            JSONArray jsonArray = new JSONArray();
            for (int j = 0; j < transactionSlots.size(); j++) {
                JSONObject jsonObject = new JSONObject();
                String[] tempSlots = transactionSlots.get(j).split(" ");
                ArrayList<String> tempRemark = new ArrayList<String>(Arrays.asList(tempSlots));
                for (int k = 0; k < tempSlots.length; k++) {
                    if (tempSlots[k].trim().equalsIgnoreCase("D") || tempSlots[k].trim().equalsIgnoreCase("Dr")) {
                        jsonObject.put("transaction_type", "Debit");
                        tempRemark.set(k, "");
                    } else if (tempSlots[k].trim().equalsIgnoreCase("C") || tempSlots[k].trim().equalsIgnoreCase("Cr")) {
                        jsonObject.put("transaction_type", "Credit");
                        tempRemark.set(k, "");
                    } else if (tempSlots[k].trim().contains(".")) {
                        jsonObject.put("transaction_amount", tempSlots[k].trim());
                        tempRemark.set(k, "");
                    } else if (DATE_PATTERN1.matcher(tempSlots[k].trim()).matches() || DATE_PATTERN2.matcher(tempSlots[k].trim()).matches()) {
                        jsonObject.put("transaction_date", tempSlots[k].trim());
                        tempRemark.set(k, "");
                    }
                }
                jsonObject.put("transaction_remark", getRemarks(tempRemark));
                jsonArray.put(jsonObject);
            }
            mainObject.put("transactions", jsonArray);
            mainObject.put("total_balance", transactionBalance.trim());
            System.out.println("JSON" + mainObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainObject;
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
                Intent intent = new Intent(StatementTransactionActivity.this, AEPS2HomeActivity.class);
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
