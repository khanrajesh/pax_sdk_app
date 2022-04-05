package com.matm.matmsdk.Error;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import isumatm.androidsdk.equitas.R;

public class ErrorMatm1Activity extends AppCompatActivity {

    String response,txnId;
    Button closeBtn;
    TextView tvResponse,errortxnID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_matm1);

        Bundle bundle = getIntent().getExtras();

        tvResponse = findViewById(R.id.tvResponse);
        errortxnID = findViewById(R.id.errortxnID);
        closeBtn = findViewById(R.id.closeBtn);

        if (bundle != null) {
            response = bundle.getString("response");
            txnId = bundle.getString("txnId");
        }
        String errorValue = "java lang NullPointerException Attempt to invoke virtual method java lang String org json JSONObject optString java lang String on a null object reference";

       if(response.contains(errorValue)){
           String replaceresponse = response.replace(errorValue,"");
           tvResponse.setText(replaceresponse);
       }else{
           tvResponse.setText(response);
       }

        errortxnID.setText("Transaction ID: " + txnId);
        closeBtn.setOnClickListener(v -> finish());
    }
}