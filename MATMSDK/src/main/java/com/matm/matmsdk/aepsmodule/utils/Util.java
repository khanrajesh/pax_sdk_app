package com.matm.matmsdk.aepsmodule.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;


import isumatm.androidsdk.equitas.R;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Util {
    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /*
    * @aUTHOR - RASHMIrANJAN
    * THIS SHAREDPREFERENCE TO SAVE THEMECOLOR */
public static void putTheme(Context ctx, String colorTheme) {
    SharedPreferences settings = ctx.getSharedPreferences("PREFS_NAME",0);
    SharedPreferences.Editor ed = settings.edit();
    ed.clear();
    ed.putString("Theme",colorTheme);
    ed.commit();
}

/*aDDED BY RAshmi Ranjan
* @return - ThemeCOlor*/
    public static String getTheme(Context context){
        SharedPreferences settings= context.getSharedPreferences("PREFS_NAME",0);
        String ThemeColor = settings.getString("Theme","DEFAULT");
        String tColor=ThemeColor;
        return tColor;
    }


    public static String getCurrentDate(){
        Date todayDate = new Date(  );
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime ( todayDate );
        String todayString = formatter.format(calendar.getTime ());
        return todayString;
    }
    public static String compareDates(String fromdate, String toDate){
        String status = "0";
        Date fromDat = convertStrToDate(fromdate);

        Date toDat = convertStrToDate(toDate);

       /* if (fromDat.after() || toDat.after( Util.getDateFromTime ())){
            msg = "From or To Date can not be  greater than Today";

        }else*/
        if(toDat.after(fromDat)){
            Log.v("subhalaxmi","Date1 is after Date2");
            status = "1";

        }
        else if(toDat.before(fromDat)){
            Log.v("subhalaxmi","Date1 is before Date2");
            status = "2";
        }
        else if(toDat.equals(fromDat)){
            status = "3";

        }
        return status;
    }
    /**
     * Get a diff between two dates
     *
     * @param oldDate the old date
     * @param newDate the new date
     * @return the diff value, in the days
     */
    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static boolean validateAadharVID(String aadharNumber){
        Pattern aadharPattern = Pattern.compile("\\d{16}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if(isValidAadhar){
            isValidAadhar = Verhoff.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }
    public static String getNextDate(String toDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, +1);

        String nextDate = dateFormat.format(calendar.getTime());
        return nextDate;
    }

    public static Date convertStrToDate(String date)  {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        Date d = null;
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        return d;


    }
    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("iinlist.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public static String getDateFromTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(time);

    }
    public static boolean validateAadharNumber(String aadharNumber){
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if(isValidAadhar){
            isValidAadhar = Verhoff.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }

    public static boolean isValidMobile(String ph){
        boolean check = false;
        if (ph.contains("+")) {
            ph = ph.replace("+", "");
        }
        if (ph.contains(" ")) {
            ph = ph.replace(" ", "");
        }
        if (ph.length() >= 10 && ph.length() <= 12) {
            check = android.util.Patterns.PHONE.matcher(ph).matches();
        }
        return check;



    }
    public static void showAlert(Context context, String title, String message){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getResources().getString(R.string.ok_error), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();

                    }
                })
                .show();
    }

    public static class PullTasksThread extends Thread {
        public void run () {
        }
    }

    public static String getSha256Hash(String password) {
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(password.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }


    private static String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }

    public static String getShortState(String state){
        try {
            if(state.equalsIgnoreCase("Andhra Pradesh") || state.equalsIgnoreCase("AP")){
                return "AP";
            } else if(state.equalsIgnoreCase("Arunachal Pradesh") || state.equalsIgnoreCase("AR")){
                return "AR";
            } else if(state.equalsIgnoreCase("Assam") || state.equalsIgnoreCase("AS")){
                return "AS";
            } else if(state.equalsIgnoreCase("Bihar")|| state.equalsIgnoreCase("BR")){
                return "BR";
            }else if(state.equalsIgnoreCase("Chandigarh") || state.equalsIgnoreCase("CH")){
                return "CH";
            } else if(state.equalsIgnoreCase("Chhattisgarh") || state.equalsIgnoreCase("CG")){
                return "CG";
            } else if(state.equalsIgnoreCase("Dadra and Nagar Haveli") || state.equalsIgnoreCase("DN")){
                return "DN";
            }else if(state.equalsIgnoreCase("Daman & Diu")|| state.equalsIgnoreCase("DD")){
                return "DD";
            } else if(state.equalsIgnoreCase("Delhi")|| state.equalsIgnoreCase("DL")){
                return "DL";
            }else if(state.equalsIgnoreCase("Goa")|| state.equalsIgnoreCase("GA")){
                return "GA";
            }else if(state.equalsIgnoreCase("Gujarat")|| state.equalsIgnoreCase("GJ")){
                return "GJ";
            }else if(state.equalsIgnoreCase("Haryana")|| state.equalsIgnoreCase("HR")){
                return "HR";
            }else if(state.equalsIgnoreCase("Himachal Pradesh")|| state.equalsIgnoreCase("HP")){
                return "HP";
            }else if(state.equalsIgnoreCase("Jammu & Kashmir")|| state.equalsIgnoreCase("JK")){
                return "JK";
            }else if(state.equalsIgnoreCase("Karnataka")|| state.equalsIgnoreCase("KA")){
                return "KA";
            }else if(state.equalsIgnoreCase("Kerala")|| state.equalsIgnoreCase("KL")){
                return "KL";
            }else if(state.equalsIgnoreCase("Lakshadweep")|| state.equalsIgnoreCase("LD")){
                return "LD";
            }else if(state.equalsIgnoreCase("Madhya Pradesh")|| state.equalsIgnoreCase("MP")){
                return "MP";
            }else if(state.equalsIgnoreCase("Maharashtra")|| state.equalsIgnoreCase("MH")){
                return "MH";
            }else if(state.equalsIgnoreCase("Manipur")|| state.equalsIgnoreCase("MN")){
                return "MN";
            }else if(state.equalsIgnoreCase("Meghalaya") || state.equalsIgnoreCase("ML")){
                return "ML";
            }else if(state.equalsIgnoreCase("Mizoram")|| state.equalsIgnoreCase("MZ")){
                return "MZ";
            }else if(state.equalsIgnoreCase("Nagaland")|| state.equalsIgnoreCase("NL")){
                return "NL";
            }else if(state.equalsIgnoreCase("Orissa")|| state.equalsIgnoreCase("OR")){
                return "OR";
            }else if(state.equalsIgnoreCase("Odisha")|| state.equalsIgnoreCase("OR")){
                return "OR";
            }else if(state.equalsIgnoreCase("Puducherry")|| state.equalsIgnoreCase("PY")){
                return "PY";
            }else if(state.equalsIgnoreCase("Punjab")|| state.equalsIgnoreCase("PB")){
                return "PB";
            }else if(state.equalsIgnoreCase("Rajasthan")|| state.equalsIgnoreCase("RJ")){
                return "RJ";
            }else if(state.equalsIgnoreCase("Sikkim")|| state.equalsIgnoreCase("SK")){
                return "SK";
            }else if(state.equalsIgnoreCase("Tamil Nadu")|| state.equalsIgnoreCase("TN")){
                return "TN";
            }else if(state.equalsIgnoreCase("Telangana")|| state.equalsIgnoreCase("TG")){
                return "TG";
            }else if(state.equalsIgnoreCase("Tripura")|| state.equalsIgnoreCase("TR")){
                return "TR";
            }else if(state.equalsIgnoreCase("Uttar Pradesh")|| state.equalsIgnoreCase("UP")){
                return "UP";
            }else if(state.equalsIgnoreCase("Uttarakhand (Uttranchal)")|| state.equalsIgnoreCase("UK")){
                return "UK";
            }else if(state.equalsIgnoreCase("West Bengal")|| state.equalsIgnoreCase("WB")){
                return "WB";
            }else{
                return "BR";
            }
        }catch (Exception e){
            return "BR";
        }
    }


}
