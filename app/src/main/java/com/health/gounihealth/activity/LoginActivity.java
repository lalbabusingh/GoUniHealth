package com.health.gounihealth.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.LoginInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmailId, edtPassword;
    private TextInputLayout inputLayoutEmailId, inputLayoutPassword;
    private Toolbar toolbar;
    private Button btnLogin, btnRegister;
    private ProgressDialog progress;
    private ArrayList<LoginInfo> infoArrayList;
    private TextView txtHeader;
    public static LoginActivity mLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

       /* toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        initialization();
        //  sendRequest();
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

        txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.labelLogin));

        mLoginActivity = this;

        ((ImageView) findViewById(R.id.imgBack)).setVisibility(View.GONE);

        inputLayoutEmailId = (TextInputLayout) findViewById(R.id.inputLayoutEmailId);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);

        edtEmailId.addTextChangedListener(new MyTextWatcher(edtEmailId));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword));

        progress = new ProgressDialog(this);
        CommonMethods.hideKeyboard(this);
        infoArrayList = new ArrayList<LoginInfo>();

    }

    public void onLoginClick(View view) {
        //  startDashBoard();
        if (CommonMethods.isConnected(this)) {
            CommonMethods.hideKeyboard(this);
            if (validateEmail() && validatePassword()) {
                new LoginAsyncTask().execute(edtEmailId.getText().toString().trim(), edtPassword.getText().toString().trim());
            }
        }/* else {
            CommonMethods.showToastMessage(this, getString(R.string.noInternet));
        }*/
    }


    public void onRegisterClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validateEmail() {
        String email = edtEmailId.getText().toString().trim();

        if (email.isEmpty()) {
            inputLayoutEmailId.setError(getString(R.string.emptyEmail));
            requestFocus(edtEmailId);
            return false;
        }
        if (!isValidEmail(email)) {
            inputLayoutEmailId.setError(getString(R.string.validEmail));
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
                response = JsonData.performLoginPostCall(LoginActivity.this, ApiManager.LOGIN_URL, urls[0], urls[1]);
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

                    if (jsonToken.has("tokenValue") && jsonToken.getString("tokenValue") != null && !jsonToken.getString("tokenValue").equalsIgnoreCase("")
                            && !jsonToken.getString("tokenValue").equalsIgnoreCase("null")) {
                        loginInfo.setTokenValue(jsonToken.getString("tokenValue"));

                    }
                    if (jsonToken.has("tokenType") && jsonToken.getString("tokenType") != null && !jsonToken.getString("tokenType").equalsIgnoreCase("")
                            && !jsonToken.getString("tokenType").equalsIgnoreCase("null")) {
                        loginInfo.setTokenType(jsonToken.getString("tokenType"));
                    }

                    System.out.println("Access----- " + jsonToken.getString("tokenType") + jsonToken.getString("tokenValue"));
                    AppSharedPreferences.saveAccessToken(LoginActivity.this, jsonToken.getString("tokenType") + jsonToken.getString("tokenValue"));
                    AppSharedPreferences.setAppStatus(LoginActivity.this,true);
                    JSONObject jsonUserProfile = jsonObject.getJSONObject("userProfile");

                    if (jsonUserProfile.has("id") && jsonUserProfile.getString("id") != null && !jsonUserProfile.getString("id").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("id").equalsIgnoreCase("null")) {
                        loginInfo.setUserId(jsonUserProfile.getString("id"));
                    }


                    if (jsonUserProfile.has("firstName") && jsonUserProfile.getString("firstName") != null && !jsonUserProfile.getString("firstName").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("firstName").equalsIgnoreCase("null")) {
                        loginInfo.setFirstName(jsonUserProfile.getString("firstName"));
                    }

                    if (jsonUserProfile.has("middleName") && jsonUserProfile.getString("middleName") != null && !jsonUserProfile.getString("middleName").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("middleName").equalsIgnoreCase("null")) {
                        loginInfo.setMiddleName(jsonUserProfile.getString("middleName"));
                    }

                    if (jsonUserProfile.has("lastName") && jsonUserProfile.getString("lastName") != null && !jsonUserProfile.getString("lastName").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("lastName").equalsIgnoreCase("null")) {
                        loginInfo.setLastName(jsonUserProfile.getString("lastName"));
                    }
                    String profileName = "";
                    if (loginInfo.getFirstName() != null) {
                        profileName = loginInfo.getFirstName().trim();
                    }
                    if (loginInfo.getMiddleName() != null) {
                        profileName = profileName+" "+loginInfo.getMiddleName().trim();
                    }
                    if (loginInfo.getLastName() != null) {
                        profileName = profileName+" "+ loginInfo.getLastName().trim();
                    }
                    AppSharedPreferences.saveProfileName(LoginActivity.this, profileName);
                    if (jsonUserProfile.has("status") && jsonUserProfile.getString("status") != null && !jsonUserProfile.getString("status").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("status").equalsIgnoreCase("null")) {
                        loginInfo.setStatus(jsonUserProfile.getString("status"));
                    }

                    if (jsonUserProfile.has("email") && jsonUserProfile.getString("email") != null && !jsonUserProfile.getString("email").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("email").equalsIgnoreCase("null")) {
                        loginInfo.setEmail(jsonUserProfile.getString("email"));
                    }

                    if (jsonUserProfile.has("password") && jsonUserProfile.getString("password") != null && !jsonUserProfile.getString("password").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("password").equalsIgnoreCase("null")) {
                        loginInfo.setPassword(jsonUserProfile.getString("password"));
                    }

                    if (jsonUserProfile.has("contactNo") && jsonUserProfile.getString("contactNo") != null && !jsonUserProfile.getString("contactNo").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("contactNo").equalsIgnoreCase("null")) {
                        loginInfo.setContactNo(jsonUserProfile.getString("contactNo"));
                    }

                    if (jsonUserProfile.has("allergyToMedicines") && jsonUserProfile.getString("allergyToMedicines") != null && !jsonUserProfile.getString("allergyToMedicines").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("allergyToMedicines").equalsIgnoreCase("null")) {
                        loginInfo.setAllergyToMedicines(jsonUserProfile.getString("allergyToMedicines"));
                    }

                    if (jsonUserProfile.has("currentIllness") && jsonUserProfile.getString("currentIllness") != null && !jsonUserProfile.getString("currentIllness").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("currentIllness").equalsIgnoreCase("null")) {
                        loginInfo.setCurrentIllness(jsonUserProfile.getString("currentIllness"));
                    }
                    if (jsonUserProfile.has("bloodGroup") && jsonUserProfile.getString("bloodGroup") != null && !jsonUserProfile.getString("bloodGroup").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("bloodGroup").equalsIgnoreCase("null")) {
                        loginInfo.setBloodGroup(jsonUserProfile.getString("bloodGroup"));
                    }

                    JSONObject jsonCity = jsonUserProfile.getJSONObject("city");

                    if (jsonCity.has("id") && jsonCity.getString("id") != null && !jsonCity.getString("id").equalsIgnoreCase("")
                            && !jsonCity.getString("id").equalsIgnoreCase("null")) {
                        loginInfo.setCityId(jsonCity.getString("id"));
                    }
                    if (jsonCity.has("name") && jsonCity.getString("name") != null && !jsonCity.getString("name").equalsIgnoreCase("")
                            && !jsonCity.getString("name").equalsIgnoreCase("null")) {
                        loginInfo.setCityName(jsonCity.getString("name"));
                    }

                    if (loginInfo.getCityName() != null) {
                        AppSharedPreferences.saveProfileCityName(LoginActivity.this, loginInfo.getCityName());
                    }

                    JSONArray emergencyContacts = jsonUserProfile.optJSONArray("emergencyContacts");
                    for (int i = 0; i < emergencyContacts.length(); i++) {
                        JSONObject jsonEmergencyContacts = emergencyContacts.getJSONObject(i);

                        if (jsonEmergencyContacts.has("id") && jsonEmergencyContacts.getString("id") != null && !jsonEmergencyContacts.getString("id").equalsIgnoreCase("")
                                && !jsonEmergencyContacts.getString("id").equalsIgnoreCase("null")) {
                            loginInfo.setEmergencyId(jsonEmergencyContacts.getString("id"));
                        }

                        if (jsonEmergencyContacts.has("contactNO") && jsonEmergencyContacts.getString("contactNO") != null && !jsonEmergencyContacts.getString("contactNO").equalsIgnoreCase("")
                                && !jsonEmergencyContacts.getString("contactNO").equalsIgnoreCase("null")) {
                            loginInfo.setEmergencyContactNo(jsonEmergencyContacts.getString("contactNO"));
                        }

                    }

                    if (jsonUserProfile.has("profileImageId") && jsonUserProfile.getString("profileImageId") != null && !jsonUserProfile.getString("profileImageId").equalsIgnoreCase("")
                            && !jsonUserProfile.getString("profileImageId").equalsIgnoreCase("null")) {
                        loginInfo.setProfileImageId(jsonUserProfile.getString("profileImageId"));
                    }

                    infoArrayList.add(loginInfo);
                    if (progress != null) {
                        progress.dismiss();
                    }
                    if (infoArrayList.size() > 0) {
                        startDashBoard();
                    } else {
                        CommonMethods.showToastMessage(LoginActivity.this, "No data to display");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonMethods.showToastMessage(LoginActivity.this, "Something wrong Please try again");
                }

            } else {
                if (progress != null) {
                    progress.dismiss();
                }
                CommonMethods.showToastMessage(LoginActivity.this, "No data to display");
            }

        }

    }

    private void startDashBoard() {
        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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


    private void sendRequest() {

        StringRequest stringRequest = new StringRequest("https://www.getpostman.com/collections/2274b8f0d2d787194f51",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response -> ", response);
                        // showJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public static LoginActivity getInstance(){
        return mLoginActivity;

    }

}
