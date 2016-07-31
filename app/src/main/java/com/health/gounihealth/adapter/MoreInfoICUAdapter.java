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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.HospitalListInfo;
import com.health.gounihealth.datainfo.MoreInfoIcuInfo;
import com.health.gounihealth.utils.CommonMethods;

import java.util.List;

/**
 * Created by LAL on 7/17/2016.
 */
public class MoreInfoICUAdapter extends RecyclerView.Adapter<MoreInfoICUAdapter.MyViewHolder> {

    private List<MoreInfoIcuInfo> moreInfoIcuList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtIcuType, txtTotalBeds, txtAvlBeds,txtLastUp,txtAvgCost;

        public MyViewHolder(View view) {
            super(view);
            txtIcuType = (TextView) view.findViewById(R.id.txtIcuType);
            txtTotalBeds = (TextView) view.findViewById(R.id.txtTotalBeds);
            txtAvlBeds = (TextView) view.findViewById(R.id.txtAvlBeds);
            txtLastUp = (TextView) view.findViewById(R.id.txtLastUp);
            txtAvgCost = (TextView) view.findViewById(R.id.txtAvgCost);
        }
    }


    public MoreInfoICUAdapter(Context context, List<MoreInfoIcuInfo> moreInfoIcuList) {
        this.moreInfoIcuList = moreInfoIcuList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_info_icu_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MoreInfoIcuInfo icuListInfo = moreInfoIcuList.get(position);

        holder.txtIcuType.setText(icuListInfo.getIcuType());
        holder.txtTotalBeds.setText(icuListInfo.getBedsNumber());
        holder.txtAvlBeds.setText(icuListInfo.getBedsOccupied());
        holder.txtLastUp.setText(icuListInfo.getLastUpdate());
        holder.txtAvgCost.setText(icuListInfo.getTotalCost());
    }

    @Override
    public int getItemCount() {
        return moreInfoIcuList.size();
    }
}