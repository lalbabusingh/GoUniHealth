package com.health.gounihealth.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;
import com.health.gounihealth.adapter.ICUTypeAdapter;
import com.health.gounihealth.datainfo.ICUTypeInfo;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppConstants;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by LAL on 7/10/2016.
 */
public class ICUEnquiry extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String LOG_TAG = "ICUEnquiry";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    //------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyAXvI-TyQnsa47sQvh73A5h4DAVLSEB6tI";

    private ArrayList<ICUTypeInfo> icuTypeInfoArray = new ArrayList<ICUTypeInfo>();
    private ProgressDialog progress;
    private String icuTypeId;
    private AutoCompleteTextView autoCompView;
    private TextInputLayout inputLayoutAutoCmpTV;
    private Double latitude,longitude;
    private Spinner spinner;
    private String callingScreen;
    TextView txtHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.icu_enquiry_layout);
        initialize();
        if(getIntent().getStringExtra(AppConstants.CALLING_SCREEN)!=null){
            callingScreen = getIntent().getStringExtra(AppConstants.CALLING_SCREEN);
            if(callingScreen.equalsIgnoreCase(AppConstants.SCREEN_ICUENQUERY)){
                txtHeader.setText(getString(R.string.labelEnquiry));
                spinner.setVisibility(View.VISIBLE);
                new GetICUTypeAsyncTask().execute();
            }else if(callingScreen.equalsIgnoreCase(AppConstants.SCREEN_BLOOD_BANK)){
                txtHeader.setText(getString(R.string.labelBloodBanksSearch));
                spinner.setVisibility(View.GONE);
            }else if(callingScreen.equalsIgnoreCase(AppConstants.SCREEN_PHARMACY)){
                txtHeader.setText(getString(R.string.labelPharmacySearh));
                spinner.setVisibility(View.GONE);
            }

        }else{
            spinner.setVisibility(View.GONE);
        }

        search();
    }

    private void initialize() {
        progress = new ProgressDialog(this);
        spinner = (Spinner) findViewById(R.id.spinnerICUType);
        txtHeader = (TextView)findViewById(R.id.txtHeader);

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteLocationTextView);

        autoCompView.setText(CommonMethods.getCompleteAddress(this,AppSharedPreferences.getLatitude(this),AppSharedPreferences.getLongitude(this)).trim());

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.place_item_list));
        autoCompView.setOnItemClickListener(this);

        inputLayoutAutoCmpTV = (TextInputLayout) findViewById(R.id.inputLayoutAutoCmpTV);

        autoCompView.addTextChangedListener(new MyTextWatcher(autoCompView));
    }

    private void search() {
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLocation()) {
                    if(callingScreen.equalsIgnoreCase(AppConstants.SCREEN_ICUENQUERY)){
                        Intent intent = new Intent(ICUEnquiry.this, ICUListActivity.class);
                        setIntentData(intent);
                        intent.putExtra("ICUTYPEID",icuTypeId);
                        startActivity(intent);
                    }else if(callingScreen.equalsIgnoreCase(AppConstants.SCREEN_BLOOD_BANK)){
                        Intent intent = new Intent(ICUEnquiry.this,BloodBanksActivity.class);
                        setIntentData(intent);
                        startActivity(intent);
                    }else if(callingScreen.equalsIgnoreCase(AppConstants.SCREEN_PHARMACY)){
                        Intent intent = new Intent(ICUEnquiry.this,PharmacyActivity.class);
                        setIntentData(intent);
                        startActivity(intent);
                    }

                }

            }
        });
    }

    private void setIntentData(Intent intent){
        if(latitude!=null && longitude!=null){
            intent.putExtra("LATITUDE",latitude);
            intent.putExtra("LONGITUDE",longitude);
            Log.d("LATITUDE ",""+latitude);
            Log.d("LONGITUDE ",""+longitude);
        }else{
            intent.putExtra("LATITUDE", AppSharedPreferences.getLatitude(this));
            intent.putExtra("LONGITUDE",AppSharedPreferences.getLongitude(this));
            Log.d("LATITUDE else",""+AppSharedPreferences.getLatitude(this));
            Log.d("LONGITUDE else",""+AppSharedPreferences.getLongitude(this));
        }
    }

    private boolean validateLocation() {
        if (autoCompView.getText().toString().trim().isEmpty()) {
            inputLayoutAutoCmpTV.setError(getString(R.string.emptyLocation));
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

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
       /* LatLng la = CommonMethods.getLocationFromAddress(this,str);
        System.out.println("URL: "+la);*/
        latitude = CommonMethods.getLatFromAddress(this, str);
        longitude = CommonMethods.getLongFromAddress(this, str);
        CommonMethods.hideKeyboard(this);
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                // System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                //  System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private void setCityListSpinner(final ArrayList<ICUTypeInfo> icuTypeInfoArray) {
        ICUTypeAdapter cityListAdapter = new ICUTypeAdapter(ICUEnquiry.this, R.layout.city_list_items, icuTypeInfoArray);
        spinner.setAdapter(cityListAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // mCityName = ((TextView)view.findViewById(R.id.txtCityName)).getText().toString();
                icuTypeId = icuTypeInfoArray.get(position).getIcuTypeId();
               // System.out.println("icuTypeId " + icuTypeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    class GetICUTypeAsyncTask extends AsyncTask<String, Void, String> {

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
                response = JsonData.getICUTypeList(ICUEnquiry.this, ApiManager.ICU_TYPE_LIST_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {
                icuTypeInfoArray.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("icuTypes");
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
                        icuTypeInfoArray.add(icuTypeInfo);

                        // binding an array of Strings
                        if (icuTypeInfoArray.size() > 0) {
                            setCityListSpinner(icuTypeInfoArray);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progress != null) {
                    progress.dismiss();
                }

            } else {
                showAlertDialog("Something Wrong","Please login again");
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
                AppSharedPreferences.saveAccessToken(ICUEnquiry.this,"");
                Intent intent = new Intent(ICUEnquiry.this, LoginActivity.class);
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
