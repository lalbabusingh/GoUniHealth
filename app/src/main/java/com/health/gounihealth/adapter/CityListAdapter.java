package com.health.gounihealth.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.CityInfo;

import java.util.ArrayList;

/**
 * Created by LAL on 7/9/2016.
 */
public class CityListAdapter  extends ArrayAdapter<CityInfo> {

    ArrayList<CityInfo> cityInfosList = new ArrayList<CityInfo>();
    LayoutInflater inflater;
    private Context context;
    ViewHolder holder = null;

    public CityListAdapter(Context context,int textViewResourceId, ArrayList<CityInfo> cityInfosList){
        super(context, textViewResourceId, cityInfosList);
        inflater = ((Activity) context).getLayoutInflater();
        this.cityInfosList = cityInfosList;
        this.context = context;

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
       /* int count = super.getCount();
        return count > 0 ? count - 1 : count;*/
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

        CityInfo listItem = cityInfosList.get(position);
        View row = convertView;

        if (null == row) {
            holder = new ViewHolder();
            row = inflater.inflate(R.layout.city_list_items, parent, false);
            holder.txtCityName = (TextView) row.findViewById(R.id.txtCityName);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.txtCityName.setText(listItem.getName());

        return row;
    }

    static class ViewHolder {
        TextView txtCityName;
    }

}