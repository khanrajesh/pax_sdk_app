package com.matm.matmsdk.Utils;


import com.matm.matmsdk.Model.Dashboard.UserProfile;

public class EnvData {
    public static String token = "";

     //public static final String TEST_URL =  "https://matm.iserveu.online";
     public static final String TEST_URL =  "https://matm.iserveu.tech";
//     public static final String TEST_URL =  "https://matmtest.iserveu.website";
      // public static final String TEST_URL =  "https://matmtest.iserveu.website";
     //public static final String TEST_URL =  "https://uatapps.iserveu.online/matm";
    // public static final String TEST_URL =  "https://matmtest.iserveu.website";



    public static String AdminName = "";
    public static String UserName = "";
    public static String UserBalance = "";
    public static String UserType = "";
    public String hexAmount = "";
    public static UserProfile userProfile;

    //Constant Data for MPOS
    public static String Transaction_Type = "9C0101";//"9C0105";
    public static String Transaction_Type_Enquery ="9C0131";//"9C0134";
    public static String Transaction_Currency_Code = "5F2A020356";
    public static String CurrencyExponent = "5F360102";
    public static String AmountOthers = "9F0306000000000000";
    public static String PINEncryptionType = "02020101";
    public static String DataEncryptionType_No = "02050100";
    public static String PINEncryptionKeyIdx = "02030120";
    public static String DataEncryptionType = "02050101";
    public static String DataEncryptionKeyIdx = "02060125";
    public static String PINBlockMode = "02040100";
    public static String TransactionProcessingMode = "02090101"; //02090100
    public static String CardEntryMode = "02140107";
    public static String FallBackAllowFlag = "02070101";
    public static String PosEntryMode = "9F39020051";
    public static String TerminalCapabilities = "9F3303E060C8";
    public static String RequestPinBlockAuto = "02150101";
    public static String OnlinePinInput = "03150101";

    public static String TerminalType = "9F3501";
    public static String AdditionalTerminalCapabilities = "9F4005";

   // public static String TransactionSequenceCounter ="9F410400000000";
    //public static String TRANSACTION_DATE = "9A03200723";
   // public  String TRANSACTION_TIME = "9A03200723";

    public static String user_id = "";


}
