package com.health.gounihealth.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.health.gounihealth.R;
import com.health.gounihealth.activity.ICUEnquiry;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppConstants;

/**
 * Created by LAL on 6/11/2016.
 */
public class AppointmentFragment extends Fragment implements View.OnClickListener {

    public AppointmentFragment() {
        // Required empty public constructor
    }

    private LinearLayout hospitalLayout,doctorLayout,labLayout;
    private ImageView imgBloodBanks;
    private ImageView imgPharmacy;
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
        imgBloodBanks = (ImageView)view.findViewById(R.id.imgBloodBanks);
        imgBloodBanks.setOnClickListener(this);
        imgPharmacy = (ImageView)view.findViewById(R.id.imgPharmacy);
        imgPharmacy.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgBloodBanks:
                startBloodBankActivity();
                break;
            case R.id.imgPharmacy:
                Intent intent = new Intent(getActivity(),ICUEnquiry.class);
                intent.putExtra(AppConstants.CALLING_SCREEN, AppConstants.SCREEN_PHARMACY);
                startActivity(intent);
                break;
        }
    }

    private void startBloodBankActivity(){
      /*  Intent intent = new Intent(getActivity(),BloodBanksActivity.class);
        startActivity(intent);*/
        Intent intent = new Intent(getActivity(),ICUEnquiry.class);
        intent.putExtra(AppConstants.CALLING_SCREEN, AppConstants.SCREEN_BLOOD_BANK);
        startActivity(intent);
    }

}