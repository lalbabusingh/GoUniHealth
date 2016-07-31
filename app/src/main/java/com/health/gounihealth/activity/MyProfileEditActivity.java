package com.health.gounihealth.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
 * Created by LAL on 7/24/2016.
 */
public class MyProfileEditActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private TextView txtNoImageDisplay;
    private String picturePath;
    private Dialog dialog;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_GALLERY_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String imageCapture;
    private ArrayList<CityInfo> cityInfoArray = new ArrayList<CityInfo>();
    private ArrayList<BloodGroupInfo> bloodGInfoArray = new ArrayList<BloodGroupInfo>();

    private EditText edtId, edtFirstName, edtLastName, edtContact, edtCity, edtEmergencyContact1,
            edtEmergencyContact2, edtAllergy, edtAddress, edtCurrentIllness, edtPasMed, edtOther;
    private TextInputLayout inputLayoutID, inputLayoutFirstName, inputLayoutLastName, inputLayoutCity,
            inputLayoutBlood, inputLayoutEmerg1, inputLayoutEmerg2, inputLayoutAllergy, inputLayoutIllness, inputLayoutOther;
    private TextView txtHeader;
    private LinearLayout layoutLabelRight;
    // private Button btnNext, btnSave;
    private RadioButton radYes, radNo;
    private String mCityName, mBloodGroup;
    private String strMobileNo, strEmailId, strPassword, strConfirmPassword;
    private boolean strBloodDonate;
    private String strCityId, strBloodGroupId = "";
    private ArrayList<LoginInfo> infoArrayList;
    private Button btnUpdate;
    private ImageView imgProfile;
    private ImageView imgUpload;
    private ImageView imgEditProfile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_edit_layout);

        initialization();
        profileImage();
        btnUpdate();
        getImageFromSdCard();
        selectionRadioButton();
        editProfile();
        new GetCityListAsyncTask().execute();
        new GetBloodGroupAsyncTask().execute();
        new GetMyProfileEditAsyncTask().execute();

        // starAddMedicalInsurance();

    }

    private void initialization() {
        progress = new ProgressDialog(this);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgUpload = (ImageView) findViewById(R.id.imgUpload);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtAllergy = (EditText) findViewById(R.id.edtAllergy);
        edtCurrentIllness = (EditText) findViewById(R.id.edtCurrentIllness);
        edtPasMed = (EditText) findViewById(R.id.edtPasMed);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtOther = (EditText) findViewById(R.id.edtOther);
        // edtCity = (EditText) findViewById(R.id.edtCity);
        //   btnSave = (Button) findViewById(R.id.btnSave);
        //  btnNext = (Button) findViewById(R.id.btnNext);
        radYes = (RadioButton) findViewById(R.id.radYes);
        radNo = (RadioButton) findViewById(R.id.radNo);

       /* layoutLabelRight = (LinearLayout) findViewById(R.id.layoutLabelRight);
        layoutLabelRight.setVisibility(View.VISIBLE);*/
        edtEmergencyContact1 = (EditText) findViewById(R.id.edtEmerg1);
        edtEmergencyContact2 = (EditText) findViewById(R.id.edtEmerg2);

       /* txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.labelMyProfile));*/

        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.inputLayoutFirstName);
        inputLayoutLastName = (TextInputLayout) findViewById(R.id.inputLayoutLastName);
        //  inputLayoutCity = (TextInputLayout) findViewById(R.id.inputLayoutCity);
        inputLayoutEmerg1 = (TextInputLayout) findViewById(R.id.inputLayoutEmerg1);
        inputLayoutEmerg2 = (TextInputLayout) findViewById(R.id.inputLayoutEmerg2);
        inputLayoutOther = (TextInputLayout) findViewById(R.id.inputLayoutOther);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        imgEditProfile = (ImageView) findViewById(R.id.imgEditProfile);

        edtFirstName.addTextChangedListener(new MyTextWatcher(edtFirstName));
        edtLastName.addTextChangedListener(new MyTextWatcher(edtLastName));
//        edtCity.addTextChangedListener(new MyTextWatcher(edtCity));
        edtEmergencyContact1.addTextChangedListener(new MyTextWatcher(edtEmergencyContact1));
        edtEmergencyContact2.addTextChangedListener(new MyTextWatcher(edtEmergencyContact2));
        edtOther.addTextChangedListener(new MyTextWatcher(edtOther));

        infoArrayList = new ArrayList<LoginInfo>();

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
       // collapsingToolbar.setTitle(getString(R.string.labelMyProfileEdit));
       // collapsingToolbar.setPadding(0,20,0,0);
        collapsingToolbar.setCollapsedTitleTypeface(CommonMethods.getLatoHeavyFont(this));
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.White));
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbar.setExpandedTitleMarginTop(20);

    }

    private void getImageFromSdCard() {
        String path = Environment.getExternalStorageDirectory() + "/GoUniHealth/Capture.jpg";
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgProfile.setImageBitmap(myBitmap);
           /* ImageView myImage = (ImageView) findViewById(R.id.imageView1);
            myImage.setImageBitmap(myBitmap);*/
        }
        /*else
            Toast.makeText(this,"no IMAGE IS PRESENT'", Toast.LENGTH_SHORT).show();*/
    }

    private void profileImage() {
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCapture();
            }
        });
    }

    private void editProfile(){
        imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
                        //Toast.makeText(MyProfileEditActivity.this,radYes.getText(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radNo:
                        int selectedNo = radioGroup.getCheckedRadioButtonId();
                        radNo = (RadioButton) findViewById(selectedNo);
                        strBloodDonate = false;
                        //   Toast.makeText(MyProfileEditActivity.this,radNo.getText(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void btnUpdate() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.isConnected(MyProfileEditActivity.this)) {
                    if (validateFirstName() && validateLastName() && validateEmergency1() && validateEmergency2()) {
                        new UpdateMyProfileAsyncTask().execute();
                    }

                }
            }
        });
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
                response = JsonData.getCityList(MyProfileEditActivity.this, ApiManager.CITY_LIST_URL);
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
                           // setCityList();
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

   /* private void setCityList() {
        if (cityInfoArray.size() > 0) {
            setCityListSpinner();
        } else {
            Toast.makeText(MyProfileEditActivity.this, "Please try again..", Toast.LENGTH_LONG).show();
        }
    }*/

    private void setBloodGroupList() {
        if (bloodGInfoArray.size() > 0) {
            setBloodGroupSpinner();
        } else {
            Toast.makeText(MyProfileEditActivity.this, "Please try again..", Toast.LENGTH_LONG).show();
        }
    }
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
    private void setCityListSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerCity);
        CityListAdapter cityListAdapter = new CityListAdapter(MyProfileEditActivity.this, R.layout.city_list_items, cityInfoArray);
        spinner.setAdapter(cityListAdapter);

      //  spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition(mCityName));
      //  spinner.setSelection(getIndex(spinner, mCityName));

        //private method of your class


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCityName = ((TextView) view.findViewById(R.id.txtCityName)).getText().toString();
                strCityId = cityInfoArray.get(position).getId();
                System.out.println("Profile Name -> " + mCityName + " cityId-> " + strCityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setBloodGroupSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerBloodGroup);
        BloodGroupAdapter bloodGroupAdapter = new BloodGroupAdapter(MyProfileEditActivity.this, R.layout.city_list_items, bloodGInfoArray);
        spinner.setAdapter(bloodGroupAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mBloodGroup = ((TextView) view.findViewById(R.id.txtCityName)).getText().toString();
                strBloodGroupId = bloodGInfoArray.get(position).getKey();
                edtOther.setText("");
                if (strBloodGroupId.equalsIgnoreCase("SOMETHING_ELSE")) {
                    inputLayoutOther.setVisibility(View.VISIBLE);
                } else {
                    inputLayoutOther.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                response = JsonData.getBloodGroupList(MyProfileEditActivity.this, ApiManager.BLOOD_GROUP_LIST_URL);
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
                /*if (progress != null) {
                    progress.dismiss();
                }*/

            } else {
              /*  if (progress != null) {
                    progress.dismiss();
                }*/
            }

        }

    }

    String profileId;

    class GetMyProfileEditAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         /*   progress.setMessage("Please wait...");
            progress.show();*/
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response = JsonData.getMyProfileEdit(MyProfileEditActivity.this, ApiManager.USER_GET_MY_PROFILE_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                //    cityInfoArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    LoginInfo loginInfo = new LoginInfo();
                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        profileId = jsonObject.getString("id");
                    }
                    if (jsonObject.has("firstName") && jsonObject.getString("firstName") != null && !jsonObject.getString("firstName").equalsIgnoreCase("")
                            && !jsonObject.getString("firstName").equalsIgnoreCase("null")) {
                        edtFirstName.setText(jsonObject.getString("firstName"));
                    }
                    if (jsonObject.has("lastName") && jsonObject.getString("lastName") != null && !jsonObject.getString("lastName").equalsIgnoreCase("")
                            && !jsonObject.getString("lastName").equalsIgnoreCase("null")) {
                        edtLastName.setText(jsonObject.getString("lastName"));
                    }
                    if (jsonObject.has("address") && jsonObject.getString("address") != null && !jsonObject.getString("address").equalsIgnoreCase("")
                            && !jsonObject.getString("address").equalsIgnoreCase("null")) {
                        edtAddress.setText(jsonObject.getString("address"));
                    }

                    JSONObject jsonCity = jsonObject.getJSONObject("city");
                    if (jsonCity.has("id") && jsonCity.getString("id") != null && !jsonCity.getString("id").equalsIgnoreCase("")
                            && !jsonCity.getString("id").equalsIgnoreCase("null")) {
                        strCityId = jsonCity.getString("id");
                    }
                    if (jsonCity.has("name") && jsonCity.getString("name") != null && !jsonCity.getString("name").equalsIgnoreCase("")
                            && !jsonCity.getString("name").equalsIgnoreCase("null")) {
                        mCityName = jsonCity.getString("name");
                        setCityListSpinner();
                    }


                    if (jsonObject.has("bloodGroup") && jsonObject.getString("bloodGroup") != null && !jsonObject.getString("bloodGroup").equalsIgnoreCase("")
                            && !jsonObject.getString("bloodGroup").equalsIgnoreCase("null")) {
                        mBloodGroup = jsonObject.getString("bloodGroup");
                    }
                    if (jsonObject.has("allergyToMedicines") && jsonObject.getString("allergyToMedicines") != null && !jsonObject.getString("allergyToMedicines").equalsIgnoreCase("")
                            && !jsonObject.getString("allergyToMedicines").equalsIgnoreCase("null")) {
                        edtAllergy.setText(jsonObject.getString("allergyToMedicines"));
                    }
                    if (jsonObject.has("currentIllness") && jsonObject.getString("currentIllness") != null && !jsonObject.getString("currentIllness").equalsIgnoreCase("")
                            && !jsonObject.getString("currentIllness").equalsIgnoreCase("null")) {
                        edtCurrentIllness.setText(jsonObject.getString("currentIllness"));
                    }
                    if (jsonObject.has("pastMedicalHistory") && jsonObject.getString("pastMedicalHistory") != null && !jsonObject.getString("pastMedicalHistory").equalsIgnoreCase("")
                            && !jsonObject.getString("pastMedicalHistory").equalsIgnoreCase("null")) {
                        edtPasMed.setText(jsonObject.getString("pastMedicalHistory"));
                    }
                    if (jsonObject.has("willingToDonateBlood")) {
                        strBloodDonate = jsonObject.getBoolean("willingToDonateBlood");
                        if (strBloodDonate) {
                            radYes.setChecked(true);
                        } else {
                            radNo.setChecked(true);
                        }
                    }

                    JSONArray emergencyContacts = jsonObject.optJSONArray("emergencyContacts");
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
                    infoArrayList.add(loginInfo);
                    if (infoArrayList != null && infoArrayList.size() > 0) {
                        edtEmergencyContact1.setText(infoArrayList.get(0).getEmergencyContactNo());
                        if (infoArrayList.size() == 2) {
                            edtEmergencyContact2.setText(infoArrayList.get(1).getEmergencyContactNo());
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
        imgProfile.setImageBitmap(thumbnail);
        SaveImage(thumbnail);
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
        imgProfile.setImageBitmap(bm);
        SaveImage(bm);
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/GoUniHealth");
        myDir.mkdirs();

        String fname = "Capture.jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject createJsonData() {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("email", strEmailId);
            jsonParam.put("id", profileId);
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
            jsonParamA.put("id", strCityId);
            // We add the object to the main object
            jsonParam.put("city", jsonParamA);

            JSONArray jsonArr = new JSONArray();
            JSONObject em1Obj = new JSONObject();
            JSONObject em2Obj = new JSONObject();

            if (infoArrayList != null && infoArrayList.size() > 0) {
                em1Obj.put("id", infoArrayList.get(0).getEmergencyId().trim());
                em1Obj.put("contactNO", edtEmergencyContact2.getText().toString().trim());
                if (infoArrayList.size() == 2) {
                    em2Obj.put("id", infoArrayList.get(1).getEmergencyId().trim());
                    em2Obj.put("contactNO", edtEmergencyContact2.getText().toString().trim());
                }else{
                    em2Obj.put("contactNO", edtEmergencyContact2.getText().toString().trim());
                }
            }
            jsonArr.put(em1Obj);
            jsonArr.put(em2Obj);
            jsonParam.put("emergencyContacts", jsonArr);

            jsonParam.put("currentIllness", edtCurrentIllness.getText().toString().trim());
            jsonParam.put("pastMedicalHistory", edtPasMed.getText().toString().trim());
            System.out.println("Profile jsonParam -> " + jsonParam.toString());
            return jsonParam;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    class UpdateMyProfileAsyncTask extends AsyncTask<String, Void, String> {
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
                JSONObject objData = createJsonData();
                response = JsonData.updateMyProfilePostCall(MyProfileEditActivity.this, ApiManager.USER_UPDATE_PROFILE_URL, objData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                //    cityInfoArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    LoginInfo loginInfo = new LoginInfo();
                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        loginInfo.setUserId(jsonObject.getString("id"));
                    }
                    if (jsonObject.has("firstName") && jsonObject.getString("firstName") != null && !jsonObject.getString("firstName").equalsIgnoreCase("")
                            && !jsonObject.getString("firstName").equalsIgnoreCase("null")) {
                        loginInfo.setFirstName(jsonObject.getString("firstName"));
                    }
                    if (jsonObject.has("lastName") && jsonObject.getString("lastName") != null && !jsonObject.getString("lastName").equalsIgnoreCase("")
                            && !jsonObject.getString("lastName").equalsIgnoreCase("null")) {
                        loginInfo.setLastName(jsonObject.getString("lastName"));
                    }

                    String profileName = "";
                    if (loginInfo.getFirstName() != null) {
                        profileName = loginInfo.getFirstName().trim();
                    }
                    if (loginInfo.getLastName() != null) {
                        profileName = profileName+" "+ loginInfo.getLastName().trim();
                    }
                    AppSharedPreferences.saveProfileName(MyProfileEditActivity.this, profileName);

                    JSONObject jsonCity = jsonObject.getJSONObject("city");

                    if (jsonCity.has("id") && jsonCity.getString("id") != null && !jsonCity.getString("id").equalsIgnoreCase("")
                            && !jsonCity.getString("id").equalsIgnoreCase("null")) {
                        loginInfo.setCityId(jsonCity.getString("id"));
                    }
                    if (jsonCity.has("name") && jsonCity.getString("name") != null && !jsonCity.getString("name").equalsIgnoreCase("")
                            && !jsonCity.getString("name").equalsIgnoreCase("null")) {
                        loginInfo.setCityName(jsonCity.getString("name"));
                    }

                    if (loginInfo.getCityName() != null) {
                        AppSharedPreferences.saveProfileCityName(MyProfileEditActivity.this, loginInfo.getCityName());
                    }

                    CommonMethods.showToastMessage(MyProfileEditActivity.this,"Profile Updated Successfully");
                    finish();

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

}
