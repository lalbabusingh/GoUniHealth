package com.health.gounihealth.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.adapter.BloodBanksAdapter;
import com.health.gounihealth.datainfo.HospitalListInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LAL on 7/16/2016.
 */
public class BloodBanksActivity extends AppCompatActivity {

    private ArrayList<HospitalListInfo> bloodBanksList = new ArrayList<HospitalListInfo>();
    private RecyclerView recyclerView;
    private BloodBanksAdapter mAdapter;
    private ProgressDialog progress;
    public  double currentLatitude;
    public  double currentLongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.blood_bank_layout);


        recyclerView = (RecyclerView) findViewById(R.id.bloodBanksListRecycleView);
        progress = new ProgressDialog(this);

        TextView txtHeader = (TextView)findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.labelBloodBanksList));

        Intent intent = getIntent();
        if(intent.getStringExtra("JSONDATA")!=null){
            jsonParser(intent.getStringExtra("JSONDATA"));
        }else{
            currentLatitude = intent.getDoubleExtra("LATITUDE",0);
            currentLongitude = intent.getDoubleExtra("LONGITUDE",0);
            new GetBloodBanksListAsyncTask().execute();
        }



    }
    private void setAdapter(ArrayList<HospitalListInfo> bloodBanksList){
        mAdapter = new BloodBanksAdapter(this,bloodBanksList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }
    int totalCount;
    class GetBloodBanksListAsyncTask extends AsyncTask<String, Void, String> {

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
                //CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
                response = JsonData.getBloodBanksList(BloodBanksActivity.this, ApiManager.USER_BLOOD_BANK,currentLatitude,currentLongitude);
              //  response = CommonMethods.loadJSONFromAsset(BloodBanksActivity.this,"hlist.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                jsonParser(result);
            } else {
                if (progress != null) {
                    progress.dismiss();
                }
            }

        }

    }
    private void jsonParser(String result){
        bloodBanksList.clear();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.optJSONArray("records");
            totalCount = jsonObject.getInt("totalRecordCount");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonList = jsonArray.getJSONObject(i);
                HospitalListInfo icuListInfo = new HospitalListInfo();
                JSONObject jsonListObj = jsonList.getJSONObject("obj");

                if (jsonListObj.has("id") && jsonListObj.getString("id") != null && !jsonListObj.getString("id").equalsIgnoreCase("")
                        && !jsonListObj.getString("id").equalsIgnoreCase("null")) {
                    icuListInfo.setId(jsonListObj.getString("id"));
                }

                if (jsonListObj.has("icuType") && jsonListObj.getString("icuType") != null && !jsonListObj.getString("icuType").equalsIgnoreCase("")
                        && !jsonListObj.getString("icuType").equalsIgnoreCase("null")) {
                    icuListInfo.setIcuType(jsonListObj.getString("icuType"));
                }

                if (jsonListObj.has("hospId") && jsonListObj.getString("hospId") != null && !jsonListObj.getString("hospId").equalsIgnoreCase("")
                        && !jsonListObj.getString("hospId").equalsIgnoreCase("null")) {
                    icuListInfo.setHospitalId(jsonListObj.getString("hospId"));
                }
                if (jsonListObj.has("name") && jsonListObj.getString("name") != null && !jsonListObj.getString("name").equalsIgnoreCase("")
                        && !jsonListObj.getString("name").equalsIgnoreCase("null")) {
                    icuListInfo.setHospitalName(jsonListObj.getString("name"));
                }

                if (jsonListObj.has("bedAvailable")) {
                    icuListInfo.setBedAvailability(jsonListObj.getBoolean("bedAvailable"));
                }
                if (jsonListObj.has("address") && jsonListObj.getString("address") != null && !jsonListObj.getString("address").equalsIgnoreCase("")
                        && !jsonListObj.getString("address").equalsIgnoreCase("null")) {
                    icuListInfo.setAddress(jsonListObj.getString("address"));
                }

                if (jsonListObj.has("contactEmergency") && jsonListObj.getString("contactEmergency") != null && !jsonListObj.getString("contactEmergency").equalsIgnoreCase("")
                        && !jsonListObj.getString("contactEmergency").equalsIgnoreCase("null")) {
                    icuListInfo.setContactEmergency(jsonListObj.getString("contactEmergency"));
                }

                if (jsonListObj.has("lat") && jsonListObj.getString("lat") != null && !jsonListObj.getString("lat").equalsIgnoreCase("")
                        && !jsonListObj.getString("lat").equalsIgnoreCase("null")) {
                    icuListInfo.setLatitude(jsonListObj.getDouble("lat"));
                }

                if (jsonListObj.has("lng") && jsonListObj.getString("lng") != null && !jsonListObj.getString("lng").equalsIgnoreCase("")
                        && !jsonListObj.getString("lng").equalsIgnoreCase("null")) {
                    icuListInfo.setLongitude(jsonListObj.getDouble("lng"));
                }


                if (jsonList.has("dist")) {
                    icuListInfo.setDistance(String.valueOf(jsonList.getInt("dist")));
                }

                bloodBanksList.add(icuListInfo);

            }
            // binding an array of Strings
            if(bloodBanksList.size()>0){
                setAdapter(bloodBanksList);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(totalCount == 0){
            CommonMethods.showToastMessage(BloodBanksActivity.this,getString(R.string.mesgNoData));
        }
        if (progress != null) {
            progress.dismiss();
        }

    }
}
