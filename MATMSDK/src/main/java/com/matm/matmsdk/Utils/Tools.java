package com.matm.matmsdk.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.paxsz.easylink.api.EasyLinkSdkManager;
import com.paxsz.easylink.model.KcvInfo;
import com.paxsz.easylink.model.KeyInfo;
import com.paxsz.easylink.model.KeyType;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.CRC32;


public class Tools {
    private static final String TAG = "Tools";
    private static final String UTF8 = "UTF-8";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private Tools() {

    }

    public static void delay(int milliSeconds) {
        SystemClock.sleep(milliSeconds);
    }

    public static byte[] string2Bytes(String source) {
        byte[] result = new byte[0];
        try {
            if (source != null)
                result = source.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            //ignore the exception
            Log.e(TAG, "", e);
        }
        return result;
    }

    public static byte[] string2Bytes(String source, int checkLen) {
        byte[] result = new byte[0];
        if (source == null || source.length() != checkLen)
            return result;
        try {
            result = source.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            //ignore the exception
            Log.e(TAG, "", e);
        }
        return result;
    }

    public static String bcd2Str(byte[] b) {
        if (b == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte i : b) {
            sb.append(HEX_DIGITS[((i & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(i & 0xF)]);
        }

        return sb.toString();
    }

    public static String bcd2Str(byte[] b, int length) {
        if (b == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; ++i) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }

        return sb.toString();
    }

    private static int strByte2Int(byte b) {
        int j;
        if ((b >= 'a') && (b <= 'z')) {
            j = b - 'a' + 0x0A;
        } else {
            if ((b >= 'A') && (b <= 'Z'))
                j = b - 'A' + 0x0A;
            else
                j = b - '0';
        }
        return j;
    }

    public static String getPaddedNumber(long num, int digit) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumIntegerDigits(digit);
        nf.setMinimumIntegerDigits(digit);
        return nf.format(num);
    }

    public static byte[] str2Bcd(String asc) {
        String str = asc;
        if (str.length() % 2 != 0) {
            str = "0" + str;
        }
        int len = str.length();
        if (len >= 2) {
            len /= 2;
        }
        byte[] bbt = new byte[len];
        byte[] abt = str.getBytes();

        for (int p = 0; p < str.length() / 2; p++) {
            bbt[p] = (byte) ((strByte2Int(abt[(2 * p)]) << 4) + strByte2Int(abt[(2 * p + 1)]));
        }
        return bbt;
    }

    public static byte[] int2ByteArray(int i) {
        byte[] to = new byte[4];
        int offset = 0;
        to[offset] = (byte) (i >>> 24 & 0xFF);
        to[(offset + 1)] = (byte) (i >>> 16 & 0xFF);
        to[(offset + 2)] = (byte) (i >>> 8 & 0xFF);
        to[(offset + 3)] = (byte) (i & 0xFF);
        for (int j = 0; j < to.length; ++j) {
            if (to[j] != 0) {
                return Arrays.copyOfRange(to, j, to.length);
            }
        }
        return new byte[]{0x00};
    }

    public static void int2ByteArray(int i, byte[] to, int offset) {
        to[offset] = (byte) (i >>> 24 & 0xFF);
        to[(offset + 1)] = (byte) (i >>> 16 & 0xFF);
        to[(offset + 2)] = (byte) (i >>> 8 & 0xFF);
        to[(offset + 3)] = (byte) (i & 0xFF);
    }

    public static void int2ByteArrayLittleEndian(int i, byte[] to, int offset) {
        to[offset] = (byte) (i & 0xFF);
        to[(offset + 1)] = (byte) (i >>> 8 & 0xFF);
        to[(offset + 2)] = (byte) (i >>> 16 & 0xFF);
        to[(offset + 3)] = (byte) (i >>> 24 & 0xFF);
    }

    public static void short2ByteArray(short s, byte[] to, int offset) {
        to[offset] = (byte) (s >>> 8 & 0xFF);
        to[(offset + 1)] = (byte) (s & 0xFF);
    }

    public static void short2ByteArrayLittleEndian(short s, byte[] to, int offset) {
        to[offset] = (byte) (s & 0xFF);
        to[(offset + 1)] = (byte) (s >>> 8 & 0xFF);
    }

    public static int byteArray2Int(byte[] from, int offset) {
        return from[offset] << 24 & 0xFF000000 | from[(offset + 1)] << 16 & 0xFF0000 |
                from[(offset + 2)] << 8 & 0xFF00 | from[(offset + 3)] & 0xFF;
    }

    public static int byteArray2IntLittleEndian(byte[] from, int offset) {
        return from[(offset + 3)] << 24 & 0xFF000000 | from[(offset + 2)] << 16 & 0xFF0000 |
                from[(offset + 1)] << 8 & 0xFF00 | from[offset] & 0xFF;
    }

    public static short byteArray2Short(byte[] from, int offset) {
        return (short) (from[offset] << 8 & 0xFF00 | from[(offset + 1)] & 0xFF);
    }

    public static short byteArray2ShortLittleEndian(byte[] from, int offset) {
        return (short) (from[(offset + 1)] << 8 & 0xFF00 | from[offset] & 0xFF);
    }

    public static long getCRC32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }

    public static int bytes2Int(byte[] buffer, int radix) {
        int result;
        try {
            result = Integer.parseInt(bytes2String(buffer), radix);
        } catch (NumberFormatException e) {
            //ignore the exception
            Log.e(TAG, "Exception:", e);
            result = 0;
        }
        return result;
    }

    public static String bytes2String(byte[] source) {
        String result = "";
        try {
            if (source.length > 0)
                result = new String(source, UTF8);
        } catch (UnsupportedEncodingException e) {
            //ignore the exception
            Log.e(TAG, "", e);
            result = "";
        }
        return result;
    }

    public static int bytes2Int(byte[] buffer) {
        int result = 0;
        int len = buffer.length;

        if ((len <= 0) || (len > 4)) {
            return 0;
        }
        for (int i = 0; i < len; i++) {
            result += (byte2Int(buffer[i]) << 8 * (len - 1 - i));
        }

        return result;
    }

    public static int byte2Int(byte b) {
        return b & 0xFF;
    }

    public static <T extends Enum<T>> T getEnum(Class<T> clazz, int ordinal) {
        for (T t : clazz.getEnumConstants()) {
            if (t.ordinal() == ordinal) {
                return t;
            }
        }
        return null;
    }

    public static byte[] fillData(int dataLength, byte[] source, int offset) {
        byte[] result = new byte[dataLength];
        if (offset >= 0)
            System.arraycopy(source, 0, result, offset, source.length);
        return result;
    }

    public static byte[] fillData(int dataLength, byte[] source, int offset, byte fillByte) {
        byte[] result = new byte[dataLength];
        for (int i = 0; i < dataLength; i++) {
            result[i] = fillByte;
        }
        if (offset >= 0)
            System.arraycopy(source, 0, result, offset, source.length);
        return result;
    }

    public static boolean byte2Boolean(byte b) {
        return b != 0;
    }

    public static byte boolean2Byte(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    public static byte[] trimTailChars(byte[] data, byte removeChar){
        int len = data.length;
        int removeCnt = 0;
        int i = 0;

        for(i = len - 1; i >= 0; i--){
            if(data[i] != removeChar){
                break;
            }
            removeCnt++;
        }

        if(data[len - 1] == removeChar){
            return Arrays.copyOfRange(data ,0, len - removeCnt );
        }
        return data;
    }



    // Code By Rajesh
    public static String decimalToHexaAmount(String amount, int length){
        String hexaAmt = amount;

        String finalAmount = "";
        for(int i= hexaAmt.length(); i<length; i++){
            finalAmount+="0";
        }
        finalAmount+=hexaAmt;
        return "9f0206" + finalAmount + "00";
    }


    public static String decimalToHexaAmountReversal(String amount, int length){
        String hexaAmt = amount;

        String finalAmount = "";
        for(int i= hexaAmt.length(); i<length; i++){
            finalAmount+="0";
        }
        finalAmount+=hexaAmt;
        return finalAmount + "00";
    }

    public static String decimalToHexaDate(String Date){
        String hexaDate = Date;
        String finalDate = "";
        for(int i= hexaDate.length(); i<4; i++){
            finalDate+="0";
        }
        finalDate+=hexaDate;
        return "9A03" + finalDate;
    }

    public static String decimalToHexaTime(String Time){
        String hexaTime = Time;
        String finalDate = "";
        for(int i= hexaTime.length(); i<4; i++){
            finalDate+="0";
        }
        finalDate+=hexaTime;
        return "9f2103" + finalDate;
    }

    public static String finalTranData(String amount,String stan,String dateStr,String timeStr){

        if(dateStr.isEmpty()){
            SimpleDateFormat format= new SimpleDateFormat("yyMMdd");
            String TRANSACTION_DATE =format.format(new Date());
            SimpleDateFormat timeformat= new SimpleDateFormat("HHmmss");
            String TRANSACTION_TIME =timeformat.format(new Date());
            String hexaDate = Tools.decimalToHexaDate(TRANSACTION_DATE);
            String hexaTime = Tools.decimalToHexaTime(TRANSACTION_TIME);
            return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type + hexaDate + hexaTime +amount+"9F4104"+"00"+stan;

        }else{
            String hexaDate = Tools.decimalToHexaDate(dateStr);
            String hexaTime = Tools.decimalToHexaTime(timeStr);

            return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type + hexaDate + hexaTime +amount+"9F4104"+"00"+stan;

        }

    }

    public static String finalBalanceTranData(String amount,String stan,String dateStr,String timeStr){

        if(dateStr.isEmpty()){
            SimpleDateFormat format= new SimpleDateFormat("yyMMdd");
            String TRANSACTION_DATE =format.format(new Date());

            SimpleDateFormat timeformat= new SimpleDateFormat("HHmmss");
            String TRANSACTION_TIME =timeformat.format(new Date());
            String hexaDate = Tools.decimalToHexaDate(TRANSACTION_DATE);
            String hexaTime = Tools.decimalToHexaTime(TRANSACTION_TIME);

            if(amount.equalsIgnoreCase("000000000000")){
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type_Enquery + hexaDate + hexaTime  + "9F0206" + amount+"9F4104"+"00"+stan;

            }else{
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type+ hexaDate + hexaTime + "9F0206" + amount+"9F4104"+"00"+stan;

            }
        }else{

            String hexaDate = Tools.decimalToHexaDate(dateStr);
            String hexaTime = Tools.decimalToHexaTime(timeStr);
            if(amount.equalsIgnoreCase("000000000000")){
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type_Enquery + hexaDate + hexaTime  + "9f0206" + amount+"9F4104"+"00"+stan;

            }else{
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type+ hexaDate + hexaTime + "9f0206" + amount+"9F4104"+"00"+stan;

            }

        }



    }




    // without Stan
    public static String finalTranData(String amount,String dateStr,String timeStr){

        if(dateStr.isEmpty()){
            SimpleDateFormat format= new SimpleDateFormat("yyMMdd");
            String TRANSACTION_DATE =format.format(new Date());
            SimpleDateFormat timeformat= new SimpleDateFormat("HHmmss");
            String TRANSACTION_TIME =timeformat.format(new Date());
            String hexaDate = Tools.decimalToHexaDate(TRANSACTION_DATE);
            String hexaTime = Tools.decimalToHexaTime(TRANSACTION_TIME);
            return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type + hexaDate + hexaTime +amount;

        }else{
            String hexaDate = Tools.decimalToHexaDate(dateStr);
            String hexaTime = Tools.decimalToHexaTime(timeStr);

            return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type + hexaDate + hexaTime +amount;

        }

    }
    // without stan
    public static String finalBalanceTranData(String amount,String dateStr,String timeStr){

        if(dateStr.isEmpty()){
            SimpleDateFormat format= new SimpleDateFormat("yyMMdd");
            String TRANSACTION_DATE =format.format(new Date());

            SimpleDateFormat timeformat= new SimpleDateFormat("HHmmss");
            String TRANSACTION_TIME =timeformat.format(new Date());
            String hexaDate = Tools.decimalToHexaDate(TRANSACTION_DATE);
            String hexaTime = Tools.decimalToHexaTime(TRANSACTION_TIME);

            if(amount.equalsIgnoreCase("000000000000")){
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type_Enquery + hexaDate + hexaTime  + "9F0206" + amount;

            }else{
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type+ hexaDate + hexaTime + "9F0206" + amount;

            }
        }else{

            String hexaDate = Tools.decimalToHexaDate(dateStr);
            String hexaTime = Tools.decimalToHexaTime(timeStr);
            if(amount.equalsIgnoreCase("000000000000")){
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type_Enquery + hexaDate + hexaTime  + "9f0206" + amount;

            }else{
                return EnvData.Transaction_Currency_Code + EnvData.CurrencyExponent + EnvData.Transaction_Type+ hexaDate + hexaTime + "9f0206" + amount;

            }

        }



    }


    public static String cleanByte(String byteArray){
        if(byteArray.length()>4){
            return byteArray.substring(4);
        }else{
            return "0";
        }
    }
    //--------------------------------------------
    // For TMK
    public static int testWriteKey_TMK(EasyLinkSdkManager easyLinkSdkManager) {
        String s = "380D4A75FEDF5BC1B3799DF2583876BA";
        byte[] tmk = new BigInteger(s,16).toByteArray();
        KeyInfo keyInfo = new KeyInfo();
        keyInfo.setSrcKeyIndex((byte) 0);
        keyInfo.setSrcKeyType(KeyType.PED_NONE);
        keyInfo.setDstKeyIndex((byte) 30);
        keyInfo.setDstKeyLen((byte) tmk.length);
        keyInfo.setDstKeyType((byte) KeyType.PED_TMK);
        keyInfo.setDstKeyValue(tmk);
        KcvInfo kcvInfo = new KcvInfo();
        kcvInfo.setCheckMode((byte) 0);
        int ret = easyLinkSdkManager.writeKey(keyInfo, kcvInfo, 60);
        // Log.d("MPOS","testWriteKey_TMK = " + ret);
        return ret;
    }
    // For TPK
    public static int testWriteKey_EncryptTPK(EasyLinkSdkManager easyLinkSdkManager, String tpkString) {
        //String s="A728593976AFB580770E2F3D1C84D951"; // actual key
        // String s = "53386A7CDD5813FDFB9E63A0536F1782";
        String s = tpkString;
        byte[] tpk = new BigInteger(s,16).toByteArray();
        if(tpk.length > 16){
            tpk = Arrays.copyOfRange(tpk, 1, tpk.length);
        }
        KeyInfo keyInfo = new KeyInfo();
        keyInfo.setSrcKeyIndex((byte) 30);
        keyInfo.setSrcKeyType(KeyType.PED_TMK);
        keyInfo.setDstKeyIndex((byte) 32);
        keyInfo.setDstKeyLen((byte) tpk.length);
        keyInfo.setDstKeyType(KeyType.PED_TPK);
        keyInfo.setDstKeyValue(tpk);
        KcvInfo kcvInfo = new KcvInfo();
        kcvInfo.setCheckMode((byte) 0);
        int ret = easyLinkSdkManager.writeKey(keyInfo, kcvInfo, 60);
        // Log.d("MPOS","testWriteKey_EncryptTPK = " + ret);
        return ret;
    }
    // For TDK
    public static int testWriteKey_EncryptTDK(EasyLinkSdkManager easyLinkSdkManager, String tdkString) {
        //String s="D9B4EEB467986711FA94F8575D839643"; // actual key
        // String s = "22616E2D79C753B9D0137D5A56F70C3E";
        String s = tdkString;
        byte[] tdk = new BigInteger(s,16).toByteArray();
        if(tdk.length>16){
            tdk = Arrays.copyOfRange(tdk, 1, tdk.length);
        }
        KeyInfo keyInfo = new KeyInfo();
        keyInfo.setSrcKeyIndex((byte) 30);
        keyInfo.setSrcKeyType((byte) KeyType.PED_TMK);
        keyInfo.setDstKeyIndex((byte) 37);
        keyInfo.setDstKeyLen((byte) tdk.length);
        keyInfo.setDstKeyType((byte) KeyType.PED_TDK);
        keyInfo.setDstKeyValue(tdk);
        KcvInfo kcvInfo = new KcvInfo();
        kcvInfo.setCheckMode((byte) 0);
        int ret = easyLinkSdkManager.writeKey(keyInfo, kcvInfo, 60);
        // Log.d("MPOS","testWriteKey_EncryptTDK = " + ret);
        return ret;
    }

    public static void saveBluetoothInstance(Context context, String currentName, String currentMac){
        SharedPreferences pref = context.getSharedPreferences("AuthData", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("DEVICE_NAME",currentName);
        editor.putString("DEVICE_MAC",currentMac);
        editor.commit();

    }


    public static void removeBluetoothInstance(){

    }
}

