package com.matm.matmsdk.Dashboard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import isumatm.androidsdk.equitas.R;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.device.DeviceInfo;
import com.paxsz.easylink.listener.FileDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileDownLoadActivity extends AppCompatActivity {

    private ListView fileListtt;
    private EasyLinkSdkManager manager;
    private ProgressDialog progressDialog;
    public static boolean iscancelfiledownload = false;

    ProgressDialog pDialog;
    String info = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filedownload);
        fileListtt = findViewById(R.id.fileList);

        manager = EasyLinkSdkManager.getInstance(this);
        progressDialog = new ProgressDialog(FileDownLoadActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        final AssetManager assetManager = getAssets();
        try {
            // for assets folder add empty string
            String[] filelist = assetManager.list("paxfile");
            // for assets/subFolderInAssets add only subfolder name
            if (filelist == null) {
                // dir does not exist or is not a directory
            } else {
                for (int i=0; i<filelist.length; i++) {
                    // Get filename of file or directory
                    String filename = filelist[i];
                    System.out.println(">>>----->>"+filename);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, filelist);

            fileListtt.setAdapter(adapter);

        } catch (IOException e) {
            e.printStackTrace();
        }

        fileListtt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                info = ( (TextView) view ).getText().toString();
                Toast.makeText( getBaseContext(), "Item " + info, Toast.LENGTH_LONG ).show();

                if(manager.isConnected(DeviceInfo.CommType.BLUETOOTH)){
                    pDialog = new ProgressDialog(FileDownLoadActivity.this);
                    pDialog.setMessage("Downloading file. Please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setMax(100);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    new Thread(runnable).start();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please go to setting and connect your bluetooth device.", Toast.LENGTH_LONG).show();
                }

            }
        });
        checkPermission();
    }

    public void checkPermission()
    {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(FileDownLoadActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                // ActivityCompat.requestPermissions(getActivity(), permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 001);

            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(FileDownLoadActivity.this,
                Manifest.permission.CAMERA);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

        }
    }

    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            getFilePath(info);
        }
    };
    public int downLoadFile(String filename, String file_path){
        // String file_path = "file:///android_asset/paxfile/ui_d180.ui"; //+ filename;

        int res=manager.fileDownLoad(file_path, new FileDownloadListener() {
            @Override
            public void onDownloadProgress(int current, int total) {
                Log.d("Current Progress", String.valueOf(current));
                Log.d("Total Size", String.valueOf(total));
                pDialog.setMax(total);
                showDownloadProgress(current,total);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public boolean cancelDownload() {
                return false;
            }
        });

        return res;
    }

    public void showDownloadProgress(int current_value , int total_size){
        Double cCom = ((double)current_value/total_size) * 100;
        //int value = Math.round(Integer.valueOf(String.valueOf(cCom)));
        try {
            if (cCom<90) {
                //String value = String.valueOf(1000L * current_value/total_size);
                pDialog.setProgress(current_value);
            } else {
                pDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getFilePath(String file_name){
        AssetManager am = getAssets();

        try {
            InputStream inputStream = getAssets().open( "paxfile/"+file_name);
            //  InputStream inputStream = am.open("/paxfile/"+file_name);
            File file = createFileFromInputStream(inputStream,file_name);
            downLoadFile(file_name,file.getPath());
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private File createFileFromInputStream(InputStream inputStream, String my_file_name) {

        try{

            File filepath = Environment.getExternalStorageDirectory();

            System.out.println(filepath);


            File dir1 = new File(filepath.getAbsolutePath() + "/" +"PAX_DOWNLOADS" + "/");
            if (!dir1.exists()) {
                dir1.mkdirs();
            }

            File outfile = new File(dir1, my_file_name);


            // File f = new File(my_file_name);
            OutputStream outputStream = new FileOutputStream(outfile);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return outfile;
        }catch (IOException e) {
            //Logging exception
            e.printStackTrace();
        }

        return null;
    }
}
