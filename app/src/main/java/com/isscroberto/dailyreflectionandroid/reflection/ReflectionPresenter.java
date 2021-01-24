package com.isscroberto.dailyreflectionandroid.reflection;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.isscroberto.dailyreflectionandroid.data.models.BingResponse;
import com.isscroberto.dailyreflectionandroid.data.models.Item;
import com.isscroberto.dailyreflectionandroid.data.models.Reflection;
import com.isscroberto.dailyreflectionandroid.data.models.RssResponse;
import com.isscroberto.dailyreflectionandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionRemoteDataSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionPresenter implements ReflectionContract.Presenter {

    private final ReflectionRemoteDataSource reflectionRemoteDataSource;
    private final ReflectionLocalDataSource reflectionLocalDataSource;
    private final ImageRemoteDataSource imageRemoteDataSource;
    private ReflectionContract.View view;

    public ReflectionPresenter(ReflectionRemoteDataSource reflectionDataSource, ReflectionLocalDataSource reflectionLocalDataSource, ImageRemoteDataSource imageDataSource) {
        reflectionRemoteDataSource = reflectionDataSource;
        this.reflectionLocalDataSource = reflectionLocalDataSource;
        imageRemoteDataSource = imageDataSource;
    }

    @Override
    public void reload() {
        loadReflection();
        loadImage();
    }

    @Override
    public void loadReflection() {
        view.setLoadingIndicator(true);
        reflectionRemoteDataSource.get(new Callback<RssResponse>() {
            @Override
            public void onResponse(@Nonnull Call<RssResponse> call, @Nonnull Response<RssResponse> response) {
                // Verify that response is not empty.
                if(response.body() != null) {
                    Item reflection = response.body().getChannel().getItem();
                    reflection.setTitle(reflection.getTitle().replace("Dig", "Reflection"));

                    // Create reflection id based on the date.
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    df.setTimeZone(TimeZone.getTimeZone("gmt"));
                    String id = df.format(new Date());

                    // Add year to reflection title.
                    reflection.setTitle(reflection.getTitle() + ", " + id.substring(0, id.indexOf("-")));

                    // Verify if reflection is saved.
                    reflection.setFav(false);
                    if (reflectionLocalDataSource.get(id) != null) {
                        reflection.setFav(true);
                    }
                    view.showReflection(reflection);
                } else {
                    view.showError();
                }

                view.setLoadingIndicator(false);
            }

            @Override
            public void onFailure(@NonNull Call<RssResponse> call, @NonNull Throwable t) {
                view.showError();
                view.logError(t.getMessage());
                view.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void loadImage() {
        imageRemoteDataSource.get(new Callback<BingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BingResponse> call, @NonNull Response<BingResponse> response) {
                // Verify response.
                if(response.body() != null) {
                    if (!response.body().getImages().isEmpty()) {
                        view.showImage("http://www.bing.com/" + response.body().getImages().get(0).getUrl());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BingResponse> call, @NonNull Throwable t) {
                // Don't do nothing.
            }
        });
    }

    @Override
    public void saveReflection(Reflection reflection) {
        reflectionLocalDataSource.put(reflection);
    }

    @Override
    public void deleteReflection(String id) {
        reflectionLocalDataSource.delete(id);
    }

    @Override
    public void takeView(ReflectionContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        this.view = null;
    }

}
