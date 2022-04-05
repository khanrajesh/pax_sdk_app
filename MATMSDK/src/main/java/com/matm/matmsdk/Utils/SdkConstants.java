package com.matm.matmsdk.Utils;


import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public class SdkConstants {

    //new constant add for onboarding
    public static String ADMIN_NAME = "";
    public static String CREATED_BY ="";

    /////////////////////////////////////////////
    public static final int REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE = 1003;
    public static final int REQUEST_FOR_ACTIVITY_CASH_DEPOSIT_CODE = 1001;
    public static final int REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE = 1002;
    public static final int REQUEST_FOR_ACTIVITY_AADHAARPAY_CODE = 1004;
    public static final int BALANCE_RELOAD = 1004;
    public static final String IIN_KEY = "IIN_KEY";
    public static final String TRANSACTION_STATUS_KEY = "TRANSACTION_STATUS_KEY";
    public static final String MICRO_ATM_TRANSACTION_STATUS_KEY = "MICRO_ATM_TRANSACTION_STATUS_KEY";
    public static final String TRANSACTION_STATUS_KEY_SDK = "TRANSACTION_STATUS_KEY_SDK";
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String USER_TOKEN_KEY = "USER_TOKEN_KEY";
    public static final String NEXT_FRESHNESS_FACTOR = "NEXT_FRESHNESS_FACTOR";
    public static final String EASY_AEPS_PREF_KEY = "EASY_AEPS_PREF_KEY";
    public static final String EASY_AEPS_USER_LOGGED_IN_KEY = "EASY_AEPS_USER_LOGGED_IN_KEY";
    public static final String EASY_AEPS_USER_NAME_KEY = "EASY_AEPS_USER_NAME_KEY";
    public static final String encryptedString = "encryptedString";

//    public static String Bluetoothname = "";
//    public static BluetoothDevice selected_btdevice = null;
//    public static BluetoothDevice selected_fingerPrint = null;
    public static final String devicename = "devicename";
    public static final String devicemac = "devicemac";
    public static final String BASE_URL = "https://mobile.9fin.co.in/";
    public static final String USER_PINCODE = "USER_PINCODE";
    public static final String USER_LATLONG = "USER_LATLONG";
    public static final String USER_CITY = "USER_CITY";
    public static final String USER_STATE = "USER_STATE";
    public static final String TEST_USER_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpdHBsIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTI5OTkyMDQxNTE1LCJleHAiOjE1MzAwMjgwNDF9.Se0zZhaoemx6eeAVlE_9N_LqkhylDgsJS8f1-5Y4uIYIfFV_7Gst5JhwnA-ogjs4whk9x9VnNvBL8T-AVkctnQ";
    public static final String RETURN_URL = "http://10.15.20.131:8023/PG/TestPage.aspx";
    public static final String VERSION = "1000";
    public static final String SERVICE_AEPS_TS = "AEPStransactionStatus";
    public static final String SERVICE_AEPS_CW = "AEPScashWithdrawal";
    public static final String SERVICE_AEPS_BE = "AEPSbalanceEnquiry";
    public static final String SERVICE_MICRO_TS = "MATMtransactionStatus";
    public static final String SERVICE_MICRO_CW = "MATMcashWithdrawal";
    public static final String SERVICE_MICRO_BE = "MATMbalanceEnquiry";
    public static final String CLIENT_REQUEST_ENCRYPTION_KEY = "11e9903b-7cde-4738-96fe-c8295c8232de";
    public static final String CLIENT_HEADER_ENCRYPTION_KEY = "982b0d01-b262-4ece-a2a2-45be82212ba1";
    public static final String MERCHANT_ID = "8270629964";
//    public static final String BASE_URL = "http://34.93.100.228:7070/";
//    public static final String BASE_URL = "https://wallet.iserveu.online/";
    //public static final String BASE_URL = "https://uatapps.iserveu.online/matm";
    public static final String AUTHKEY = "333-444-555";
    public static final String CLIENTID = "8";
    public static final int RELOADREPORTS = 1005;
    public static final String USB_DEVICE = "USB_DEVICE";
    public static final String WALLET2_TRANSACTION_API = "https://dmt.iserveu.tech/generate/v42";
    public static final String WALLET3_TRANSACTION_API = "https://dmt.iserveu.tech/generate/v83";
    public static final String BULK_TRANSFER = "https://dmt.iserveu.tech/generate/v5";
    public static final String BULK_TRANSFER_WALLET3 = "https://dmt.iserveu.tech/generate/v84";
    public static final String VERIFY_BENE_URL = "https://dmt.iserveu.tech/generate/v17";
    public static final String ADD_AND_PAY = "https://dmt.iserveu.tech/generate/v6";
    public static final String ADD_BENE_URL = "https://dmt.iserveu.tech/generate/v13";
    public static final String ADD_CUSTOMER = "https://dmt.iserveu.tech/generate/v14";
    public static final String DELETE_BENE_DMT = "https://dmt.iserveu.tech/generate/v25";
    public static final String RESEND_OTP = "https://dmt.iserveu.tech/generate/v19";
    public static final String TRANSACTION = "https://dmt.iserveu.tech/generate/v4";
    public static final String VERIFY_OTP = "https://dmt.iserveu.tech/generate/v18";
    public static final String GET_LOAD_WALLET_URL = "https://dmt.iserveu.tech/generate/v20";
    //Notification constants
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static String USER_NAME_NOTIFY = "";
    //New Constants Subhashree
    public static String AADHAAR_NUMBER = "";
    public static String MOBILENUMBER = "";
    public static String IIN_NUMBER = "";
    public static boolean isSL = false;

    //New Constants for Custom Themes Added by Rashmi_Ranjan
   /* private static int sTheme;
    public  static int THEME_DEFAULT = 0;
    public  static int THEME_DARK = 1;
    public  static int THEME_YELLOW = 2;
    public  static int THEME_BLUE = 3;
    public  static int THEME_RED = 4;
    public  static int THEME_CUSTOM = 5;
    public  static int THEME_GREEN = 6;
    public  static int THEME_BROWN = 7;
    public  static int THEME_TEAL = 8;*/
    //public static String CUSTOM_THEMES ="";
    public static String DRIVER_ACTIVITY = "";
    public static String NOTIFICATION_ACTIVITY = "";
    public static String MANUFACTURE_FLAG = "";
    public static String BRAND_NAME = "";
    public static BluetoothDevice bluetoothDevice = null;
    //    public static String USER_NAME_NOTIFY = "";
    public static String bankIIN = "";
    public static boolean bluetoothConnector = false;


    /////////////////////////////////////////////////////////////////
    /*AEPS SDK CONSTANTS*/
    public static String Bluetoothname = "";
    public static BluetoothDevice selected_btdevice = null;
    public static BluetoothDevice selected_fingerPrint = null;
    public static boolean from_RD, from_BT, from_PT = false;
    public static String transactionAmount = "0";
    public static String transactionType = "";
    public static String paramA = "";
    public static String paramB = "";
    public static String paramC = "";
    public static String encryptedData = "";
    public static String balanceEnquiry = "0";
    public static String cashWithdrawal = "1";
    public static String ministatement = "2";
    public static String adhaarPay = "3";
    public static String transactionResponse = "";
    public static String loginID = "";
    public static String responseData = "responseData";
    public static int MATM_REQUEST_CODE = 5;
    public static String FRESHNESS_FACTOR = "";
    public static String USER_NAME = "";


    //Added these new constats
    public static String USER_TOKEN = "";
    public static String tokenFromCoreApp = "";
    public static String userNameFromCoreApp = "";
    public static String applicationType = "";
    public static String LogOut = ""; //0 --For logout
    public static String BlueToothPairFlag = "1";   // 1--> for allow
    public static String Matm1BluetoothFlag = "0"; // 1--->pair Bluetooth
    public static String deviceSerialNo = "";
    public static String CustomTheme = "THEME";
    //on 4 July by manas
    //layout for bluetooth
    public static int bluetoothLayout = 0;
    public static int bluetoothItem = 0;
    //layout for pos and card
    public static int posLayout = 0;
    public static int cardLayout = 0;
    //layout for status
    public static int errorLayout = 0;


    //DMT Constants
    public static int statusLayout = 0;
    public static boolean bioauth = false;
    public static String applicationUserName = "";
    public static int REQUEST_CODE = 55;
    public static boolean transactionStatus = true;
    public static String RECEIVE_DRIVER_DATA = "";
    //on July 4 by manas
    //layout for aeps
    public static int dashboardLayout = 0;
    public static int bankList = 0;
    public static int bankItem = 0;
    //layout for bio auth
    public static int bioAuthLayout = 0;
    //layout for aeps status
    public static int aepsStatusLayout = 0;
    //layout for statement
    public static int statementLayout = 0;
    public static int statementItem = 0;
    //enable Response
    public static boolean showTrans = true;
    //edit amount
    public static boolean editable = false;
    public static String SHOP_NAME = "iServeU";
    public static String USER_MOBILE_NO = "";
    public static String selectBank = "";
    public static String BANK_NAME = "";
    public static ArrayList<String> user_feature_array = new ArrayList<>();
    public static String token = "";
    public static String DEVICE_TOKEN = "";
    public static String SERVER_KEY = "";
    public static String DEVICE_TOPIC = "";
    public static String COMPLETE_REGISTRATION = "";
    public static String PUSH_NOTIFICATION = "";

    public static boolean showNotification = false;

//    public static String AADHAAR_NUMBER = "";
//    public static String IIN_NUMBER = "";
//    public static String MOBILENUMBER = "";

//    public static boolean firstCheck = false; // this is the correct value is for terms and conditions but for now the featue is hidden
//    public static boolean secondCheck = false; //// this is the correct value is for terms and conditions but for now the featue is hidden


    public static boolean firstCheck = true;// This is hardcoded, to avoid the terms check
    public static boolean secondCheck = true;// This is hardcoded, to avoid the terms check

    public static String SLAB_X = "";
    public static String SLAB_Y = "";

    public static String DEVICE_TYPE = "";     //Added due to morefun and pax check
    public static boolean IS_BETA_USER=false;
    public static String DEVICE_NAME = "";
    public static String CUSTOMER_MOB = "";



}
