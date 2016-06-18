package com.health.gounihealth.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.health.gounihealth.R;
import com.health.gounihealth.utils.CommonMethods;

/**
 * Created by LAL on 6/11/2016.
 */
public class AppointmentFragment extends Fragment implements View.OnClickListener {

    public AppointmentFragment() {
        // Required empty public constructor
    }

    private LinearLayout hospitalLayout,doctorLayout,labLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.appoitment_layout, container, false);

        initialization(view);

        return view;
    }

    private void initialization(View view){
        hospitalLayout = (LinearLayout)view.findViewById(R.id.hospitalLayout);
        hospitalLayout.setOnClickListener(this);
        doctorLayout = (LinearLayout)view.findViewById(R.id.doctorLayout);
        doctorLayout.setOnClickListener(this);
        labLayout = (LinearLayout)view.findViewById(R.id.labLayout);
        labLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hospitalLayout:
                CommonMethods.showToastMessage(getContext(),"Comming soon...");
                break;
            case R.id.doctorLayout:
                CommonMethods.showToastMessage(getContext(),"Comming soon...");
                break;
            case R.id.labLayout:
                CommonMethods.showToastMessage(getContext(),"Comming soon...");
                break;
        }

    }
}