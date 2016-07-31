package com.health.gounihealth.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.adapter.CityListAdapter;
import com.health.gounihealth.adapter.SearchAdapter;
import com.health.gounihealth.datainfo.ICUTypeInfo;
import com.health.gounihealth.datainfo.SearchTypeInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppConstants;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LAL on 7/24/2016.
 */
public class GlobalSearchActivity extends AppCompatActivity {

    private ArrayList<SearchTypeInfo> searchInfoArray = new ArrayList<SearchTypeInfo>();
    private ProgressDialog progress;
    private String searchTypeId;
    private AutoCompleteTextView autoCompView;
    private TextInputLayout inputLayoutAutoCmpTV;
    private String mSearchValue;
    private String mSearchKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        initialize();
        setArrayListData();
        search();
    }

    private void initialize(){
        progress = new ProgressDialog(this);
        TextView txtHeader = (TextView)findViewById(R.id.txtHeader);
        txtHeader.setText(getString(R.string.labelSearch));

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteLocationTextView);
       // autoCompView.setHint(getString(R.string.searchKeyHint));
        inputLayoutAutoCmpTV = (TextInputLayout) findViewById(R.id.inputLayoutAutoCmpTV);

        autoCompView.addTextChangedListener(new MyTextWatcher(autoCompView));
    }
    private void setArrayListData(){
        SearchTypeInfo searchTypeInfoH = new SearchTypeInfo();
        searchTypeInfoH.setTypeId("H");
        searchTypeInfoH.setSearchValue("Hospital");
        searchInfoArray.add(searchTypeInfoH);

        SearchTypeInfo searchTypeInfoB = new SearchTypeInfo();
        searchTypeInfoB.setTypeId("B");
        searchTypeInfoB.setSearchValue("BloodBank");
        searchInfoArray.add(searchTypeInfoB);

        SearchTypeInfo searchTypeInfoA = new SearchTypeInfo();
        searchTypeInfoA.setTypeId("A");
        searchTypeInfoA.setSearchValue("Ambulance");
        searchInfoArray.add(searchTypeInfoA);

        SearchTypeInfo searchTypeInfoP = new SearchTypeInfo();
        searchTypeInfoP.setTypeId("P");
        searchTypeInfoP.setSearchValue("Pharmacy");
        searchInfoArray.add(searchTypeInfoP);
        setCityListSpinner();
    }
    private void search() {
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethods.hideKeyboard(GlobalSearchActivity.this);
                if(CommonMethods.isConnected(GlobalSearchActivity.this)){
                    if (validateLocation()) {
                    String search = autoCompView.getText().toString().trim().replace(" ","%20");
                     new GetSearchResultAsyncTask().execute(search,mSearchKey);
                    }
                }

            }
        });
    }
    private boolean validateLocation() {
        if (autoCompView.getText().toString().trim().isEmpty()) {
            inputLayoutAutoCmpTV.setError(getString(R.string.emptySearchKey));
            requestFocus(autoCompView);
            return false;
        } else {
            inputLayoutAutoCmpTV.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private void setCityListSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerICUType);
        SearchAdapter cityListAdapter = new SearchAdapter(GlobalSearchActivity.this, R.layout.city_list_items, searchInfoArray);
        spinner.setAdapter(cityListAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSearchValue = ((TextView) view.findViewById(R.id.txtCityName)).getText().toString();
                mSearchKey = searchInfoArray.get(position).getTypeId();
                System.out.println("Profile Name -> " + mSearchValue + " cityId-> " + mSearchKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    class GetSearchResultAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Please wait...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = null;
            try {
                response = JsonData.getGlobalSearchList(GlobalSearchActivity.this, ApiManager.USER_SEARCH_URL,urls[0],urls[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                //icuTypeInfoArray.clear();
                try {
                    if(mSearchKey.equalsIgnoreCase("H")){
                         Intent intent = new Intent(GlobalSearchActivity.this,HospitalListActivity.class);
                         intent.putExtra("JSONDATA",result.toString());
                         startActivity(intent);
                    }else if(mSearchKey.equalsIgnoreCase("B")){
                        Intent intent = new Intent(GlobalSearchActivity.this,BloodBanksActivity.class);
                        intent.putExtra("JSONDATA",result.toString());
                        startActivity(intent);
                    }
                   /* JSONArray jsonArray = jsonObject.optJSONArray("icuTypes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonCity = jsonArray.getJSONObject(i);

                        ICUTypeInfo icuTypeInfo = new ICUTypeInfo();
                        if (jsonCity.has("id") && jsonCity.getString("id") != null && !jsonCity.getString("id").equalsIgnoreCase("")
                                && !jsonCity.getString("id").equalsIgnoreCase("null")) {
                            icuTypeInfo.setIcuTypeId(jsonCity.getString("id"));
                        }

                        if (jsonCity.has("value") && jsonCity.getString("value") != null && !jsonCity.getString("value").equalsIgnoreCase("")
                                && !jsonCity.getString("value").equalsIgnoreCase("null")) {
                            icuTypeInfo.setIcuTypeValue(jsonCity.getString("value"));
                        }
                   //     icuTypeInfoArray.add(icuTypeInfo);

                        // binding an array of Strings
                      *//*  if (icuTypeInfoArray.size() > 0) {
                            setCityListSpinner(icuTypeInfoArray);
                        }*//*

                    }
*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null) {
                    progress.dismiss();
                }

            } else {
               /// showAlertDialog(getString(R.string.alertTitle),getString(R.string.alertMessage));
                if (progress != null) {
                    progress.dismiss();
                }
            }

        }

    }
    public  void showAlertDialog(String title,String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AppSharedPreferences.saveAccessToken(GlobalSearchActivity.this,"");
                Intent intent = new Intent(GlobalSearchActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.autoCompleteLocationTextView:
                    validateLocation();
                    break;


            }
        }
    }


}
