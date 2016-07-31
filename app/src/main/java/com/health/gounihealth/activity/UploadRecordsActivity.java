package com.health.gounihealth.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.CommonMethods;
import com.health.gounihealth.utils.HttpFileUpload;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by LAL on 7/23/2016.
 */
public class UploadRecordsActivity extends AppCompatActivity {

    private static String TAG = "UploadRecords";

    private Button btnUpload;
    private ProgressDialog progress;
    private Dialog dialog;
    private static final int RESULT_LOAD_GALLERY_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String folderId;
    private LinearLayout layoutImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_medical_record);

        initialize();
        displayImage("");
        new GetFolderIdToDownload().execute();
    }
    private void initialize(){
        btnUpload = (Button)findViewById(R.id.btnUpload);
        progress = new ProgressDialog(this);
        folderId = getIntent().getStringExtra("FOLDERID");
        layoutImage = (LinearLayout)findViewById(R.id.layoutImage);
        Log.d(TAG+" folderId",folderId);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCapture();
            }
        });

    }

    private void displayImage(String fileName){
        layoutImage.removeAllViews();
        ArrayList arrayList = new ArrayList();
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/2.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/3.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/4.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/5.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/2.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/3.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/4.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/5.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/2.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/3.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/4.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/5.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/2.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/3.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/4.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/5.jpg");
        arrayList.add("http://www.gstatic.com/webp/gallery/1.jpg");
        arrayList.add("http://res.cloudinary.com/demo/image/upload/sample.jpg");
        for(int i = 0; i < arrayList.size(); i++){
            ImageView imgView = new ImageView(this);
            //imgView.setId(i+1);
            Picasso.with(this)
                    .load(arrayList.get(i).toString())
                    .into(imgView);
            imgView.setPadding(10,0,10,0);
            // info.setPosition(i);
            layoutImage.addView(imgView);
        }

    }

    class GetFolderIdToDownload extends AsyncTask<String, Void, String> {

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
                response = JsonData.getMedicalFolderId(UploadRecordsActivity.this, ApiManager.DOWNLOAD_ID_MEDICAL_RECORD,folderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
               // medicalRecordCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("records");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        if (jsonList.has("id") && jsonList.getString("id") != null && !jsonList.getString("id").equalsIgnoreCase("")
                                && !jsonList.getString("id").equalsIgnoreCase("null")) {
                            String id = jsonList.getString("id");
                            new DownloadImageBasedOnId().execute(id);
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

    class DownloadImageBasedOnId extends AsyncTask<String, Void, String> {

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
               // displayImage(JsonData.downloadImageBasedOnId(UploadRecordsActivity.this, ApiManager.DOWNLOAD_FILE_MEDICAL_RECORD,urls[0]));
               response = JsonData.downloadImageBasedOnId(UploadRecordsActivity.this, ApiManager.DOWNLOAD_FILE_MEDICAL_RECORD,urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                // medicalRecordCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // JSONArray jsonArray = jsonObject.optJSONArray("records");

                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        String id = jsonObject.getString("id");
                        //  new GetMedicalInsuranceFolder().execute();
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
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.txtCamera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activeTakePhoto();
                        dialog.dismiss();
                    }
                });

        // show dialog on screen
        dialog.show();
    }

    /**
     * take a photo
     */
    private void activeTakePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * to gallery
     */
    private void activeGallery() {
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

                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK) {
                    onCaptureImageResult(data);
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
        SaveImage(thumbnail);
        new UploadImage().execute();
       // upload(thumbnail);
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
        SaveImage(bm);
        new UploadImage().execute();

       //  upload(bm);
        // imgProfile.setImageBitmap(bm);
    }
    String ba1;
    private void upload( Bitmap bm) {
      //  Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
        byte[] ba = bao.toByteArray();
      // ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
        ba1 =  Base64.encodeToString(ba, Base64.DEFAULT);



        // Upload image to server
        new UploadImage().execute();

    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/GoUniHealth");
        myDir.mkdirs();

        String fname = "upload.jpg";
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

    private String getImageFromSdCard(){
        String path = Environment.getExternalStorageDirectory()+ "/GoUniHealth/upload.jpg";
        return path;
    }

    class UploadImage extends AsyncTask<String, Void, String> {

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
                response = JsonData.multipartRequest(UploadRecordsActivity.this, ApiManager.UPLOAD_MEDICAL_RECORD,folderId,getImageFromSdCard());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
               if(result.equalsIgnoreCase("success")){
                   CommonMethods.showToastMessage(UploadRecordsActivity.this,getString(R.string.imageUploadSuccess));
               }else{
                   CommonMethods.showToastMessage(UploadRecordsActivity.this,getString(R.string.imageUploadFailure));
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

    public void UploadFile(){
        try {
            // Set your file path here
            FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/GoUniHealth/upload.jpg");

            // Set your server page url (and the file title/description)
            HttpFileUpload hfu = new HttpFileUpload(this,ApiManager.UPLOAD_MEDICAL_RECORD+"/"+folderId);

            hfu.Send_Now(fstrm);

        } catch (FileNotFoundException e) {
            // Error: File not found
        }
    }
}
