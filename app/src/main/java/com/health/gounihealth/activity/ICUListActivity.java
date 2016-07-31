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
import com.health.gounihealth.adapter.ICUListAdapter;
import com.health.gounihealth.datainfo.IcuListInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LAL on 7/10/2016.
 */
public class ICUListActivity extends AppCompatActivity {

    private ArrayList<IcuListInfo> icuListInfoList = new ArrayList<IcuListInfo>();
    private RecyclerView recyclerView;
    private ICUListAdapter mAdapter;
    private ProgressDialog progress;
    private String icuTypeId;
    private Double latitude,longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.icu_list_layout);


        recyclerView = (RecyclerView) findViewById(R.id.icuListRecycleView);
        progress = new ProgressDialog(this);

        Intent intent = getIntent();
        icuTypeId = intent.getStringExtra("ICUTYPEID");
        latitude = intent.getDoubleExtra("LATITUDE",0);
        longitude = intent.getDoubleExtra("LONGITUDE",0);

        TextView txtHeader = (TextView)findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.labelIcuList));

        new GetICUTypeAsyncTask().execute();
    }
    private void setAdapter(ArrayList<IcuListInfo> icuListInfoList){
        mAdapter = new ICUListAdapter(this,icuListInfoList);
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
                response = JsonData.getICUList(ICUListActivity.this, ApiManager.ICU_LIST_URL,latitude,longitude,icuTypeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        int totalCount;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                icuListInfoList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("records");
                    totalCount = jsonObject.getInt("totalRecordCount");
                    System.out.println("TotalCount -> "+totalCount);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        IcuListInfo icuListInfo = new IcuListInfo();
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
                        if (jsonListObj.has("hospName") && jsonListObj.getString("hospName") != null && !jsonListObj.getString("hospName").equalsIgnoreCase("")
                                && !jsonListObj.getString("hospName").equalsIgnoreCase("null")) {
                            icuListInfo.setHospitalName(jsonListObj.getString("hospName"));
                        }

                        if (jsonListObj.has("bedAvailable")) {
                            icuListInfo.setBedAvailability(jsonListObj.getBoolean("bedAvailable"));
                        }
                        if (jsonListObj.has("address") && jsonListObj.getString("address") != null && !jsonListObj.getString("address").equalsIgnoreCase("")
                                && !jsonListObj.getString("address").equalsIgnoreCase("null")) {
                            icuListInfo.setAddress(jsonListObj.getString("address"));
                        }

                        if (jsonList.has("dist")) {
                            icuListInfo.setDistance(String.valueOf(jsonList.getInt("dist")));
                        }

                        icuListInfoList.add(icuListInfo);

                    }
                    // binding an array of Strings
                    if(icuListInfoList.size()>0){
                        setAdapter(icuListInfoList);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(totalCount == 0){
                    CommonMethods.showToastMessage(ICUListActivity.this,getString(R.string.mesgNoData));
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
