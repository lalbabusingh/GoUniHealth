package com.health.gounihealth.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.health.gounihealth.R;

/**
 * Created by LAL on 6/11/2016.
 */
public class EmergencyFragment  extends Fragment {

    public EmergencyFragment() {
        // Required empty public constructor
    }
    Context context;
    private Spinner citySpinner;
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
        setSpinnerData();
        return view;
    }
   private void initialization(View view){
       citySpinner = (Spinner)view.findViewById(R.id.citySpinner);
   }

    private void setSpinnerData(){
        // Declaring an Adapter and initializing it to the data pump
        try{
            ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1 ,cityName);
            citySpinner.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }


// Setting OnItemClickListener to the Spinner
       // citySpinner.setOnItemSelectedListener(context);
    }
}