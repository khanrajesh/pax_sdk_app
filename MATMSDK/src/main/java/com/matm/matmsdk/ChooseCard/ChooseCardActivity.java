package com.matm.matmsdk.ChooseCard;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.Utils.SdkConstants;

import isumatm.androidsdk.equitas.R;

public class ChooseCardActivity extends AppCompatActivity {

    public static ChooseCardActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this);
        if (SdkConstants.cardLayout == 0) {
            setContentView(R.layout.activity_choose_card);
        } else {
            setContentView(SdkConstants.cardLayout);
        }

        instance=this;
    }

    void doFinish(){
        instance.finish();
    }
}
