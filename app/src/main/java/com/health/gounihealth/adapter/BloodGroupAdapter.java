package com.health.gounihealth.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.BloodGroupInfo;
import com.health.gounihealth.datainfo.CityInfo;

import java.util.ArrayList;

/**
 * Created by LAL on 7/10/2016.
 */
public class BloodGroupAdapter  extends ArrayAdapter<BloodGroupInfo> {

    ArrayList<BloodGroupInfo> cityInfosList = new ArrayList<BloodGroupInfo>();
    LayoutInflater inflater;
    private Context context;
    ViewHolder holder = null;

    public BloodGroupAdapter(Context context,int textViewResourceId, ArrayList<BloodGroupInfo> cityInfosList){
        super(context, textViewResourceId, cityInfosList);
        inflater = ((Activity) context).getLayoutInflater();
        this.cityInfosList = cityInfosList;
        this.context = context;

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return cityInfosList.size();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
   /* @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return cityInfosList.get(position);
    }*/

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        BloodGroupInfo listItem = cityInfosList.get(position);
        View row = convertView;

        if (null == row) {
            holder = new ViewHolder();
            row = inflater.inflate(R.layout.city_list_items, parent, false);
            holder.txtCityName = (TextView) row.findViewById(R.id.txtCityName);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.txtCityName.setText(listItem.getValue());

        return row;
    }

    static class ViewHolder {
        TextView txtCityName;
    }

}