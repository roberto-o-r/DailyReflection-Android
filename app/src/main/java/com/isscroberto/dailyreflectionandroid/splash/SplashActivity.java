package com.isscroberto.dailyreflectionandroid.splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.isscroberto.dailyreflectionandroid.R;
import com.isscroberto.dailyreflectionandroid.reflection.ReflectionActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // AdMob.
        MobileAds.initialize(this, getString(R.string.ad_mob_app_id));

        Intent intent = new Intent(this, ReflectionActivity.class);
        startActivity(intent);
        finish();
    }
}
