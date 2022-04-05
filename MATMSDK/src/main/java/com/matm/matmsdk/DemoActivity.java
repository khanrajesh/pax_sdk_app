package com.matm.matmsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import isumatm.androidsdk.equitas.R;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        String accessClassName =  getIntent().getStringExtra("activity");
        String flagName = getIntent().getStringExtra("driverFlag");

        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            Intent launchIntent = new Intent(this, targetActivity);
            launchIntent.putExtra("driverFlag",flagName);
            launchIntent.putExtra("freshnesFactor","");
            startActivity(launchIntent);
            System.out.println("Rajesh::"+targetActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



    }
}
