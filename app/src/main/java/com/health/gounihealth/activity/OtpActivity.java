package com.health.gounihealth.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.HospitalListInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LAL on 7/23/2016.
 */
public class OtpActivity extends AppCompatActivity {

    private Button btnResend, btnSubmit;
    private TextView txtError;
    private EditText edtOtp;
    private ProgressDialog progress;
    private String otpId,userId;
    private String accessToken=null,tokenType=null,refreshToken=null;
    public static OtpActivity mOtpActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.otp_layout);
        initialize();
        getIds();
        submitOTP();
        resendOTP();

    }

    private void getIds(){
        otpId = getIntent().getStringExtra("OPTID");
        userId = getIntent().getStringExtra("USERID");
    }

    private void initialize() {
        btnResend = (Button) findViewById(R.id.btnResend);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        txtError = (TextView) findViewById(R.id.txtError);
        edtOtp = (EditText) findViewById(R.id.edtOtp);
        mOtpActivity = this;
        TextView txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.headerPhoneVerify));
        progress = new ProgressDialog(this);
    }

    private void submitOTP() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.isConnected(OtpActivity.this)) {
                    if (validateOtp()) {
                        new GetVerifyOTP().execute(edtOtp.getText().toString().trim());
                    }
                }

            }
        });
    }

    private void resendOTP() {
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.isConnected(OtpActivity.this)) {
                    new ResendOTP().execute();
                }
            }
        });
    }

    private boolean validateOtp() {
        if (edtOtp.getText().toString().trim().isEmpty()) {
            txtError.setVisibility(View.VISIBLE);
            return false;
        } else {
            txtError.setVisibility(View.GONE);
        }
        return true;
    }
    private void startAddMedicalInsurance(){
        Intent intent = new Intent(OtpActivity.this,AddMedicalInsurance.class);
        startActivity(intent);
    }
    class GetVerifyOTP extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Please wait...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response = JsonData.getVerifyOTP(OtpActivity.this, ApiManager.GET_OTP_VERIFY,otpId, userId,urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        int totalCount;

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {

                try {
                    JSONObject jsonUserProfile = new JSONObject(result);
                    JSONObject jsonToken = jsonUserProfile.getJSONObject("accessToken");
                    if (jsonToken.has("tokenValue") && jsonToken.getString("tokenValue") != null && !jsonToken.getString("tokenValue").equalsIgnoreCase("")
                            && !jsonToken.getString("tokenValue").equalsIgnoreCase("null")) {
                        accessToken = jsonToken.getString("tokenValue");

                    }
                    if (jsonToken.has("tokenType") && jsonToken.getString("tokenType") != null && !jsonToken.getString("tokenType").equalsIgnoreCase("")
                            && !jsonToken.getString("tokenType").equalsIgnoreCase("null")) {
                        tokenType = jsonToken.getString("tokenType");
                    }
                    if (jsonToken.has("refreshToken") && jsonToken.getString("refreshToken") != null && !jsonToken.getString("refreshToken").equalsIgnoreCase("")
                            && !jsonToken.getString("refreshToken").equalsIgnoreCase("null")) {
                        refreshToken = jsonToken.getString("refreshToken");
                    }
                    if(accessToken!=null && tokenType!=null){
                        System.out.println("Access----- " + tokenType+ accessToken);
                        AppSharedPreferences.saveAccessToken(OtpActivity.this, tokenType + accessToken);
                        AppSharedPreferences.saveRefreshAccessToken(OtpActivity.this, tokenType + refreshToken);
                        AppSharedPreferences.setAppStatus(OtpActivity.this,true);
                        startAddMedicalInsurance();
                    }else{
                        CommonMethods.showToastMessage(OtpActivity.this,getString(R.string.genericError));
                    }

                } catch (JSONException e) {
                    CommonMethods.showToastMessage(OtpActivity.this,getString(R.string.genericError));
                }


                if (progress != null) {
                    progress.dismiss();
                }
            } else {
                CommonMethods.showToastMessage(OtpActivity.this,getString(R.string.genericError));
                if (progress != null) {
                    progress.dismiss();
                }
            }

        }

    }

    class ResendOTP extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Please wait...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response = JsonData.getResendOTP(OtpActivity.this, ApiManager.GET_OTP_VERIFY,otpId, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        int totalCount;

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {

                try {
                    CommonMethods.showToastMessage(OtpActivity.this,"OTP Sent");
                    JSONObject jsonUserProfile = new JSONObject(result);


                } catch (JSONException e) {
                    CommonMethods.showToastMessage(OtpActivity.this,getString(R.string.genericError));
                }


                if (progress != null) {
                    progress.dismiss();
                }
            } else {
                CommonMethods.showToastMessage(OtpActivity.this,getString(R.string.genericError));
                if (progress != null) {
                    progress.dismiss();
                }
            }

        }

    }

    public static OtpActivity getInstance(){
        return mOtpActivity;
    }
}
