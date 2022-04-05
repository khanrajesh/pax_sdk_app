package com.matm.matmsdk.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.matm.matmsdk.Bluetooth.BluetoothActivity;
import com.matm.matmsdk.MPOS.PosActivity;
import isumatm.androidsdk.equitas.R;
import com.matm.matmsdk.Utils.ResultEvent;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.api.ResponseCode;
import com.paxsz.easylink.device.DeviceInfo;

import org.greenrobot.eventbus.EventBus;


public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {

    Button bluetoothBtn,download,startTransaction;
    private EasyLinkSdkManager manager;
    String TOKEN="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        initView();
    }

    private void initView() {

        bluetoothBtn = findViewById(R.id.bluetoothBtn);
        bluetoothBtn.setOnClickListener(this);
        download = findViewById(R.id.download);
        download.setOnClickListener(this);
        startTransaction = findViewById(R.id.startTransaction);
        startTransaction.setOnClickListener(this);
        manager = EasyLinkSdkManager.getInstance(DashBoardActivity.this);
        TOKEN  = getIntent().getStringExtra("token_key");
        //EnvData.token = TOKEN;
       // System.out.println("TOKEN>>>--"+EnvData.token);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkDeviceDeviceInfo();
            }
        };
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bluetoothBtn) {
            goToBluetoothActivity();
        } else if (id == R.id.download) {
            goToDownloadActivity();

        }else if(id == R.id.startTransaction){
            goToStartTransactionActivty();

        }
    }

    private void goToStartTransactionActivty() {
        Intent posIntent = new Intent(DashBoardActivity.this, PosActivity.class);
        posIntent.putExtra("token",TOKEN);
        startActivity(posIntent);
    }

    private void goToDownloadActivity() {
        Intent intent = new Intent(DashBoardActivity.this,FileDownLoadActivity.class);
        startActivity(intent);
    }

    private void goToBluetoothActivity() {

        Intent intent = new Intent(DashBoardActivity.this, BluetoothActivity.class);
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_PRIVILEGED}, 1001);
            Toast.makeText(getApplicationContext(),"Please Grant all the permissions", Toast.LENGTH_LONG).show();
        } else {
            startActivity(intent);
        }
    }


    private void checkDeviceDeviceInfo(){
        SharedPreferences pref = DashBoardActivity.this.getSharedPreferences("AuthData", 0);
        if(pref.contains("DEVICE_NAME") && pref.contains("DEVICE_MAC")){
            connectDevice(pref.getString("DEVICE_NAME",""),pref.getString("DEVICE_MAC",""));
        }
    }

    public void doEvent(ResultEvent event) {
        EventBus.getDefault().post(event);

    }

    private void connectDevice(String deviceName, String deviceMac) {
        DeviceInfo deviceInfo = new DeviceInfo(DeviceInfo.CommType.BLUETOOTH, deviceName, deviceMac);
        int ret = manager.connect(deviceInfo);
        /*if (getActivity().isDestroyed()) {
            return;
        }*/
        if (ret == ResponseCode.EL_RET_OK) {
            Log.i("log", "connect success");
            doEvent(new ResultEvent(ResultEvent.Status.CONNECT_SUCCESS));
            System.out.println(">>>>-----  "+deviceInfo.getIdentifier());
            Log.i("log", deviceInfo.getIdentifier());

        } else {
            doEvent(new ResultEvent(ResultEvent.Status.CONNECT_FAILED, ret));
        }
    }
}
