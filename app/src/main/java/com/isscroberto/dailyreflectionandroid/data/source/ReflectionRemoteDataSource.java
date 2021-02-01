package com.isscroberto.dailyreflectionandroid.data.source;

import com.isscroberto.dailyreflectionandroid.data.models.RssResponse;
import com.isscroberto.dailyreflectionandroid.data.source.retrofit.ReflectionApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionRemoteDataSource implements BaseDataSource<RssResponse> {

    @Override
    public void get(final Callback<RssResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.plough.com/").addConverterFactory(SimpleXmlConverterFactory.create()).build();
        ReflectionApi api = retrofit.create(ReflectionApi.class);
        Call<RssResponse> apiCall = api.get();
        apiCall.enqueue(callback);
    }

}