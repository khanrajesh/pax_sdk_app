package com.matm.matmsdk.Error;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import isumatm.androidsdk.equitas.R;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener {

    TextView error_txt;
    ImageView closeView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        int errorInt  = getIntent().getIntExtra("errorResponse",0);
        error_txt = findViewById(R.id.error_txt);
        closeView =findViewById(R.id.closeView);
        closeView.setOnClickListener(this);
        switch (errorInt) {
            case 4046:
                error_txt.setText("CardExpire");
                break;
            case 4001:
                error_txt.setText("BlockCard");
                 break;
            case 4003:
                error_txt.setText("BlockApplication");
                break;
            case 4006:
                error_txt.setText("UnknownAID");
                break;
            case 4011:
                error_txt.setText("Transaction is denied");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeView) {
            finish();

        }
    }
}
