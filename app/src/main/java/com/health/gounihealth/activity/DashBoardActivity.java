package com.health.gounihealth.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.health.gounihealth.JsonCalls.JsonData;
import com.health.gounihealth.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.health.gounihealth.fragment.AppointmentFragment;
import com.health.gounihealth.fragment.EmergencyFragment;
import com.health.gounihealth.utils.ApiManager;
import com.health.gounihealth.utils.AppConstants;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

/**
 * Created by LAL on 6/6/2016.
 */
public class DashBoardActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,NavigationView.OnNavigationItemSelectedListener {

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static double currentLatitude;
    public static double currentLongitude;

    private DrawerLayout mDrawerLayout;
    private ImageView imgProfilePicture;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private TextView txtProfileName,txtCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_board_layout);

        AppSharedPreferences.saveLatitude(this,0);
        AppSharedPreferences.saveLongitude(this,0);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        initialization();

        finishActivites();

        setting();
        toolBar();
        menuSlider();
        //    floatingActionBar();
        //viewPagerAdapter();

    }

    private void initialization() {
      /*  imgProfilePicture = (ImageView)findViewById(R.id.imgProfilePicture);
        imgProfilePicture.setBackgroundResource(R.mipmap.person);*/

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView=navigationView.getHeaderView(0);
        txtProfileName = (TextView)headerView.findViewById(R.id.txtProfileName);
        txtCityName = (TextView)headerView.findViewById(R.id.txtCityName);
        imgProfilePicture = (ImageView) headerView.findViewById(R.id.imgProfilePicture);


        if(AppSharedPreferences.getProfileName(this)!=null){
            txtProfileName.setText(AppSharedPreferences.getProfileName(this));
        }
       if(AppSharedPreferences.getProfileCityName(this)!=null){
           txtCityName.setText(AppSharedPreferences.getProfileCityName(this));
       }

    }

    private void finishActivites() {
        try {
            if (LoginActivity.getInstance() != null) {
                LoginActivity.getInstance().finish();
            }
            if (RegistrationActivity.getInstance() != null) {
                RegistrationActivity.getInstance().finish();
            }
            if (MyProfileActivity.getInstance() != null) {
                MyProfileActivity.getInstance().finish();
            }
            if (OtpActivity.getInstance() != null) {
                OtpActivity.getInstance().finish();
            }
            if (AddMedicalInsurance.getInstance() != null) {
                AddMedicalInsurance.getInstance().finish();
            }

        } catch (Exception e) {

        }

    }

    private void toolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void getImageFromSdCard(){
        String path = Environment.getExternalStorageDirectory()+ "/GoUniHealth/Capture.jpg";
        File imgFile = new File(path);
        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgProfilePicture.setImageBitmap(myBitmap);
           /* ImageView myImage = (ImageView) findViewById(R.id.imageView1);
            myImage.setImageBitmap(myBitmap);*/
        }
        /*else
            Toast.makeText(this,"no IMAGE IS PRESENT'", Toast.LENGTH_SHORT).show();*/
    }
    private void menuSlider() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
              //  Toast.makeText(DashBoardActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                if(menuItem.getTitle().toString().equalsIgnoreCase(getString(R.string.menuLogOut))){
                    //CommonMethods.saveAccessToken(DashBoardActivity.this,"");
                    new LogOutAsyncTask().execute();

                }
                if(menuItem.getTitle().toString().equalsIgnoreCase(getString(R.string.menuMyRecords))){
                    Intent intent = new Intent(DashBoardActivity.this, AddMedicalInsurance.class);
                    intent.putExtra(AppConstants.CALLING_SCREEN,AppConstants.MENU_RECORDS);
                    startActivity(intent);

                }
                if(menuItem.getTitle().toString().equalsIgnoreCase(getString(R.string.menuViewProfile))){
                    Intent intent = new Intent(DashBoardActivity.this, MyProfileEditActivity.class);
                    startActivity(intent);

                }
                return true;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EmergencyFragment(), getString(R.string.titleEmergency));
        adapter.addFragment(new AppointmentFragment(), getString(R.string.titleAppointment));
        // adapter.addFragment(new InfoFragment(), getString(R.string.titleInfo));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

   /* private void floatingActionBar() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.coordinator), "I'm a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DashBoardActivity.this, "Snackbar Action", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });
    }*/

    class LogOutAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         /*   progress.setMessage("Please wait...");
            progress.show();*/
        }

        @Override
        protected String doInBackground(String... urls) {
            boolean isSuccess = false;
            try {
                isSuccess = JsonData.performLogOutPostCall(DashBoardActivity.this, ApiManager.LOGOUT_URL);
                if(isSuccess){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppSharedPreferences.saveAccessToken(DashBoardActivity.this,"");
                            CommonMethods.showToastMessage(DashBoardActivity.this,"Logout successfully");
                            Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // CommonMethods.saveAccessToken(DashBoardActivity.this,"");
                            CommonMethods.showToastMessage(DashBoardActivity.this,"Logout failure");
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.trim().length() > 1) {

                /*if (progress != null) {
                    progress.dismiss();
                }*/

            } else {
              /*  if (progress != null) {
                    progress.dismiss();
                }*/
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(DashBoardActivity.this,GlobalSearchActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();

       // CommonMethods.showToastMessage(DashBoardActivity.this,"OnResume called");
        try{
            if(AppSharedPreferences.getProfileName(this)!=null){
                txtProfileName.setText(AppSharedPreferences.getProfileName(this));
            }
            if(AppSharedPreferences.getProfileCityName(this)!=null){
                txtCityName.setText(AppSharedPreferences.getProfileCityName(this));
            }
            getImageFromSdCard();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            AppSharedPreferences.saveLatitude(this,currentLatitude);
            AppSharedPreferences.saveLongitude(this,currentLongitude);

           // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        AppSharedPreferences.saveLatitude(this,currentLatitude);
        AppSharedPreferences.saveLongitude(this,currentLongitude);


       // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }

   private void setting(){
       // Get Location Manager and check for GPS & Network location services
       LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
       if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
           // Build the alert dialog
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Location Services Not Active");
           builder.setMessage("Please enable Location Services and GPS");
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialogInterface, int i) {
                   // Show location settings when the user acknowledges the alert dialog
                   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                   startActivity(intent);
               }
           });
           Dialog alertDialog = builder.create();
           alertDialog.setCanceledOnTouchOutside(false);
           alertDialog.show();
       }
   }
}
