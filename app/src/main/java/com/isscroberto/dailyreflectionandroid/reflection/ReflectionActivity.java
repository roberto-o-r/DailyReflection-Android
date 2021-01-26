package com.isscroberto.dailyreflectionandroid.reflection;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.google.android.gms.ads.LoadAdError;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.isscroberto.dailyreflectionandroid.R;
import com.isscroberto.dailyreflectionandroid.analytics.AnalyticsHelper;
import com.isscroberto.dailyreflectionandroid.analytics.EventType;
import com.isscroberto.dailyreflectionandroid.data.models.Item;
import com.isscroberto.dailyreflectionandroid.data.models.Reflection;
import com.isscroberto.dailyreflectionandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionRemoteDataSource;
import com.isscroberto.dailyreflectionandroid.databinding.ActivityReflectionBinding;
import com.isscroberto.dailyreflectionandroid.reflectionssaved.ReflectionsSavedActivity;
import com.isscroberto.dailyreflectionandroid.settings.SettingsActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class ReflectionActivity extends AppCompatActivity implements ReflectionContract.View, SwipeRefreshLayout.OnRefreshListener, TextToSpeech.OnInitListener {

    private ReflectionContract.Presenter presenter;
    private FirebaseAnalytics firebaseAnalytics;
    private Item reflection;
    private ActivityReflectionBinding binding;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding.
        binding = ActivityReflectionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonFav.setOnClickListener((View v) -> buttonFavOnClick(view));
        binding.buttonPlay.setOnClickListener((View v) -> playPrayer());

        // Setup toolbar.
        setSupportActionBar(binding.toolbar);

        // Feedback.
        if (savedInstanceState == null) {
            DefaultLayoutPromptView promptView = findViewById(R.id.prompt_view);
            Amplify.getSharedInstance().promptIfReady(promptView);
        }

        // Setup swipe refresh layout.
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        // Setup text to speech.
        tts = new TextToSpeech(this, this);

        // AdMob.
        setupAds();

        // Analytics.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the presenter
        presenter = new ReflectionPresenter(new ReflectionRemoteDataSource(), new ReflectionLocalDataSource(), new ImageRemoteDataSource());
        presenter.takeView(this);

        // Load the reflection.
        presenter.reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        tts.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dropView();
        tts.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);
        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            if (reflection != null) {
                // Log share event.
                AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Share, null);

                // App's link to append.
                String link = "Daily Prayer https://play.google.com/store/apps/details?id=com.isscroberto.dailyreflectionandroid";

                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Daily Reflection");
                i.putExtra(android.content.Intent.EXTRA_TEXT, reflection.getDescription() + link);
                startActivity(Intent.createChooser(i, "Share this Daily Reflection"));
            }
        } else if (id == R.id.menu_item_favorites) {
            navigateToFavorites();
        } else if (id == R.id.menu_item_settings) {
            navigateToSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                // Verify if ads are enabled.
                boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyreflectionandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
                if (!adsEnabled) {
                    binding.adWrapper.setVisibility(View.GONE);
                }
                break;
            case 2:
                presenter.reload();
                break;
        }
    }

    @Override
    public void showReflection(Item reflection) {
        this.reflection = reflection;
        String description = this.reflection.getDescription();
        if (description.contains("<hr>")) {
            this.reflection.setDescription(description.substring(0, description.lastIndexOf("<hr>")));
        }
        this.reflection.setDescription(Html.fromHtml(this.reflection.getDescription()).toString());
        binding.textTitle.setText(this.reflection.getTitle());
        binding.textContent.setText(this.reflection.getDescription());
        if (this.reflection.getFav()) {
            binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
        } else {
            binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
        }
    }

    @Override
    public void showError() {
        binding.textTitle.setText("Error loading reflection. Please try again.\nPull down to refresh.");
        binding.textContent.setText("");
    }

    @Override
    public void logError(String message) {
        // Log error.
        Bundle params = new Bundle();
        params.putString("error_message", message);
        AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Error, params);
    }

    @Override
    public void showImage(String url) {
        Picasso.with(this).load(url).fit().centerCrop().into(binding.imageBack);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            binding.layoutProgress.setVisibility(View.VISIBLE);
            binding.buttonFav.setVisibility(View.GONE);
            binding.buttonPlay.setVisibility(View.GONE);
        } else {
            binding.layoutProgress.setVisibility(View.GONE);
            if (binding.swipeRefreshLayout.isRefreshing()) {
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Reflection Updated!", Toast.LENGTH_SHORT).show();
            }
            redrawFab();
        }
    }

    @Override
    public void onRefresh() {
        presenter.reload();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                binding.buttonPlay.setEnabled(false);
            } else {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_arrow_white_24dp));
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }

                    @Override
                    public void onStop(String utteranceId, boolean interrupted) {
                        super.onStop(utteranceId, interrupted);
                        binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_arrow_white_24dp));
                    }
                });
            }
        } else {
            binding.buttonPlay.setEnabled(false);
        }
    }

    public void buttonFavOnClick(View view) {
        if (reflection != null) {
            // Create reflection id based on the date.
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("gmt"));
            String id = df.format(new Date());

            if (!reflection.getFav()) {
                // Prepare reflection for storage.
                Reflection newReflection = new Reflection();
                newReflection.setId(id);
                newReflection.setTitle(reflection.getTitle());
                newReflection.setDescription(reflection.getDescription());

                // Save reflection.
                presenter.saveReflection(newReflection);
                reflection.setFav(true);

                AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Favorite, null);
            } else {
                // Remove reflection from favorites.
                presenter.deleteReflection(id);
                reflection.setFav(false);
            }

            redrawFab();
        }
    }

    private void navigateToSettings() {
        // Settings.
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    private void navigateToFavorites() {
        // Favorites.
        Intent intent = new Intent(this, ReflectionsSavedActivity.class);
        startActivityForResult(intent, 2);
    }

    private void redrawFab() {
        // Fav button.
        binding.buttonFav.hide();
        binding.buttonPlay.hide();
        if (reflection != null) {
            if (reflection.getFav()) {
                binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
            } else {
                binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
            }
        }
        binding.buttonFav.show();
        binding.buttonPlay.show();
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

    private void playPrayer() {
        if (reflection != null) {
            if (tts.isSpeaking()) {
                tts.stop();
                binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white_24dp));
            } else {
                tts.speak(reflection.getDescription(), TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString());
                binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop_white_24dp));
                AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Play, null);
            }
        }
    }

}
