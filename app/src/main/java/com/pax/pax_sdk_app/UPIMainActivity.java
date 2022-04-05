package com.pax.pax_sdk_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.upitransaction.UPIConstant;
import com.matm.matmsdk.upitransaction.UPIHomeActivity;

public class UPIMainActivity  extends AppCompatActivity implements View.OnClickListener {
private Button SubmitBtn;
private EditText UserRefId,BranchName,loanId,loginId,phoneNo,Amount;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CustomThemes(this);
        setContentView(R.layout.activity_upi_main);
        initView();
        }
private void initView() {
        UserRefId = findViewById(R.id.UserRefId);
        BranchName = findViewById(R.id.BranchName);
        loanId = findViewById(R.id.loanId);
        loginId = findViewById(R.id.loginId);
        phoneNo = findViewById(R.id.phoneNo);
        Amount = findViewById(R.id.Amount);
        SubmitBtn = findViewById(R.id.SubmitBtn);
        SubmitBtn.setOnClickListener(this);
        }
@Override
public void onClick(View v) {
        UPIConstant.paramA = UserRefId.getText().toString();
        UPIConstant.paramB = BranchName.getText().toString();
        UPIConstant.paramC = loanId.getText().toString();
        UPIConstant.loginID = loginId.getText().toString();
        UPIConstant.mobileNo = phoneNo.getText().toString();
        UPIConstant.Amount = Amount.getText().toString();
        UPIConstant.encryptedData = "TYqmJRyB%2B4Mb39MQf%2BPqVpIe5GTicZBDzzhMPI2zsRRQlMFOg0JZZnTOqaPBgp0GN%2FDPeW%2F5%2FyAsOXurHMBdDA%3D%3D";
        // UPIConstant.encryptedData = "TYqmJRyB%2B4Mb39MQf%2BPqVig57qDYhBKptGEb%2BMs%2B94eD9xGNkpvEOkRDsIaAlxM9WOXDLdxps0TwuafZs2K%2BGA%3D%3D";
        Intent intent = new Intent(UPIMainActivity.this, UPIHomeActivity.class);
        startActivityIfNeeded(intent,UPIConstant.REQUEST_CODE);
        }
}
