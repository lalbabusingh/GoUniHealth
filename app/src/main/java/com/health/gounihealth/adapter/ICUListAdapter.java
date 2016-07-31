package com.health.gounihealth.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.health.gounihealth.R;
import com.health.gounihealth.activity.MoreInfoICUActivity;
import com.health.gounihealth.datainfo.IcuListInfo;

import java.util.List;

/**
 * Created by LAL on 7/10/2016.
 */
public class ICUListAdapter  extends RecyclerView.Adapter<ICUListAdapter.MyViewHolder> {

    private List<IcuListInfo> moviesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHospitalName, txtDistance, txtAddress;
        private LinearLayout layoutAvailability;
        private Button btnMoreInfo;

        public MyViewHolder(View view) {
            super(view);
            txtHospitalName = (TextView) view.findViewById(R.id.txtHospitalName);
            txtDistance = (TextView) view.findViewById(R.id.txtDistance);
            txtAddress = (TextView) view.findViewById(R.id.txtAddress);
            layoutAvailability = (LinearLayout) view.findViewById(R.id.layoutAvailability);
            btnMoreInfo = (Button) view.findViewById(R.id.btnMoreInfo);
        }
    }


    public ICUListAdapter(Context context,List<IcuListInfo> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icu_list_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        IcuListInfo icuListInfo = moviesList.get(position);

        if(icuListInfo.getBedAvailability()){
            holder.layoutAvailability.setBackgroundColor(context.getResources().getColor(R.color.Brown));
        }else{
            holder.layoutAvailability.setBackgroundColor(context.getResources().getColor(R.color.PowderBlue));
        }

        holder.txtHospitalName.setText(icuListInfo.getHospitalName());
        holder.txtDistance.setText(icuListInfo.getDistance()+"Km");
        holder.txtAddress.setText(icuListInfo.getAddress());

        holder.btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MoreInfoICUActivity.class);
                intent.putExtra("HOSPITALID",moviesList.get(position).getHospitalId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}