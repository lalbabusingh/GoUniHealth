package com.health.gounihealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by LAL on 7/20/2016.
 */
public class AppSharedPreferences {

    public static void saveAccessToken(Context context, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ACCESS", value);
        editor.commit();
    }

    public static String getAccessToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getString("ACCESS", "");
    }

    public static void saveRefreshAccessToken(Context context, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("REFRESHACCESS", value);
        editor.commit();
    }

    public static String getRefreshAccessToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getString("REFRESHACCESS", "");
    }

    public static void saveLatitude(Context context,double value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("LATITUDE",(float) value);
        editor.commit();
    }

    public static double getLatitude(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getFloat("LATITUDE", 0);
    }

    public static void saveLongitude(Context context,double value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("LONGITUDE",(float) value);
        editor.commit();
    }

    public static double getLongitude(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getFloat("LONGITUDE", 0);
    }

    public static void saveProfileName(Context context,String name){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PROFILENAME",name);
        editor.commit();
    }

    public static String getProfileName(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getString("PROFILENAME","");
    }

    public static void saveProfileCityName(Context context,String name){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CITYNAME",name);
        editor.commit();
    }

    public static String getProfileCityName(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getString("CITYNAME","");
    }

    public static void setAppStatus(Context context,boolean isFirstTime){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("APPSTATUS",isFirstTime);
        editor.commit();
    }

    public static boolean getAppStatus(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean("APPSTATUS",false);
    }
}
