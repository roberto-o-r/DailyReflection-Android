package com.isscroberto.dailyreflectionandroid.reflection;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.isscroberto.dailyreflectionandroid.R;
import com.isscroberto.dailyreflectionandroid.BuildConfig;
import com.isscroberto.dailyreflectionandroid.data.models.Item;
import com.isscroberto.dailyreflectionandroid.data.models.Reflection;
import com.isscroberto.dailyreflectionandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionRemoteDataSource;
import com.isscroberto.dailyreflectionandroid.reflectionssaved.ReflectionsSavedActivity;
import com.isscroberto.dailyreflectionandroid.settings.SettingsActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReflectionActivity extends AppCompatActivity implements ReflectionContract.View, SwipeRefreshLayout.OnRefreshListener {

    //----- Bindings.
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.layout_progress)
    RelativeLayout layoutProgress;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_content)
    TextView textContent;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.ad_view)
    AdView adView;
    @BindView(R.id.button_fav)
    FloatingActionButton buttonFav;

    private ReflectionContract.Presenter mPresenter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Item mReflection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflection);

        // Bind views with Butter Knife.
        ButterKnife.bind(this);

        // Setup toolbar.
        setSupportActionBar(toolbar);

        // Setup swipe refresh layout.
        swipeRefreshLayout.setOnRefreshListener(this);

        // Feedback.
        if (savedInstanceState == null) {
            DefaultLayoutPromptView promptView = (DefaultLayoutPromptView) findViewById(R.id.prompt_view);
            Amplify.getSharedInstance().promptIfReady(promptView);
        }

        // Verify if ads are enabled.
        Boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyreflectionandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
        if (adsEnabled) {
            // Load Ad Banner.
            AdRequest adRequest;
            if (BuildConfig.DEBUG) {
                adRequest = new AdRequest.Builder()
                        .addTestDevice(getString(R.string.test_device))
                        .build();
            } else {
                adRequest = new AdRequest.Builder().build();
            }
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    adView.setVisibility(View.GONE);
                }
            });
        }

        // Firebase analytics.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the presenter
        new ReflectionPresenter(new ReflectionRemoteDataSource(), new ReflectionLocalDataSource(), new ImageRemoteDataSource(), this);
        mPresenter.start();
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
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                if (mReflection != null) {
                    // Log share event.
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "reflection");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Daily Reflection");
                    i.putExtra(android.content.Intent.EXTRA_TEXT, mReflection.getDescription());
                    startActivity(Intent.createChooser(i, "Share this Daily Reflection"));
                }
                break;
            case R.id.menu_item_favorites:
                navigateToFavorites();
                break;
            case R.id.menu_item_settings:
                navigateToSettings();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // Verify if ads are enabled.
            Boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyreflectionandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
            if (!adsEnabled) {
                adView.setVisibility(View.GONE);
            }
        }
        if (requestCode == 2) {
            // Verify if reflection is favorited.
            mPresenter.start();
        }
    }

    @Override
    public void setPresenter(ReflectionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showReflection(Item reflection) {
        mReflection = reflection;
        String description = mReflection.getDescription();
        if (description.contains("<hr>")) {
            mReflection.setDescription(description.substring(0, description.lastIndexOf("<hr>")));
        }
        mReflection.setDescription(Html.fromHtml(mReflection.getDescription()).toString());
        textTitle.setText(mReflection.getTitle());
        textContent.setText(mReflection.getDescription());
        if(mReflection.getFav()) {
            buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
        } else {
            buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
        }
    }

    @Override
    public void showError() {
        textTitle.setText("Error loading reflection. Please try again.\nPull down to refresh.");
        textContent.setText("");
    }

    @Override
    public void showImage(String url) {
        Picasso.with(this).load(url).fit().centerCrop().into(imageBack);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            layoutProgress.setVisibility(View.VISIBLE);
            buttonFav.setVisibility(View.INVISIBLE);
        } else {
            layoutProgress.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Reflection Updated!", Toast.LENGTH_SHORT).show();
            }
            buttonFav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.start();
    }

    @OnClick(R.id.button_fav)
    public void buttonFavOnClick(View view) {
        if (mReflection != null) {
            // Create reflection id based on the date.
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("gmt"));
            String id = df.format(new Date());

            if(!mReflection.getFav()) {
                // Prepare reflection for storage.
                Reflection newReflection = new Reflection();
                newReflection.setId(id);
                newReflection.setTitle(mReflection.getTitle());
                newReflection.setDescription(mReflection.getDescription());

                // Save reflection.
                mPresenter.saveReflection(newReflection);
                mReflection.setFav(true);
                buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
            } else {
                // Remove reflection from favorites.
                mPresenter.deleteReflection(id);
                mReflection.setFav(false);
                buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
            }
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

}
