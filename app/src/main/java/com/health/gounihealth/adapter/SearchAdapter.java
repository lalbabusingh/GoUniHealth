package com.health.gounihealth.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.health.gounihealth.R;
import com.health.gounihealth.datainfo.SearchTypeInfo;

import java.util.ArrayList;

/**
 * Created by LAL on 7/24/2016.
 */
public class SearchAdapter extends ArrayAdapter<SearchTypeInfo> {

    ArrayList<SearchTypeInfo> searchInfosList = new ArrayList<SearchTypeInfo>();
    LayoutInflater inflater;
    private Context context;
    ViewHolder holder = null;

    public SearchAdapter(Context context,int textViewResourceId, ArrayList<SearchTypeInfo> searchInfosList){
        super(context, textViewResourceId, searchInfosList);
        inflater = ((Activity) context).getLayoutInflater();
        this.searchInfosList = searchInfosList;
        this.context = context;

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
      /*  int count = super.getCount();
        return count > 0 ? count - 1 : count;*/
         return searchInfosList.size();
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

        SearchTypeInfo listItem = searchInfosList.get(position);
        View row = convertView;

        if (null == row) {
            holder = new ViewHolder();
            row = inflater.inflate(R.layout.city_list_items, parent, false);
            holder.txtCityName = (TextView) row.findViewById(R.id.txtCityName);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.txtCityName.setText(listItem.getSearchValue());

        return row;
    }

    static class ViewHolder {
        TextView txtCityName;
    }
}
