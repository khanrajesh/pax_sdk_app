package com.matm.matmsdk.aepsmodule.utils;


import android.bluetooth.BluetoothDevice;

/**
 * This class represents the constants used in the app.
 *
 * @author Subhalaxmi Panda
 * @date 23/06/18.
 */
public class Constants {
    /**
     * Base Url of the server
     */
    public static final String BASE_URL = "https://itpl.iserveu.tech/";
    //    public static final String BASE_URL = "http://35.200.175.157:8080";
//    public static final String BASE_URL = "http://35.220.171.29:8080";
    //public static final String recharge = "https://iserveu.xyz/";
    public static final String recharge = "https://itpl.iserveu.tech/";

    public static final String BASE_URL_FUND_TRANSFER = "https://habizglobal.in/";
    public static final int REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE = 1003;
    public static final int REQUEST_FOR_ACTIVITY_CASH_DEPOSIT_CODE = 1001;
    public static final int REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE = 1002;
    public static final int BALANCE_RELOAD = 1004;
    public static final String IIN_KEY = "IIN_KEY";
    public static final String USB_DEVICE = "USB_DEVICE";
    public static final String AUTH2STATUS = "AUTH2STATUS";
    public static final String AUTH2DATE = "AUTH2DATE";

    public static boolean NOTIFICATION_RECEIVED = false;
    public static final String TRANSACTION_STATUS_KEY = "TRANSACTION_STATUS_KEY";
    public static final String MICRO_ATM_TRANSACTION_STATUS_KEY = "MICRO_ATM_TRANSACTION_STATUS_KEY";
    public static final String AEPS_2_TRANSACTION_STATUS_KEY = "AEPS_2_TRANSACTION_STATUS_KEY";
    public static final String TRANSACTION_STATUS_KEY_SDK = "TRANSACTION_STATUS_KEY_SDK";
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String USER_TOKEN_KEY = "USER_TOKEN_KEY";
    public static final String NEXT_FRESHNESS_FACTOR = "NEXT_FRESHNESS_FACTOR";
    public static final String EASY_AEPS_PREF_KEY = "EASY_AEPS_PREF_KEY";
    public static final String EASY_AEPS_USER_LOGGED_IN_KEY = "EASY_AEPS_USER_LOGGED_IN_KEY";
    public static final String EASY_AEPS_USER_NAME_KEY = "EASY_AEPS_USER_NAME_KEY";
    public static final String EASY_AEPS_PASSWORD_KEY = "EASY_AEPS_PASSWORD_KEY";
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
    //    public static final String CLIENT_REQUEST_ENCRYPTION_KEY = "5c542938-904a-49fa-b981-10203c7db1f1";
    public static final String CLIENT_HEADER_ENCRYPTION_KEY = "982b0d01-b262-4ece-a2a2-45be82212ba1";
    public static final String MERCHANT_ID = "8270629964";
    public static final String AUTHKEY = "333-444-555";
    public static final String CLIENTID = "8";
    public static final int RELOADREPORTS = 1005;
    public static String USER_NAME = "";
    public static String USERNAME = "";
    public static String ADMIN_NAME = "";

    public static String USER_SHOP_NAME = "";
    public static String USER_MOBILE_NO = "";

    public static String USER_VERIFICATION_STATUS = "unverified";

    public static String VERIFIED_USERID = "";
    public static String VERIFIED_USERPHONE = "";
    public static String VERIFIED_USER_MPIN = "";
    public static String VERIFIED_USER_PWD = "";
    public static String VERIFIED_USER_EMAIL = "";
    public static String VERIFIED_USER_VIDEO = "";
    public static String VERIFIED_USER_LOC = "";
    public static String VERIFIED_USER_LACT = "";
    public static String VERIFIED_USER_LONG = "";

    public static boolean USER_WALLET_2_ACTIVE_STATUS = false;
    public static boolean USER_WALLET_3_ACTIVE_STATUS = false;

    public static BluetoothDevice bluetoothDevice = null;
    public static String SHOP_NAME = "";
    public static String BRAND_NAME = "";
    public static String AADHAR_CARD = "";
    public static String BANK_NAME = "";

    public static String THE_FIREBASE_DATA = "";

    /** Added by Rashmi Ranjan*/
    /**
     * 30/Dec/2020
     */
//    public static String FETCH_TRANSACTION_DETAILS= "http://35.200.175.157:8080/COMPOSER/transactionEnquiry";

//    public static String FETCH_TRANSACTION_DETAILS = "https://aepscomposer-vn3k2k7q7q-uc.a.run.app/transactionEnquiry";
    public static String FETCH_TRANSACTION_DETAILS = "https://aepscomposer.iserveu.tech/transactionEnquiry";
    public static String URL_AADHAARPAY = "http://35.200.175.157:8080/COMPOSER/aeps2/aadhaarPay";
    public static String URL_CASHWITHDRAWAL = "http://35.200.175.157:8080/COMPOSER/aeps2/cashWithdrawal";
    public static String URL_MINISTATEMENT = "http://35.200.175.157:8080/COMPOSER/aeps2/miniStatement";
    public static String URL_BALANCENQUIRY = "http://35.200.175.157:8080/COMPOSER/aeps2/balanceEnquiry";


    public static String STAGGING_URL = "http://35.200.175.157:8080/COMPOSER/";
    //   public static String  LIVE_URL= "https://uat.iserveu.online/COMPOSERLIVE/";
//    public static String LIVE_URL = "https://aepscomposer-vn3k2k7q7q-uc.a.run.app/";
    public static String LIVE_URL = "https://aepscomposer.iserveu.tech/";
    public static boolean isDataLoadedForAadharPay = false;

    //[7:58 PM] Piyush Padhy
    //
    //https://aepscomposer.iserveu.tech/cashWithdrawal

    //[7:14 PM] Raj Kishore
    //
    //https://uat.iserveu.online/COMPOSERLIVE/


}
