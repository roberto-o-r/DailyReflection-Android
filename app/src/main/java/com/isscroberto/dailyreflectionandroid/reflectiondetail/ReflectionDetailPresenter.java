package com.isscroberto.dailyreflectionandroid.reflectiondetail;

import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionDetailPresenter implements ReflectionDetailContract.Presenter {

    private final ReflectionLocalDataSource mReflectionLocalDataSource;
    private final ReflectionDetailContract.View mView;

    public ReflectionDetailPresenter(ReflectionLocalDataSource reflectionLocalDataSource, ReflectionDetailContract.View view) {
        mReflectionLocalDataSource = reflectionLocalDataSource;
        mView = view;

        view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void deleteReflection(String id) {
        mReflectionLocalDataSource.delete(id);
    }
}
