package com.matm.matmsdk.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.matm.matmsdk.Service.DataSetting;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.api.ResponseCode;
import com.paxsz.easylink.model.DataModel;
import com.paxsz.easylink.model.ShowPageInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.matm.matmsdk.Service.PosServices.getTestData;

public class PAXScreen {

    public static void showSuccess(EasyLinkSdkManager manager) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1","ABCD");
                int timeout = 6000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                manager.showPage ("Success.xml",timeout, showPageList);
            }
        });
    }

    public static void showFailure(EasyLinkSdkManager manager) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1","ABCD");
                int timeout = 6000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                manager.showPage ("Failure.xml",timeout, showPageList);
            }
        });

    }

    public static void showMsg(final EasyLinkSdkManager manager) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1", "ABCD");
                int timeout = 3000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                manager.showPage("NeedOnline.xml", timeout, showPageList);
            }
        });
    }

    public static void showMsgBoxFunnFallBack(final EasyLinkSdkManager manager) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1", "ABCD");
                int timeout = 5000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                manager.showPage("Processingswipe.xml", timeout, showPageList);
            }
        });
    }

    public static void showMsgFallBack(final EasyLinkSdkManager manager) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1", "ABCD");
                int timeout = 2000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                manager.showPage("Processingswipe.xml", timeout, showPageList);
            }
        });
    }


    public static void showMsgBoxFun(EasyLinkSdkManager manager) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1", "ABCD");
                int timeout = 12000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                manager.showPage("Processing.xml", timeout, showPageList);
            }
        });
    }






    public static void showErrorOnPax(EasyLinkSdkManager manager, int errorCode) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1","ABCD");
                int timeout = 6000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                switch (errorCode) {
                    case 4046:
                        manager.showPage ("CardExpire.xml",timeout, showPageList);
                        break;
                    case 4001:
                        manager.showPage ("BlockCard.xml",timeout, showPageList);
                        break;
                    case 4003:
                        manager.showPage ("BlockApplication.xml",timeout, showPageList);
                        break;
                    case 4006:
                        manager.showPage ("UnknownAID.xml",timeout, showPageList);
                        break;
                }
            }
        });

    }

    //show bank response on pax

    public static void showBankResponseOnPax(EasyLinkSdkManager manager, int errorCode) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                ShowPageInfo showPageInfo = new ShowPageInfo("Title1","ABCD");
                int timeout = 6000; // 600 * 100ms
                ArrayList<ShowPageInfo> showPageList = new ArrayList<ShowPageInfo>();
                showPageList.add(showPageInfo);
                switch (errorCode) {
                    case 55:
                        manager.showPage ("IncorrectPin.xml",timeout, showPageList);
                        break;
                    /*case 4001:
                        manager.showPage ("BlockCard.xml",timeout, showPageList);
                        break;
                    case 4003:
                        manager.showPage ("BlockApplication.xml",timeout, showPageList);
                        break;
                    case 4006:
                        manager.showPage ("UnknownAID.xml",timeout, showPageList);
                        break;*/
                }
            }
        });

    }

    public static void completeTransaction(EasyLinkSdkManager manage){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //code you want to run on the background
                completeTransAndGetData(manage);
            }
        });
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

}

