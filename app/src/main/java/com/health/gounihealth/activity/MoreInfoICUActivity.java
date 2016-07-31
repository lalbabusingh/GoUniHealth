package com.health.gounihealth.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.adapter.MoreInfoICUAdapter;
import com.health.gounihealth.datainfo.MoreInfoIcuInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LAL on 7/17/2016.
 */
public class MoreInfoICUActivity extends AppCompatActivity {

    private ArrayList<MoreInfoIcuInfo> moreInfoList = new ArrayList<MoreInfoIcuInfo>();
    private RecyclerView recyclerView;
    private MoreInfoICUAdapter mAdapter;
    private ProgressDialog progress;
    private String hospitalId;
    private Double latitude, longitude;
    private TextView txtHospitalName,txtDistance,txtDescription1,txtDescription2;
    private ImageView imgCall,imgDirection;
    String hospitalName = null;
    String hospitalContact = null;
    String distance = null;
    double dLatitude,dLongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.more_info_icu_layout);

        initialize();
        recyclerView = (RecyclerView) findViewById(R.id.moreInfoListRecycleView);
        progress = new ProgressDialog(this);

        TextView txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.labelIcuInfo));
        hospitalId = getIntent().getStringExtra("HOSPITALID");
        new GetICUTypeAsyncTask().execute();
    }

    private void initialize(){
        txtHospitalName = (TextView)findViewById(R.id.txtHospitalName);
        txtDistance = (TextView)findViewById(R.id.txtDistance);
        txtDescription1 = (TextView)findViewById(R.id.txtDescription1);
        txtDescription2 = (TextView)findViewById(R.id.txtDescription2);
        imgCall = (ImageView)findViewById(R.id.imgCall);
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + hospitalContact));
                    if (ActivityCompat.checkSelfPermission(MoreInfoICUActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                } catch (Exception e) {
                    // no activity to handle intent. show error dialog/toast whatever
                }
            }
        });
        imgDirection = (ImageView)findViewById(R.id.imgDirection);
        imgDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethods.routeDirection(MoreInfoICUActivity.this, AppSharedPreferences.getLatitude(MoreInfoICUActivity.this),AppSharedPreferences.getLongitude(MoreInfoICUActivity.this)
                        ,dLatitude, dLongitude);
            }
        });
    }
    private void setAdapter(ArrayList<MoreInfoIcuInfo> icuListInfoList) {
        mAdapter = new MoreInfoICUAdapter(this, icuListInfoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    class GetICUTypeAsyncTask extends AsyncTask<String, Void, String> {

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
                response = JsonData.getMoreHospitalICUList(MoreInfoICUActivity.this, ApiManager.MORE_INFO_HOSPITAL_ICU, hospitalId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                moreInfoList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("hospName") && jsonObject.getString("hospName") != null && !jsonObject.getString("hospName").equalsIgnoreCase("")
                            && !jsonObject.getString("hospName").equalsIgnoreCase("null")) {
                        hospitalName = jsonObject.getString("hospName");
                    }
                    if (jsonObject.has("hospContactNumber") && jsonObject.getString("hospContactNumber") != null
                            && !jsonObject.getString("hospContactNumber").equalsIgnoreCase("") && !jsonObject.getString("hospContactNumber").equalsIgnoreCase("null")) {
                        hospitalContact = jsonObject.getString("hospContactNumber");
                    }
                    if (jsonObject.has("distance") && jsonObject.getString("distance") != null && !jsonObject.getString("distance").equalsIgnoreCase("")
                            && !jsonObject.getString("distance").equalsIgnoreCase("null")) {
                        distance = String.valueOf(jsonObject.getInt("distance"));
                    }
                    if (jsonObject.has("lat") && jsonObject.getString("lat") != null && !jsonObject.getString("lat").equalsIgnoreCase("")
                            && !jsonObject.getString("lat").equalsIgnoreCase("null")) {
                        dLatitude = jsonObject.getDouble("lat");
                    }
                    if (jsonObject.has("lng") && jsonObject.getString("lng") != null && !jsonObject.getString("lng").equalsIgnoreCase("")
                            && !jsonObject.getString("lng").equalsIgnoreCase("null")) {
                        dLongitude = jsonObject.getDouble("lng");
                    }
                    JSONArray jsonArray = jsonObject.optJSONArray("icus");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        MoreInfoIcuInfo icuListInfo = new MoreInfoIcuInfo();
                     //   JSONObject jsonListObj = jsonList.getJSONObject("obj");

                        if (jsonList.has("icuType") && jsonList.getString("icuType") != null && !jsonList.getString("icuType").equalsIgnoreCase("")
                                && !jsonList.getString("icuType").equalsIgnoreCase("null")) {
                            icuListInfo.setIcuType(jsonList.getString("icuType"));
                        }

                        if (jsonList.has("lastUpdated") && jsonList.getString("lastUpdated") != null && !jsonList.getString("lastUpdated").equalsIgnoreCase("")
                                && !jsonList.getString("lastUpdated").equalsIgnoreCase("null")) {
                            icuListInfo.setLastUpdate(jsonList.getString("lastUpdated"));
                        }
                        if (jsonList.has("noOfBeds") && jsonList.getString("noOfBeds") != null && !jsonList.getString("noOfBeds").equalsIgnoreCase("")
                                && !jsonList.getString("noOfBeds").equalsIgnoreCase("null")) {
                            icuListInfo.setBedsNumber(jsonList.getString("noOfBeds"));
                        }

                        if (jsonList.has("noOfBedsOccupied") && jsonList.getString("noOfBedsOccupied") != null && !jsonList.getString("noOfBedsOccupied").equalsIgnoreCase("")
                                && !jsonList.getString("noOfBedsOccupied").equalsIgnoreCase("null")) {
                            icuListInfo.setBedsOccupied(jsonList.getString("noOfBedsOccupied"));
                        }
                        if (jsonList.has("cost") && jsonList.getString("cost") != null && !jsonList.getString("cost").equalsIgnoreCase("")
                                && !jsonList.getString("cost").equalsIgnoreCase("null")) {
                            icuListInfo.setTotalCost(jsonList.getString("cost"));
                        }
                        moreInfoList.add(icuListInfo);
                    }
                    if(hospitalName!=null){
                        txtHospitalName.setText(hospitalName);
                    }
                    if(distance!=null){
                        txtDistance.setText(distance+"KM");
                    }

                    // binding an array of Strings
                    if (moreInfoList.size() > 0) {
                        setAdapter(moreInfoList);
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
}
