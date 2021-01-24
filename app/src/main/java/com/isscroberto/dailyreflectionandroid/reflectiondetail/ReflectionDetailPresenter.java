package com.isscroberto.dailyreflectionandroid.reflectiondetail;

import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionDetailPresenter implements ReflectionDetailContract.Presenter {

    private final ReflectionLocalDataSource reflectionLocalDataSource;
    private ReflectionDetailContract.View view;

    public ReflectionDetailPresenter(ReflectionLocalDataSource reflectionLocalDataSource) {
        this.reflectionLocalDataSource = reflectionLocalDataSource;
    }

    @Override
    public void deleteReflection(String id) {
        reflectionLocalDataSource.delete(id);
    }

    @Override
    public void takeView(ReflectionDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        this.view = null;
    }
}
