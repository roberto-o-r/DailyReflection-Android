package com.isscroberto.dailyreflectionandroid.data.source.retrofit;

import com.isscroberto.dailyreflectionandroid.data.models.BingResponse;
import com.isscroberto.dailyreflectionandroid.data.models.RssResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public interface ImageApi {

    @GET("HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US")
    Call<BingResponse> get();

}
