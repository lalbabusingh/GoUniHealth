package com.health.gounihealth.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.AutoCallInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LAL on 7/11/2016.
 */
public class AutoCallActivity extends AppCompatActivity {

    private ArrayList<AutoCallInfo> autoCallInfoArrayList = new ArrayList<AutoCallInfo>();
    private RecyclerView recyclerView;
    private AutoCallAdapter mAdapter;
    private ProgressDialog progress;
    public double currentLatitude;
    public double currentLongitude;
    private ImageView imgCancel;
    private LinearLayout layoutMain;
    private Button btnRegister;
    ArrayList<String> hospitalIdArray = new ArrayList<>();
    TextView txtCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.auto_call_layout);

        recyclerView = (RecyclerView) findViewById(R.id.autoCalltRecycleView);
        imgCancel = (ImageView) findViewById(R.id.imgCancel);
        layoutMain = (LinearLayout) findViewById(R.id.layoutMain);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        progress = new ProgressDialog(this);
        new GetAutoCallAsyncTask().execute();
        // setListDataTest();
        cancel();
        alertAllHospital();
    }

    private void cancel() {
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void alertAllHospital(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoCallInfoArrayList!=null && autoCallInfoArrayList.size() > 0){
                    for(int i = 0; i < autoCallInfoArrayList.size();i++){
                        hospitalIdArray.add(autoCallInfoArrayList.get(i).getId());
                    }
                    new PanicAsyncTask().execute();
                }
            }
        });
    }
    private void setAdapter(ArrayList<AutoCallInfo> icuListInfoList) {
        mAdapter = new AutoCallAdapter(this, icuListInfoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    class GetAutoCallAsyncTask extends AsyncTask<String, Void, String> {

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
                response = JsonData.getNearByHospitalList(AutoCallActivity.this, ApiManager.HOSPITAL_LIST_URL, AppSharedPreferences.getLatitude(AutoCallActivity.this), AppSharedPreferences.getLongitude(AutoCallActivity.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        int totalCount;

        @Override
        protected void onPostExecute(String result) {
            layoutMain.setVisibility(View.VISIBLE);
            // layoutMain.setBackgroundColor(ContextCompat.getColor(AutoCallActivity.this, R.color.blackTransparent));
            if (result != null && result.trim().length() > 1) {
                autoCallInfoArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("records");
                    totalCount = jsonObject.getInt("totalRecordCount");
                    System.out.println("TotalCount -> " + totalCount);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        AutoCallInfo autoCallInfo = new AutoCallInfo();
                        JSONObject jsonListObj = jsonList.getJSONObject("obj");

                        if (jsonListObj.has("id") && jsonListObj.getString("id") != null && !jsonListObj.getString("id").equalsIgnoreCase("")
                                && !jsonListObj.getString("id").equalsIgnoreCase("null")) {
                            autoCallInfo.setId(jsonListObj.getString("id"));
                        }
                        if (jsonListObj.has("name") && jsonListObj.getString("name") != null && !jsonListObj.getString("name").equalsIgnoreCase("")
                                && !jsonListObj.getString("name").equalsIgnoreCase("null")) {
                            autoCallInfo.setHospitalName(jsonListObj.getString("name"));
                        }
                        if (jsonListObj.has("contactEmergency") && jsonListObj.getString("contactEmergency") != null && !jsonListObj.getString("contactEmergency").equalsIgnoreCase("")
                                && !jsonListObj.getString("contactEmergency").equalsIgnoreCase("null")) {
                            autoCallInfo.setEmergencyContanct(jsonListObj.getString("contactEmergency"));
                        }

                        if (jsonListObj.has("address") && jsonListObj.getString("address") != null && !jsonListObj.getString("address").equalsIgnoreCase("")
                                && !jsonListObj.getString("address").equalsIgnoreCase("null")) {
                            autoCallInfo.setAddress(jsonListObj.getString("address"));
                        }
                        if (jsonListObj.has("lat") && jsonListObj.getString("lat") != null && !jsonListObj.getString("lat").equalsIgnoreCase("")
                                && !jsonListObj.getString("lat").equalsIgnoreCase("null")) {
                            autoCallInfo.setdLatitude(jsonListObj.getDouble("lat"));
                        }
                        if (jsonListObj.has("lng") && jsonListObj.getString("lng") != null && !jsonListObj.getString("lng").equalsIgnoreCase("")
                                && !jsonListObj.getString("lng").equalsIgnoreCase("null")) {
                            autoCallInfo.setdLongitude(jsonListObj.getDouble("lng"));
                        }

                        if (jsonList.has("dist")) {
                            autoCallInfo.setDistance(String.valueOf(jsonList.getInt("dist")));
                        }

                        autoCallInfoArrayList.add(autoCallInfo);

                    }
                    // binding an array of Strings
                    if (autoCallInfoArrayList.size() > 0) {
                        setAdapter(autoCallInfoArrayList);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (totalCount == 0) {
                    CommonMethods.showToastMessage(AutoCallActivity.this, getString(R.string.mesgNoData));
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

    class AutoCallAdapter extends RecyclerView.Adapter<AutoCallAdapter.MyViewHolder> {

        private List<AutoCallInfo> autoCallArrayList;
        private Context context;
        ProgressDialog progress;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtHospitalName, txtDistance, imgCall, imgAlert, imgDirection;
            private LinearLayout layoutAvailability;
            private Button btnMoreInfo;

            public MyViewHolder(View view) {
                super(view);
                txtHospitalName = (TextView) view.findViewById(R.id.txtHospitalName);
                txtDistance = (TextView) view.findViewById(R.id.txtDistance);
                imgCall = (TextView) view.findViewById(R.id.imgCall);
                imgAlert = (TextView) view.findViewById(R.id.imgAlert);
                imgDirection = (TextView) view.findViewById(R.id.imgDirection);
                //  txtAddress = (TextView) view.findViewById(R.id.txtAddress);
                //  layoutAvailability = (LinearLayout) view.findViewById(R.id.layoutAvailability);
                // btnMoreInfo = (Button) view.findViewById(R.id.btnMoreInfo);
            }
        }

        public AutoCallAdapter(Context context, List<AutoCallInfo> autoCallArrayList) {
            this.autoCallArrayList = autoCallArrayList;
            this.context = context;
            progress = new ProgressDialog(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.auto_call_items, parent, false);

            return new MyViewHolder(itemView);
        }

        /* @Override
         public void onClick(View v) {
             if(v.getId() == allAlertButton){
                 for(int i = 0; i < autoCallArrayList.size();i++){
                     hospitalId.add(autoCallArrayList.get(i).getId());
                 }
                 new PanicAsyncTask().execute();
             }

         }*/
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            AutoCallInfo icuListInfo = autoCallArrayList.get(position);

        /*if(icuListInfo.getBedAvailability()){
            holder.layoutAvailability.setBackgroundColor(context.getResources().getColor(R.color.Brown));
        }else{
            holder.layoutAvailability.setBackgroundColor(context.getResources().getColor(R.color.PowderBlue));
        }*/

            holder.txtHospitalName.setText(icuListInfo.getHospitalName());
            holder.txtDistance.setText(icuListInfo.getDistance() + "Km");
            // holder.txtAddress.setText(context.getString(R.string.labelAddress)+":\n"+icuListInfo.getAddress());

            holder.imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + autoCallArrayList.get(position).getEmergencyContanct()));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        context.startActivity(callIntent);
                    } catch (Exception e) {
                        // no activity to handle intent. show error dialog/toast whatever
                    }
                }
            });

            holder.imgDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonMethods.routeDirection(context, AppSharedPreferences.getLatitude(context), AppSharedPreferences.getLongitude(context)
                            , autoCallArrayList.get(position).getdLatitude(), autoCallArrayList.get(position).getdLongitude());
                }
            });

            holder.imgAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hospitalIdArray.add(autoCallArrayList.get(position).getId());
                    new PanicAsyncTask().execute();
                }
            });


        }

        @Override
        public int getItemCount() {
            return autoCallArrayList.size();
        }


    }

    class PanicAsyncTask extends AsyncTask<String, Void, String> {

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
                JSONObject jObj = createJsonObject(AppSharedPreferences.getLatitude(AutoCallActivity.this),AppSharedPreferences.getLongitude(AutoCallActivity.this),hospitalIdArray);
                response = JsonData.performUserPanicPostCall(AutoCallActivity.this, ApiManager.USER_PANIC_URL, jObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String status,panicId;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                //   autoCallInfoArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        panicId = jsonObject.getString("id");

                    }
                    if (jsonObject.has("userId") && jsonObject.getString("userId") != null && !jsonObject.getString("userId").equalsIgnoreCase("")
                            && !jsonObject.getString("userId").equalsIgnoreCase("null")) {
                        String userId = jsonObject.getString("userId");

                    }
                    if (jsonObject.has("createdTime") && jsonObject.getString("createdTime") != null && !jsonObject.getString("createdTime").equalsIgnoreCase("")
                            && !jsonObject.getString("createdTime").equalsIgnoreCase("null")) {
                        String createdTime = jsonObject.getString("createdTime");

                    }
                    if (jsonObject.has("status") && jsonObject.getString("status") != null && !jsonObject.getString("status").equalsIgnoreCase("")
                            && !jsonObject.getString("status").equalsIgnoreCase("null")) {
                        status = jsonObject.getString("status");

                    }
                    Log.d("Timer panicId",panicId);
                    if (jsonObject.has("timeOut")) {
                        //timeOut = jsonObject.getInt("timeOut");
                        customDialog(jsonObject.getInt("timeOut"),panicId);
                    }

                    /*if (status != null) {
                        CommonMethods.showToastMessage(AutoCallActivity.this, status);
                    } else {
                        CommonMethods.showToastMessage(AutoCallActivity.this, "No status");
                    }*/

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

    private JSONObject createJsonObject(double latitude,double longitude,ArrayList hospitalId) {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("lat", latitude);
            jsonParam.put("lng", longitude);
            JSONArray hospitalIdJsonArry = new JSONArray();
            for (int i = 0; i < hospitalId.size(); i++) {
                hospitalIdJsonArry.put(hospitalId.get(i));
            }

            jsonParam.put("hospId", hospitalIdJsonArry);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonParam;
    }
     Dialog dialog;
    private void customDialog(int time,String panicId){
        dialog = new Dialog(AutoCallActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting custom layout to dialog
        dialog.setContentView(R.layout.custom_progressbar);
        txtCount = (TextView)dialog.findViewById(R.id.txtCount);
        timer(panicId);
        countDown(time,dialog);

        dialog.setCancelable(false);
        dialog.show();
    }

    private void countDown(int time,final Dialog dialog){
        CountDownTimer Count = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                long totalTimeRem = millisUntilFinished/1000;
                txtCount.setText("Seconds: " + totalTimeRem);
            }

            public void onFinish() {
               // txtCount.setText("Finished");
                dialog.dismiss();
                if(myTimer!=null){
                    myTimer.cancel();
                }
                new PanicCallAsyncTask().execute();
            }
        };

        Count.start();
    }
    Timer myTimer;
    private void timer(final String panicId){
        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               Log.d("Timer","Timer");
                new PanicStatusAsyncTask().execute(panicId);
            }
        }, 0, 10000);//started after 10 sec
    }

    class PanicStatusAsyncTask extends AsyncTask<String, Void, String> {

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
                response = JsonData.getPanicStatus(AutoCallActivity.this, ApiManager.USER_PANIC_STATUS_URL, urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        String panicStatus;
        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                //   autoCallInfoArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").equalsIgnoreCase("")
                            && !jsonObject.getString("id").equalsIgnoreCase("null")) {
                        String id = jsonObject.getString("id");

                    }
                    if (jsonObject.has("userId") && jsonObject.getString("userId") != null && !jsonObject.getString("userId").equalsIgnoreCase("")
                            && !jsonObject.getString("userId").equalsIgnoreCase("null")) {
                        String userId = jsonObject.getString("userId");

                    }
                    if (jsonObject.has("createdTime") && jsonObject.getString("createdTime") != null && !jsonObject.getString("createdTime").equalsIgnoreCase("")
                            && !jsonObject.getString("createdTime").equalsIgnoreCase("null")) {
                        String createdTime = jsonObject.getString("createdTime");

                    }
                    if (jsonObject.has("status") && jsonObject.getString("status") != null && !jsonObject.getString("status").equalsIgnoreCase("")
                            && !jsonObject.getString("status").equalsIgnoreCase("null")) {
                        panicStatus = jsonObject.getString("status");

                    }
                    Log.d("Timer panicStatus",panicStatus);
                   if(panicStatus.equalsIgnoreCase("SIGNED")){
                       if(dialog!=null){
                           dialog.dismiss();
                       }
                       CommonMethods.showToastMessage(AutoCallActivity.this, "Nearest Hospital will contact you immediately");
                   }
                    /*if (status != null) {
                        CommonMethods.showToastMessage(AutoCallActivity.this, status);
                    } else {
                        CommonMethods.showToastMessage(AutoCallActivity.this, "No status");
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*if (progress != null) {
                    progress.dismiss();
                }*/

            } else {
                /*if (progress != null) {
                    progress.dismiss();
                }*/
            }

        }

    }
    class PanicCallAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* progress.setMessage("Please wait...");
            progress.show();*/
        }

        @Override
        protected String doInBackground(String... urls) {
            boolean response = false;
            try {
                response = JsonData.getPanicCall(AutoCallActivity.this, ApiManager.USER_PANIC_CALL_URL);
                if(response){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonMethods.showToastMessage(AutoCallActivity.this, "Please wait, while we are getting touch with you");
                        }
                    });

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonMethods.showToastMessage(AutoCallActivity.this, "Something wrong...");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        String panicStatus;
        @Override
        protected void onPostExecute(String result) {


        }

    }


}
