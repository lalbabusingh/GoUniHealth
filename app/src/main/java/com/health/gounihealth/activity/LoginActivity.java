package com.health.gounihealth.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.LoginInfo;
import com.health.gounihealth.utils.AppConstants;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmailId, edtPassword;
    private TextInputLayout inputLayoutEmailId, inputLayoutPassword;
    private Toolbar toolbar;
    private Button btnLogin, btnRegister;
    private ProgressDialog progress;
    private ArrayList<LoginInfo> infoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

       /* toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        initialization();
    }

    private void initialization() {
        edtEmailId = (EditText) findViewById(R.id.edtEmailId);
        edtEmailId.setTypeface(CommonMethods.getLatoLightFont(this));
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setTypeface(CommonMethods.getLatoLightFont(this));
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setTypeface(CommonMethods.getLatoHeavyFont(this));
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setTypeface(CommonMethods.getLatoHeavyFont(this));

        inputLayoutEmailId = (TextInputLayout) findViewById(R.id.inputLayoutEmailId);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);

        edtEmailId.addTextChangedListener(new MyTextWatcher(edtEmailId));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword));

        progress = new ProgressDialog(this);
        CommonMethods.hideKeyboard(this);
        infoArrayList = new ArrayList<LoginInfo>();

    }

    public void onLoginClick(View view) {

        if(CommonMethods.isConnected(this)){
            CommonMethods.hideKeyboard(this);
            if (validateEmail() && validatePassword()) {
                new LoginAsyncTask().execute(edtEmailId.getText().toString().trim(), edtPassword.getText().toString().trim());
            }
        }else{
            CommonMethods.showToastMessage(this,getString(R.string.noInternet));
        }
    }


    public void onRegisterClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }

    private boolean validateEmail() {
        String email = edtEmailId.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmailId.setError(getString(R.string.emptyEmail));
            requestFocus(edtEmailId);
            return false;
        } else {
            inputLayoutEmailId.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (edtPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.emptyPassword));
            requestFocus(edtPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    class LoginAsyncTask extends AsyncTask<String, Void, String> {

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
                response = JsonData.performLoginPostCall(LoginActivity.this, AppConstants.LOGIN_URL, urls[0], urls[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                try {
                    infoArrayList.clear();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject jsonToken = jsonObject.getJSONObject("accessToken");
                    LoginInfo loginInfo = new LoginInfo();

                    if (jsonToken.getString("tokenValue") != null && !jsonToken.getString("tokenValue").equalsIgnoreCase("")
                            && !jsonToken.getString("tokenValue").equalsIgnoreCase("null")) {
                        loginInfo.setTokenValue(jsonToken.getString("tokenValue"));
                    }
                    if (jsonToken.getString("tokenType") != null && !jsonToken.getString("tokenType").equalsIgnoreCase("")
                            && !jsonToken.getString("tokenType").equalsIgnoreCase("null")) {
                        loginInfo.setTokenType(jsonToken.getString("tokenType"));
                    }

                    JSONObject jsonUserProfile = jsonObject.getJSONObject("userProfile");

                    if (jsonUserProfile.getString("id") != null && !jsonUserProfile.getString("id").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("id").equalsIgnoreCase("null")) {
                        loginInfo.setUserId(jsonUserProfile.getString("id"));
                    }

                    if (jsonUserProfile.getString("firstName") != null && !jsonUserProfile.getString("firstName").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("firstName").equalsIgnoreCase("null")) {
                        loginInfo.setFirstName(jsonUserProfile.getString("firstName"));
                    }

                    if (jsonUserProfile.getString("middleName") != null && !jsonUserProfile.getString("middleName").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("middleName").equalsIgnoreCase("null")) {
                        loginInfo.setMiddleName(jsonUserProfile.getString("middleName"));
                    }

                    if (jsonUserProfile.getString("lastName") != null && !jsonUserProfile.getString("lastName").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("lastName").equalsIgnoreCase("null")) {
                        loginInfo.setLastName(jsonUserProfile.getString("lastName"));
                    }

                    if (jsonUserProfile.getString("status") != null && !jsonUserProfile.getString("status").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("status").equalsIgnoreCase("null")) {
                        loginInfo.setStatus(jsonUserProfile.getString("status"));
                    }

                    if (jsonUserProfile.getString("email") != null && !jsonUserProfile.getString("email").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("email").equalsIgnoreCase("null")) {
                        loginInfo.setEmail(jsonUserProfile.getString("email"));
                    }

                    if (jsonUserProfile.getString("password") != null && !jsonUserProfile.getString("password").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("password").equalsIgnoreCase("null")) {
                        loginInfo.setPassword(jsonUserProfile.getString("password"));
                    }

                    if (jsonUserProfile.getString("contactNo") != null && !jsonUserProfile.getString("contactNo").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("contactNo").equalsIgnoreCase("null")) {
                        loginInfo.setContactNo(jsonUserProfile.getString("contactNo"));
                    }

                    if (jsonUserProfile.getString("allergyToMedicines") != null && !jsonUserProfile.getString("allergyToMedicines").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("allergyToMedicines").equalsIgnoreCase("null")) {
                        loginInfo.setAllergyToMedicines(jsonUserProfile.getString("allergyToMedicines"));
                    }

                    if (jsonUserProfile.getString("currentIllness") != null && !jsonUserProfile.getString("currentIllness").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("currentIllness").equalsIgnoreCase("null")) {
                        loginInfo.setCurrentIllness(jsonUserProfile.getString("currentIllness"));
                    }
                    if (jsonUserProfile.getString("bloodGroup") != null && !jsonUserProfile.getString("bloodGroup").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("bloodGroup").equalsIgnoreCase("null")) {
                        loginInfo.setBloodGroup(jsonUserProfile.getString("bloodGroup"));
                    }

                    JSONObject jsonCity = jsonUserProfile.getJSONObject("city");

                    if (jsonCity.getString("id") != null && !jsonCity.getString("id").equalsIgnoreCase("")
                            && !jsonCity.getString("id").equalsIgnoreCase("null")) {
                        loginInfo.setCityId(jsonCity.getString("id"));
                    }
                    if (jsonCity.getString("name") != null && !jsonCity.getString("name").equalsIgnoreCase("")
                            && !jsonCity.getString("name").equalsIgnoreCase("null")) {
                        loginInfo.setCityName(jsonCity.getString("name"));
                    }

                    JSONArray emergencyContacts = jsonUserProfile.optJSONArray("emergencyContacts");
                    for (int i = 0; i < emergencyContacts.length(); i++) {
                        JSONObject jsonEmergencyContacts = emergencyContacts.getJSONObject(i);

                        if (jsonEmergencyContacts.getString("id") != null && !jsonEmergencyContacts.getString("id").equalsIgnoreCase("")
                                && !jsonEmergencyContacts.getString("id").equalsIgnoreCase("null")) {
                            loginInfo.setEmergencyId(jsonEmergencyContacts.getString("id"));
                        }

                        if (jsonEmergencyContacts.getString("contactNO") != null && !jsonEmergencyContacts.getString("contactNO").equalsIgnoreCase("")
                                && !jsonEmergencyContacts.getString("contactNO").equalsIgnoreCase("null")) {
                            loginInfo.setEmergencyContactNo(jsonEmergencyContacts.getString("contactNO"));
                        }

                    }

                    if (jsonUserProfile.getString("profileImageId") != null && !jsonUserProfile.getString("profileImageId").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("profileImageId").equalsIgnoreCase("null")) {
                        loginInfo.setProfileImageId(jsonUserProfile.getString("profileImageId"));
                    }

                    infoArrayList.add(loginInfo);
                    if (progress != null) {
                        progress.dismiss();
                    }
                    if(infoArrayList.size() > 0){
                        startDashBoard();
                    }else{
                        CommonMethods.showToastMessage(LoginActivity.this,"No data to display");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                if (progress != null) {
                    progress.dismiss();
                }
                CommonMethods.showToastMessage(LoginActivity.this,"No data to display");
            }

        }

    }

    private void startDashBoard() {
        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edtEmailId:
                    validateEmail();
                    break;
                case R.id.edtPassword:
                    validatePassword();
                    break;

            }
        }
    }

    /**
     * Called to process touch screen events.
     *//*
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }*/
}
