package com.matm.matmsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.utils.Util;

import isumatm.androidsdk.equitas.R;

/*
 * For CustomThemes
 * @Author - RashmiRanjan
 * */
public class CustomThemes extends View {
    public static int color = 0xff3b5998;
    private static int sTheme;


    public CustomThemes(Context context) {
        super(context);

      //  Util.putTheme(context, SdkConstants.CustomTheme);
       //SdkConstants.CustomTheme = Util.getTheme(context);
        if (SdkConstants.CustomTheme.equals("THEME_YELLOW")){
            context.setTheme(R.style.YellowTheme);
        }else if (SdkConstants.CustomTheme.equals("THEME_BLUE")){
            context.setTheme(R.style.BlueTheme);
        }else if (SdkConstants.CustomTheme.equals("THEME_DARK")){
            context.setTheme(R.style.DarkTheme);
        }else if (SdkConstants.CustomTheme.equals("THEME_RED")){
            context.setTheme(R.style.RedTheme);
        }else if (SdkConstants.CustomTheme.equals("THEME_BROWN")){
            context.setTheme(R.style.BrownTheme);
        }else if (SdkConstants.CustomTheme.equals("THEME_GREEN")){
            context.setTheme(R.style.GreenTheme);
        }else if (SdkConstants.CustomTheme.equals("MediumSlateBlue")){
            context.setTheme(R.style.MediumSlateBlue);
        }else if (SdkConstants.CustomTheme.equals("Bluetiful")){
            context.setTheme(R.style.Bluetiful);
        }else if (SdkConstants.CustomTheme.equals("PermanentGeraniumLake")){
            context.setTheme(R.style.PermanentGeraniumlake);
        }else if (SdkConstants.CustomTheme.equals("VioletBlue")){
            context.setTheme(R.style.VioletBlue);
        }else {
            context.setTheme(R.style.AppTheme);
        }

    }

    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    public static void onActivityCreateSetTheme(AppCompatActivity activity)
    {
        if (SdkConstants.CustomTheme == "THEME_YELLOW"){
            activity.setTheme(R.style.YellowTheme);
        }else if (SdkConstants.CustomTheme == "THEME_BLUE"){
            activity.setTheme(R.style.BlueTheme);
        }else if (SdkConstants.CustomTheme == "THEME_DARK"){
            activity.setTheme(R.style.DarkTheme);
        }else if (SdkConstants.CustomTheme == "THEME_RED"){
            activity.setTheme(R.style.RedTheme);
        }else if (SdkConstants.CustomTheme == "THEME_BROWN"){
            activity.setTheme(R.style.BrownTheme);
        }else if (SdkConstants.CustomTheme == "THEME_GREEN"){
            activity.setTheme(R.style.GreenTheme);
        }else {
            activity.setTheme(R.style.AppTheme);
        }







        /*switch (sTheme)
        {
            default:
            case :
                activity.setTheme(R.style.AppTheme);
                break;
            case SdkConstants.THEME_DARK:
                activity.setTheme(R.style.DarkTheme);
                break;
            case SdkConstants.THEME_YELLOW:
                activity.setTheme(R.style.YellowTheme);
                break;
            case SdkConstants.THEME_BLUE:
                activity.setTheme(R.style.BlueTheme);
                break;
            case SdkConstants.THEME_RED:
                activity.setTheme(R.style.RedTheme);
                break;
            case SdkConstants.THEME_CUSTOM:
              //  activity.setTheme(theme);
                break;
            case SdkConstants.THEME_GREEN:
                activity.setTheme(R.style.GreenTheme);
                break;
            case SdkConstants.THEME_BROWN:
                activity.setTheme(R.style.BrownTheme);
                break;
            case SdkConstants.THEME_TEAL:
             //   activity.setTheme(R.style.TealTheme);
                break;
        }*/
    }

}

