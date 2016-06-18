package com.health.gounihealth.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.health.gounihealth.R;
import com.health.gounihealth.utils.CommonMethods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    private ImageView imgProfile;
    private EditText edtId,edtName,edtContact,edtCity,edtBloodGroup,edtEmergencyContact1,
                     edtEmergencyContact2,edtAllergy,edtIlness;
    private TextInputLayout inputLayoutID, inputLayoutName, inputLayoutCity,inputLayoutContact,
                            inputLayoutBlood,inputLayoutEmerg1,inputLayoutEmerg2,inputLayoutAllergy
                             ,inputLayoutIllness;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_layout);

        initialize();

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.labelMyProfile));
    }

    private void initialize(){
        progress = new ProgressDialog(this);
        imgProfile = (ImageView)findViewById(R.id.imgProfile);
        edtId = (EditText)findViewById(R.id.edtId);
        edtName = (EditText)findViewById(R.id.edtName);
        edtCity = (EditText)findViewById(R.id.edtCity);
        edtContact = (EditText)findViewById(R.id.edtContact);
        edtBloodGroup = (EditText)findViewById(R.id.edtBlood);
        edtEmergencyContact1 = (EditText)findViewById(R.id.edtEmerg1);
        edtEmergencyContact2 = (EditText)findViewById(R.id.edtEmerg1);
        edtAllergy = (EditText)findViewById(R.id.edtAllergy);
        edtIlness = (EditText)findViewById(R.id.edtIllness);

        inputLayoutID = (TextInputLayout) findViewById(R.id.inputLayoutID);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutCity = (TextInputLayout) findViewById(R.id.inputLayoutCity);
        inputLayoutContact = (TextInputLayout) findViewById(R.id.inputLayoutContact);
        inputLayoutBlood = (TextInputLayout) findViewById(R.id.inputLayoutBlood);
        inputLayoutEmerg1 = (TextInputLayout) findViewById(R.id.inputLayoutEmerg1);
        inputLayoutEmerg2 = (TextInputLayout) findViewById(R.id.inputLayoutEmerg2);
        inputLayoutAllergy = (TextInputLayout) findViewById(R.id.inputLayoutAllergy);
        inputLayoutIllness = (TextInputLayout) findViewById(R.id.inputLayoutIllness);

        edtId.addTextChangedListener(new MyTextWatcher(edtId));
        edtName.addTextChangedListener(new MyTextWatcher(edtName));
        edtCity.addTextChangedListener(new MyTextWatcher(edtCity));
        edtContact.addTextChangedListener(new MyTextWatcher(edtContact));
        edtBloodGroup.addTextChangedListener(new MyTextWatcher(edtBloodGroup));
        edtEmergencyContact1.addTextChangedListener(new MyTextWatcher(edtEmergencyContact1));
        edtEmergencyContact2.addTextChangedListener(new MyTextWatcher(edtEmergencyContact2));
        edtAllergy.addTextChangedListener(new MyTextWatcher(edtAllergy));
        edtIlness.addTextChangedListener(new MyTextWatcher(edtIlness));

    }
    public void onNextClick(View view) {
        if (CommonMethods.isConnected(this)) {
            if (validateName() && validateCity() && validateBloodGroup() && validateEmergency1()) {
                Intent intent = new Intent(MyProfileActivity.this, DashBoardActivity.class);
                startActivity(intent);
            }
        } else {
            CommonMethods.showToastMessage(this, getString(R.string.noInternet));
        }
    }

    public void onUploadImage(View view){
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
        startActivityForResult(Intent.createChooser(intent, "Select File"),RESULT_LOAD_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
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
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
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
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgProfile.setImageBitmap(bm);
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
                    || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }*/

    private boolean validateName() {
        if (edtName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.emptyName));
            requestFocus(edtName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateCity() {
        if (edtCity.getText().toString().trim().isEmpty()) {
            inputLayoutCity.setError(getString(R.string.emptyCity));
            requestFocus(edtCity);
            return false;
        } else {
            inputLayoutCity.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateContact() {
        if (edtContact.getText().toString().trim().isEmpty()) {
            inputLayoutContact.setError(getString(R.string.emptyContact));
            requestFocus(edtContact);
            return false;
        } else {
            inputLayoutContact.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateBloodGroup() {
        if (edtBloodGroup.getText().toString().trim().isEmpty()) {
            inputLayoutBlood.setError(getString(R.string.emptyBloodgroup));
            requestFocus(edtBloodGroup);
            return false;
        } else {
            inputLayoutBlood.setErrorEnabled(false);
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
                case R.id.edtName:
                    validateName();
                    break;
                case R.id.edtCity:
                    validateCity();
                    break;
                case R.id.edtContact:
                    validateContact();
                    break;
                case R.id.edtBlood:
                    validateBloodGroup();
                    break;
                case R.id.edtEmerg1:
                    validateEmergency1();
                    break;

            }
        }
    }
}
