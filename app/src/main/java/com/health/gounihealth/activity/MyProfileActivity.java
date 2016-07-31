package com.health.gounihealth.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.adapter.BloodGroupAdapter;
import com.health.gounihealth.adapter.CityListAdapter;
import com.health.gounihealth.datainfo.BloodGroupInfo;
import com.health.gounihealth.datainfo.CityInfo;
import com.health.gounihealth.datainfo.LoginInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by LAL on 6/8/2016.
 */
public class MyProfileActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private TextView txtNoImageDisplay;
    private String picturePath;
    private Dialog dialog;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_GALLERY_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String imageCapture;
    //  private ImageView imgProfile;
    private EditText edtId, edtFirstName, edtLastName, edtContact, edtCity, edtEmergencyContact1,
            edtEmergencyContact2, edtAllergy, edtAddress,edtCurrentIllness,edtPasMed,edtOther;
    private TextInputLayout inputLayoutID, inputLayoutFirstName, inputLayoutLastName, inputLayoutCity,
            inputLayoutBlood, inputLayoutEmerg1, inputLayoutEmerg2, inputLayoutAllergy, inputLayoutIllness,inputLayoutOther;
    private TextView txtHeader;
    private LinearLayout layoutLabelRight;
    private Button btnNext, btnSave;
    private RadioButton radYes, radNo;
    private ArrayList<CityInfo> cityInfoArray = new ArrayList<CityInfo>();
    private ArrayList<BloodGroupInfo> bloodGInfoArray = new ArrayList<BloodGroupInfo>();
    private String mCityName, mBloodGroup;
    private String strMobileNo, strEmailId, strPassword, strConfirmPassword;
    private boolean strBloodDonate;
    private String strCityId,strBloodGroupId="";
    private ArrayList<LoginInfo> infoArrayList;
    public static MyProfileActivity mMyProfileActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        initialize();
        selectionRadioButton();
        getData();

        new GetCityListAsyncTask().execute();
        new GetBloodGroupAsyncTask().execute();
        onSaveClick();
        onNextClick();

       // starAddMedicalInsurance();

    }

    private void getData() {
        try {
            Intent intent = getIntent();
            strMobileNo = intent.getStringExtra("MOBILE");
            strEmailId = intent.getStringExtra("EMAIL");
            strPassword = intent.getStringExtra("PASSWORD");
            strConfirmPassword = intent.getStringExtra("CONFIRMPASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initialize() {
        progress = new ProgressDialog(this);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtAllergy = (EditText) findViewById(R.id.edtAllergy);
        edtCurrentIllness = (EditText) findViewById(R.id.edtCurrentIllness);
        edtPasMed = (EditText) findViewById(R.id.edtPasMed);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtOther = (EditText) findViewById(R.id.edtOther);
        // edtCity = (EditText) findViewById(R.id.edtCity);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setVisibility(View.GONE);
        btnNext = (Button) findViewById(R.id.btnNext);
        radYes = (RadioButton) findViewById(R.id.radYes);
        radNo = (RadioButton) findViewById(R.id.radNo);

        mMyProfileActivity = this;

        layoutLabelRight = (LinearLayout) findViewById(R.id.layoutLabelRight);
        layoutLabelRight.setVisibility(View.VISIBLE);
        edtEmergencyContact1 = (EditText) findViewById(R.id.edtEmerg1);
        edtEmergencyContact2 = (EditText) findViewById(R.id.edtEmerg2);

        txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.labelMyProfile));

        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.inputLayoutFirstName);
        inputLayoutLastName = (TextInputLayout) findViewById(R.id.inputLayoutLastName);
        //  inputLayoutCity = (TextInputLayout) findViewById(R.id.inputLayoutCity);
        inputLayoutEmerg1 = (TextInputLayout) findViewById(R.id.inputLayoutEmerg1);
        inputLayoutEmerg2 = (TextInputLayout) findViewById(R.id.inputLayoutEmerg2);
        inputLayoutOther = (TextInputLayout) findViewById(R.id.inputLayoutOther);

        edtFirstName.addTextChangedListener(new MyTextWatcher(edtFirstName));
        edtLastName.addTextChangedListener(new MyTextWatcher(edtLastName));
//        edtCity.addTextChangedListener(new MyTextWatcher(edtCity));
        edtEmergencyContact1.addTextChangedListener(new MyTextWatcher(edtEmergencyContact1));
        edtEmergencyContact2.addTextChangedListener(new MyTextWatcher(edtEmergencyContact2));
        edtOther.addTextChangedListener(new MyTextWatcher(edtOther));

        infoArrayList = new ArrayList<LoginInfo>();

    }

    private void selectionRadioButton() {
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radYes:
                        int selectedYes = radioGroup.getCheckedRadioButtonId();
                        radYes = (RadioButton) findViewById(selectedYes);
                        strBloodDonate = true;
                        Toast.makeText(MyProfileActivity.this,radYes.getText(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radNo:
                        int selectedNo = radioGroup.getCheckedRadioButtonId();
                        radNo = (RadioButton) findViewById(selectedNo);
                        strBloodDonate = false;
                        Toast.makeText(MyProfileActivity.this,radNo.getText(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private JSONObject createJsonData(){
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("email", strEmailId);
            jsonParam.put("password", strPassword);
            jsonParam.put("firstName", edtFirstName.getText().toString().trim());
            jsonParam.put("middleName", "");
            jsonParam.put("lastName", edtLastName.getText().toString().trim());
            jsonParam.put("bloodGroup", strBloodGroupId);
            jsonParam.put("address", edtAddress.getText().toString().trim());
            jsonParam.put("otherBloodGroup", edtOther.getText().toString().trim());
            jsonParam.put("willingToDonateBlood", strBloodDonate);
            jsonParam.put("allergyToMedicines", edtAllergy.getText().toString().trim());
            jsonParam.put("contactNo", strMobileNo);

            JSONObject jsonParamA = new JSONObject();
            jsonParamA.put("id",strCityId);
            // We add the object to the main object
            jsonParam.put("city", jsonParamA);

            JSONArray jsonArr = new JSONArray();
            JSONObject em1Obj = new JSONObject();
            JSONObject em2Obj = new JSONObject();
            em1Obj.put("contactNO", edtEmergencyContact1.getText().toString().trim());
            em2Obj.put("contactNO", edtEmergencyContact2.getText().toString().trim());
            jsonArr.put(em1Obj);
            jsonArr.put(em2Obj);
            jsonParam.put("emergencyContacts", jsonArr);

            jsonParam.put("currentIllness", edtCurrentIllness.getText().toString().trim());
            jsonParam.put("pastMedicalHistory", edtPasMed.getText().toString().trim());
            System.out.println("Profile jsonParam -> "+jsonParam.toString());
            return jsonParam;

        } catch (JSONException e) {
            e.printStackTrace();
        }
       return null;

    }

   /* public void onNextClick(View view) {
        Intent intent = new Intent(MyProfileActivity.this, DashBoardActivity.class);
        startActivity(intent);
       *//* if (CommonMethods.isConnected(this)) {
            if (validateName() && validateCity() && validateBloodGroup() && validateEmergency1()) {
                Intent intent = new Intent(MyProfileActivity.this, DashBoardActivity.class);
                startActivity(intent);
            }
        } else {
            CommonMethods.showToastMessage(this, getString(R.string.noInternet));
        }*//*
    }
*/

    private void onSaveClick() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDashBoard();
            }
        });
    }

    private void onNextClick() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDashBoard();
            }
        });

    }

    private void startDashBoard() {
        if (CommonMethods.isConnected(this)) {
            if (validateFirstName() && validateFirstName() && validateEmergency1()
                    && validateEmergency2() && validateOtherBloodGroup()) {

                new SignUpAsyncTask().execute();
               /* Intent intent = new Intent(MyProfileActivity.this, DashBoardActivity.class);
                startActivity(intent);*/
            }
        } else {
            CommonMethods.showToastMessage(this, getString(R.string.noInternet));
        }
    }
    private void startDashBoardScreen() {
        Intent intent = new Intent(MyProfileActivity.this, DashBoardActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void startAddMedicalInsurance(){
        Intent intent = new Intent(MyProfileActivity.this,AddMedicalInsurance.class);
        startActivity(intent);
    }
    private void startOTPActivity(String otpId,String userId){
        Intent intent = new Intent(MyProfileActivity.this,OtpActivity.class);
        intent.putExtra("OPTID",otpId);
        intent.putExtra("USERID",userId);
        startActivity(intent);
    }
    private boolean validateFirstName() {
        if (edtFirstName.getText().toString().trim().isEmpty()) {
            inputLayoutFirstName.setError(getString(R.string.emptyFirstName));
            requestFocus(edtFirstName);
            return false;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName() {
        if (edtLastName.getText().toString().trim().isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.emptyLastName));
            requestFocus(edtLastName);
            return false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }
        return true;
    }

   /* private boolean validateCity() {
        if (edtCity.getText().toString().trim().isEmpty()) {
            inputLayoutCity.setError(getString(R.string.emptyCity));
            requestFocus(edtCity);
            return false;
        } else {
            inputLayoutCity.setErrorEnabled(false);
        }

        return true;
    }*/

    private void setCityList() {
        if (cityInfoArray.size() > 0) {
            setCityListSpinner();
        } else {
            Toast.makeText(MyProfileActivity.this, "Please try again..", Toast.LENGTH_LONG).show();
        }
    }

    private void setBloodGroupList() {
        if (bloodGInfoArray.size() > 0) {
            setBloodGroupSpinner();
        } else {
            Toast.makeText(MyProfileActivity.this, "Please try again..", Toast.LENGTH_LONG).show();
        }
    }

    /* private boolean validateBloodGroup() {
         if (spinnerBlood.getText().toString().trim().isEmpty()) {
             inputLayoutBlood.setError(getString(R.string.emptyBloodgroup));
             requestFocus(spinnerBlood);
             return false;
         } else {
             inputLayoutBlood.setErrorEnabled(false);
         }
         return true;
     }*/
    private boolean validateEmergency1() {
        if (edtEmergencyContact1.getText().toString().trim().isEmpty()) {
            inputLayoutEmerg1.setError(getString(R.string.emptyEmergency1));
            requestFocus(edtEmergencyContact1);
            return false;
        } else {
            inputLayoutEmerg1.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmergency2() {
        if (edtEmergencyContact2.getText().toString().trim().isEmpty()) {
            inputLayoutEmerg2.setError(getString(R.string.emptyEmergency2));
            requestFocus(edtEmergencyContact2);
            return false;
        } else {
            inputLayoutEmerg2.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateOtherBloodGroup() {
        if (edtOther.getText().toString().trim().isEmpty() && strBloodGroupId.equalsIgnoreCase("SOMETHING_ELSE")) {
            inputLayoutOther.setError(getString(R.string.emptyOtherBloodG));
            requestFocus(edtOther);
            return false;
        } else {
            inputLayoutOther.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.edtFirstName:
                    validateFirstName();
                    break;
                case R.id.edtLastName:
                    validateLastName();
                    break;
               /* case R.id.edtCity:
                    validateCity();
                    break;*/

                case R.id.edtEmerg1:
                    validateEmergency1();
                    break;
                case R.id.edtEmerg2:
                    validateEmergency2();
                    break;
                case R.id.edtOther:
                    validateOtherBloodGroup();
                    break;

            }
        }
    }


    class GetCityListAsyncTask extends AsyncTask<String, Void, String> {

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
                response = JsonData.getCityList(MyProfileActivity.this, ApiManager.CITY_LIST_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                cityInfoArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonCity = jsonArray.getJSONObject(i);

                        CityInfo cityInfo = new CityInfo();
                        if (jsonCity.has("id") && jsonCity.getString("id") != null && !jsonCity.getString("id").equalsIgnoreCase("")
                                && !jsonCity.getString("id").equalsIgnoreCase("null")) {
                            cityInfo.setId(jsonCity.getString("id"));
                        }

                        if (jsonCity.has("name") && jsonCity.getString("name") != null && !jsonCity.getString("name").equalsIgnoreCase("")
                                && !jsonCity.getString("name").equalsIgnoreCase("null")) {
                            cityInfo.setName(jsonCity.getString("name"));
                        }
                        cityInfoArray.add(cityInfo);

                        // binding an array of Strings
                        if (cityInfoArray.size() > 0) {
                            setCityList();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
               /* if (progress != null) {
                    progress.dismiss();
                }*/

            } else {
               /* if (progress != null) {
                    progress.dismiss();
                }*/
            }

        }

    }

    class GetBloodGroupAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* progress.setMessage("Please wait...");
            progress.show();*/
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response = JsonData.getBloodGroupList(MyProfileActivity.this, ApiManager.BLOOD_GROUP_LIST_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                bloodGInfoArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("bgType");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonCity = jsonArray.getJSONObject(i);

                        BloodGroupInfo bloodGroupInfo = new BloodGroupInfo();
                        if (jsonCity.has("id") && jsonCity.getString("id") != null && !jsonCity.getString("id").equalsIgnoreCase("")
                                && !jsonCity.getString("id").equalsIgnoreCase("null")) {
                            bloodGroupInfo.setKey(jsonCity.getString("id"));
                        }

                        if (jsonCity.has("value") && jsonCity.getString("value") != null && !jsonCity.getString("value").equalsIgnoreCase("")
                                && !jsonCity.getString("value").equalsIgnoreCase("null")) {
                            bloodGroupInfo.setValue(jsonCity.getString("value"));
                        }
                        bloodGInfoArray.add(bloodGroupInfo);

                        // binding an array of Strings
                        if (bloodGInfoArray.size() > 0) {
                            setBloodGroupList();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progress != null) {
                    progress.dismiss();
                }

            } else {
                if (progress != null) {
                    progress.dismiss();
                }
            }

        }

    }

    class SignUpAsyncTask extends AsyncTask<String, Void, String> {

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
                response = JsonData.performUserSignUpPostCall(MyProfileActivity.this, ApiManager.SIGN_UP_URL,createJsonData());
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
                    JSONObject jsonUserProfile = new JSONObject(result);

                    if (result.contains("errorInfo")) {
                        JSONArray jsonToken = jsonUserProfile.optJSONArray("errorInfo");
                        JSONObject jsonError = jsonToken.getJSONObject(0);
                        if (jsonError.has("message") && jsonError.getString("message") != null && !jsonError.getString("message").equalsIgnoreCase("")
                                && !jsonError.getString("message").equalsIgnoreCase("null")) {
                            String message = jsonError.getString("message");
                            CommonMethods.showToastMessage(MyProfileActivity.this, message);
                        }


                    } else {
                        LoginInfo loginInfo = new LoginInfo();
                       /* JSONObject jsonToken = jsonObject.getJSONObject("accessToken");
                        if (jsonToken.has("tokenValue") && jsonToken.getString("tokenValue") != null && !jsonToken.getString("tokenValue").equalsIgnoreCase("")
                                && !jsonToken.getString("tokenValue").equalsIgnoreCase("null")) {
                            loginInfo.setTokenValue(jsonToken.getString("tokenValue"));

                        }
                        if (jsonToken.has("tokenType") && jsonToken.getString("tokenType") != null && !jsonToken.getString("tokenType").equalsIgnoreCase("")
                                && !jsonToken.getString("tokenType").equalsIgnoreCase("null")) {
                            loginInfo.setTokenType(jsonToken.getString("tokenType"));
                        }

                        System.out.println("Access----- " + jsonToken.getString("tokenType") + jsonToken.getString("tokenValue"));
                        AppSharedPreferences.saveAccessToken(MyProfileActivity.this, jsonToken.getString("tokenType") + jsonToken.getString("tokenValue"));
                        JSONObject jsonUserProfile = jsonObject.getJSONObject("userProfile");*/

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
                        AppSharedPreferences.saveProfileName(MyProfileActivity.this,profileName);
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
                            AppSharedPreferences.saveProfileCityName(MyProfileActivity.this, loginInfo.getCityName());
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
                        if (jsonUserProfile.has("otpId") && jsonUserProfile.getString("otpId") != null && !jsonUserProfile.getString("otpId").equalsIgnoreCase("")
                                && !jsonUserProfile.getString("otpId").equalsIgnoreCase("null")) {
                            loginInfo.setOtpId(jsonUserProfile.getString("otpId"));
                        }

                        infoArrayList.add(loginInfo);
                        if (progress != null) {
                            progress.dismiss();
                        }
                        if (infoArrayList.size() > 0 && infoArrayList.get(0).getOtpId()!=null) {
                           // startAddMedicalInsurance();
                           startOTPActivity(infoArrayList.get(0).getOtpId(),infoArrayList.get(0).getUserId());
                        } else {
                            CommonMethods.showToastMessage(MyProfileActivity.this, "No data to display");
                        }
                    }
                } catch (JSONException e) {
                    if (progress != null) {
                        progress.dismiss();
                    }
                    CommonMethods.showToastMessage(MyProfileActivity.this, "Something wrong Please try again");
                }

            } else {
                if (progress != null) {
                    progress.dismiss();
                }
                CommonMethods.showToastMessage(MyProfileActivity.this,"No data to display");
            }

        }

    }

    private void setCityListSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerCity);
        CityListAdapter cityListAdapter = new CityListAdapter(MyProfileActivity.this, R.layout.city_list_items, cityInfoArray);
        spinner.setAdapter(cityListAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCityName = ((TextView) view.findViewById(R.id.txtCityName)).getText().toString();
                strCityId = cityInfoArray.get(position).getId();
                System.out.println("Profile Name -> "+mCityName+" cityId-> "+strCityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setBloodGroupSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerBloodGroup);
        BloodGroupAdapter bloodGroupAdapter = new BloodGroupAdapter(MyProfileActivity.this, R.layout.city_list_items, bloodGInfoArray);
        spinner.setAdapter(bloodGroupAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mBloodGroup = ((TextView) view.findViewById(R.id.txtCityName)).getText().toString();
                strBloodGroupId = bloodGInfoArray.get(position).getKey();
                edtOther.setText("");
                if(strBloodGroupId.equalsIgnoreCase("SOMETHING_ELSE")){
                    inputLayoutOther.setVisibility(View.VISIBLE);
                }else{
                    inputLayoutOther.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void onUploadImage() {
        imageCapture();
    }

    public void imageCapture() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.setTitle("Image capture");
        ImageView btnExit = (ImageView) dialog.findViewById(R.id.imgTakePhoto);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.txtGallery)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activeGallery();
                    }
                });
        dialog.findViewById(R.id.txtCamera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activeTakePhoto();
                    }
                });

        // show dialog on screen
        dialog.show();
    }

    /**
     * take a photo
     */
    private void activeTakePhoto() {
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * to gallery
     */
    private void activeGallery() {
        /*Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_GALLERY_IMAGE);*/
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), RESULT_LOAD_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_GALLERY_IMAGE:
                if (requestCode == RESULT_LOAD_GALLERY_IMAGE &&
                        resultCode == RESULT_OK && null != data) {
                    onSelectFromGalleryResult(data);
                   /* Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver()
                            .query(selectedImage, filePathColumn, null, null,
                                    null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    if (picturePath != null && !picturePath.trim().equalsIgnoreCase("")) {
                        Toast.makeText(MyProfileActivity.this,"picture not null ",Toast.LENGTH_LONG).show();
                    }*/
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    //cursor.close();
                    //image.setDatetime(System.currentTimeMillis());
                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK) {
                    onCaptureImageResult(data);
                    /*String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor =
                            managedQuery(mCapturedImageURI, projection, null,
                                    null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    picturePath = cursor.getString(column_index_data);
                    if (picturePath != null && !picturePath.trim().equalsIgnoreCase("")) {
                        Toast.makeText(MyProfileActivity.this,"gallery picture not null ",Toast.LENGTH_LONG).show();
                    }*/
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    //  cursor.close();
                }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imgProfile.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // imgProfile.setImageBitmap(bm);
    }
    public static MyProfileActivity getInstance(){
        return mMyProfileActivity;

    }
}
