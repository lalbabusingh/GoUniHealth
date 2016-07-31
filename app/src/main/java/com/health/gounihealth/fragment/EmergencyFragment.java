package com.health.gounihealth.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.activity.AutoCallActivity;
import com.health.gounihealth.activity.DashBoardActivity;
import com.health.gounihealth.activity.HospitalListActivity;
import com.health.gounihealth.activity.ICUEnquiry;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppConstants;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONObject;

/**
 * Created by LAL on 6/11/2016.
 */
public class EmergencyFragment  extends Fragment {

    public EmergencyFragment() {
        // Required empty public constructor
    }
    Context context;
    private Spinner citySpinner;
    private ProgressDialog progress;
    // Array of City Name as a data pump
    String[] cityName = { "Bengaluru", "Delhi", "Mumbai", "Chennai", "Kolkata",
            "Patna", "Pune", "Hydrabad", "Goa", "NCR Delhi", "Ludhiana","Rachi" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.emergency_layout, container, false);
        context = container.getContext();
        initialization(view);
       // setSpinnerData();
        onClickEnergencyAmbulanc(view);
        onClickEnergencyCare(view);
        onClickICUEnquiry(view);
        return view;
    }
   private void initialization(View view){
       //citySpinner = (Spinner)view.findViewById(R.id.citySpinner);
       progress = new ProgressDialog(getActivity());
   }

    private void onClickEnergencyAmbulanc(View view){
        ImageView imgEmgAmbulance = (ImageView) view.findViewById(R.id.imgEmgAmbulance);
        imgEmgAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // new PanicAsyncTask().execute();
                if(AppSharedPreferences.getLatitude(getContext())!= 0 && AppSharedPreferences.getLongitude(getContext()) !=0){
                    Intent intent = new Intent(getActivity(),AutoCallActivity.class);
                    startActivity(intent);
                }else{
                    CommonMethods.showToastMessage(getContext(),"Please wait for few seconds fetching current location");
                }

            }
        });
    }

    private void onClickEnergencyCare(View view){
        LinearLayout layoutEmergencyCare = (LinearLayout) view.findViewById(R.id.layoutEmergencyCare);
        layoutEmergencyCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HospitalListActivity.class);
                intent.putExtra("LATITUDE", DashBoardActivity.currentLatitude);
                intent.putExtra("LONGITUDE", DashBoardActivity.currentLongitude);
                startActivity(intent);
            }
        });
    }

    private void onClickICUEnquiry(View view){
        LinearLayout layoutIcuEnguiry = (LinearLayout) view.findViewById(R.id.layoutIcuEnguiry);
        layoutIcuEnguiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ICUEnquiry.class);
                intent.putExtra(AppConstants.CALLING_SCREEN, AppConstants.SCREEN_ICUENQUERY);
                startActivity(intent);
            }
        });
    }

    private void setSpinnerData(){
        // Declaring an Adapter and initializing it to the data pump
        try{
            ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1 ,cityName);
            citySpinner.setAdapter(adapter);
        }catch (Exception e){
        }

    }

}