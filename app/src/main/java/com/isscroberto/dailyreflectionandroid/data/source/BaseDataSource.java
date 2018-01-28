package com.isscroberto.dailyreflectionandroid.data.source;

import retrofit2.Callback;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public interface BaseDataSource<T> {

    void get(Callback<T> callback);

}
