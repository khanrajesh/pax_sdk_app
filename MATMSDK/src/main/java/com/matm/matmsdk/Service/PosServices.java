package com.matm.matmsdk.Service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.matm.matmsdk.Utils.ResultEvent;
import com.matm.matmsdk.Utils.Tools;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.api.ResponseCode;
import com.paxsz.easylink.model.DataModel;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;

public class PosServices {

    // Here transacton will be going to verified offline. the transaction will be done i the sattelment process in the backend.

    public static Integer offline(EasyLinkSdkManager manager){
        Log.d("MPOS","Get Card Version Number" + DataSetting.AppVersionNumber(manager));
        Log.d("MPOS","Get Data : " + DataSetting.getAllData(manager));
        Log.d("MPOS","Get Card Acceptor Terminal Identification : " + DataSetting.TerminalIdentification(manager));
        Log.d("MPOS","Get Card Acceptor Identification : " + DataSetting.CardAcceptorIdentification(manager));
        Log.d("MPOS","Get Transaction Type : " + DataSetting.TransactionType(manager));

        return ResponseCode.EL_RET_OK;
    }

    public static Integer completeTransAndGetData(EasyLinkSdkManager manager){

        int result;

        ByteArrayOutputStream getTransResultTags = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA, new byte[]{(byte) 0x9F, 0x27}, getTransResultTags);
        Log.d("MPOS", "get trans result: ret = " + result + "  " + Tools.cleanByte(Tools.bcd2Str(getTransResultTags.toByteArray())));
        if (result != ResponseCode.EL_RET_OK) {
            return result;
        }

        result = manager.completeTransaction();
        Log.d("MPOS", "complete trans: ret = " + result);

        Log.d("MPOS","Complete Transaction Get Data : " + DataSetting.getAllData(manager));

        getTestData(manager);

        ByteArrayOutputStream getTransactionStatusInformation = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9B}, getTransactionStatusInformation);


        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Transaction status Error");

        }
        else {
            Log.d("MPOS", Tools.cleanByte(Tools.bcd2Str(getTransactionStatusInformation.toByteArray())));
        }

        if (result != ResponseCode.EL_RET_OK) {
            return result;
        }

        ByteArrayOutputStream getTransDataTags = new ByteArrayOutputStream();
        return manager.getData(DataModel.DataType.TRANSACTION_DATA, new byte[]{(byte) 0x9F, 0x27, 0x5A, 0x57}, getTransDataTags);
    }

    public static Integer setResponseData(EasyLinkSdkManager manager, String responseCode, String OnlineAuthManager, String AuthCode, String Response){
        int result;

        //DE39

        responseCode = asciiToHex(responseCode);
        responseCode = "8A02" + responseCode;
        byte[] responseCodeByte = Tools.str2Bcd(responseCode);

        ByteArrayOutputStream responseCodeData = new ByteArrayOutputStream();
        result = manager.setData(DataModel.DataType.TRANSACTION_DATA, responseCodeByte, responseCodeData);
        Log.d("MPOS", "Set Response Code: ret = " + result );
        if (result != ResponseCode.EL_RET_OK) {
            return result;
        }


        //DE39
        OnlineAuthManager = "030801" + OnlineAuthManager; //00
        byte[] OnlineAuthManagerByte = Tools.str2Bcd(OnlineAuthManager);

        ByteArrayOutputStream OnlineAuthManagerData = new ByteArrayOutputStream();
        result = manager.setData(DataModel.DataType.CONFIGURATION_DATA, OnlineAuthManagerByte, OnlineAuthManagerData);
        Log.d("MPOS", "Set Online Auth Manager: ret = " + result );
        if (result != ResponseCode.EL_RET_OK) {
            return result;
        }

        //DE38
        AuthCode = asciiToHex(AuthCode);
        AuthCode = "8906" + AuthCode;
        byte[] AuthCodeByte = Tools.str2Bcd(AuthCode);

        ByteArrayOutputStream AuthCodeData = new ByteArrayOutputStream();
        result = manager.setData(DataModel.DataType.TRANSACTION_DATA, AuthCodeByte, AuthCodeData);
        Log.d("MPOS", "Set Online Auth Code: ret = " + result);
        if (result != ResponseCode.EL_RET_OK) {
            return result;
        }

        // Set the field 55 in 031E tag here
        byte[] ResponseByte = Tools.str2Bcd(Response);

        ByteArrayOutputStream ResponseData = new ByteArrayOutputStream();
        result = manager.setData(DataModel.DataType.TRANSACTION_DATA, ResponseByte, ResponseData);
        Log.d("MPOS", "Set Online Auth Code: ret = " + result);
        if (result != ResponseCode.EL_RET_OK) {
            return result;
        }

        return result;
    }

    private static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }


    public static void errorHandel(Integer response, Context context){
        if(response != ResponseCode.EL_RET_OK){
            Log.d("MPOS", "Error : = " + ResponseCode.getRespCodeMsg(response) + " Error Reference Code : " + response);
            Toast.makeText(context, ResponseCode.getRespCodeMsg(response), Toast.LENGTH_LONG).show();
            disTransFailed(response);
            return;
        }
    }

    public static void disTransFailed(int result) {
        doEvent(new ResultEvent(ResultEvent.Status.FAILED, result));
    }

    public static void doEvent(ResultEvent event) {
        EventBus.getDefault().post(event);
    }

    public static void getTestData(EasyLinkSdkManager manager){
        Integer result = 0;
        String TestData = "";

        //Transaction Status Information
        ByteArrayOutputStream taga = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1B}, taga);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(taga.toByteArray()));
        }

        ByteArrayOutputStream tagb = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1A}, tagb);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagb.toByteArray()));
        }

        ByteArrayOutputStream tagc = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1C}, tagc);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagc.toByteArray()));
        }

        ByteArrayOutputStream tagd = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1D}, tagd);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagd.toByteArray()));
        }

        ByteArrayOutputStream tage = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x27}, tage);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tage.toByteArray()));
        }

        ByteArrayOutputStream tagf = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x33}, tagf);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagf.toByteArray()));
        }

        ByteArrayOutputStream tagg = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x34}, tagg);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagg.toByteArray()));
        }

        ByteArrayOutputStream tagh = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x35}, tagh);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagh.toByteArray()));
        }

        ByteArrayOutputStream tagi = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x40}, tagi);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagi.toByteArray()));
        }

        ByteArrayOutputStream tagj = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x89}, tagj);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagj.toByteArray()));
        }

        ByteArrayOutputStream tagK = new ByteArrayOutputStream();
        result = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9C}, tagK);

        if (result != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            TestData+= Tools.cleanByte(Tools.bcd2Str(tagK.toByteArray()));
        }

        Log.d("MPOS","Get Test Data : " + TestData);

    }




}
