package com.matm.matmsdk.Service;

import android.util.Log;

import com.matm.matmsdk.Utils.Tools;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.api.ResponseCode;
import com.paxsz.easylink.model.DataModel;

import java.io.ByteArrayOutputStream;

public class PaxConditional {
    public static Integer checkPanDigit(EasyLinkSdkManager manager){
        Integer response = null;
        String panNumber;
        ByteArrayOutputStream getPanNumber = new ByteArrayOutputStream();
        response = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x5F, 0x34},getPanNumber);
        if(response == ResponseCode.EL_RET_OK){
            panNumber = Tools.cleanByte(Tools.bcd2Str(getPanNumber.toByteArray()));
            panNumber = panNumber.substring(6);
            Log.d("MPOS","Pan Sequence Number :" + panNumber);
            if(Integer.parseInt(panNumber) == 15){
                return 15;
            }else if(Integer.parseInt(panNumber) == 03){
                return 3;
            }
            else {
                return 1;
            }


        }
        else{
            return 0;
        }
    }


    public static String checkPanDigitStr(EasyLinkSdkManager manager){
        Integer response = null;
        String panNumber;
        ByteArrayOutputStream getPanNumber = new ByteArrayOutputStream();
        response = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x5F, 0x34},getPanNumber);
        if(response == ResponseCode.EL_RET_OK){
            panNumber = Tools.cleanByte(Tools.bcd2Str(getPanNumber.toByteArray()));
            panNumber = panNumber.substring(6);
            Log.d("MPOS","Pan Sequence Number :" + panNumber);

            //return panNumber;
            if(panNumber.equalsIgnoreCase("00")){
                return "0";
            }else if(panNumber.equalsIgnoreCase("03")){
                return "3";
            }
            else {
                return "1";
            }


        }
        else{
            return "00";
        }
    }



   /* public static Integer checkPanDigitFallback(EasyLinkSdkManager manager){
        Integer response = null;
        String panNumber;
        ByteArrayOutputStream getPanNumber = new ByteArrayOutputStream();
        response = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte)0x5F, 0x34},getPanNumber);
        if(response == ResponseCode.EL_RET_OK){
            panNumber = Tools.cleanByte(Tools.bcd2Str(getPanNumber.toByteArray()));
            panNumber = panNumber.substring(6);
            Log.d("MPOS","Pan Sequence Number :" + panNumber);
            if(Integer.parseInt(panNumber) == 15){
                return 15;
            }else if(Integer.parseInt(panNumber) == 03){
                return 3;
            }
            else {
                return 1;
            }
        }
        else{
            return 0;
        }
    }*/



}
