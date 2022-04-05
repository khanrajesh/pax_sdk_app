package com.matm.matmsdk.Error;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import isumatm.androidsdk.equitas.R;

public class Error2Activity extends AppCompatActivity implements View.OnClickListener {

    TextView error_txt;
    ImageView closeView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        String errorStr  = "";
        errorStr = getIntent().getStringExtra("errorResponse");
        error_txt = findViewById(R.id.error_txt);
        closeView =findViewById(R.id.closeView);
        closeView.setOnClickListener(this);
        error_txt.setText(errorStr);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeView) {
            finish();

        }
    }
}
