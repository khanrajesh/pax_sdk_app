package com.matm.matmsdk.upitransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;



import douglasspgyn.com.github.circularcountdown.CircularCountdown;
import douglasspgyn.com.github.circularcountdown.listener.CircularListener;
import isumatm.androidsdk.equitas.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  UPIHomeActivity extends AppCompatActivity {

    private ImageView QrCodeImg;
    Session session;
    private ProgressBar progressBar;
    private ArrayList<String> dataList = new ArrayList<>();
    CircularCountdown cdtimer;
    TextView circularCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_home);
        progressBar = findViewById(R.id.progressBar);
        QrCodeImg = findViewById(R.id.QrCodeImg);
        circularCountdown = findViewById(R.id.circularCountdown);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("QR Code ");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        session = new Session();
       // getUserAuthToken();
       // doGenerateQRCode();
       // FirebaseApp.initializeApp(getApplicationContext());
        retriveUserList();
    }
    private void getUserAuthToken(){
        progressBar.setVisibility(View.VISIBLE);
        String url = UPIConstant.BASE_URL+"api/getAuthenticateData" ;
        JSONObject obj = new JSONObject();
        try {
            obj.put("encryptedData",UPIConstant.encryptedData);
            obj.put("retailerUserName",UPIConstant.loginID);
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");

                                if(status.equalsIgnoreCase("success")) {
                                    String userName = obj.getString("username");
                                    String userToken = obj.getString("usertoken");
                                    //session.setUsername(userName);
                                   // session.setUserToken(userToken);
                                    //session.setUserToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTg0MzY4NTc3MDk3LCJleHAiOjE1ODQzNzAzNzd9.WbA6uzqnN1sEv9opi_ozqZtoOdVqhefiMjD7H8yKCGQ3MlBsLs47TbKmVN26owyaPNrXCLayd4G7LyGC_yRGVQ");
                                   // progressBar.setVisibility(View.GONE);
                                    apiCalling(userToken);
                                   /* if (SDKConstants.transactionType.equalsIgnoreCase(SDKConstants.cashWithdrawal)) {
                                        apiCalling();
                                    } else if (SDKConstants.transactionType.equalsIgnoreCase(SDKConstants.balanceEnquiry)) {
                                        balanceEnquiryApiCalling();
                                    }*/
                                }else {
                                    showAlert(status);
                                    progressBar.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                showAlert("Invalid Encrypted Data");
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            progressBar.setVisibility(View.GONE);
                        }

                    });
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

    private void apiCalling(String userToken) {
        String url = "https://uat.iserveu.online/upipayment/credo/restCallToCredoPayForDynamicQr" ;
        JSONObject obj = new JSONObject();
        try {
            obj.put("amount",UPIConstant.Amount);
            obj.put("phone_no",UPIConstant.mobileNo);
            obj.put("paramA",UPIConstant.paramA);
            obj.put("paramB",UPIConstant.paramB);
            obj.put("paramC",UPIConstant.paramC);
            obj.put("auth",userToken);
           // obj.put("auth","eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTg1MDUxODM3OTEzLCJleHAiOjE1ODUwODc4Mzd9.tL5zPzRFzU9lySDTqq2b94iu7efQcAr7HT3ln0hNgvhI-lCm3OurEiKy7S3p-oBU9eHGYIwNOtpSm2E6imK_dg");
            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String statusCode = obj.getString("statusCode");
                                String statusDesc = obj.getString("statusDescription");

                                if(statusCode.equalsIgnoreCase("0")) {
                                    //String userName = obj.getString("username");
                                    //String userToken = obj.getString("usertoken");
                                    String qr_code_url = obj.getString("qr_code_url");
                                    /*Glide
                                            .with(UPIHomeActivity.this)
                                            .load(qr_code_url)
                                            .centerCrop()
                                            .into(QrCodeImg);*/

                                    LoadQRImage(qr_code_url);

                                   // strtCountDowntimer();
                                }else {
                                    showAlert(statusDesc);
                                    progressBar.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                showAlert("Invalid Encrypted Data");
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            progressBar.setVisibility(View.GONE);
                        }

                    });
        }catch ( Exception e){
            e.printStackTrace();
        }

    }

    private void LoadQRImage(String img_url) {

        Glide.with(this)
                .load(img_url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable final GlideException e,
                                                final Object model, final Target<Drawable> target,
                                                final boolean isFirstResource) {

                        Toast.makeText(UPIHomeActivity.this,"Qr code not generated .Please reload page.",Toast.LENGTH_SHORT).show();


                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Drawable resource,
                                                   final Object model,
                                                   final Target<Drawable> target,
                                                   final DataSource dataSource,
                                                   final boolean isFirstResource) {



                        QrCodeImg.setImageDrawable(resource);
                        progressBar.setVisibility(View.GONE);

                        return false;
                    }
                })
                .into(QrCodeImg);
        strtCountDowntimer();
    }

    public void showAlert(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(UPIHomeActivity.this);
        builder.setTitle("Alert!!");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void strtCountDowntimer(){

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                circularCountdown.setText( "00:"+millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                circularCountdown.setText("Invalid Qr code!");
                onBackPressed();
            }

        }.start();
       /* cdtimer = new CircularCountdown(UPIHomeActivity.this);
        cdtimer.create(1, 60, CircularCountdown.TYPE_SECOND)
              .listener(new CircularListener() {
                  @Override
                  public void onTick(int i) {

                  }

                  @Override
                  public void onFinish(boolean b, int i) {
                      Toast.makeText(UPIHomeActivity.this,"Finish",Toast.LENGTH_SHORT).show();

                  }
              })
        .start()
        ;*/
    }
/*    public static Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int size = 256;
        BitMatrix bitMatrix = qrCodeWriter.encode("www.example.org", BarcodeFormat.QR_CODE, size, size, hintMap);
        int width = bitMatrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(y, x, bitMatrix.get(x, y)? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }*/


    private void retriveUserList() {
        final PackageManager packageManager = this.getPackageManager();
        Intent intent = getIntent();
        List<ResolveInfo> packages = packageManager.queryIntentActivities(intent,0);

        String pkgName = "";
        for(ResolveInfo res : packages){
            pkgName = res.activityInfo.packageName;
            Log.w("Package Name: ",pkgName);
        }
        getUserAuthToken();
        /*if(pkgName.equalsIgnoreCase("com.example.annapurna_aeps")|| pkgName.equalsIgnoreCase("com.example.midland_microfin")|| pkgName.equalsIgnoreCase("com.pax.pax_sdk_app")||pkgName.equalsIgnoreCase("com.jayam.impactapp")||pkgName.equalsIgnoreCase("com.isu.coreapp")){
            System.out.println("Allow"); //com.pax.pax_sdk_app
            getUserAuthToken();

        }else{
            showAlert("Package name does not resister.");
        }*/
      //-------------------------------------------------------------




 /*       FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                if (hashUser != null) {
                    String Drinks = hashUser.get("package1").toString();
                    Toast.makeText(UPIHomeActivity.this, Drinks , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UPIHomeActivity.this, "Database error." , Toast.LENGTH_SHORT).show();

            }
        });*/
    }
}
