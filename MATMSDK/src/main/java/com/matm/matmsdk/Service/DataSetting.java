package com.matm.matmsdk.Service;

import android.util.Log;
import com.matm.matmsdk.Utils.Tools;
import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.api.ResponseCode;
import com.paxsz.easylink.model.DataModel;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.matm.matmsdk.Utils.EnvData.CardEntryMode;
import static com.matm.matmsdk.Utils.EnvData.DataEncryptionKeyIdx;
import static com.matm.matmsdk.Utils.EnvData.DataEncryptionType;
import static com.matm.matmsdk.Utils.EnvData.DataEncryptionType_No;
import static com.matm.matmsdk.Utils.EnvData.FallBackAllowFlag;
import static com.matm.matmsdk.Utils.EnvData.PINBlockMode;
import static com.matm.matmsdk.Utils.EnvData.PINEncryptionKeyIdx;
import static com.matm.matmsdk.Utils.EnvData.PINEncryptionType;
import static com.matm.matmsdk.Utils.EnvData.TransactionProcessingMode;
import static com.matm.matmsdk.Utils.EnvData.Transaction_Type_Enquery;



public class DataSetting {

    public static Integer setAllData(EasyLinkSdkManager manager){
        ByteArrayOutputStream failedTags = new ByteArrayOutputStream();

        byte[] configData = Tools.str2Bcd( PINEncryptionType + PINEncryptionKeyIdx + DataEncryptionType + DataEncryptionKeyIdx + TransactionProcessingMode + CardEntryMode + PINBlockMode + FallBackAllowFlag);

        int configRet = manager.setData(DataModel.DataType.CONFIGURATION_DATA,configData,failedTags);

        return configRet;
    }

    public static String getAllDataopt(EasyLinkSdkManager manager){
        Integer responseData;
        String cardData = "";

        //get Amount Authorized
        ByteArrayOutputStream getAmountAuthorizedTags = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x02}, getAmountAuthorizedTags);
        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS", "Get Data Error") ;
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getAmountAuthorizedTags.toByteArray()));
        }

        //Transaction Type
        ByteArrayOutputStream getTransactionType = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9C}, getTransactionType);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionType.toByteArray()));
        }


        //Transaction Currency Code
        ByteArrayOutputStream getTransactionCurrencyCode = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x5F,0x2A}, getTransactionCurrencyCode);

        if (responseData != ResponseCode.EL_RET_OK) {
            //     Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionCurrencyCode.toByteArray()));
        }


        //Transaction Currency Exponent
        ByteArrayOutputStream getTransactionCurrencyExp = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x5F,0x36}, getTransactionCurrencyExp);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionCurrencyExp.toByteArray()));
        }


        //Transaction Date
        ByteArrayOutputStream getTransactionDate = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9A}, getTransactionDate);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionDate.toByteArray()));
        }


        // Transaction Time

        ByteArrayOutputStream getTransactionTime = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x21}, getTransactionTime);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            String tranTime = Tools.cleanByte(Tools.bcd2Str(getTransactionTime.toByteArray()));
            String subTime = tranTime.substring(6);
            cardData+= subTime;

        }
        // Log.d("MPOS", "Transaction Get Data : " + cardData);
        return cardData;
    }

    public static String getAllData(EasyLinkSdkManager manager){
        Integer responseData;
        String cardData = "";
        //get Amount Authorized
        ByteArrayOutputStream getAmountAuthorizedTags = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x02}, getAmountAuthorizedTags);
        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS", "Get Data Error") ;
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getAmountAuthorizedTags.toByteArray()));
        }

        //Terminal Verification Result
        ByteArrayOutputStream getTerminalVerificationResultTags = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x95}, getTerminalVerificationResultTags);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS", "Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTerminalVerificationResultTags.toByteArray()));
        }

        //Transaction Status Information
        ByteArrayOutputStream getTransactionStatusInformation = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9B}, getTransactionStatusInformation);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionStatusInformation.toByteArray()));
            //reversal
            //cardData+= "9B026800";

            // System.out.println("MPOS");
        }


        //Dedicated File DF Name
        ByteArrayOutputStream getDedicatedFileDFName = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x84}, getDedicatedFileDFName);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getDedicatedFileDFName.toByteArray()));
        }

        //Application Cryptogram (AC)
        ByteArrayOutputStream getApplicationCryptogramAC = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x26}, getApplicationCryptogramAC);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationCryptogramAC.toByteArray()));
        }

        //Application Preferred Name
        // channges 10_08_20
       /* ByteArrayOutputStream getApplicationPreferredName = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x12}, getApplicationPreferredName);

        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationPreferredName.toByteArray()));
        }
*/
        //Application Interchange Profile
        ByteArrayOutputStream getApplicationInterchangeProfile = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x82}, getApplicationInterchangeProfile);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationInterchangeProfile.toByteArray()));
        }

        //Transaction Date
        ByteArrayOutputStream getTransactionDate = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9A}, getTransactionDate);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionDate.toByteArray()));
          /*  SimpleDateFormat format= new SimpleDateFormat("yyMMdd");
            cardData+="9A03"+format.format(new Date());*/
        }

        // Transaction Time

        // changes 10_08_2020

     /*  ByteArrayOutputStream getTransactionTime = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x21}, getTransactionTime);

        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            String tranTime = Tools.cleanByte(Tools.bcd2Str(getTransactionTime.toByteArray()));
            String subTime = tranTime.substring(6);
            cardData+= "9F2103"+subTime;

        }*/

        //Transaction Type
        ByteArrayOutputStream getTransactionType = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9C}, getTransactionType);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionType.toByteArray()));
        }

        //Transaction Currency Code
        ByteArrayOutputStream getTransactionCurrencyCode = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x5F,0x2A}, getTransactionCurrencyCode);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionCurrencyCode.toByteArray()));
        }

        //Terminal Capabilities
        ByteArrayOutputStream getTerminalCapabilities = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x33}, getTerminalCapabilities);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTerminalCapabilities.toByteArray()));
        }

        //Terminal Country Code
        ByteArrayOutputStream getTerminalCountryCode = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1A}, getTerminalCountryCode);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTerminalCountryCode.toByteArray()));
        }

        //Interface Device (IFD) Serial Number
        ByteArrayOutputStream getInterfaceDeviceIFDSerialNumber = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1E}, getInterfaceDeviceIFDSerialNumber);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getInterfaceDeviceIFDSerialNumber.toByteArray()));
        }

        //Cryptogram Information Data (CID)
        ByteArrayOutputStream getCryptogramInformationDataCID = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x27}, getCryptogramInformationDataCID);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getCryptogramInformationDataCID.toByteArray()));
        }

        //Application Transaction Counter (ATC)
        ByteArrayOutputStream getApplicationTransactionCounterATC = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x36}, getApplicationTransactionCounterATC);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationTransactionCounterATC.toByteArray()));
        }

        //Unpredictable Number (UN)
        ByteArrayOutputStream getUnpredictableNumber = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x37}, getUnpredictableNumber);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getUnpredictableNumber.toByteArray()));
        }

        //Amount, Other (Numeric)
       /* ByteArrayOutputStream getAmountOtherNumeric = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x03}, getAmountOtherNumeric);
        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getAmountOtherNumeric.toByteArray()));
        }*/
        cardData+="9F0306000000000000";


        //-----------------------

        //Application Primary Account Number (PAN) Sequence Number (PSN)
        // changes 10_08_20

/*        ByteArrayOutputStream getApplicationPrimaryAccountNumber = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x5F,0x34}, getApplicationPrimaryAccountNumber);

        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Application Primary Account Number (PAN) Sequence Number (PSN) Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationPrimaryAccountNumber.toByteArray()));
        }*/

        //Cardholder Verification Method (CVM) Results
        ByteArrayOutputStream getCardholderVerificationMethod = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x34}, getCardholderVerificationMethod);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Cardholder Verification Method (CVM) Results Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getCardholderVerificationMethod.toByteArray()));
        }

        //Issuer Application Data (IAD)
        ByteArrayOutputStream getIssuerApplicationData = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x10}, getIssuerApplicationData);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Issuer Application Data (IAD) Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getIssuerApplicationData.toByteArray()));
        }


        // Get PAN no Data
        // changes 10_08_20

       /* ByteArrayOutputStream getTransactionPanno = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x5A}, getTransactionPanno);

        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionPanno.toByteArray()));
        }*/


        // Transaction Sequence Counter

        // changes 10_08_20

     /*   ByteArrayOutputStream getTransactionSequenceCounter = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x41}, getTransactionSequenceCounter);

        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Transaction Sequence Counter Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionSequenceCounter.toByteArray()));
        }
*/




        //Issuer Script Result
        // ByteArrayOutputStream getIssuerScriptResult = new ByteArrayOutputStream();
        //responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte) 0x9F,0x5B}, getIssuerScriptResult);
        //cardData+= Tools.cleanByte(Tools.bcd2Str(getIssuerScriptResult.toByteArray()));


       /* if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Issuer Script Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getIssuerScriptResult.toByteArray()));
        }*/


        //Additional Terminal Capabilities
//        ByteArrayOutputStream getAdditionalTerminalCapabilities = new ByteArrayOutputStream();
//        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x40}, getAdditionalTerminalCapabilities);
//
//        if (responseData != ResponseCode.EL_RET_OK) {
//            Log.d("MPOS","Get Additional Terminal Capabilities Data Error");
//        }
//        else {
//            cardData+= Tools.cleanByte(Tools.bcd2Str(getAdditionalTerminalCapabilities.toByteArray()));
//        }

        //Card Holder Verification List
//        ByteArrayOutputStream getCardHolderVerificationList = new ByteArrayOutputStream();
//        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x8E}, getCardHolderVerificationList);
//
//        if (responseData != ResponseCode.EL_RET_OK) {
//            Log.d("MPOS","Get Additional Terminal Capabilities Data Error");
//        }
//        else {
//            cardData+= Tools.cleanByte(Tools.bcd2Str(getCardHolderVerificationList.toByteArray()));
//        }

        // Log.d("MPOS", "Transaction Get Data : " + cardData);
        return cardData;
    }



    //get data for reversal case
    public static String getAllDataReversal(EasyLinkSdkManager manager){
        Integer responseData;
        String cardData = "";


        //get Amount Other
        /*ByteArrayOutputStream getAmountOtherTags = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x03}, getAmountOtherTags);
        if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS", "Get Data Error") ;
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getAmountOtherTags.toByteArray()));
        }*/

        //get Amount Authorized
        ByteArrayOutputStream getAmountAuthorizedTags = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x02}, getAmountAuthorizedTags);
        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS", "Get Data Error") ;
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getAmountAuthorizedTags.toByteArray()));
        }

        //Terminal Verification Result
        ByteArrayOutputStream getTerminalVerificationResultTags = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x95}, getTerminalVerificationResultTags);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS", "Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTerminalVerificationResultTags.toByteArray()));
        }

        //Transaction Status Information
        ByteArrayOutputStream getTransactionStatusInformation = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9B}, getTransactionStatusInformation);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionStatusInformation.toByteArray()));
        }


        //Dedicated File DF Name
        ByteArrayOutputStream getDedicatedFileDFName = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x84}, getDedicatedFileDFName);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getDedicatedFileDFName.toByteArray()));
        }

        //Application Cryptogram (AC)
        ByteArrayOutputStream getApplicationCryptogramAC = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x26}, getApplicationCryptogramAC);

        if (responseData != ResponseCode.EL_RET_OK) {
            //   Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationCryptogramAC.toByteArray()));
        }

        //Application Preferred Name
        ByteArrayOutputStream getApplicationPreferredName = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9f, 0x12}, getApplicationPreferredName);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationPreferredName.toByteArray()));
        }

        //Application Interchange Profile
        ByteArrayOutputStream getApplicationInterchangeProfile = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x82}, getApplicationInterchangeProfile);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationInterchangeProfile.toByteArray()));
        }

        //Transaction Date
        ByteArrayOutputStream getTransactionDate = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9A}, getTransactionDate);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionDate.toByteArray()));

        }

        //Transaction Type
        ByteArrayOutputStream getTransactionType = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9C}, getTransactionType);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionType.toByteArray()));
        }

        //Transaction Currency Code
        ByteArrayOutputStream getTransactionCurrencyCode = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x5F,0x2A}, getTransactionCurrencyCode);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTransactionCurrencyCode.toByteArray()));
        }

        //Terminal Capabilities
        ByteArrayOutputStream getTerminalCapabilities = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x33}, getTerminalCapabilities);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTerminalCapabilities.toByteArray()));
        }

        //Terminal Country Code
        ByteArrayOutputStream getTerminalCountryCode = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1A}, getTerminalCountryCode);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getTerminalCountryCode.toByteArray()));
        }

        //Interface Device (IFD) Serial Number
        ByteArrayOutputStream getInterfaceDeviceIFDSerialNumber = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1E}, getInterfaceDeviceIFDSerialNumber);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getInterfaceDeviceIFDSerialNumber.toByteArray()));
        }

        //Cryptogram Information Data (CID)
        ByteArrayOutputStream getCryptogramInformationDataCID = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x27}, getCryptogramInformationDataCID);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getCryptogramInformationDataCID.toByteArray()));
        }

        //Application Transaction Counter (ATC)
        ByteArrayOutputStream getApplicationTransactionCounterATC = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x36}, getApplicationTransactionCounterATC);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationTransactionCounterATC.toByteArray()));
        }

        //Unpredictable Number (UN)
        ByteArrayOutputStream getUnpredictableNumber = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x37}, getUnpredictableNumber);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getUnpredictableNumber.toByteArray()));
        }

        //Amount, Other (Numeric)
        ByteArrayOutputStream getAmountOtherNumeric = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x03}, getAmountOtherNumeric);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getAmountOtherNumeric.toByteArray()));
        }

        //cardData+="9F0306000000000000";




        //Application Primary Account Number (PAN) Sequence Number (PSN)
        ByteArrayOutputStream getApplicationPrimaryAccountNumber = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x5F,0x34}, getApplicationPrimaryAccountNumber);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Application Primary Account Number (PAN) Sequence Number (PSN) Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getApplicationPrimaryAccountNumber.toByteArray()));
        }

        //Cardholder Verification Method (CVM) Results
        ByteArrayOutputStream getCardholderVerificationMethod = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x34}, getCardholderVerificationMethod);

        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Cardholder Verification Method (CVM) Results Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getCardholderVerificationMethod.toByteArray()));
        }

        //Issuer Application Data (IAD)
        ByteArrayOutputStream getIssuerApplicationData = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x10}, getIssuerApplicationData);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Issuer Application Data (IAD) Data Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getIssuerApplicationData.toByteArray()));
        }





        //get 031F Tag value for adding in 9F5B value



        //Issuer Script Result
        /*ByteArrayOutputStream getIssuerScriptResult = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x5B}, getIssuerScriptResult);
        cardData+= Tools.cleanByte(Tools.bcd2Str(getIssuerScriptResult.toByteArray()));*/



        ByteArrayOutputStream getIssuerValue = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte) 0x03,0x1F}, getIssuerValue);

        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS","Get Issuer Scripting Value Error");
        }
        else {

            String resValue = Tools.cleanByte(Tools.bcd2Str(getIssuerValue.toByteArray()));
            resValue = resValue.substring(4);

            if(!resValue.equalsIgnoreCase("00")){

                cardData+= "9F5B"+resValue;
            }


        }


       /* if (responseData != ResponseCode.EL_RET_OK) {
            Log.d("MPOS","Get Issuer Script Error");
        }
        else {
            cardData+= Tools.cleanByte(Tools.bcd2Str(getIssuerScriptResult.toByteArray()));
        }*/


        //Additional Terminal Capabilities
//        ByteArrayOutputStream getAdditionalTerminalCapabilities = new ByteArrayOutputStream();
//        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x40}, getAdditionalTerminalCapabilities);
//
//        if (responseData != ResponseCode.EL_RET_OK) {
//            Log.d("MPOS","Get Additional Terminal Capabilities Data Error");
//        }
//        else {
//            cardData+= Tools.cleanByte(Tools.bcd2Str(getAdditionalTerminalCapabilities.toByteArray()));
//        }

        //Card Holder Verification List
//        ByteArrayOutputStream getCardHolderVerificationList = new ByteArrayOutputStream();
//        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x8E}, getCardHolderVerificationList);
//
//        if (responseData != ResponseCode.EL_RET_OK) {
//            Log.d("MPOS","Get Additional Terminal Capabilities Data Error");
//        }
//        else {
//            cardData+= Tools.cleanByte(Tools.bcd2Str(getCardHolderVerificationList.toByteArray()));
//        }

        // Log.d("MPOS", "Transaction Get Data : " + cardData);
        return cardData;
    }

    // Function to get the terminal identification
    public static String TerminalIdentification (EasyLinkSdkManager manager){

        String terminalIdentificationData = "";
        Integer responseData;

        //Card Acceptor Terminal Identification
        ByteArrayOutputStream getTerminalIdentificationData = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x1C}, getTerminalIdentificationData);
        terminalIdentificationData = Tools.cleanByte(Tools.bcd2Str(getTerminalIdentificationData.toByteArray()));
        terminalIdentificationData = terminalIdentificationData.substring(6);
        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Terminal Identification Data Error";
        }

        return terminalIdentificationData;
    }

    // Data for Card Acceptor Identification
    public static String CardAcceptorIdentification (EasyLinkSdkManager manager){

        String cardAcceptorIdentificationData = "";
        Integer responseData;

        //Merchant Identifier
        ByteArrayOutputStream getCardAcceptorIdentificationData = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9F,0x16}, getCardAcceptorIdentificationData);
        cardAcceptorIdentificationData = Tools.cleanByte(Tools.bcd2Str(getCardAcceptorIdentificationData.toByteArray()));
        cardAcceptorIdentificationData = cardAcceptorIdentificationData.substring(6);
        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Card Acceptor Identification Data Error";
        }

        return cardAcceptorIdentificationData;
    }

    // Data for the TranctionType
    public static String TransactionType (EasyLinkSdkManager manager){

        String transactionTypeData = "";
        Integer responseData;

        //Processing Code
        ByteArrayOutputStream getTransactionTypeData = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9C}, getTransactionTypeData);
        transactionTypeData = Tools.cleanByte(Tools.bcd2Str(getTransactionTypeData.toByteArray()));
        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Transaction Type Data Error";
        }

        return transactionTypeData;
    }

    //get App Card Version

    public static String getAppVersionCode (EasyLinkSdkManager manager){

        String transactionTypeData = "";
        Integer responseData;

        //Processing Code
        ByteArrayOutputStream getTransactionTypeData = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA, new byte[]{(byte) 0x9F, 0x08}, getTransactionTypeData);
        transactionTypeData = Tools.cleanByte(Tools.bcd2Str(getTransactionTypeData.toByteArray()));
        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get App version Error";
        }

        return transactionTypeData;
    }




    // Data fo the transaction processing result
    public static String CardProcessingResult(EasyLinkSdkManager manager){
        String cardProcessingResult;
        //Get Card Processing Result
        ByteArrayOutputStream getCardProcessingResultTags = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA, new byte[]{(byte) 0x03, 0x1B}, getCardProcessingResultTags);
        cardProcessingResult = Tools.cleanByte(Tools.bcd2Str(getCardProcessingResultTags.toByteArray()));
        cardProcessingResult = cardProcessingResult.substring(6);
        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Card Processing Result Data Error";
        }
        return cardProcessingResult;
    }

    // Data to get the amount
    public static String getAuthorizedAmount(EasyLinkSdkManager manager){
        String authAmount;
        ByteArrayOutputStream getAuthAmount = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x9f,0x02},getAuthAmount);
        authAmount = Tools.cleanByte(Tools.bcd2Str(getAuthAmount.toByteArray()));

        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Authorization Amount Data Error";
        }
        return authAmount.substring(6);
    }
    // Local Transaction Time
    public static String getTransactionTime(EasyLinkSdkManager manager){
        String tranTime;
        ByteArrayOutputStream getTranTime = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x9f,0x21},getTranTime);
        tranTime = Tools.cleanByte(Tools.bcd2Str(getTranTime.toByteArray()));

        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Transaction Time Data Error";
        }
        return tranTime.substring(6);
    }

    //Card Sequence Number
  /*  public static String cardSequenceNumber(EasyLinkSdkManager manager){
        String CardSequenceNumber;
        ByteArrayOutputStream getCardSequenceNumber = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x9f,0x21},getCardSequenceNumber);
        CardSequenceNumber = Tools.cleanByte(Tools.bcd2Str(getCardSequenceNumber.toByteArray()));

        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Card Sequence Number Data Error";
        }
        return CardSequenceNumber.substring(6);
    }*/

    //Application Verson Number
    public static String AppVersionNumber(EasyLinkSdkManager manager){
        String AppVersionNumber;
        ByteArrayOutputStream getAppVersionNumber = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x9f,0x08},getAppVersionNumber);
        AppVersionNumber = Tools.cleanByte(Tools.bcd2Str(getAppVersionNumber.toByteArray()));
        AppVersionNumber = AppVersionNumber.substring(6);
        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Card Version Number Data Error";
        }
        return AppVersionNumber;
    }

    //Card AID(Application Identifier)
    public static String cardAID(EasyLinkSdkManager manager){
        String cardAID;
        ByteArrayOutputStream getcardAID = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x9f,0x06},getcardAID);


        if (responseData == ResponseCode.EL_RET_OK) {
            cardAID = Tools.cleanByte(Tools.bcd2Str(getcardAID.toByteArray()));
            cardAID = cardAID.substring(6);
            return cardAID;

        }else{
            return "Unknown CARD AID ";
        }

    }
    //Card AID(Application Identifier)
    public static String applName(EasyLinkSdkManager manager){
        String cardAID;
        ByteArrayOutputStream getcardAID = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte)0x9f,0x12},getcardAID);


        if (responseData == ResponseCode.EL_RET_OK) {
            cardAID = Tools.cleanByte(Tools.bcd2Str(getcardAID.toByteArray()));
            cardAID = cardAID.substring(6);
            return cardAID;

        }else{
            return "Unknown APPL NAME ";
        }

    }

    //Card AID(Application Identifier)
    public static String cardType(EasyLinkSdkManager manager){
        String cardAID;
        ByteArrayOutputStream getcardAID = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte)0x03, 0x01},getcardAID);


        if (responseData == ResponseCode.EL_RET_OK) {
            cardAID = Tools.cleanByte(Tools.bcd2Str(getcardAID.toByteArray()));
            cardAID = cardAID.substring(6);
            return cardAID;

        }else{
            return "Unknown APPL NAME ";
        }

    }

    //Track 2 Data
    public static String track2Data(EasyLinkSdkManager manager){
        String track2Data;
        ByteArrayOutputStream getTrack2Data = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x57},getTrack2Data);

        //    Log.d("MPOS","Decoded track 2 Data:" + bcd2Str(getTrack2Data.toByteArray()));
        track2Data = Tools.cleanByte(Tools.bcd2Str(getTrack2Data.toByteArray()));
        // Log.d("MPOS","Track 2 orginal Data : " + track2Data);
        track2Data = track2Data.substring(4);
        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Track 2 Data Error";
        }
        return track2Data;
    }

    //Track 1 Data
    public static String track1Data(EasyLinkSdkManager manager) {
        String track1Data;
        ByteArrayOutputStream a = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA, new byte[]{(byte) 0x03, 0x04}, a);
        track1Data = Tools.cleanByte(Tools.bcd2Str(a.toByteArray()));
        // Log.d("MPOS", "Track 2 orginal Data : " + track1Data);
        //track1Data = track1Data.substring(4);
        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS", "Get Data Error");
            return "Get Track 1 Data Error";
        }
        return track1Data;
    }



    // GET PAN No

    public static String PAN_NO(EasyLinkSdkManager manager) {
        String panno;
        ByteArrayOutputStream a = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA, new byte[]{(byte) 0x5A}, a);
        panno = Tools.cleanByte(Tools.bcd2Str(a.toByteArray()));
        // Log.d("MPOS", "Pan  Data : " + panno);
        if (responseData != ResponseCode.EL_RET_OK) {
            // Log.d("MPOS", "Get Data Error");
            return "Get Track 1 Data Error";
        }
        return panno;
    }




    /*
     ByteArrayOutputStream getTrack2Data = new ByteArrayOutputStream();
     Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x57},getTrack2Data);
     track1Data = Tools.cleanByte(Tools.bcd2Str(getTrack2Data.toByteArray()));
     Log.d("MPOS","Track 2 orginal Data : " + track1Data);
     track1Data = track1Data.substring(4);
     if (responseData != ResponseCode.EL_RET_OK) {
         return "Get Track 2 Data Error";
     }
     return track1Data;*/
    //}
    /* //Get card No
     public static String getPANNo(EasyLinkSdkManager manager){
         String panNo;
         ByteArrayOutputStream a = new ByteArrayOutputStream(); //

         Integer responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte) 0x03, 0x1C}, a);
         panNo = Tools.cleanByte(Tools.bcd2Str(a.toByteArray()));
         Log.d("MPOS","PAN no  : " + panNo);
         //track1Data = track1Data.substring(4);
         if (responseData != ResponseCode.EL_RET_OK) {
             Log.d("MPOS", "Get Data Error") ;
             return "Get PAN No Data Error";
         }
         return panNo;



     }*/
    public static String getPinBlock(EasyLinkSdkManager manager){
        String pinBlock="";
        ByteArrayOutputStream getpinBlock = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte)0x03, 0x16},getpinBlock);
        pinBlock = Tools.cleanByte(Tools.bcd2Str(getpinBlock.toByteArray()));
        // Log.d("MPOS","PIN BLOCK FULL DATA "+pinBlock);
        pinBlock = pinBlock.substring(6);
        // Log.d("MPOS","PIN BLOCK "+pinBlock);

        if(pinBlock.equalsIgnoreCase("")|| pinBlock.equalsIgnoreCase("null")){

            ByteArrayOutputStream pinBlockByte = new ByteArrayOutputStream();
            ByteArrayOutputStream ksn = new ByteArrayOutputStream();
            Integer pinBlock1 = manager.getPinBlock("", pinBlockByte, ksn);//6074841230000296
            pinBlock = Tools.bcd2Str(pinBlockByte.toByteArray());


            return pinBlock;
        }else{
            if (responseData != ResponseCode.EL_RET_OK) {
                return "Get Pin Block Data Error";
            }
            return pinBlock;
        }
    }




    public static String bcd2Str(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }


    public static Integer setEncryptionType(EasyLinkSdkManager manager){
        ByteArrayOutputStream failedTags = new ByteArrayOutputStream();
        byte[] configData = Tools.str2Bcd(PINEncryptionType + PINEncryptionKeyIdx + DataEncryptionType_No + DataEncryptionKeyIdx + TransactionProcessingMode + CardEntryMode + PINBlockMode + FallBackAllowFlag);
        int configRet = manager.setData(DataModel.DataType.CONFIGURATION_DATA,configData,failedTags);

        return configRet;
    }
    public static String trackData(EasyLinkSdkManager manager){

        setEncryptionType(manager);
        ByteArrayOutputStream getTrackData = new ByteArrayOutputStream();
        int responseData = manager.getData(DataModel.DataType.CONFIGURATION_DATA,new byte[]{(byte)0x03, 0x05},getTrackData);
        String track2Data = Tools.cleanByte(Tools.bcd2Str(getTrackData.toByteArray()));
        if (responseData == ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Track New Data : " + track2Data);
        }
        return track2Data.substring(6);

    }
    public static String DateTimeStamp (EasyLinkSdkManager manager){
        String dateTmStr = "";
        String tranTime ="";
        String tranDate = "";

        ByteArrayOutputStream getTranTime = new ByteArrayOutputStream();
        Integer responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte)0x9f,0x21},getTranTime);
        tranTime = Tools.cleanByte(Tools.bcd2Str(getTranTime.toByteArray()));

        if (responseData != ResponseCode.EL_RET_OK) {
            return "Get Transaction Time Data Error";
        }else {
            tranTime = tranTime.substring(6);
        }

        //Date

        ByteArrayOutputStream getTransactionDate = new ByteArrayOutputStream();
        responseData = manager.getData(DataModel.DataType.TRANSACTION_DATA,new byte[]{(byte) 0x9A}, getTransactionDate);
        tranDate = Tools.cleanByte(Tools.bcd2Str(getTransactionDate.toByteArray()));


        if (responseData != ResponseCode.EL_RET_OK) {
            //  Log.d("MPOS","Get Data Error");
        }
        else {
            tranDate = tranDate.substring(4);
        }

        // Log.d("MPOS","Date:::"+tranDate);
        //Log.d("MPOS","Date:::"+tranTime);

        if(!tranDate.equalsIgnoreCase("")||!tranTime.equalsIgnoreCase("")){

            try {
                String dtTime = tranDate+tranTime;
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
                Date date = (Date) sdf.parse(dtTime);

                SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateTmStr = format.format(date);
                //  Log.d("MPOS","Date:::"+dateTmStr);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dateTmStr;

    }


}
