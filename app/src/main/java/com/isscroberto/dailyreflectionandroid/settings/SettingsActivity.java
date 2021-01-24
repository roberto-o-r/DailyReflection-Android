package com.isscroberto.dailyreflectionandroid.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.isscroberto.dailyreflectionandroid.R;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.isscroberto.dailyreflectionandroid.BuildConfig;
import com.isscroberto.dailyreflectionandroid.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private BillingProcessor billingProcessor;
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Binding.
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonRemoveAds.setOnClickListener((View v) -> btnRemoveAdsOnClick(view));
        binding.textMoreApps.setOnClickListener((View v) -> textMoreAppsOnClick(view));
        binding.textPrivacyPolicy.setOnClickListener((View v) -> textPrivacyPolicy(view));

        // Setup toolbar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Verify if ads are enabled.
        boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyreflectionandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
        if(adsEnabled) {
            // Initialize billing processor.
            billingProcessor = new BillingProcessor(this, getString(R.string.billing_license_key), this);
        } else {
            binding.buttonRemoveAds.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        // Product was purchased succesfully.
        disableAds();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
        // Verify if user already removed ads.
        boolean purchased;
        if (BuildConfig.DEBUG) {
            purchased = billingProcessor.isPurchased("android.test.purchased");
        } else {
            purchased = billingProcessor.isPurchased("com.isscroberto.dailyreflectionandroid.removeads");
        }

        if(purchased) {
            disableAds();
            Toast.makeText(this, "Ads Removed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void disableAds(){
        SharedPreferences.Editor editor = getSharedPreferences("com.isscroberto.dailyreflectionandroid", MODE_PRIVATE).edit();
        editor.putBoolean("AdsEnabled", false);
        editor.apply();

        binding.buttonRemoveAds.setVisibility(View.GONE);
    }

    public void btnRemoveAdsOnClick(View view) {
        if (BuildConfig.DEBUG) {
            billingProcessor.purchase(this, "android.test.purchased");
        } else {
            billingProcessor.purchase(this, "com.isscroberto.dailyreflectionandroid.removeads");
        }
    }

    public void textMoreAppsOnClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:isscroberto")));
    }

    public void textPrivacyPolicy(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://isscroberto.com/daily-bible-privacy-policy/")));
    }

}
