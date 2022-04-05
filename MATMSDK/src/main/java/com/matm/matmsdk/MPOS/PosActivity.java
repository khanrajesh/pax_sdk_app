package com.matm.matmsdk.MPOS;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matm.matmsdk.ChooseCard.ChooseCardActivity;
import com.matm.matmsdk.Dashboard.MainActivity;
import com.matm.matmsdk.Error.Error2Activity;
import com.matm.matmsdk.Error.ErrorActivity;
import com.matm.matmsdk.Interface.MPOS.PosApiInterface;
import com.matm.matmsdk.Model.MPOS.PosTransResponse;

import isumatm.androidsdk.equitas.R;

import com.matm.matmsdk.SNTPClient;
import com.matm.matmsdk.Service.BankResponse;
import com.matm.matmsdk.Service.DataSetting;
import com.matm.matmsdk.Service.PaxConditional;
import com.matm.matmsdk.Service.PosServices;
import com.matm.matmsdk.Service.TempApiAuthFactory;
import com.matm.matmsdk.Utils.EnvData;
import com.matm.matmsdk.Utils.PAXScreen;
import com.matm.matmsdk.Utils.ResponseConstant;
import com.matm.matmsdk.Utils.ResultEvent;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.Utils.Session;
import com.matm.matmsdk.Utils.Tools;
import com.matm.matmsdk.Utils.TransactionResponseHandler;
import com.matm.matmsdk.notification.NotificationHelper;
import com.matm.matmsdk.transaction_report.TransactionStatusActivity;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.api.ResponseCode;
import com.paxsz.easylink.device.DeviceInfo;
import com.paxsz.easylink.model.DataModel.DataType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.matm.matmsdk.Service.PosServices.completeTransAndGetData;
import static com.matm.matmsdk.Service.PosServices.disTransFailed;
import static com.matm.matmsdk.Service.PosServices.setResponseData;
import static com.matm.matmsdk.Utils.EnvData.RequestPinBlockAuto;
import static com.matm.matmsdk.Utils.SdkConstants.deviceSerialNo;
import static com.matm.matmsdk.Utils.Tools.decimalToHexaAmountReversal;


public class PosActivity extends AppCompatActivity {

    public String amount = "0";
    public EasyLinkSdkManager manager;
    private Handler handler;
    public PosServices posServices;
    private ExecutorService backgroundExecutor;
    int SetResponseCodeRes = 99999;
    private Boolean withdraw_bool=false,balance_enq=false;
    String transaction_type="00";
    String  PANSN="00";
    public static String tempTrack2Data="";
    public static String tempDeviceData="";
    public static String PINBLOCK="";
    public static String PANNO="";
    public static String cardType="";
    public static Integer RESULT_INT = 00;
    String pairedDeviceName ="6B409842";
    private String lactStr="0.0";
    private String LngStr="0.0";

    String transactionType,tokenStr;
    public static PosActivity instance;
    ProgressDialog loadingView;
    Session session;
    JsonObject obj;
    String transaction_amount="";
    Handler timer_handler;

    String retailerName = "";
    String stanNo = "";
    String dateString="";
    String tempDate = "";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;



      String setDateStr="";
     String setTimeStr ="";
     String setDateTimeStr = "";
     String android_id;


    //22/07
    private NotificationHelper notificationHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        instance = PosActivity.this;
        posServices = new PosServices();
        session = new Session(PosActivity.this);
        loadingView = new ProgressDialog(PosActivity.this);
        //manager = EasyLinkSdkManager.getInstance(PosActivity.this);

        // 22/7
        notificationHelper = new NotificationHelper(this);

         new SNTPtask().execute("yyMMdd","HHmmss","yyyy-MM-dd HH:mm:ss");


        if(SdkConstants.applicationType.equalsIgnoreCase("CORE")){
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            EnvData.token = SdkConstants.tokenFromCoreApp;
            SdkConstants.isSL = false;
            retailerName = SdkConstants.userNameFromCoreApp;

            register(this);
            manager = EasyLinkSdkManager.getInstance(PosActivity.this);
            handler = new Handler();
            backgroundExecutor = Executors.newFixedThreadPool(10, runnable -> {
                Thread thread = new Thread(runnable, "Background executor service");
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.setDaemon(true);
                return thread;
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                initView();
            }


        }else {
            SdkConstants.isSL = true;
            if (SdkConstants.encryptedData.trim().length() != 0 && SdkConstants.paramA.trim().length() != 0 && SdkConstants.paramB.trim().length() != 0 && SdkConstants.transactionType.trim().length() != 0 && SdkConstants.loginID.trim().length() != 0) {
                loadingView.setCancelable(false);
                loadingView.setMessage("Please Wait..");
                retailerName = SdkConstants.loginID;
                getUserAuthToken();

                register(this);
                manager = EasyLinkSdkManager.getInstance(PosActivity.this);
                handler = new Handler();
                backgroundExecutor = Executors.newFixedThreadPool(10, runnable -> {
                    Thread thread = new Thread(runnable, "Background executor service");
                    thread.setPriority(Thread.MIN_PRIORITY);
                    thread.setDaemon(true);
                    return thread;
                });


            } else {
                showAlert(getResources().getString(R.string.error_msg));
            }
        }

        pairedDeviceName = manager.getConnectedDevice().getDeviceName();

        pairedDeviceName = pairedDeviceName.replace("D180-","").trim();
        deviceSerialNo = pairedDeviceName;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView(){
        tokenStr = SdkConstants.USER_TOKEN_KEY;
        amount = SdkConstants.transactionAmount;
        transactionType = SdkConstants.transactionType;

        if(transactionType.equalsIgnoreCase("0")){
            transaction_type = "01";
            balance_enq = true;
        }else{
            transaction_type = "00";
            balance_enq = false;
        }
        if(balance_enq){
            amount="0";
            amount = String.valueOf(0);
            if(manager.isConnected(DeviceInfo.CommType.BLUETOOTH)){
                // this.finish();
                runInBackground(() -> PosActivity.this.cardTransaction(amount));
            }else{
                Toast.makeText(getApplicationContext(),"Please connect your bluetooth device, before transaction.", Toast.LENGTH_LONG).show();
            }

        }else{
            amount = SdkConstants.transactionAmount;
            try {
                if(Integer.parseInt(amount) >=100){
                    if(manager.isConnected(DeviceInfo.CommType.BLUETOOTH)){
                        //this.finish();
                        runInBackground(() -> PosActivity.this.cardTransaction(amount));
                    } else{
                        Toast.makeText(getApplicationContext(),"Please connect your bluetooth device, before transaction.", Toast.LENGTH_LONG).show();

                    }
                } else{
                    goToErrorPage2(001);
                }
            }catch (Exception e){

            }
        }
    }
    public static boolean isBlueToothConnected(Context context){
        EasyLinkSdkManager easyLinkSdkManager = EasyLinkSdkManager.getInstance(context);
        if(easyLinkSdkManager.isConnected(DeviceInfo.CommType.BLUETOOTH)){
            return true;
        }else{
            return false;
        }
    }

    public static void BlueToothDisConnected(Context context){
        EasyLinkSdkManager easyLinkSdkManager = EasyLinkSdkManager.getInstance(context);
        easyLinkSdkManager.disconnect();
    }

    public void cardTransaction(String amount){
        //Starting the Card Showing Activity
        Intent cardShowActivity = new Intent(getApplicationContext(), ChooseCardActivity.class);
        startActivity(cardShowActivity);

        //................Debug mode

        manager.setDebugMode(true);//-------------Rajesh

        int getResponse;
        /*
         * Prepair Transaction
         * */
        Integer transPrepareRet = transPrepare();
        if(transPrepareRet == 0){
            int response = manager.startTransaction(reportedData -> {
                if(reportedData!=null){
                    Log.d("MPOS","Report Data : " + new String(reportedData));
                    PosServices.doEvent(new ResultEvent(ResultEvent.Status.RECV_REPORT, new String(reportedData)));
                }
                return new byte[0];
            });

            if(response != ResponseCode.EL_RET_OK){

                Log.d("MPOS","GET ERROR : "+response);
                switch (response){
                    case 4046:
                        PAXScreen.showErrorOnPax(manager,response);
                        goToErrorPage(response);
                        //break;
                        return;
                    case 4001:
                        PAXScreen.showErrorOnPax(manager,response);
                        goToErrorPage(response);
                        // break;
                        return;
                    case 4003:
                        PAXScreen.showErrorOnPax(manager,response);
                        goToErrorPage(response);
                        //break;
                        return;
                    case 4006:
                        PAXScreen.showErrorOnPax(manager,response);
                        goToErrorPage(response);
                        // break;
                        return;
                    default:
                        goToErrorPage(response);
                        return;

                }
                // return;
            }
            //get enivernment of terminal

            ByteArrayOutputStream getCardEnvirnmentType = new ByteArrayOutputStream();
            getResponse = manager.getData(DataType.CONFIGURATION_DATA,new byte[]{(byte) 0x9C}, getCardEnvirnmentType);
            Log.d("MPOS", "get card envirnment: ret = " + getResponse + "  " + Tools.cleanByte(Tools.bcd2Str(getCardEnvirnmentType.toByteArray())));
            //get card type
            ByteArrayOutputStream getCardTypeTags = new ByteArrayOutputStream();
            getResponse = manager.getData(DataType.CONFIGURATION_DATA,new byte[]{0x03, 0x01}, getCardTypeTags);
            Log.d("MPOS", "get card type: ret = " + getResponse + "  " + Tools.cleanByte(Tools.bcd2Str(getCardTypeTags.toByteArray())));
            if (getResponse != ResponseCode.EL_RET_OK) {
                PosServices.errorHandel(getResponse,getApplicationContext());
                disTransFailed(getResponse);
                return;
            }
            Integer ret = transFlow(getCardTypeTags);
            if (ret != ResponseCode.EL_RET_OK) {
                ResponseCode.getRespCodeMsg(ret);
                disTransFailed(ret);
                return;
            }
        }
    }

    private void goToErrorPage(Integer response) {
        backgroundExecutor.shutdownNow();
        backgroundExecutor.shutdown();
        ChooseCardActivity.instance.finish();
        postNotification("Error", response.toString(), ErrorActivity.class, response.toString());
        Intent intent = new Intent(PosActivity.this, ErrorActivity.class);
        intent.putExtra("errorResponse",response);
        startActivity(intent);
    }

    private void goToErrorPage3(String response) {
        backgroundExecutor.shutdownNow();
        backgroundExecutor.shutdown();
        ChooseCardActivity.instance.finish();
        postNotification("Error", response, ErrorActivity.class, response);
        Intent intent = new Intent(PosActivity.this, Error2Activity.class);
        intent.putExtra("errorResponse",response);
        startActivity(intent);
    }

    private void goToErrorPage4(String response) {
        backgroundExecutor.shutdownNow();
        backgroundExecutor.shutdown();
        Intent intent = new Intent(PosActivity.this, Error2Activity.class);
        intent.putExtra("errorResponse",response);
        startActivity(intent);
    }

    private void goToErrorPage2(Integer response) {
        backgroundExecutor.shutdownNow();
        backgroundExecutor.shutdown();
        // ChooseCardActivity.instance.finish();
        postNotification("Error", response.toString(), ErrorActivity.class, response.toString());
        // ChooseCardActivity.instance.finish();
        Intent intent = new Intent(PosActivity.this, ErrorActivity.class);
        intent.putExtra("errorResponse",response);
        startActivity(intent);
    }

    private int transPrepare(){

        String finalTransactionData = "";
        String generateStan = generateStan();
        Integer stanVal = Integer.parseInt(generateStan);

        if(stanVal<100000){

            if(balance_enq==true){

                finalTransactionData = Tools.finalBalanceTranData("000000000000",generateStan,setDateStr,setTimeStr);

            }else{
                String hexAmt = Tools.decimalToHexaAmount(amount,10);
                finalTransactionData = Tools.finalTranData(hexAmt,generateStan,setDateStr,setTimeStr);
            }
        }else{

            if(balance_enq==true){

                finalTransactionData = Tools.finalBalanceTranData("000000000000",setDateStr,setTimeStr);

            }else{
                String hexAmt = Tools.decimalToHexaAmount(amount,10);
                finalTransactionData = Tools.finalTranData(hexAmt,setDateStr,setTimeStr);
            }
        }


        Log.d("MPOS","Transaction Data : " + finalTransactionData);


        // Log.d("MPOS","Transaction Data : " + finalTransactionData);

        byte[] data = Tools.str2Bcd(finalTransactionData);
        ByteArrayOutputStream failedTags = new ByteArrayOutputStream();
        int transDataSetRet = manager.setData(DataType.TRANSACTION_DATA,data,failedTags);
        int confgDataSetRet = DataSetting.setAllData(manager);
        // Log.d("MPOS","Set Data Transaction: " + transDataSetRet);
        //Log.d("MPOS","Set Data Configuration: " + confgDataSetRet);

        if (confgDataSetRet != ResponseCode.EL_RET_OK && transDataSetRet != ResponseCode.EL_RET_OK) {
            disTransFailed(ResponseCode.EL_TRANS_RET_EMV_DENIAL);
            return ResponseCode.EL_TRANS_RET_EMV_DENIAL;

        }
        else {
            return ResponseCode.EL_RET_OK;
        }
    }
    private int transFlow(ByteArrayOutputStream cardType) {
        if ((cardType.toByteArray()[cardType.size() - 1] == 1) || (cardType.toByteArray()[cardType.size() - 1] == 2)) {
            //MSR card
            return goMSRBranch();
        } else {
            //not MSR card
            return goNotMSRBranch();
        }
    }

    private int goMSRBranch() {
        Log.d("MPOS","Find here");
        ByteArrayOutputStream failedTags = new ByteArrayOutputStream();
        byte[] configData = Tools.str2Bcd(RequestPinBlockAuto);
        int configRet = manager.setData(DataType.CONFIGURATION_DATA,configData,failedTags);
        if(configRet == ResponseCode.EL_RET_OK){
            cardType = "MSR";
            online(manager,"MSR");
            return 0;
        }
        else{
            return 4011;
        }
    }

    private int goNotMSRBranch() {
        Integer result = null;
        String cardProcessingRes = DataSetting.CardProcessingResult(manager);
        // Log.d("MPOS","Card Processing Result"+ cardProcessingRes);
        switch(cardProcessingRes){
            case "02":
                cardType="Chip";
                online(manager,"Chip");
            case "01":
                Toast.makeText(getApplicationContext(),"Transaction Approved", Toast.LENGTH_LONG).show();
                result = posServices.offline(manager);
                if (result != ResponseCode.EL_RET_OK) {
                    return result;
                }
                else {
                    return completeTransAndGetData(manager);
                }
            case "00":
                Toast.makeText(getApplicationContext(),"Transaction Declined", Toast.LENGTH_LONG).show();
                result = ResponseCode.EL_TRANS_RET_TRASN_DECLINED;
                if (result != ResponseCode.EL_RET_OK) {
                    return result;
                }
                break;
        }
        return result;
    }

    public void finish() {
        if(ChooseCardActivity.instance!=null){
            ChooseCardActivity.instance.finish();
        }else{
            Intent i = new Intent(PosActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    public void register(Object obj) {
        if (!EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().register(obj);
        }
    }

    public void unregister(Object obj) {
        if (EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().unregister(obj);
        }
    }

    public void runInBackground(final Runnable runnable) {
        backgroundExecutor.submit(runnable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ResultEvent event) {
        switch ((ResultEvent.Status) event.getStatus()) {
            case SUCCESS:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                //transSuccess();
                break;
            case FAILED:
                finish();
                Toast.makeText(getApplicationContext(), ResponseCode.getRespCodeMsg((int) event.getData()), Toast.LENGTH_LONG);
                break;
            default:
                break;
        }
    }

    public String generateStan(){
        String stanStr = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String rawDate = formatter.format(date);
        tempDate = rawDate;
        String dateStr = sharedPref.getString("DATE",null);
        if(dateStr==null){
            editor.putString("DATE", tempDate);
            editor.commit();
        }else{
            String dt = rawDate;// new Date
            tempDate =dateStr;// old Date
            try {
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = sdformat.parse(tempDate);
                Date d2 = sdformat.parse(dt);
                if(d2.compareTo(d1)>0){
                    editor.putInt(deviceSerialNo+"STANN", 0);
                    editor.commit();
                    editor.putString("DATE", dt);
                    editor.commit();

                }else{
                    editor.putString("DATE", tempDate);
                    editor.commit();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int stan = sharedPref.getInt(deviceSerialNo+"STANN", 0);
        if(stan==0){
            stan = 200000+1;
            editor.putInt(deviceSerialNo+"STANN", stan);
            editor.commit();
            stanNo = String.valueOf(stan);
            stanStr = stanNo;
        }else{
            stan = stan+1;
            editor.putInt(deviceSerialNo+"STANN", stan);
            editor.commit();
            stanNo = String.valueOf(stan);
            System.out.println("STAN NO "+stanNo);
            stanStr =stanNo;

        }
        return stanStr;

    }
    public void online(EasyLinkSdkManager manager, String Type) {
        try{
            obj = null;
            PosApiInterface posApiInterface = TempApiAuthFactory.getTestClient().create(PosApiInterface.class);
            Call<PosTransResponse> call = null;
            if(Type.equalsIgnoreCase("Chip")){
                PINBLOCK = DataSetting.getPinBlock(manager);

                DeviceInfo deviceInfo = manager.getConnectedDevice();
                PANSN = PaxConditional.checkPanDigitStr(manager);
                PAXScreen.showMsgBoxFun(manager);
                tempTrack2Data = DataSetting.track2Data(manager);
                tempDeviceData = DataSetting.getAllData(manager);
                //sendDeviceInfo(pairedDeviceName,tempTrack2Data);
                obj = ApiJsonMap(DataSetting.getAuthorizedAmount(manager),tempTrack2Data,pairedDeviceName,PINBLOCK,tempDeviceData,PANSN);

                PAXScreen.showMsg(manager);
            }
            else {

                String trackData2 = DataSetting.trackData(manager);
                Log.d("MPOS","TRACK Dataa2 : "+trackData2);
                PAXScreen.showMsgBoxFunnFallBack(manager);
                String amountStr =  decimalToHexaAmountReversal(amount,10);
                //sendDeviceInfo(pairedDeviceName,trackData2);

                obj = ApiJsonMapFallback(pairedDeviceName,amountStr,trackData2, DataSetting.getPinBlock(manager),0);

                PAXScreen.showMsgFallBack(manager);
            }

            if(balance_enq==true){
                //For Balance Inquery
                /**
                 *  Start Timer for wait 90 Secs if response not come
                 */
                startTimer("balance");

                call = posApiInterface.SendTransRequestBalanceInq(EnvData.token,obj);
                call.enqueue(new Callback<PosTransResponse>() {
                    @Override
                    public void onResponse(Call<PosTransResponse> call, Response<PosTransResponse> response) {
                        // Log.d("SuccessAPI", String.valueOf(response.body()));
                        /**
                         *  If response came befor 90secs stope the timer
                         */
                        timer_handler.removeCallbacksAndMessages(null);

                        if(response.isSuccessful() && response.body()!=null) {
                            PosTransResponse posTransResponse = response.body();
                            Integer statusInt = posTransResponse.getStatus();
                            if (statusInt == null || statusInt == -1) {
                                PAXScreen.completeTransaction(manager);
                                String stsDesc = posTransResponse.getStatusDes();
                                goToErrorPage3(stsDesc);

                            }
                            else {
                                String txnId = posTransResponse.getTxnId();
                                String details = posTransResponse.getStatusDes();
                                TransactionResponseHandler.responseHandler(details);
                                String AuthManager = "";
                                // Log.d("MPOS","RESPONSE CODE : " + ResponseConstant.DE39);
                                if ((ResponseConstant.DE39).equalsIgnoreCase("0000")) {
                                    AuthManager = "00";
                                } else {
                                    AuthManager = "01";
                                }
                                // System.out.println(AuthManager);
                                if (cardType.equalsIgnoreCase("Chip")) {
                                    SetResponseCodeRes = setResponseData(manager, ResponseConstant.DE39.substring(2), AuthManager, ResponseConstant.DE38, ResponseConstant.DE55);
                                    manager.completeTransaction();
                                }
                                String str = ResponseConstant.DE54;
                                Log.d("MPOS", "VALUE ON DE55 : " + ResponseConstant.DE55);

                                if (!str.isEmpty() && !str.equalsIgnoreCase("null") && str != null ) {
                                    try {
                                        String amount_value = ResponseConstant.DE54;
                                        String current_balance1 = amount_value.substring(0, 20);
                                        String current_balance2 = amount_value.substring(20, amount_value.length());

                                        if (Integer.valueOf(current_balance1.substring(0, 6)) > Integer.valueOf(current_balance2.substring(0, 6))) {
                                            amount = current_balance1.substring(7);
                                        } else {
                                            amount = current_balance2.substring(7);
                                        }

                                        String BalsanceType = amount.substring(0, 1);
                                        amount = amount.substring(1);
                                        if (BalsanceType.equalsIgnoreCase("C")) {
                                            amount = "" + String.valueOf(Integer.parseInt(amount));
                                        }
                                    }catch (Exception e){

                                    }

                                }

                                if (ResponseConstant.DE39.substring(2).equalsIgnoreCase(String.valueOf(BankResponse.Approved))) {
                                    // Log.d("MPOS", String.valueOf(result));

                                    // ReversalTestApi(DataSetting.getAuthorizedAmount(manager),DataSetting.cardSequenceNumber(manager),DataSetting.track2Data(manager),"6B409832",DataSetting.getPinBlock(manager),DataSetting.getAllData(manager),PANSN,String.valueOf(BankResponse.Approved));

                                    Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
                                    intent.putExtra("flag", "success");
                                    intent.putExtra("TRANSACTION_ID",txnId);
                                    intent.putExtra("TRANSACTION_TYPE", "balance");
                                    intent.putExtra("TRANSACTION_AMOUNT", transaction_amount);
                                    intent.putExtra("RRN_NO", ResponseConstant.DE37);
                                    intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
                                    intent.putExtra("APP_NAME", DataSetting.applName(manager));
                                    intent.putExtra("AID", details);
                                    intent.putExtra("AMOUNT", amount);
                                    intent.putExtra("MID", ResponseConstant.DE42);
                                    intent.putExtra("TID", ResponseConstant.DE41);
                                    intent.putExtra("TXN_ID", "1234567");
                                    intent.putExtra("INVOICE", "1111111");
                                    intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                    intent.putExtra("APPR_CODE", "123456");
                                    intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);
                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("flag", "success");
                                        object.put("TRANSACTION_ID",txnId);
                                        object.put("TRANSACTION_TYPE", "balance");
                                        object.put("TRANSACTION_AMOUNT", transaction_amount);
                                        object.put("RRN_NO", ResponseConstant.DE37);
                                        object.put("RESPONSE_CODE", ResponseConstant.DE39);
                                        object.put("APP_NAME", DataSetting.applName(manager));
                                        object.put("AID", details);
                                        object.put("AMOUNT", amount);
                                        object.put("MID", ResponseConstant.DE42);
                                        object.put("TID", ResponseConstant.DE41);
                                        object.put("TXN_ID", "1234567");
                                        object.put("INVOICE", "1111111");
                                        object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                        object.put("APPR_CODE", "123456");
                                        object.put("CARD_NUMBER", ResponseConstant.DE35);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("intentData", object.toString());
                                    statusNotification("Success", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());
                                    startActivity(intent);
                                    finish();
                                    backgroundExecutor.shutdown();
                                } else {
                                    Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
                                    intent.putExtra("flag", "failure");
                                    intent.putExtra("TRANSACTION_ID",txnId);
                                    intent.putExtra("TRANSACTION_TYPE", "balance");
                                    intent.putExtra("TRANSACTION_AMOUNT", transaction_amount);
                                    intent.putExtra("RRN_NO", ResponseConstant.DE37);
                                    intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
                                    intent.putExtra("APP_NAME", "RuPay Debit");
                                    intent.putExtra("AID", DataSetting.cardAID(manager));
                                    intent.putExtra("AMOUNT", amount);
                                    intent.putExtra("MID", ResponseConstant.DE42);
                                    intent.putExtra("TID", ResponseConstant.DE41);
                                    intent.putExtra("TXN_ID", "1234567");
                                    intent.putExtra("INVOICE", "1111111");
                                    intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                    intent.putExtra("APPR_CODE", "123456");
                                    intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);
//
                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("flag", "failure");
                                        object.put("TRANSACTION_ID",txnId);
                                        object.put("TRANSACTION_TYPE", "balance");
                                        object.put("TRANSACTION_AMOUNT", transaction_amount);
                                        object.put("RRN_NO", ResponseConstant.DE37);
                                        object.put("RESPONSE_CODE", ResponseConstant.DE39);
                                        object.put("APP_NAME", "RuPay Debit");
                                        object.put("AID", DataSetting.cardAID(manager));
                                        object.put("AMOUNT", amount);
                                        object.put("MID", ResponseConstant.DE42);
                                        object.put("TID", ResponseConstant.DE41);
                                        object.put("TXN_ID", "1234567");
                                        object.put("INVOICE", "1111111");
                                        object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                        object.put("APPR_CODE", "123456");
                                        object.put("CARD_NUMBER", ResponseConstant.DE35);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("intentData", object.toString());

                                    statusNotification("Failed", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());

                                    startActivity(intent);
                                    finish();
                                    backgroundExecutor.shutdown();
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<PosTransResponse> call, Throwable t) {
                        call.cancel();
                        Log.d("Failure", String.valueOf(t));
                    }
                });
            }
            else{
                //for Withdraw
                /**
                 *  Start Timer for wait 90 Secs if response not come
                 */
                startTimer("withdraw");
                //////////////////////////////////
                call = posApiInterface.SendTransRequest(EnvData.token,obj);
                call.enqueue(new Callback<PosTransResponse>() {
                    @Override
                    public void onResponse(Call<PosTransResponse> call, Response<PosTransResponse> response) {
                        Log.d("MPOS: SuccessAPI", String.valueOf(response.body()));
                        /**
                         *  If response came befor 90secs stope the timer
                         */
                        timer_handler.removeCallbacksAndMessages(null);
                        ////////////////////////////////////

                        if(response.isSuccessful() && response.body()!= null){
                            PosTransResponse posTransResponse = response.body();
                            Integer statusInt = posTransResponse.getStatus();
                            if(statusInt==null || statusInt ==-1){
                                PAXScreen.completeTransaction(manager);
                                String stsDesc = posTransResponse.getStatusDes();
                                goToErrorPage3(stsDesc);

                            }
                            else {
                                String txnId = posTransResponse.getTxnId();
                                String details = posTransResponse.getStatusDes();
                                TransactionResponseHandler.responseHandler(details);
                                String AuthManager = "";

                                if ((ResponseConstant.DE39).equalsIgnoreCase("0000")) {
                                    AuthManager = "00";
                                } else {
                                    AuthManager = "01";
                                }

                                String str = ResponseConstant.DE54;
                                Log.d("MPOS", "VALUE ON DE55 : " + ResponseConstant.DE55);
                                if (!str.isEmpty() && !str.equalsIgnoreCase("null") && str != null) {
                                    String amount_value = ResponseConstant.DE54;

                                    String current_balance1 = amount_value.substring(0, 20);
                                    String current_balance2 = amount_value.substring(20, amount_value.length());


                                    try {
                                        if (Integer.valueOf(current_balance1.substring(0, 6)) > Integer.valueOf(current_balance2.substring(0, 6))) {
                                            amount = current_balance1.substring(7);
                                        } else {
                                            amount = current_balance2.substring(7);
                                        }
                                    }catch (Exception e){


                                    }


                                    /*if (Integer.valueOf(current_balance1.substring(0, 6)) > Integer.valueOf(current_balance2.substring(0, 6))) {
                                        amount = current_balance1.substring(7);
                                    } else {
                                        amount = current_balance2.substring(7);
                                    }*/
                                    //amount = ResponseConstant.DE54.substring(27);

                                    String BalsanceType = amount.substring(0, 1);
                                    amount = amount.substring(1);
                                    if (BalsanceType.equalsIgnoreCase("C")) {
                                        amount = "" + String.valueOf(Integer.parseInt(amount));
                                    }
                                }
                                if (cardType.equalsIgnoreCase("Chip")) {

                                    SetResponseCodeRes = setResponseData(manager, ResponseConstant.DE39.substring(2), AuthManager, ResponseConstant.DE38, ResponseConstant.DE55);
                                    int result = manager.completeTransaction();//completeTransAndGetData(manager);
                                    RESULT_INT = result;
                                }

                                if ((ResponseConstant.DE39.substring(2).equalsIgnoreCase(String.valueOf(BankResponse.Approved))) && statusInt == 0) {

                                    if (RESULT_INT == 4011) {
                                        ReversalTestApi(ResponseConstant.DE11, DataSetting.getAuthorizedAmount(manager), tempTrack2Data, pairedDeviceName, PINBLOCK, DataSetting.getAllDataReversal(manager), PANSN, "E1");
                                    } else {

                                        Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
                                        intent.putExtra("flag", "success");
                                        intent.putExtra("TRANSACTION_ID",txnId);
                                        intent.putExtra("TRANSACTION_TYPE", "cash");
                                        intent.putExtra("TRANSACTION_AMOUNT", transaction_amount);
                                        intent.putExtra("RRN_NO", ResponseConstant.DE37);
                                        intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
                                        intent.putExtra("APP_NAME", "RuPay Debit");
                                        intent.putExtra("AID", DataSetting.cardAID(manager));
                                        intent.putExtra("AMOUNT", amount);
                                        intent.putExtra("MID", ResponseConstant.DE42);
                                        intent.putExtra("TID", ResponseConstant.DE41);
                                        intent.putExtra("TXN_ID", "1234567");
                                        intent.putExtra("INVOICE", "1111111");
                                        intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                        intent.putExtra("APPR_CODE", "123456");
                                        intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);

                                        JSONObject object = new JSONObject();
                                        try {
                                            object.put("flag", "success");
                                            object.put("TRANSACTION_ID",txnId);
                                            object.put("TRANSACTION_TYPE", "cash");
                                            object.put("TRANSACTION_AMOUNT", transaction_amount);
                                            object.put("RRN_NO", ResponseConstant.DE37);
                                            object.put("RESPONSE_CODE", ResponseConstant.DE39);
                                            object.put("APP_NAME", "RuPay Debit");
                                            object.put("AID", DataSetting.cardAID(manager));
                                            object.put("AMOUNT", amount);
                                            object.put("MID", ResponseConstant.DE42);
                                            object.put("TID", ResponseConstant.DE41);
                                            object.put("TXN_ID", "1234567");
                                            object.put("INVOICE", "1111111");
                                            object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                            object.put("APPR_CODE", "123456");
                                            object.put("CARD_NUMBER", ResponseConstant.DE35);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        intent.putExtra("intentData", object.toString());
                                        statusNotification("Success", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());


                                        startActivity(intent);
                                        finish();
                                        backgroundExecutor.shutdown();
                                    }
                                } else {
                                    Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
                                    intent.putExtra("flag", "failure");
                                    intent.putExtra("TRANSACTION_ID",txnId);
                                    intent.putExtra("TRANSACTION_TYPE", "cash");
                                    intent.putExtra("TRANSACTION_AMOUNT", transaction_amount);
                                    intent.putExtra("RRN_NO", ResponseConstant.DE37);
                                    intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
                                    intent.putExtra("APP_NAME", "RuPay Debit");
                                    intent.putExtra("AID", DataSetting.cardAID(manager));
                                    intent.putExtra("AMOUNT", amount);
                                    intent.putExtra("MID", ResponseConstant.DE42);
                                    intent.putExtra("TID", ResponseConstant.DE41);
                                    intent.putExtra("TXN_ID", "1234567");
                                    intent.putExtra("INVOICE", "1111111");
                                    intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                    intent.putExtra("APPR_CODE", "123456");
                                    intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);

                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("flag", "failure");
                                        object.put("TRANSACTION_ID",txnId);
                                        object.put("TRANSACTION_TYPE", "cash");
                                        object.put("TRANSACTION_AMOUNT", transaction_amount);
                                        object.put("RRN_NO", ResponseConstant.DE37);
                                        object.put("RESPONSE_CODE", ResponseConstant.DE39);
                                        object.put("APP_NAME", "RuPay Debit");
                                        object.put("AID", DataSetting.cardAID(manager));
                                        object.put("AMOUNT", amount);
                                        object.put("MID", ResponseConstant.DE42);
                                        object.put("TID", ResponseConstant.DE41);
                                        object.put("TXN_ID", "1234567");
                                        object.put("INVOICE", "1111111");
                                        object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                        object.put("APPR_CODE", "123456");
                                        object.put("CARD_NUMBER", ResponseConstant.DE35);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("intentData", object.toString());
                                    statusNotification("Failed", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());
                                    startActivity(intent);
                                    finish();
                                    backgroundExecutor.shutdown();
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<PosTransResponse> call, Throwable t) {
                        call.cancel();
                        Log.d("Failure", String.valueOf(t));
                    }
                });
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private JsonObject ApiJsonMap(String authorizedAmount, String track2Data, String s1, String pinBlock, String allData, String panSlNo) {
        JsonObject jsonObject = new JsonObject();
        try{
            JSONObject obj = new JSONObject();
            obj.put("pansn",panSlNo);
            obj.put("amount",authorizedAmount);
            //obj.put("cardSequenceNumber",cardSequenceNumber);
            obj.put("trackData",track2Data);
            obj.put("deviceSerial",s1);
            obj.put("pinblock",pinBlock);
            obj.put("deviceData",allData);
            obj.put("transactionType",transaction_type);
            obj.put("isFallback","no");
            obj.put("txn_card_type",convertHexToStringValue(DataSetting.applName(manager)));
            obj.put("txnLatitude",lactStr);
            obj.put("txnLongitude",LngStr);
            obj.put("paramA",SdkConstants.paramA);
            obj.put("paramB",SdkConstants.paramB);
            obj.put("paramC",SdkConstants.paramC);
            obj.put("retailer",retailerName);
            obj.put("isSL",SdkConstants.isSL);
           // obj.put("stan",stanNo);
            obj.put("timestamp",DataSetting.DateTimeStamp(manager));
            transaction_amount = authorizedAmount;
            // sendDeviceInfo(s1,track2Data);
            //

            Log.d("MPOS","Request Data : " + obj.toString());
            JsonParser jsonParser = new JsonParser();
            jsonObject = (JsonObject) jsonParser.parse(obj.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }
    //For fallback
    private JsonObject ApiJsonMapFallback(String s1, String authorizedAmount, String track2Data, String pinBlock, Integer panSn) {
        JsonObject jsonObject = new JsonObject();
        try{
            JSONObject obj = new JSONObject();
            obj.put("amount",authorizedAmount);  //000000610000
            obj.put("trackData",track2Data);
            obj.put("pinblock",pinBlock);
            obj.put("deviceSerial",s1);
            obj.put("transactionType",transaction_type);
            obj.put("pansn", String.valueOf(panSn));
            obj.put ("isFallback","yes");
            obj.put("txn_card_type",convertHexToStringValue(DataSetting.applName(manager)));
            obj.put("txnLatitude",lactStr);
            obj.put("txnLongitude",LngStr);
            obj.put("paramA",SdkConstants.paramA);
            obj.put("paramB",SdkConstants.paramB);
            obj.put("paramC",SdkConstants.paramC);
            obj.put("retailer",retailerName);
            obj.put("isSL",SdkConstants.isSL);
            //obj.put("stan",stanNo);
            obj.put("timestamp",DataSetting.DateTimeStamp(manager));
            transaction_amount = authorizedAmount;

            Log.d("MPOS","Request Data : " + obj.toString());
            // sendDeviceInfo(s1,track2Data);
            JsonParser jsonParser = new JsonParser();
            jsonObject = (JsonObject) jsonParser.parse(obj.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void ReversalTestApi(String stan,String authorizedAmount, String track2Data, String s1, String pinBlock, String allData, String panSlNo, String respCode) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("amount", authorizedAmount);
            // obj.put("cardSequenceNumber",cardSequenceNumber);
            obj.put("trackData",track2Data);
            obj.put("deviceSerial",s1);
            obj.put("pinblock",pinBlock);
            obj.put("deviceData",allData);
            obj.put("pansn",panSlNo);
            obj.put("respCode",respCode);//respCode
            //obj.put("stan",stan);
            obj.put("timestamp",DataSetting.DateTimeStamp(manager));
            transaction_amount = authorizedAmount;
            Log.d("MPOS :","Requested Data: "+obj.toString());
            AndroidNetworking.post(EnvData.TEST_URL+"/DONTDEPLOY/doCashWithdralReversal")
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //api_progress.setVisibility(View.GONE);
                                JSONObject obj = new JSONObject(response.toString());
                                Log.d("MPOS :",obj.toString());
                                String statusDesc = obj.getString("statusDes");
                                TransactionResponseHandler.responseHandler(statusDesc);
                                String AuthManager = "";
                                Log.d("MPOS","RESPONSE CODE : " + ResponseConstant.DE39);
                                if((ResponseConstant.DE39).equalsIgnoreCase("0000")){
                                    AuthManager = "00";
                                }else{
                                    AuthManager ="01";
                                }
                                // System.out.println(AuthManager);
                                SetResponseCodeRes = setResponseData(manager, ResponseConstant.DE39.substring(2),AuthManager, ResponseConstant.DE38, ResponseConstant.DE55);
                                int result = completeTransAndGetData(manager);
                                String str = ResponseConstant.DE54;
                                Log.d("MPOS","VALUE ON DE55 : " + ResponseConstant.DE55);
                                if(!str.isEmpty() && !str.equalsIgnoreCase("null") && str!=null) {
                                    amount = ResponseConstant.DE54.substring(27);
                                    String BalsanceType = amount.substring(0, 1);
                                    amount = amount.substring(1);
                                    if (BalsanceType.equalsIgnoreCase("C")) {
                                        amount = "" + String.valueOf(Integer.parseInt(amount));
                                    }
                       /* if(!amount.equalsIgnoreCase("0")) {
                            amount = amount.substring(0, amount.length() - 2);
                        }*/
                                    //Log.d("MPOS", amount);
                                }
                                if(ResponseConstant.DE39.substring(2).equalsIgnoreCase(String.valueOf(BankResponse.Approved))){
                                    Log.d("MPOS", String.valueOf(result));
                                    Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
                                    intent.putExtra("flag","success");
                                    intent.putExtra("TRANSACTION_ID","");
                                    intent.putExtra("TRANSACTION_TYPE","cash");
                                    intent.putExtra("TRANSACTION_AMOUNT",transaction_amount);
                                    intent.putExtra("RRN_NO", ResponseConstant.DE37);
                                    intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
                                    intent.putExtra("APP_NAME", "RuPay Debit");
                                    intent.putExtra("AID", DataSetting.cardAID(manager));
                                    intent.putExtra("AMOUNT", amount);
                                    intent.putExtra("MID", ResponseConstant.DE42);
                                    intent.putExtra("TID", ResponseConstant.DE41);
                                    intent.putExtra("TXN_ID", "1234567");
                                    intent.putExtra("INVOICE", "1111111");
                                    intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                    intent.putExtra("APPR_CODE", "123456");
                                    intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);

                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("flag", "success");
                                        object.put("TRANSACTION_ID","");
                                        object.put("TRANSACTION_TYPE", "cash");
                                        object.put("TRANSACTION_AMOUNT", transaction_amount);
                                        object.put("RRN_NO", ResponseConstant.DE37);
                                        object.put("RESPONSE_CODE", ResponseConstant.DE39);
                                        object.put("APP_NAME", "RuPay Debit");
                                        object.put("AID", DataSetting.cardAID(manager));
                                        object.put("AMOUNT", amount);
                                        object.put("MID", ResponseConstant.DE42);
                                        object.put("TID", ResponseConstant.DE41);
                                        object.put("TXN_ID", "1234567");
                                        object.put("INVOICE", "1111111");
                                        object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                        object.put("APPR_CODE", "123456");
                                        object.put("CARD_NUMBER", ResponseConstant.DE35);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("intentData", object.toString());
                                    statusNotification("Success", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());


                                    startActivity(intent);
                                    finish();
                                    backgroundExecutor.shutdown();
                                }
                                else{
                                    Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
                                    intent.putExtra("flag","failure");
                                    intent.putExtra("TRANSACTION_ID","");
                                    intent.putExtra("TRANSACTION_TYPE","cash");
                                    intent.putExtra("TRANSACTION_AMOUNT",transaction_amount);
                                    intent.putExtra("RRN_NO", ResponseConstant.DE37);
                                    intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
                                    intent.putExtra("APP_NAME", "RuPay Debit");
                                    intent.putExtra("AID", DataSetting.cardAID(manager));
                                    intent.putExtra("AMOUNT", amount);
                                    intent.putExtra("MID", ResponseConstant.DE42);
                                    intent.putExtra("TID", ResponseConstant.DE41);
                                    intent.putExtra("TXN_ID", "1234567");
                                    intent.putExtra("INVOICE", "1111111");
                                    intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                    intent.putExtra("APPR_CODE", "123456");
                                    intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);

                                    JSONObject object = new JSONObject();
                                    try {
                                        object.put("flag", "failure");
                                        object.put("TRANSACTION_ID","");
                                        object.put("TRANSACTION_TYPE", "cash");
                                        object.put("TRANSACTION_AMOUNT", transaction_amount);
                                        object.put("RRN_NO", ResponseConstant.DE37);
                                        object.put("RESPONSE_CODE", ResponseConstant.DE39);
                                        object.put("APP_NAME", "RuPay Debit");
                                        object.put("AID", DataSetting.cardAID(manager));
                                        object.put("AMOUNT", amount);
                                        object.put("MID", ResponseConstant.DE42);
                                        object.put("TID", ResponseConstant.DE41);
                                        object.put("TXN_ID", "1234567");
                                        object.put("INVOICE", "1111111");
                                        object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                                        object.put("APPR_CODE", "123456");
                                        object.put("CARD_NUMBER", ResponseConstant.DE35);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("intentData", object.toString());
                                    statusNotification("Failed", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());
                                    startActivity(intent);
                                    finish();
                                    backgroundExecutor.shutdown();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            Log.d("MPOS :",anError.getErrorBody());
                        }
                    });
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //--------------
    public static String convertHexToStringValue(String hex) {
        StringBuilder stringbuilder = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            stringbuilder.append((char)decimal);
        }
        return stringbuilder.toString();
    }


    /*
     * Get user Token From encrypted Data  and Retailer User Name
     * */
    private void getUserAuthToken(){
        loadingView.show();
        String url = SdkConstants.BASE_URL+"/api/getAuthenticateData" ;
        // String url = "https://newapp.iserveu.online/AEPS2NEW"+"/api/getAuthenticateData";
        JSONObject obj = new JSONObject();
        try {
            obj.put("encryptedData",SdkConstants.encryptedData);
            obj.put("retailerUserName",SdkConstants.loginID);

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
                                    session.setUsername(userName);
                                    session.setUserToken(userToken);
                                    EnvData.token = userToken;
                                    //session.setUserToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTg4MTkxODU5NjMzLCJleHAiOjE1ODgxOTM2NTl9.0tZb8XrRIkFJ3ZamNuoL3n5OkqXvXPc4xU2EoJzbivrOOlg1jMse_WzpJtZDRH9-ESKBBOlfQ680V8U09WwUKg");
                                    //EnvData.token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTg4MTkxODU5NjMzLCJleHAiOjE1ODgxOTM2NTl9.0tZb8XrRIkFJ3ZamNuoL3n5OkqXvXPc4xU2EoJzbivrOOlg1jMse_WzpJtZDRH9-ESKBBOlfQ680V8U09WwUKg";
                                    loadingView.dismiss();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        initView();
                                    }

                                    // getUserId(session.getUserToken(),SdkConstants.BASE_URL+"user/user_details");
                                }else {
                                    showAlert(status);
                                    loadingView.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                loadingView.dismiss();
                                showAlert("Invalid Encrypted Data");
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            loadingView.dismiss();
                        }
                    });
        }catch ( Exception e){
            e.printStackTrace();
        }
    }


    public void showAlert(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(PosActivity.this);
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
    /**
     *  Wait for 90 secs to run
     */
    public void startTimer(String transactionType){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timer_handler = new Handler();
                timer_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //add your code here
                        getTransactionDetails(transactionType);
                    }
                }, 90000);

            }
        });
    }

    public void getTransactionDetails(final String transactionTyp){
        Toast.makeText(PosActivity.this, "METHOD EXECUTED", Toast.LENGTH_SHORT).show();
        try {
            JSONObject obj = new JSONObject();
            obj.put("clientUniqueId",System.currentTimeMillis());

            AndroidNetworking.post(EnvData.TEST_URL+"/DONTDEPLOY/getTransactionStatus")
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");
                                String statusDesc = obj.getString("statusDesc");

                                if(status.equalsIgnoreCase("0")){

                                    if(obj.has("txnData")){
                                        String transaction_response = obj.getString("txnData");

                                        if(transaction_response.trim().length()!=0){
                                            TransactionResponseHandler.responseHandler(transaction_response);
                                            if(transactionTyp.equalsIgnoreCase("Balance")){
                                                processBalanceEnquiry();
                                            }else{
                                                processCashWithdraw();
                                            }
                                        }else{
                                            showStatus("Unable to fetch the transaction record, please see the report for the status. ");
                                        }

                                    }

                                }else{
                                    manager.completeTransaction();
                                    goToErrorPage(2);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            System.out.println("Error  "+anError.getErrorDetail());
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void processBalanceEnquiry(){

        String AuthManager = "";
        // Log.d("MPOS","RESPONSE CODE : " + ResponseConstant.DE39);
        if((ResponseConstant.DE39).equalsIgnoreCase("0000")){
            AuthManager = "00";
        }else{
            AuthManager ="01";
        }
        // System.out.println(AuthManager);
        if(cardType.equalsIgnoreCase("Chip")) {
            SetResponseCodeRes = setResponseData(manager, ResponseConstant.DE39.substring(2), AuthManager, ResponseConstant.DE38, ResponseConstant.DE55);
            manager.completeTransaction();
        }

        //PAXScreen.completeTransaction(manager);

        String str = ResponseConstant.DE54;
        Log.d("MPOS","VALUE ON DE55 : " + ResponseConstant.DE55);
        if(!str.isEmpty() && !str.equalsIgnoreCase("null") && str!=null) {
            String amount_value = ResponseConstant.DE54;

            String current_balance1 = amount_value.substring(0,20);
            String current_balance2 = amount_value.substring(20,amount_value.length());


            if(Integer.valueOf(current_balance1.substring(0,6))>Integer.valueOf(current_balance2.substring(0,6))){
                amount = current_balance1.substring(7);
            }else{
                amount = current_balance2.substring(7);
            }

            String BalsanceType = amount.substring(0, 1);
            amount = amount.substring(1);
            if (BalsanceType.equalsIgnoreCase("C")) {
                amount = "" + String.valueOf(Integer.parseInt(amount));
            }

        }

        if(ResponseConstant.DE39.substring(2).equalsIgnoreCase(String.valueOf(BankResponse.Approved))){

            Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
            intent.putExtra("flag","success");
            intent.putExtra("TRANSACTION_TYPE","balance");
            intent.putExtra("TRANSACTION_AMOUNT",transaction_amount);
            intent.putExtra("RRN_NO", ResponseConstant.DE37);
            intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
            intent.putExtra("APP_NAME", DataSetting.applName(manager));
            intent.putExtra("AID", DataSetting.cardAID(manager));
            intent.putExtra("AMOUNT", amount);
            intent.putExtra("MID", ResponseConstant.DE42);
            intent.putExtra("TID", ResponseConstant.DE41);
            intent.putExtra("TXN_ID", "1234567");
            intent.putExtra("INVOICE", "1111111");
            intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
            intent.putExtra("APPR_CODE", "123456");
            intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);

            JSONObject object = new JSONObject();
            try {
                object.put("flag", "success");
                object.put("TRANSACTION_TYPE", "balance");
                object.put("TRANSACTION_AMOUNT", transaction_amount);
                object.put("RRN_NO", ResponseConstant.DE37);
                object.put("RESPONSE_CODE", ResponseConstant.DE39);
                object.put("APP_NAME", DataSetting.applName(manager));
                object.put("AID", DataSetting.cardAID(manager));
                object.put("AMOUNT", amount);
                object.put("MID", ResponseConstant.DE42);
                object.put("TID", ResponseConstant.DE41);
                object.put("TXN_ID", "1234567");
                object.put("INVOICE", "1111111");
                object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                object.put("APPR_CODE", "123456");
                object.put("CARD_NUMBER", ResponseConstant.DE35);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra("intentData", object.toString());
            statusNotification("Success", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());


            startActivity(intent);
            finish();
            backgroundExecutor.shutdown();
        }
        else {
            Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
            intent.putExtra("flag","failure");
            intent.putExtra("TRANSACTION_TYPE","balance");
            intent.putExtra("TRANSACTION_AMOUNT",transaction_amount);
            intent.putExtra("RRN_NO", ResponseConstant.DE37);
            intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
            intent.putExtra("APP_NAME", "RuPay Debit");
            intent.putExtra("AID", DataSetting.cardAID(manager));
            intent.putExtra("AMOUNT", amount);
            intent.putExtra("MID", ResponseConstant.DE42);
            intent.putExtra("TID", ResponseConstant.DE41);
            intent.putExtra("TXN_ID", "1234567");
            intent.putExtra("INVOICE", "1111111");
            intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
            intent.putExtra("APPR_CODE", "123456");
            intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);

            JSONObject object = new JSONObject();
            try {
                object.put("flag", "failure");
                object.put("TRANSACTION_TYPE", "balance");
                object.put("TRANSACTION_AMOUNT", transaction_amount);
                object.put("RRN_NO", ResponseConstant.DE37);
                object.put("RESPONSE_CODE", ResponseConstant.DE39);
                object.put("APP_NAME", "RuPay Debit");
                object.put("AID", DataSetting.cardAID(manager));
                object.put("AMOUNT", amount);
                object.put("MID", ResponseConstant.DE42);
                object.put("TID", ResponseConstant.DE41);
                object.put("TXN_ID", "1234567");
                object.put("INVOICE", "1111111");
                object.put("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                object.put("APPR_CODE", "123456");
                object.put("CARD_NUMBER", ResponseConstant.DE35);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra("intentData", object.toString());
            statusNotification("Failed", "Transaction Amount "+transaction_amount, TransactionStatusActivity.class, object.toString());

            startActivity(intent);
            finish();
            backgroundExecutor.shutdown();
        }

    }
    public void processCashWithdraw(){
        String AuthManager = "";

        if((ResponseConstant.DE39).equalsIgnoreCase("0000")){
            AuthManager = "00";
        }else{
            AuthManager ="01";
        }

        String str = ResponseConstant.DE54;
        Log.d("MPOS","VALUE ON DE55 : " + ResponseConstant.DE55);
        if(!str.isEmpty() && !str.equalsIgnoreCase("null") && str!=null) {
            String amount_value = ResponseConstant.DE54;

            String current_balance1 = amount_value.substring(0,20);
            String current_balance2 = amount_value.substring(20,amount_value.length());


            if(Integer.valueOf(current_balance1.substring(0,6))>Integer.valueOf(current_balance2.substring(0,6))){
                amount = current_balance1.substring(7);
            }else{
                amount = current_balance2.substring(7);
            }

            //amount = ResponseConstant.DE54.substring(27);
            try {
                String BalsanceType = amount.substring(0, 1);
                amount = amount.substring(1);
                if (BalsanceType.equalsIgnoreCase("C")) {
                    amount = "" + String.valueOf(Integer.parseInt(amount));
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            /*String BalsanceType = amount.substring(0, 1);
            amount = amount.substring(1);
            if (BalsanceType.equalsIgnoreCase("C")) {
                amount = "" + String.valueOf(Integer.parseInt(amount));
            }*/
        }
        if(cardType.equalsIgnoreCase("Chip")){

            SetResponseCodeRes = setResponseData(manager, ResponseConstant.DE39.substring(2),AuthManager, ResponseConstant.DE38, ResponseConstant.DE55);
            int result = manager.completeTransaction();//completeTransAndGetData(manager);
            RESULT_INT = result;
        }

        if(ResponseConstant.DE39.substring(2).equalsIgnoreCase(String.valueOf(BankResponse.Approved))){

            if(RESULT_INT==4011){
                ReversalTestApi(ResponseConstant.DE11, DataSetting.getAuthorizedAmount(manager),tempTrack2Data,pairedDeviceName,PINBLOCK, DataSetting.getAllDataReversal(manager),PANSN,"E1");
            }

            else{

                Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
                intent.putExtra("flag","success");
                intent.putExtra("TRANSACTION_TYPE","cash");
                intent.putExtra("TRANSACTION_AMOUNT",transaction_amount);
                intent.putExtra("RRN_NO", ResponseConstant.DE37);
                intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
                intent.putExtra("APP_NAME", "RuPay Debit");
                intent.putExtra("AID", DataSetting.cardAID(manager));
                intent.putExtra("AMOUNT",  amount);
                intent.putExtra("MID", ResponseConstant.DE42);
                intent.putExtra("TID", ResponseConstant.DE41);
                intent.putExtra("TXN_ID", "1234567");
                intent.putExtra("INVOICE", "1111111");
                intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
                intent.putExtra("APPR_CODE", "123456");
                intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);
                startActivity(intent);
                finish();
                backgroundExecutor.shutdown();
            }
        }
        else {
            Intent intent = new Intent(PosActivity.this, TransactionStatusActivity.class);
            intent.putExtra("flag","failure");
            intent.putExtra("TRANSACTION_TYPE","cash");
            intent.putExtra("TRANSACTION_AMOUNT",transaction_amount);
            intent.putExtra("RRN_NO", ResponseConstant.DE37);
            intent.putExtra("RESPONSE_CODE", ResponseConstant.DE39);
            intent.putExtra("APP_NAME", "RuPay Debit");
            intent.putExtra("AID", DataSetting.cardAID(manager));
            intent.putExtra("AMOUNT",  amount);
            intent.putExtra("MID", ResponseConstant.DE42);
            intent.putExtra("TID", ResponseConstant.DE41);
            intent.putExtra("TXN_ID", "1234567");
            intent.putExtra("INVOICE", "1111111");
            intent.putExtra("CARD_TYPE", convertHexToStringValue(DataSetting.applName(manager)));
            intent.putExtra("APPR_CODE", "123456");
            intent.putExtra("CARD_NUMBER", ResponseConstant.DE35);

            startActivity(intent);
            finish();
            backgroundExecutor.shutdown();
        }
    }
    private void showStatus(final String statusDesc){


        AlertDialog.Builder builder1 = new AlertDialog.Builder(PosActivity.this);
        builder1.setMessage(statusDesc);
        builder1.setTitle("Alert");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void postNotification(String title, String body, Class intnetClass, String raw){
        if (SdkConstants.showNotification){

            NotificationCompat.Builder builder = notificationHelper.createFailNotification(this,
                    title,
                    body,
                    intnetClass,
                    raw);

            if (builder != null) {
                notificationHelper.create(0, builder);
            }
        }
    }
    public void statusNotification(String title, String body, Class intnetClass, String raw){
        if (SdkConstants.showNotification) {
            NotificationCompat.Builder builder = notificationHelper.createNotification(this,
                    title,
                    body,
                    intnetClass,
                    raw);

            if (builder != null) {
                notificationHelper.create(0, builder);
            }
        }
    }
//========================================================================


    public class SNTPtask extends AsyncTask<String,Integer,Boolean> {
        final SNTPClient sntpClient = new SNTPClient();
        String dateSt = "";
        String timeSt = "";
        String dtTimeSt ="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            // super.onPostExecute(aBoolean);

            if(aBoolean){
                Date date = new Date(sntpClient.getNtpTime());
                System.out.println(date.toString());
                DateFormat formatter = new SimpleDateFormat(dateSt);
                DateFormat formatter2 = new SimpleDateFormat(timeSt);
                DateFormat formatter3= new SimpleDateFormat(dtTimeSt);

                formatter.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                formatter2.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                formatter3.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                setDateStr =formatter.format(date);
                setTimeStr = formatter2.format(date);
                setDateTimeStr = formatter3.format(date);
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            dateSt = strings[0];
            timeSt = strings[1];
            dtTimeSt = strings[2];
            return sntpClient.requestTime("1.in.pool.ntp.org", 5000);
        }
    }

    private void callCompleteTransaction(String transId, String status, Integer statusCode){

        JSONObject obj = new JSONObject();
        try {
            obj.put("id",transId);
            obj.put("status",status);
            obj.put("statusCode",statusCode);
            AndroidNetworking.post(EnvData.TEST_URL+"/completetransaction")
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");

                                if(status.equalsIgnoreCase("0")) {
                                    System.out.println("API Success");

                                }else {
                                    //   showAlert("Error");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println("API Error");
                                // pd.dismiss();
                                // showAlert("Invalid Encrypted Data");
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            // pd.dismiss();
                            System.out.println("API Errorrr");
                        }

                    });
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

}
