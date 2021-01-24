package com.isscroberto.dailyreflectionandroid.splash;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.isscroberto.dailyreflectionandroid.reflection.ReflectionActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ReflectionActivity.class);
        startActivity(intent);
        finish();
    }
}
