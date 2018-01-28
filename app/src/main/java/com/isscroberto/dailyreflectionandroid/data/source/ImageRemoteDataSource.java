package com.isscroberto.dailyreflectionandroid.data.source;

import com.isscroberto.dailyreflectionandroid.data.models.BingResponse;
import com.isscroberto.dailyreflectionandroid.data.source.retrofit.ImageApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ImageRemoteDataSource implements BaseDataSource<BingResponse> {

    @Override
    public void get(final Callback<BingResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.bing.com/").addConverterFactory(GsonConverterFactory.create()).build();
        ImageApi api = retrofit.create(ImageApi.class);
        Call<BingResponse> apiCall = api.get();
        apiCall.enqueue(callback);
    }

}