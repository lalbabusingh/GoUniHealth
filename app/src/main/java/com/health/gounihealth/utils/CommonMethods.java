package com.health.gounihealth.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.health.gounihealth.R;
import com.health.gounihealth.activity.ICUEnquiry;
import com.health.gounihealth.activity.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by LAL on 6/2/2016.
 */
public class CommonMethods {

    public static boolean isConnected(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){

            return true;
        } else{
            showToastMessage(context,context.getString(R.string.noInternet));
            return false;
        }


    }

    public static Typeface getLatoHeavyFont(Context context){
           return Typeface.createFromAsset(context.getAssets(), "lato_heavy.TTF");
    }
    public static Typeface getLatoLightFont(Context context){
        return Typeface.createFromAsset(context.getAssets(), "lato_light.ttf");
    }
    public static void hideKeyboard(Context context) {
        // Check if no view has focus:
      //  Activity activity = (Activity) context;
        View view = ((Activity)context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Context context) {
        // Check if no view has focus:
        View view = ((Activity)context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void showToastMessage(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static double getLatFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        double dLat = 0;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if(address!=null){
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                dLat = location.getLatitude();
            }
         //   p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return dLat;
    }

    public static double getLongFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        double dLong = 0;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if(address!=null){
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                dLong = location.getLongitude();
            }
            //   p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return dLong;
    }

    public static String getCompleteAddress(Context context,double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction", "Canont get Address!");
        }
        return strAdd;
    }
    public static String loadJSONFromAsset(Context context,String name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(name);
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

    public static void routeDirection(Context context, double currentLatitude, double currentLongitude, double destLatitude, double destLongitude) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?   saddr=" + currentLatitude + ","
                    + currentLongitude + "&daddr=" + destLatitude + "," + destLongitude));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlertDialog(final Context context,String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AppSharedPreferences.saveAccessToken(context,"");
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static String getDateFormat(String strCurrentDate){
      //  String strCurrentDate = "Wed, 18 Apr 2012 07:55:29 +0000";
       // SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddT HH:mm:ss.SSSZ");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        String date = format.format(newDate);
        return date;
    }

    public static String formattedDateFromString(String inputFormat, String outputFormat, String inputDate){
        if(inputFormat.equals("")){ // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd hh:mm:ss";
        }
        if(outputFormat.equals("")){
            outputFormat = "EEEE d 'de' MMMM 'del' yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        // You can set a different Locale, This example set a locale of Country Mexico.
        //SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new Locale("es", "MX"));
        //SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }

    public static String getCurrentDateTime(Context context){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(AppConstants.CURRENT_DATE_FORMAT);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
