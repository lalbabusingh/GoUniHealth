package com.health.gounihealth.JsonCalls;

import android.content.Context;
import android.util.Log;

import com.health.gounihealth.utils.AppSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by LAL on 6/2/2016.
 */
public class JsonData {

    /**
     * Handle server connectivity, send request and get response from server.
     *
     * @param context   Get the application context
     * @param serverUrl This is the login server url
     * @return response Return json data result as String
     */
    public static String performLoginPostCall(Context context, String serverUrl, String userName, String password) {
        URL url;
        String response = "";
        HttpURLConnection connection = null;

        //ConnectionDetector connectionDetector = new ConnectionDetector(context);

        // if(connectionDetector.isConnectingToInternet()) {
        try {
            url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(40000);
            connection.setConnectTimeout(40000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            //connection.setRequestProperty("Host", "android.schoolportal.gr");
            connection.connect();//  wr.writeBytes(otherParametersUrServiceNeed);
            System.setProperty("http.keepAlive", "false");
               /* OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));*/

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("userId", userName);
            jsonParam.put("password", password);

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(jsonParam.toString());

            //  writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            //  os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
       /* }else{
           // throw new NetworkException(NetworkError.NO_NETWORK);
        }*/
        return response;
    }

    public static String performUserSignUpPostCall(Context context, String serverUrl, JSONObject inputJson) {
        String line;
        StringBuffer jsonString = new StringBuffer();
        HttpURLConnection connection = null;
        try {

            URL url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStream os = connection.getOutputStream();
            os.write(inputJson.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode(); //can call this instead of con.connect()
            if (responseCode >= 400 && responseCode <= 499) {
                throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
            }
            //  response.setResponseMessage(connection.getResponseMessage());
            //  response.setResponseReturnCode(connection.getResponseCode());
            //  br.close();
            // connection.disconnect();
        } catch (Exception e) {
            Log.w("Exception ", e);
            return jsonString.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        String json = jsonString.toString();
        //   response.setResponseJsonString(json);
        return jsonString.toString();
    }

    public static boolean performLogOutPostCall(Context context, String serverUrl) {

        boolean isOk = false;
        URL url;
        //String response = "";
        HttpURLConnection connection = null;

        //ConnectionDetector connectionDetector = new ConnectionDetector(context);

        // if(connectionDetector.isConnectingToInternet()) {
        try {
            url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(40000);
            connection.setConnectTimeout(40000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
            connection.connect();//  wr.writeBytes(otherParametersUrServiceNeed);
            // System.setProperty("http.keepAlive", "false");
               /* OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));*/

           /* JSONObject jsonParam = new JSONObject();
            jsonParam.put("lat", latitude);
            jsonParam.put("lng", longitude);*/

        /*    DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(inputJson);

            //  writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();*/
            //  os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                isOk = true;
                /*String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }*/
            } else {
                isOk = false;
                //  response = "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
       /* }else{
           // throw new NetworkException(NetworkError.NO_NETWORK);
        }*/
        return isOk;
    }

    //Post call
    public static String performUserPanicPostCall(Context context, String serverUrl, JSONObject inputJson) {
        String line;
        StringBuffer jsonString = new StringBuffer();
        try {

            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
            OutputStream os = connection.getOutputStream();
            os.write(inputJson.toString().getBytes("UTF-8"));
            os.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            //  response.setResponseMessage(connection.getResponseMessage());
            //  response.setResponseReturnCode(connection.getResponseCode());
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            Log.w("Exception ", e);
            return jsonString.toString();
        }
        String json = jsonString.toString();
        //   response.setResponseJsonString(json);
        return jsonString.toString();
    }


    public static String getCityList1(Context context, String serverUrl) {
        URL url;
        String response = "";
        HttpURLConnection connection = null;

        //ConnectionDetector connectionDetector = new ConnectionDetector(context);

        // if(connectionDetector.isConnectingToInternet()) {
        try {
            url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(40000);
            connection.setConnectTimeout(40000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();//  wr.writeBytes(otherParametersUrServiceNeed);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            // writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            //  os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
       /* }else{
           // throw new NetworkException(NetworkError.NO_NETWORK);
        }*/
        return response;
    }

    // HTTP GET request
    public static String getCityList(Context context, String serverUrl) throws Exception {

        StringBuffer response;

        URL obj = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getBloodGroupList(Context context, String serverUrl) throws Exception {

        StringBuffer response;

        URL obj = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getICUTypeList(Context context, String serverUrl) throws Exception {

        StringBuffer response;

        URL obj = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        System.out.println("Access token : " + AppSharedPreferences.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getICUList(Context context, String serverUrl, Double lat, Double longitude, String icuTypt) throws Exception {

        StringBuffer response;

        String url = serverUrl + "?lat=" + lat + "&lng=" + longitude + "&start=0&type=" + icuTypt;

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getHospitalList(Context context, String serverUrl, Double lat, Double longitude) throws Exception {

        StringBuffer response;

        String url = serverUrl + "?lat=" + lat + "&lng=" + longitude + "&start=0";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getBloodBanksList(Context context, String serverUrl, Double lat, Double longitude) throws Exception {

        StringBuffer response;

        String url = serverUrl + "?lat=" + lat + "&lng=" + longitude + "&start=0";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getNearByHospitalList(Context context, String serverUrl, Double lat, Double longitude) throws Exception {
        StringBuffer response;
        String url = serverUrl + "?lat=" + lat + "&lng=" + longitude + "&start=0";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");
        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    // HTTP GET request
    public static String getMoreHospitalICUList(Context context, String serverUrl, String hospitalId) throws Exception {
        StringBuffer response;
        String url = serverUrl + "/" + hospitalId;
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");
        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    // HTTP GET request
    public static String getNearByPharmacyList(Context context, String serverUrl, double latitude, double longitude) throws Exception {
        StringBuffer response;
        String url = serverUrl + "?lat=" + latitude + "&lng=" + longitude + "&start=0";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");
        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    // HTTP GET request
    public static String getPanicStatus(Context context, String serverUrl, String panicId) throws Exception {
        StringBuffer response;
        String url = serverUrl + "?id=" + panicId + "&start=0";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static boolean getPanicCall(Context context, String serverUrl)throws Exception {

        boolean isOk = false;
        URL obj = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            isOk = true;

        } else {
            isOk = false;
        }

       /* }else{
           // throw new NetworkException(NetworkError.NO_NETWORK);
        }*/
        return isOk;
    }

    // HTTP GET request
    public static String getMedicalRecordCreated(Context context, String serverUrl, String incidentType) throws Exception {

        StringBuffer response;
        String url = serverUrl + "?type=" + incidentType;
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+AppSharedPreferences.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    //POST
    public static String medicalRecordCreationPostCall(Context context, String serverUrl, JSONObject inputJson) {
        String line;
        StringBuffer jsonString = new StringBuffer();
        HttpURLConnection connection = null;
        try {

            URL url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
            OutputStream os = connection.getOutputStream();
            os.write(inputJson.toString().getBytes("UTF-8"));
            os.close();


            int responseCode = connection.getResponseCode(); //can call this instead of con.connect()
            if (responseCode >= 400 && responseCode <= 499) {
                throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
                br.close();
            }
            //  response.setResponseMessage(connection.getResponseMessage());
            //  response.setResponseReturnCode(connection.getResponseCode());

            // connection.disconnect();
        } catch (Exception e) {
            return jsonString.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jsonString.toString();
    }

    // HTTP GET request
    public static String getGlobalSearchList(Context context, String serverUrl, String searchKey, String searchType) throws Exception {

        StringBuffer response;
        String url = serverUrl + "?key="+searchKey+"&type="+searchType+"&start=0&pageSize=20";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }
    //POST
    public static String deleteMedicalRecordPostCall(Context context, String serverUrl, JSONObject inputJson) {
        String line;
        StringBuffer jsonString = new StringBuffer();
        try {

            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
            OutputStream os = connection.getOutputStream();
            os.write(inputJson.toString().getBytes("UTF-8"));
            os.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            //  response.setResponseMessage(connection.getResponseMessage());
            //  response.setResponseReturnCode(connection.getResponseCode());
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            return jsonString.toString();
        }
        return jsonString.toString();
    }

    // HTTP GET request
    public static String getMyProfileEdit(Context context, String serverUrl) throws Exception {

        StringBuffer response;
        URL obj = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");
        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        System.out.println("Access token : " + AppSharedPreferences.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }
    //POST
    public static String updateMyProfilePostCall(Context context, String serverUrl, JSONObject inputJson) {
        String line;
        StringBuffer jsonString = new StringBuffer();
        try {

            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
            OutputStream os = connection.getOutputStream();
            os.write(inputJson.toString().getBytes("UTF-8"));
            os.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            //  response.setResponseMessage(connection.getResponseMessage());
            //  response.setResponseReturnCode(connection.getResponseCode());
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            return jsonString.toString();
        }
        return jsonString.toString();
    }

    // HTTP GET request
    public static String getVerifyOTP(Context context, String serverUrl, String otpId, String userId, String otp) throws Exception {

        StringBuffer response;

        String url = serverUrl + "?id=" + otpId + "&userId=" + userId + "&start=0&otp=" + otp;

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        // connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getResendOTP(Context context, String serverUrl, String otpId, String userId) throws Exception {

        StringBuffer response;

        String url = serverUrl + "?id=" + otpId + "&userId=" + userId;

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // con.setRequestProperty("User-Agent", USER_AGENT);
        // connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    // HTTP GET request
    public static String getMedicalFolderId(Context context, String serverUrl, String folderId) throws Exception {

        StringBuffer response;

        String url = serverUrl + "/" + folderId + "?start=0&pageSize=20";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }
    public static String multipartRequest(Context context,String serverUrl,String folderId, String filepath) throws Exception {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);
            String strUrl = serverUrl + "/" + folderId;
            URL url = new URL(strUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            //  connection.setRequestProperty("Connection", "Keep-Alive");
            //  connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "file" + "\"; filename=\"" + "test.jpg" + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: application/octet-stream"+lineEnd + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            outputStream.writeBytes(lineEnd);
            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            // send multipart form data necesssary after file data...
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int responseCode = connection.getResponseCode();
            Log.d("ResponseCide",""+responseCode);

            if (responseCode >= 400 && responseCode <= 499) {
                result = "failure";

            } else {
                result = "success";
            }

           /* inputStream = connection.getInputStream();

            result = convertStreamToString(inputStream);*/
            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            return result;
        } catch (Exception e) {
            Log.d("Error",e.getLocalizedMessage());
        }
        return result;
    }
    // HTTP GET request
    public static String downloadImageBasedOnId(Context context, String serverUrl, String id) throws Exception {

        StringBuffer response;

        String url = serverUrl + "/" + id;

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(40000);
        // optional default is GET
        connection.setRequestMethod("GET");

        //add request header
        // connection.setRequestProperty("Accept", "application/json");
        //  connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Authorization", AppSharedPreferences.getAccessToken(context));
        //  System.out.println("Access token : "+CommonMethods.getAccessToken(context));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    /*public static String POST(String url, Person person){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", person.getName());
            jsonObject.accumulate("country", person.getCountry());
            jsonObject.accumulate("twitter", person.getTwitter());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }*/

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /**
     * Handle data as a string
     *
     * @param params Contains username, password and token parameter
     * @return result as String
     * @throws UnsupportedEncodingException
     */
    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
