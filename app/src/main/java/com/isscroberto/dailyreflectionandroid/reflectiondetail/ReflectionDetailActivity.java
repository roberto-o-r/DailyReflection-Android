package com.isscroberto.dailyreflectionandroid.reflectiondetail;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.isscroberto.dailyreflectionandroid.R;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;
import com.isscroberto.dailyreflectionandroid.databinding.ActivityReflectionDetailBinding;

public class ReflectionDetailActivity extends AppCompatActivity implements ReflectionDetailContract.View {

    private ReflectionDetailContract.Presenter presenter;
    private String mid;
    private ActivityReflectionDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding.
        binding = ActivityReflectionDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Setup toolbar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Reflections");
        }

        // Ads.
        setupAds();

        // Get reflection.
        mid = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        // Show reflection.
        binding.textTitle.setText(title);
        binding.textContent.setText(description);

        // Create the presenter
        presenter = new ReflectionDetailPresenter(new ReflectionLocalDataSource());
        presenter.takeView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dropView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.reflection, menu);

        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ReflectionDetailActivity.this);
            alert.setTitle("Delete");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", (dialog, which) -> {
                presenter.deleteReflection(mid);
                finish();
            });

            alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupAds() {
        // Verify if ads are enabled.
        boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyreflectionandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
        if (adsEnabled) {
            // Load Ad Banner.
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.adView.loadAd(adRequest);

            binding.adView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    binding.adWrapper.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    super.onAdFailedToLoad(adError);
                    binding.adWrapper.setVisibility(View.GONE);
                }
            });
        }
    }
}
