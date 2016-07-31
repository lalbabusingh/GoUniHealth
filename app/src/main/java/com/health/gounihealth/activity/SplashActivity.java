package com.health.gounihealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.health.gounihealth.R;
import com.health.gounihealth.utils.AppSharedPreferences;
import com.health.gounihealth.utils.CommonMethods;

/**
 * Created by LAL on 6/8/2016.
 */
public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT =2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

       /* String s = "2016-07-05T10:57:03.000Z";
        s = s.replace("T"," ").replace("Z","").trim();
        try{

            Log.d("ICU s ",s);
            String str = CommonMethods.formattedDateFromString("yyyy-MM-dd HH:mm:ss.SSS","yyyy-MM-dd HH:mm:ss",s);
            Log.d("ICU date ",str);
        }catch (Exception e){
            Log.d("ICU date ","Exception");
        }*/


        delay();
    }

    private void delay(){
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent;
                if(AppSharedPreferences.getAccessToken(SplashActivity.this)!=null
                        && AppSharedPreferences.getAccessToken(SplashActivity.this).length() > 5){
                    System.out.println("AccessToken: "+AppSharedPreferences.getAccessToken(SplashActivity.this));
                    intent = new Intent(SplashActivity.this, DashBoardActivity.class);
                    //intent = new Intent(SplashActivity.this, RegistrationActivity.class);  //Test
                }else{
                    if(AppSharedPreferences.getAppStatus(SplashActivity.this)){
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }else{
                        intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                    }

                    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
