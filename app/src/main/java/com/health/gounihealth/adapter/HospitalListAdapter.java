package com.health.gounihealth.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.HospitalListInfo;
import com.health.gounihealth.datainfo.IcuListInfo;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import java.util.List;

/**
 * Created by LAL on 7/11/2016.
 */
public class HospitalListAdapter extends RecyclerView.Adapter<HospitalListAdapter.MyViewHolder> {

    private List<HospitalListInfo> moviesList;
    private Context context;
    HospitalListInfo icuListInfo;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHospitalName, txtDistance, txtAddress;
        private LinearLayout layoutAvailability;
        private Button btnMoreInfo;
        private ImageView imgCall, imgDirection;

        public MyViewHolder(View view) {
            super(view);
            txtHospitalName = (TextView) view.findViewById(R.id.txtHospitalName);
            txtDistance = (TextView) view.findViewById(R.id.txtDistance);
            txtAddress = (TextView) view.findViewById(R.id.txtAddress);
            layoutAvailability = (LinearLayout) view.findViewById(R.id.layoutAvailability);
            btnMoreInfo = (Button) view.findViewById(R.id.btnMoreInfo);
            imgCall = (ImageView) view.findViewById(R.id.imgCall);
            imgDirection = (ImageView) view.findViewById(R.id.imgDirection);
        }
    }


    public HospitalListAdapter(Context context, List<HospitalListInfo> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hospital_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        icuListInfo = moviesList.get(position);

        /*if(icuListInfo.getBedAvailability()){
            holder.layoutAvailability.setBackgroundColor(context.getResources().getColor(R.color.Brown));
        }else{
            holder.layoutAvailability.setBackgroundColor(context.getResources().getColor(R.color.PowderBlue));
        }*/

        holder.txtHospitalName.setText(icuListInfo.getHospitalName());
        holder.txtDistance.setText(icuListInfo.getDistance() + "Km");
        holder.txtAddress.setText(icuListInfo.getAddress());

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + moviesList.get(position).getContactEmergency()));
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
                CommonMethods.routeDirection(context, AppSharedPreferences.getLatitude(context),AppSharedPreferences.getLongitude(context)
                        , moviesList.get(position).getLatitude(), moviesList.get(position).getLongitude());
            }
        });

    }

   /* public void bind(final HospitalListInfo item, final AdapterView.OnItemClickListener listener) {
        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }*/

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}