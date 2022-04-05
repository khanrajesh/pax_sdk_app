package com.pax.pax_sdk_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.matm.matmsdk.Bluetooth.BluetoothActivity;
import com.matm.matmsdk.DemoActivity;
import com.matm.matmsdk.MPOS.BluetoothServiceActivity;
import com.matm.matmsdk.MPOS.MorefunServiceActivity;
import com.matm.matmsdk.MPOS.PosActivity;
import com.matm.matmsdk.MPOS.PaxA910ServiceActivity;
import com.matm.matmsdk.MPOS.PosServiceActivity;
import com.matm.matmsdk.MPOS.WiseasyServiceActivity;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.AEPS2HomeActivity;
import com.matm.matmsdk.aepsmodule.AEPSHomeActivity;
import com.matm.matmsdk.aepsmodule.BioAuthActivity;
import com.matm.matmsdk.aepsmodule.aadharpay.AadharpayActivity;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedBioAuthActivity;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedAepsActivity;
import com.matm.matmsdk.matm1.MatmActivity;
import com.matm.matmsdk.notification.NotificationHelper;
import com.matm.matmsdk.notification.service.SubscribeGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.matm.matmsdk.Utils.SdkConstants.Matm1BluetoothFlag;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    RadioGroup rgTransactionType;
    RadioButton rbCashWithdrawal;
    RadioButton rbBalanceEnquiry, rb_mini, rb_adhaarpay;
    EditText etAmount, et_paramA, et_paramB;
    Button btnProceed, btn_pair, btn_proceedaeps, upiPagebtn, BtnUnpair, Btndriver, btn_matm1, btnmatm1pair, btn_check;
    public static final int REQUEST_CODE = 5;
    Boolean isAepsClicked = false, isMatmClicked = false;
    String manufactureFlag = "";
    UsbManager musbManager;
    private UsbDevice usbDevice;
    ProgressDialog pd;
    public static String token, adminName;
    ProgressDialog dialog;
    String username = "Snehasony";
    NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //  FirebaseApp.initializeApp(MainActivity.this);
        notificationHelper = new NotificationHelper(this);
        subscribeGlobal();
        //retriveUserList();
        String str = "609384625620";
        String hashString = getSha256Hash(str);
        System.out.println("String Value :" + hashString);

        Date date = Calendar.getInstance().getTime();

        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = formatter.format(date);

        Log.e(TAG, "subscribeGlobal: current date " + currentDateandTime);

    }


    public void subscribeGlobal() {
        String specialChar = "!@#$%^&*";

        if (SdkConstants.USER_NAME_NOTIFY.matches(specialChar)) {
            SdkConstants.DEVICE_TOPIC = "";
        } else {
            SdkConstants.DEVICE_TOPIC = "AEPS_Snehasony";//+SdkConstants.loginID;
        }

        SdkConstants.COMPLETE_REGISTRATION = "registrationComplete";
        SdkConstants.PUSH_NOTIFICATION = "pushNotification";

        Log.d("TAG", "subscribeGlobal: " + SdkConstants.DEVICE_TOPIC);
        SubscribeGlobal global = new SubscribeGlobal(this);
        global.subscribe();
        global.registerBroadcast();

    }


    private void initView() {
        musbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        rgTransactionType = findViewById(R.id.rg_trans_type);
        rbCashWithdrawal = findViewById(R.id.rb_cw);
        rbBalanceEnquiry = findViewById(R.id.rb_be);
        btnProceed = findViewById(R.id.btn_proceed);
        btn_pair = findViewById(R.id.btn_pair);
        rb_mini = findViewById(R.id.rb_mini);
        rb_adhaarpay = findViewById(R.id.rb_adhaarpay);
        etAmount = findViewById(R.id.et_amount);
        btn_proceedaeps = findViewById(R.id.proceedBtn);
        upiPagebtn = findViewById(R.id.upiPagebtn);
        BtnUnpair = findViewById(R.id.BtnUnpair);
        Btndriver = findViewById(R.id.Btndriver);
        btn_check = findViewById(R.id.btn_check);


        loginToken();


        BtnUnpair = findViewById(R.id.BtnUnpair);
        btn_matm1 = findViewById(R.id.btn_matm1);
        btnmatm1pair = findViewById(R.id.btnmatm1pair);

        btn_matm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SdkConstants.transactionAmount = etAmount.getText().toString().trim();
                if (rbCashWithdrawal.isChecked()) {
                    SdkConstants.transactionType = SdkConstants.cashWithdrawal;
                    SdkConstants.transactionAmount = etAmount.getText().toString();

                }
                if (rbBalanceEnquiry.isChecked()) {
                    SdkConstants.transactionType = SdkConstants.balanceEnquiry;
                    SdkConstants.transactionAmount = "0";
                }
                SdkConstants.loginID = "9425300691";
                SdkConstants.encryptedData = "mmBIDCjHcOhhFmWOTEBKkOawKi3GsmjsB%2B1BUhKfk%2BPf71PNyEZQCeqS%2BIphX5v9pH697R2my7mulqff1m4dFwcyN74ZpYIiui58QdwjE7IPuPeS409xiDhyuHuHie66";


                Intent intent = new Intent(MainActivity.this, MatmActivity.class);
                startActivity(intent);
            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = getDecimalString(etAmount.getText().toString());

                Toast.makeText(MainActivity.this, "value" + value, Toast.LENGTH_SHORT).show();
            }
        });

        btnmatm1pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matm1BluetoothFlag = "1";
                Intent intent = new Intent(MainActivity.this, MatmActivity.class);
                startActivity(intent);
            }
        });


        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SdkConstants.BlueToothPairFlag.equalsIgnoreCase("1")) {
//                    if (PosActivity.isBlueToothConnected(MainActivity.this)) {

                    if (rbCashWithdrawal.isChecked()) {
                        String Amount = etAmount.getText().toString().trim();
                        if (!Amount.equals("")) {
                            if (Integer.parseInt(Amount) >= 100) {
                                callMATMSDKApp();
                            } else {
                                Toast.makeText(MainActivity.this, "Please Enter Amount more Than 100", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please Enter some Amount", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (rbBalanceEnquiry.isChecked()) {
                        callMATMSDKApp();
                    }

//                    } else {
//                        Toast.makeText(MainActivity.this, "Please pair the bluetooth device", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please pair bluetooth device for ATM transaction.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_proceedaeps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(biometricDeviceConnect()){
                callAEPSSDKApp();
//                }else{
//                    Toast.makeText(MainActivity.this, "Connect your device.", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        btn_pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothServiceActivity.class);
                intent.putExtra("userName", "Snehasony");
                intent.putExtra("user_token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTbmVoYXNvbnkiLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE2MjA3MjM3Njc2NDYsImV4cCI6MTYyMDcyNTU2N30.--MAMha1Ab70x89FjBD4kyu3EgbsqJl0N9gHG-HlM-caQ8gY2UX3ULX0fapgkReOEug32HlKtcSz5TG7Ec-YMA");
                Log.d("TAG", "token_pair: " + token);
                SdkConstants.applicationType = "CORE";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_PRIVILEGED}, 1001);
                            Toast.makeText(getApplicationContext(), "Please Grant all the permissions", Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(intent);
                        }
                    } else {
                        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_PRIVILEGED}, 1001);
                            Toast.makeText(getApplicationContext(), "Please Grant all the permissions", Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(intent);
                        }
                    }
                } else {
                    startActivity(intent);
                }
            }
        });
        BtnUnpair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SdkConstants.LogOut = "0";
                SdkConstants.BlueToothPairFlag = "0";
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                intent.putExtra("user_id", "488");
                intent.putExtra("user_token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTkzMjcwOTQzNzcwLCJleHAiOjE1OTMyNzI3NDN9.U6OHRnz-UvkcDOicmUYm_yT8FSDofbzZ9H8kJsLdLwgTRwt5vJ0PIwTKYC1JRpM3kXwqGg6sCpvQn1PmUz9lgw");

                startActivity(intent);
            }
        });

        Btndriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DemoActivity.class);
                intent.putExtra("activity", "com.pax.pax_sdk_app.DriverActivity");
                intent.putExtra("driverFlag", "MANTRA");

                startActivity(intent);
            }
        });

        upiPagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UPIMainActivity.class);
                startActivity(intent);
            }
        });

        rgTransactionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_cw) {
                    etAmount.setClickable(true);
                    etAmount.setHint("Amount");
                    etAmount.setVisibility(View.VISIBLE);
                    etAmount.setText("");
                    etAmount.setEnabled(true);
                    etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (checkedId == R.id.rb_be) {
                    etAmount.setVisibility(View.GONE);
                    etAmount.setClickable(false);
                    etAmount.setEnabled(false);
                } else if (checkedId == R.id.rb_adhaarpay) {
                    etAmount.setClickable(true);
                    etAmount.setHint("Amount");
                    etAmount.setVisibility(View.VISIBLE);
                    etAmount.setText("");
                    etAmount.setEnabled(true);
                    etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (checkedId == R.id.rb_mini) {
                    etAmount.setVisibility(View.GONE);
                    etAmount.setClickable(false);
                    etAmount.setEnabled(false);
                }
            }
        });
    }

//    private void callAEPSSDKApp() {
//        SdkConstants.transactionAmount = etAmount.getText().toString().trim();
//        if (rbCashWithdrawal.isChecked()) {
//            SdkConstants.transactionType = SdkConstants.cashWithdrawal;
//            SdkConstants.transactionAmount = etAmount.getText().toString();
//        } else if (rbBalEnquiry.isChecked()) {
//            SdkConstants.transactionType = SdkConstants.balanceEnquiry;
//        } else if (rbMiniStatement.isChecked()) {
//            SdkConstants.transactionType =
//                    SdkConstants.ministatement;
//        }
//        SdkConstants.paramA = "";
//        SdkConstants.paramB = "";
//        SdkConstants.paramC = "";
//        SdkConstants.loginID = "aepsTestR";
//        SdkConstants.encryptedData = "cssC%2BcHGxugRFLTjpk%2BJN2Hbbo%2F%2BDokPsBwb9uFdXebdGg%2FE aqOvFXBEoU7ve % 2FAP6r abeaskLloqjx6bF6tC cw % 3D % 3D ";// will be provided by ISU
//        SdkConstants.MANUFACTURE_FLAG = manufactureFlag;
//        SdkConstants.DRIVER_ACTIVITY = "com.example.sdksetup.DriverActivity"; //this value should be replaced by Your driver activities package
//        Intent intent = new Intent(AEPSTransactionActivity.this, BioAuthActivity.class
//        );
//        startActivity(intent);
//    }

    private void callAEPS1SDKApp() {
        /*for sdk user*/
        String tx_amount = etAmount.getText().toString();
        if (biometricDeviceConnect()) {
            SdkConstants.DRIVER_ACTIVITY = "com.pax.pax_sdk_app.DriverActivity";
            SdkConstants.MANUFACTURE_FLAG = manufactureFlag;
            if (rbCashWithdrawal.isChecked()) {
                SdkConstants.transactionType = SdkConstants.cashWithdrawal;
                SdkConstants.transactionAmount = tx_amount.trim();
                SdkConstants.paramA = "paramA";//"annapurna";
                SdkConstants.paramB = "paramB";//"BLS1";
                SdkConstants.paramC = "paramC";//"loanID1234";
//            SdkConstants.CustomTheme = "DEFAULT";
                SdkConstants.BRAND_NAME = "IServeU";
                SdkConstants.SHOP_NAME = "iServeU";
                SdkConstants.loginID = "aepsTestR";//login_ID
                SdkConstants.encryptedData = "cssC%2BcHGxugRFLTjpk%2BJN2Hbbo%2F%2BDokPsBwb9uFdXebdGg%2FEaqOvFXBEoU7ve%2FAP6rabeaskLloqjx6bF6tCcw%3D%3D";
                Intent intent = new Intent(MainActivity.this, AEPSHomeActivity.class);
                intent.putExtra("manufatureName", "");
                Toast.makeText(MainActivity.this, "AEPS1 clicked..!!", Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else if (rbBalanceEnquiry.isChecked()) {
                SdkConstants.transactionType = SdkConstants.balanceEnquiry;
                SdkConstants.transactionAmount = tx_amount.trim();
                SdkConstants.paramA = "paramA";//"annapurna";
                SdkConstants.paramB = "paramB";//"BLS1";
                SdkConstants.paramC = "paramC";//"loanID1234";
//            SdkConstants.CustomTheme = "DEFAULT";
                SdkConstants.BRAND_NAME = "IServeU";
                SdkConstants.SHOP_NAME = "iServeU";
                SdkConstants.loginID = "aepsTestR";//login_ID
                SdkConstants.encryptedData = "cssC%2BcHGxugRFLTjpk%2BJN2Hbbo%2F%2BDokPsBwb9uFdXebdGg%2FEaqOvFXBEoU7ve%2FAP6rabeaskLloqjx6bF6tCcw%3D%3D";
                Intent intent = new Intent(MainActivity.this, AEPSHomeActivity.class);
                intent.putExtra("manufatureName", "");
                Toast.makeText(MainActivity.this, "AEPS1 clicked..!!", Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else if (rb_mini.isChecked()) {
                Toast.makeText(MainActivity.this, "Feature unavailable....!!", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null & resultCode == RESULT_OK) {

            /**
             *  FOR AEPS TRANSACTION RESPONSE
             */
            if (requestCode == SdkConstants.REQUEST_CODE) {
                String response = data.getStringExtra(SdkConstants.responseData);
                System.out.println("Response: " + response);
                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
            }
            /**
             *  FOR MATM TRANSACTION RESPONSE
             */
            if (requestCode == SdkConstants.MATM_REQUEST_CODE) {
                String response = data.getStringExtra(SdkConstants.responseData);
                System.out.println("Response: " + response);
                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        }
    }


      /*  SdkConstants.transactionAmount = etAmount.getText().toString().trim();
        if (rbCashWithdrawal.isChecked()) {
            SdkConstants.transactionType = SdkConstants.cashWithdrawal;

        } else if(rbBalanceEnquiry.isChecked()) {
            SdkConstants.transactionType = SdkConstants.balanceEnquiry;
        } else {

            Toast.makeText(this,"Please select any transaction type !!!",Toast.LENGTH_LONG);
        }

        SdkConstants.paramA = "Subhalaxmi";
        SdkConstants.paramB = "1111111";
        SdkConstants.paramC = "123456";

        //SdkConstants.applicationType = "CORE";
        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTbmVoYXNvbnkiLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE2MDQ5MTA5NDg2NTQsImV4cCI6MTYwNDkxMjc0OH0.BWEErMERTGt0LA186C1eJ0xdm_AdmPgDNzB5Xbn55MHCBDJptWW6JGhkBcQYTBLijfJPE87ZwZWsFYNQ6GN5KA";
        SdkConstants.userNameFromCoreApp = "Snehasony";
        SdkConstants.CustomTheme = "THEME_YELLOW";

        //THEME_YELLOW
        //THEME_BLUE
        //THEME_DARK
        //THEME_RED
        //THEME_BROWN
        //THEME_GREEN
        //DEFAULT


//        SdkConstants.loginID = "jhunjhunuap.2512";
//        SdkConstants.encryptedData ="TYqmJRyB%2B4Mb39MQf%2BPqVrOZefRP0s10rREkrzYvxahvS4SiPYqnTY3R3MgarVjnyvrz3mjOEb%2F261GisLVNYQ%3D%3D";

        //annarpurna
//        SdkConstants.loginID = "aepsTestR";
//        SdkConstants.encryptedData ="cssC%2BcHGxugRFLTjpk%2BJN2Hbbo%2F%2BDokPsBwb9uFdXebdGg%2FEaqOvFXBEoU7ve%2FAP6rabeaskLloqjx6bF6tCcw%3D%3D";
        //instantmudra
//        SdkConstants.loginID = "7003585693";
//        SdkConstants.encryptedData ="TYqmJRyB%2B4Mb39MQf%2BPqVpG%2BMXYkFjv7FvFq5zSop426IBfOKVTFtcsgZDUCORAu%2FDJvr85SGAUeQVWgRINTI5teZqYzUL1nyFMcf1eO69A%3D";


        System.out.println("Rajesh::::" + manufactureFlag);
        SdkConstants.MANUFACTURE_FLAG = manufactureFlag;
        SdkConstants.DRIVER_ACTIVITY = "com.pax.pax_sdk_app.DriverActivity";

        Intent intent = new Intent(MainActivity.this, AEPS2HomeActivity.class);
        //intent.putExtra("activity","com.pax.pax_sdk_app.DriverActivity");
        //intent.putExtra("driverFlag",manufactureFlag);
        startActivity(intent);*/
    //}

    private void callAEPSSDKApp() {
        SdkConstants.transactionAmount = etAmount.getText().toString().trim();
        if (rbCashWithdrawal.isChecked()) {
            SdkConstants.transactionType = SdkConstants.cashWithdrawal;
        } else if (rbBalanceEnquiry.isChecked()) {

            SdkConstants.transactionType = SdkConstants.balanceEnquiry;
        } else if (rb_mini.isChecked()) {
            SdkConstants.transactionType = SdkConstants.ministatement;
        } else if (rb_adhaarpay.isChecked()) {
            SdkConstants.transactionType = SdkConstants.adhaarPay;
        }

        SdkConstants.paramA = "test";
        SdkConstants.paramB = "BLS1";
        SdkConstants.paramC = "loanID";
        SdkConstants.applicationType = "CORE";
        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdWJoYVRlc3QiLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE2NDgwMTY0MzU2MTgsImV4cCI6MTY0ODAyMDAzNX0.E0JiutyGPrFSPOf_l1fnJKNHv8fQvm3X4D83nf39p9Q778Jf9BKc_F4Q16V71sH8QoUZDU2qnhykDotkkSAkaw";
        SdkConstants.userNameFromCoreApp = "subhaTest";
        SdkConstants.MANUFACTURE_FLAG = manufactureFlag;
        SdkConstants.USER_NAME_NOTIFY = "test";
        SdkConstants.DRIVER_ACTIVITY = "com.pax.pax_sdk_app.DriverActivity";
        SdkConstants.NOTIFICATION_ACTIVITY = "com.isuisudmt.activity.Notification_History_Activity";

//        Intent intent = new Intent(MainActivity.this, BioAuthActivity.class);
        Intent intent = new Intent(MainActivity.this, UnifiedBioAuthActivity.class);
        startActivity(intent);
    }

    private void callMATMSDKApp() {
        rb_mini.setVisibility(View.GONE);
        SdkConstants.transactionAmount = etAmount.getText().toString().trim();
        if (rbCashWithdrawal.isChecked()) {
            SdkConstants.transactionType = SdkConstants.cashWithdrawal;
            SdkConstants.transactionAmount = etAmount.getText().toString();

        }
        if (rbBalanceEnquiry.isChecked()) {
            SdkConstants.transactionType = SdkConstants.balanceEnquiry;
            SdkConstants.transactionAmount = "0";
        }
        SdkConstants.paramA = "123456789";
        SdkConstants.paramB = "branch1";
        SdkConstants.paramC = "loanID1234";
        SdkConstants.encryptedData ="cssC%2BcHGxugRFLTjpk%2BJN2Hbbo%2F%2BDokPsBwb9uFdXebdGg%2FEaqOvFXBEoU7ve%2FAP6rabeaskLloqjx6bF6tCcw%3D%3D";
        SdkConstants.loginID = "aepsTestR";
        Log.d("TAG", "token_callMATMSDKApp: " + token);
      //  SdkConstants.applicationType = "CORE";
        SdkConstants.IS_BETA_USER=true;
//        SdkConstants.userNameFromCoreApp = "Snehasony";
//        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTbmVoYXNvbnkiLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE2MjA3MjM3Njc2NDYsImV4cCI6MTYyMDcyNTU2N30.--MAMha1Ab70x89FjBD4kyu3EgbsqJl0N9gHG-HlM-caQ8gY2UX3ULX0fapgkReOEug32HlKtcSz5TG7Ec-YMA";

//        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNjMwNjUwMzU3MDM2LCJleHAiOjE2MzA2NTIxNTd9.yvimlRCvmVyNv_Uv1d6xkRFF-2Ru_j1fDdNRKCBv7g-xnm9hBdr0xBwnC9D5vm7-vaz91eSOVzrq5r4R5blUpQ";
//        SdkConstants.userNameFromCoreApp = "Itpl";


//        SdkConstants.loginID = "aepsTestR";//login_ID
//        SdkConstants.encryptedData = "cssC%2BcHGxugRFLTjpk%2BJN2Hbbo%2F%2BDokPsBwb9uFdXebdGg%2FEaqOvFXBEoU7ve%2FAP6rabeaskLloqjx6bF6tCcw%3D%3D";



//        SdkConstants.userNameFromCoreApp = "itpl";
//        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNjM4NzkxNTE0NzgxLCJleHAiOjE2Mzg3OTMzMTR9.aVyV9T3TXySjxMm-O5i8XfUIIWsV0u4y9tubmPN857nycjSYlmb_HIx7QDxgZGWqHMm0M698oQ3nNMmks52K-w";
        SdkConstants.DEVICE_TYPE="morefun";

        //        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTbmVoYXNvbnkiLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE2MjAxMjY0NzQxMjEsImV4cCI6MTYyMDEyODI3NH0.yyg3e1bdPY9E2ZPEqYKuOP-CGwoVo3GrTHj4lupv-vzom-mWJ2bQEn7UZPURA4dslHwTiis8_tpixqkOyFlnIw";
//        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTbmVoYXNvbnkiLCJhdWRpZW5jZSI6IndlYiIsImNyZWF0ZWQiOjE2MDYzOTAwMzkwODcsImV4cCI6MTYwNjM5MTgzOX0.k3x0n6OSIPwzWdrIxBdQS1U9jXHu0I7SLHMaV81fN_HuFJ83f7EwSYVn38lYDgh6g8mBgJroo38-MH89aROW3g";
//        SdkConstants.tokenFromCoreApp = token;
        //SdkConstants.loginID = "Bindlish.a.11250";
        //SdkConstants.encryptedData ="TYqmJRyB%2B4Mb39MQf%2BPqVrOZefRP0s10rREkrzYvxahvS4SiPYqnTY3R3MgarVjnyvrz3mjOEb%2F261GisLVNYQ%3D%3D";
        Intent intent = new Intent(MainActivity.this, PosServiceActivity.class);
        startActivityForResult(intent, SdkConstants.MATM_REQUEST_CODE);
    }


    @Override
    protected void onResume() {
        String str = SdkConstants.responseData;
//        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
        super.onResume();
    }


    public String getSha256Hash(String password) {
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(password.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }

    private Boolean biometricDeviceConnect() {
        HashMap<String, UsbDevice> connectedDevices = musbManager.getDeviceList();
        usbDevice = null;
        if (connectedDevices.isEmpty()) {
            deviceConnectMessgae();
            return false;
        } else {
            for (UsbDevice device : connectedDevices.values()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (device != null && device.getManufacturerName() != null) {
                        usbDevice = device;
                        manufactureFlag = usbDevice.getManufacturerName();
                        return true;
                    }

                }
            }
        }
        return false;
    }

    private void deviceConnectMessgae() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setCancelable(false);
        builder.setTitle("No device found")
                .setMessage("Unable to find biometric device connected . Please connect your biometric device for AEPS transactions.")
                .setPositiveButton(getResources().getString(isumatm.androidsdk.equitas.R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // finish();
                    }
                })
                .show();
    }

    private void loginToken() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        String url = "https://itpl.iserveu.tech/generate/v1";
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String hello = response.getString("hello");
                            byte[] data = Base64.decode(hello, Base64.DEFAULT);
                            String base64 = "";
                            try {
                                base64 = new String(data, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            generateToken(base64);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                    }
                });
    }

    private void generateToken(String url) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
            obj.put("password", "Password@1");

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                token = response.getString("token");
                                adminName = response.getString("adminName");
                                Toast.makeText(MainActivity.this, "Token Generated", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            dialog.dismiss();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
        }

    }

    public String getDecimalString(String s) {
        String d = "N/A";
        if (s != null && !s.contains(" ") && !s.equals("")) {
            if (s.contains(".")) {
                int index = s.lastIndexOf(".");
                int size = s.length() - 1;
                if (index == size) {
                    d = s + "00";
                } else if (index == size - 1) {
                    d = s + "0";
                } else {
                    d = s;
                }
            } else {
                d = s + ".00";
            }
        }
        return d;
    }

}
