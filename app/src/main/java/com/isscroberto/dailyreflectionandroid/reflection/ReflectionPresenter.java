package com.isscroberto.dailyreflectionandroid.reflection;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionPresenter implements ReflectionContract.Presenter {

    private final ReflectionRemoteDataSource mReflectionDataSource;
    private final ReflectionLocalDataSource mReflectionLocalDataSource;
    private final ImageRemoteDataSource mImageDataSource;
    private final ReflectionContract.View mView;

    public ReflectionPresenter(ReflectionRemoteDataSource reflectionDataSource, ReflectionLocalDataSource reflectionLocalDataSource, ImageRemoteDataSource imageDataSource, ReflectionContract.View view) {
        mReflectionDataSource = reflectionDataSource;
        mReflectionLocalDataSource = reflectionLocalDataSource;
        mImageDataSource = imageDataSource;
        mView = view;

        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadReflection();
        loadImage();
    }

    @Override
    public void loadReflection() {
        mView.setLoadingIndicator(true);
        mReflectionDataSource.get(new Callback<RssResponse>() {
            @Override
            public void onResponse(Call<RssResponse> call, Response<RssResponse> response) {
                // Verify that response is not empty.
                if(response.body() != null) {
                    Item reflection = response.body().getChannel().getItem();
                    reflection.setTitle(reflection.getTitle().replace("Dig", "Reflection"));

                    // Create reflection id based on the date.
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    df.setTimeZone(TimeZone.getTimeZone("gmt"));
                    String id = df.format(new Date());

                    // Add year to reflection title.
                    reflection.setTitle(reflection.getTitle() + ", " + id.substring(0, id.indexOf("-")));

                    // Verify if reflection is saved.
                    reflection.setFav(false);
                    if (mReflectionLocalDataSource.get(id) != null) {
                        reflection.setFav(true);
                    }
                    mView.showReflection(reflection);
                } else {
                    mView.showError();
                }

                mView.setLoadingIndicator(false);
            }

            @Override
            public void onFailure(Call<RssResponse> call, Throwable t) {
                mView.showError();
                mView.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void loadImage() {
        mImageDataSource.get(new Callback<BingResponse>() {
            @Override
            public void onResponse(Call<BingResponse> call, Response<BingResponse> response) {
                // Verify response.
                if(response.body() != null) {
                    if (!response.body().getImages().isEmpty()) {
                        mView.showImage("http://www.bing.com/" + response.body().getImages().get(0).getUrl());
                    }
                }
            }

            @Override
            public void onFailure(Call<BingResponse> call, Throwable t) {
                // Don't do nothing.
            }
        });
    }

    @Override
    public void saveReflection(Reflection reflection) {
        mReflectionLocalDataSource.put(reflection);
    }

    @Override
    public void deleteReflection(String id) {
        mReflectionLocalDataSource.delete(id);
    }


}
