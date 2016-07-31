package com.health.gounihealth.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.IcuListInfo;
import com.health.gounihealth.datainfo.MedicalRecordCreationInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppConstants;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LAL on 7/20/2016.
 */
public class AddMedicalInsurance extends AppCompatActivity {
    private LinearLayout layoutInsurance,layoutMedical;
    private ProgressDialog progress;
    private ArrayList<MedicalRecordCreationInfo> medicalRecordCreationsArray = new ArrayList<MedicalRecordCreationInfo>();
    private ArrayList<MedicalRecordCreationInfo> medicalInsuranceCreationsArray = new ArrayList<MedicalRecordCreationInfo>();
    private Drawable drawableImage;
    private ImageView imgAddInsurance,imgRecordAdd;
    private boolean isAddFolder = false;
    private TextView txtInsuranceFolderName,txtRecordFolderName;
    private LinearLayout layoutLabelRight;
    private Button btnNext, btnSave;
    private String insuranceRecord = "INSURANCE_RECORDS";
    private String medicalRecord = "MEDICAL_RECORDS";
    public static AddMedicalInsurance mAddMedicalInsurance;
    private HorizontalScrollView insuranceHorizontalScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_insurance_layout);

        initialize();
        addInsuranceFolder();
        addRecordFolder();
        isAddFolder = false;
        if (CommonMethods.isConnected(this)) {
            new GetMedicalInsuranceFolder().execute();
            new GetMedicalRecordFolder().execute();
        }
        onSaveClick();
        onNextClick();
        leftScrollView();

        //  deleteFolder();
    }

    private void initialize(){
        layoutInsurance = (LinearLayout)findViewById(R.id.layoutInsurance);
        layoutMedical = (LinearLayout)findViewById(R.id.layoutMedical);
        progress = new ProgressDialog(this);
        drawableImage = ContextCompat.getDrawable(this,R.drawable.folder_records);
        imgAddInsurance = (ImageView)findViewById(R.id.imgAdd);
        imgRecordAdd = (ImageView)findViewById(R.id.imgRecordAdd);
        mAddMedicalInsurance = this;
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setText("Skip");
      //  btnSave.setVisibility(View.GONE);
        btnNext = (Button) findViewById(R.id.btnNext);
        layoutLabelRight = (LinearLayout) findViewById(R.id.layoutLabelRight);

        if(getIntent().getStringExtra(AppConstants.CALLING_SCREEN)!=null &&
                getIntent().getStringExtra(AppConstants.CALLING_SCREEN).equalsIgnoreCase(AppConstants.MENU_RECORDS)){
            layoutLabelRight.setVisibility(View.GONE);
        }else{
            layoutLabelRight.setVisibility(View.VISIBLE);
        }

        TextView txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.headerMedicalRecord));
        insuranceHorizontalScroll = (HorizontalScrollView)findViewById(R.id.insuranceHorizontalScroll);

    }

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

    private void startDashBoard(){
        Intent intent = new Intent(AddMedicalInsurance.this, DashBoardActivity.class);
        startActivity(intent);
    }

    private void leftScrollView(){
        ImageView imgNext = (ImageView)findViewById(R.id.imgNext);
        imgNext.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;
            private long mInitialDelay = 300;
            private long mRepeatDelay = 100;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null)
                            return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, mInitialDelay);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                            return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    insuranceHorizontalScroll.scrollTo((int) insuranceHorizontalScroll.getScrollX() + 10, (int) insuranceHorizontalScroll.getScrollY());
                    mHandler.postDelayed(mAction, mRepeatDelay);
                }
            };
        });
    }

    private void addInsuranceFolder(){
        imgAddInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.isConnected(AddMedicalInsurance.this)) {
                    isAddFolder = true;
                    inputFolderName(0,insuranceRecord);

                }
            }
        });
    }
    private void addRecordFolder(){
        imgRecordAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.isConnected(AddMedicalInsurance.this)) {
                    isAddFolder = true;
                    inputFolderName(1,medicalRecord);

                }
            }
        });
    }
    private void createRecordFolder(final ArrayList<MedicalRecordCreationInfo> medicalRecordCreationsArray){
        layoutMedical.removeAllViews();
        for(int i = 0; i< medicalRecordCreationsArray.size(); i++){
            MedicalRecordCreationInfo info = new MedicalRecordCreationInfo();
            txtRecordFolderName = new TextView(this);
            txtRecordFolderName.setId(i+1);
            txtRecordFolderName.setCompoundDrawablesWithIntrinsicBounds(null, drawableImage , null, null);
            txtRecordFolderName.setGravity(Gravity.CENTER);
            txtRecordFolderName.setText(medicalRecordCreationsArray.get(i).getFolderName()+"\n"+medicalRecordCreationsArray.get(i).getFolderCreationDate().replace(" ","\n"));
            txtRecordFolderName.setPadding(10,0,10,0);
           // info.setPosition(i);
            layoutMedical.addView(txtRecordFolderName);
           // medicalRecordCreationsArray.add(info);

            final int index = i;
            txtRecordFolderName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isAddFolder = true;

                    showAlertDialog("Alert","Do you want to delete "+medicalRecordCreationsArray.get(index).getFolderName()+" folder",
                            index,medicalRecordCreationsArray,medicalRecord);

                    return true;
                }
            });

            txtRecordFolderName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddMedicalInsurance.this,UploadRecordsActivity.class);
                    intent.putExtra("FOLDERID",medicalRecordCreationsArray.get(index).getFolderId());
                    startActivity(intent);
                }
            });
        }
    }

    private void createInsuranceFolder(final ArrayList<MedicalRecordCreationInfo> medicalRecordCreationsArray){
        layoutInsurance.removeAllViews();
        for(int i = 0; i< medicalRecordCreationsArray.size(); i++){
            txtInsuranceFolderName = new TextView(this);
            txtInsuranceFolderName.setId(i+1);
            txtInsuranceFolderName.setCompoundDrawablesWithIntrinsicBounds(null, drawableImage , null, null);
            txtInsuranceFolderName.setGravity(Gravity.CENTER);
            txtInsuranceFolderName.setText(medicalRecordCreationsArray.get(i).getFolderName()+"\n"+medicalRecordCreationsArray.get(i).getFolderCreationDate().replace(" ","\n"));
            txtInsuranceFolderName.setPadding(10,0,10,0);
            // info.setPosition(i);
            layoutInsurance.addView(txtInsuranceFolderName);
            // medicalRecordCreationsArray.add(info);

            final int index = i;
            txtInsuranceFolderName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isAddFolder = true;

                    showAlertDialog("Alert","Do you want to delete "+medicalRecordCreationsArray.get(index).getFolderName()+" folder",
                            index,medicalRecordCreationsArray,insuranceRecord);
                    return true;
                }
            });

            txtInsuranceFolderName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddMedicalInsurance.this,UploadRecordsActivity.class);
                    intent.putExtra("FOLDERID",medicalRecordCreationsArray.get(index).getFolderId());
                    startActivity(intent);
                }
            });
        }
    }

    class GetMedicalInsuranceFolder extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isAddFolder){
                progress.setMessage("Please wait...");
                progress.show();
            }

        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response = JsonData.getMedicalRecordCreated(AddMedicalInsurance.this, ApiManager.GET_MEDICAL_RECORDS,"INSURANCE_RECORDS");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                CommonMethods.hideKeyboard(AddMedicalInsurance.this);
                medicalInsuranceCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("records");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        MedicalRecordCreationInfo medicalRecordCreationInfo = new MedicalRecordCreationInfo();
                        if (jsonList.has("id") && jsonList.getString("id") != null && !jsonList.getString("id").equalsIgnoreCase("")
                                && !jsonList.getString("id").equalsIgnoreCase("null")) {
                            medicalRecordCreationInfo.setFolderId(jsonList.getString("id"));
                        }
                        if (jsonList.has("incidentName") && jsonList.getString("incidentName") != null && !jsonList.getString("incidentName").equalsIgnoreCase("")
                                && !jsonList.getString("incidentName").equalsIgnoreCase("null")) {
                            medicalRecordCreationInfo.setFolderName(jsonList.getString("incidentName"));
                        }
                        if (jsonList.has("incidentDate") && jsonList.getString("incidentDate") != null && !jsonList.getString("incidentDate").equalsIgnoreCase("")
                                && !jsonList.getString("incidentDate").equalsIgnoreCase("null")) {
                            String date = jsonList.getString("incidentDate");
                            date = date.replace("T"," ").replace("Z","").trim();
                            medicalRecordCreationInfo.setFolderCreationDate( CommonMethods.formattedDateFromString(AppConstants.DATE_INPUT_FORMAT,AppConstants.DATE_OUTPUT_FORMAT,date));
                        }
                        if (jsonList.has("incidentType") && jsonList.getString("incidentType") != null && !jsonList.getString("incidentType").equalsIgnoreCase("")
                                && !jsonList.getString("incidentType").equalsIgnoreCase("null")) {
                            medicalRecordCreationInfo.setIncidentType(jsonList.getString("incidentType"));
                        }
                        medicalInsuranceCreationsArray.add(medicalRecordCreationInfo);
                    }

                    if(medicalInsuranceCreationsArray!=null && medicalInsuranceCreationsArray.size() > 0){
                        isAddFolder = false;
                        createInsuranceFolder(medicalInsuranceCreationsArray);
                    }else{
                        CommonMethods.showToastMessage(AddMedicalInsurance.this,"No folder found for you");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progress != null) {
                    progress.dismiss();
                }

            } else {
                CommonMethods.showToastMessage(AddMedicalInsurance.this,"No folder found for you");
                if (progress != null) {
                    progress.dismiss();
                }
            }

        }

    }

    class MedicalInsuranceCreation extends AsyncTask<String, Void, String> {

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
                String date = CommonMethods.getCurrentDateTime(AddMedicalInsurance.this).replace(" ","T").concat("Z");
                JSONObject jsonData = createJson(urls[0],date,urls[1]);
                response = JsonData.medicalRecordCreationPostCall(AddMedicalInsurance.this, ApiManager.CREATE_MEDICAL_RECORDS,jsonData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                medicalRecordCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // JSONArray jsonArray = jsonObject.optJSONArray("records");

                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        String id = jsonObject.getString("id");
                        new GetMedicalInsuranceFolder().execute();
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

    class DeleteMedicalInsurance extends AsyncTask<String, Void, String> {

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
                String date = urls[2].replace(" ","T").concat("Z");
                JSONObject jsonData = createJsonDelete(urls[0],urls[1],date);
                response = JsonData.deleteMedicalRecordPostCall(AddMedicalInsurance.this, ApiManager.CREATE_MEDICAL_RECORDS_DELETE,jsonData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                //medicalRecordCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // JSONArray jsonArray = jsonObject.optJSONArray("records");

                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        String id = jsonObject.getString("id");
                        new GetMedicalInsuranceFolder().execute();
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
    class GetMedicalRecordFolder extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isAddFolder){
                progress.setMessage("Please wait...");
                progress.show();
            }

        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response = JsonData.getMedicalRecordCreated(AddMedicalInsurance.this, ApiManager.GET_MEDICAL_RECORDS,"MEDICAL_RECORDS");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                CommonMethods.hideKeyboard(AddMedicalInsurance.this);
                medicalRecordCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("records");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        MedicalRecordCreationInfo medicalRecordCreationInfo = new MedicalRecordCreationInfo();
                        if (jsonList.has("id") && jsonList.getString("id") != null && !jsonList.getString("id").equalsIgnoreCase("")
                                && !jsonList.getString("id").equalsIgnoreCase("null")) {
                            medicalRecordCreationInfo.setFolderId(jsonList.getString("id"));
                        }
                        if (jsonList.has("incidentName") && jsonList.getString("incidentName") != null && !jsonList.getString("incidentName").equalsIgnoreCase("")
                                && !jsonList.getString("incidentName").equalsIgnoreCase("null")) {
                            medicalRecordCreationInfo.setFolderName(jsonList.getString("incidentName"));
                        }
                        if (jsonList.has("incidentDate") && jsonList.getString("incidentDate") != null && !jsonList.getString("incidentDate").equalsIgnoreCase("")
                                && !jsonList.getString("incidentDate").equalsIgnoreCase("null")) {
                            String date = jsonList.getString("incidentDate");
                            date = date.replace("T"," ").replace("Z","").trim();
                            medicalRecordCreationInfo.setFolderCreationDate( CommonMethods.formattedDateFromString(AppConstants.DATE_INPUT_FORMAT,AppConstants.DATE_OUTPUT_FORMAT,date));
                        }
                        if (jsonList.has("incidentType") && jsonList.getString("incidentType") != null && !jsonList.getString("incidentType").equalsIgnoreCase("")
                                && !jsonList.getString("incidentType").equalsIgnoreCase("null")) {
                            medicalRecordCreationInfo.setIncidentType(jsonList.getString("incidentType"));
                        }
                        medicalRecordCreationsArray.add(medicalRecordCreationInfo);
                    }

                    if(medicalRecordCreationsArray!=null && medicalRecordCreationsArray.size() > 0){
                        isAddFolder = false;
                        createRecordFolder(medicalRecordCreationsArray);
                    }else{
                       // CommonMethods.showToastMessage(AddMedicalInsurance.this,"No folder found for you");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progress != null) {
                    progress.dismiss();
                }

            } else {
               // CommonMethods.showToastMessage(AddMedicalInsurance.this,"No folder found for you");
                if (progress != null) {
                    progress.dismiss();
                }
            }

        }

    }

    class MedicalRecordCreation extends AsyncTask<String, Void, String> {

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
                String date = CommonMethods.getCurrentDateTime(AddMedicalInsurance.this).replace(" ","T").concat("Z");
                JSONObject jsonData = createJson(urls[0],date,urls[1]);
                response = JsonData.medicalRecordCreationPostCall(AddMedicalInsurance.this, ApiManager.CREATE_MEDICAL_RECORDS,jsonData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                medicalRecordCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // JSONArray jsonArray = jsonObject.optJSONArray("records");

                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        String id = jsonObject.getString("id");
                        new GetMedicalRecordFolder().execute();
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

    class DeleteMedicalRecord extends AsyncTask<String, Void, String> {

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
                String date = urls[2].replace(" ","T").concat("Z");
                JSONObject jsonData = createJsonDelete(urls[0],urls[1],date);
                response = JsonData.deleteMedicalRecordPostCall(AddMedicalInsurance.this, ApiManager.CREATE_MEDICAL_RECORDS_DELETE,jsonData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                //medicalRecordCreationsArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // JSONArray jsonArray = jsonObject.optJSONArray("records");

                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        String id = jsonObject.getString("id");
                        new GetMedicalRecordFolder().execute();
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
    private JSONObject createJson(String folderName,String creationDate,String incidentType){
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("incidentName", folderName);
            jsonParam.put("incidentDate", creationDate);
            jsonParam.put("incidentType", incidentType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParam;
    }

    private JSONObject createJsonDelete(String folderId,String folderName,String creationDate){
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("id", folderId);
            jsonParam.put("incidentName", folderName);
            jsonParam.put("incidentDate", creationDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParam;
    }
    public  void hideKeyboard() {
        // Check if no view has focus:
        //  Activity activity = (Activity) context;
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void inputFolderName(final int mId,final String incidentType) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.edtFolderName);
        final TextView txtError = (TextView) promptsView.findViewById(R.id.txtError);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //CommonMethods.hideKeyboard(AddMedicalInsurance.this);
                                hideKeyboard();
                                if (userInput.getText().toString().trim().isEmpty()) {
                                    txtError.setVisibility(View.VISIBLE);

                                } else {
                                    txtError.setVisibility(View.GONE);
                                    if(mId ==0 ){
                                        new MedicalInsuranceCreation().execute(userInput.getText().toString(),incidentType);
                                    }else{
                                        new MedicalRecordCreation().execute(userInput.getText().toString(),incidentType);
                                    }

                                }
                                // result.setText(userInput.getText());
                            }
                        }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //CommonMethods.hideKeyboard(AddMedicalInsurance.this);
                        hideKeyboard();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    Dialog alertDialog;
    public  void showAlertDialog(String title,String message,final int index,final ArrayList<MedicalRecordCreationInfo> medicalRecordCreationsArray, final String type) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                if(type.equalsIgnoreCase(medicalRecord)){
                    new DeleteMedicalRecord().execute(medicalRecordCreationsArray.get(index).getFolderId(),
                            medicalRecordCreationsArray.get(index).getFolderName(),
                            medicalRecordCreationsArray.get(index).getFolderCreationDate());
                }else{
                    new DeleteMedicalInsurance().execute(medicalRecordCreationsArray.get(index).getFolderId(),
                            medicalRecordCreationsArray.get(index).getFolderName(),
                            medicalRecordCreationsArray.get(index).getFolderCreationDate());
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    public static AddMedicalInsurance getInstance(){
        return mAddMedicalInsurance;
    }
}
