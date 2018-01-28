package com.isscroberto.dailyreflectionandroid.reflectionssaved;

import com.isscroberto.dailyreflectionandroid.data.models.Reflection;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;

import io.realm.RealmResults;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionsSavedPresenter implements ReflectionsSavedContract.Presenter {

    private final ReflectionLocalDataSource mReflectionLocalDataSource;
    private final ReflectionsSavedContract.View mView;

    public ReflectionsSavedPresenter(ReflectionLocalDataSource reflectionLocalDataSource, ReflectionsSavedContract.View view) {
        mReflectionLocalDataSource = reflectionLocalDataSource;
        mView = view;

        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadReflections();
    }

    @Override
    public void loadReflections() {
        RealmResults<Reflection> reflections = mReflectionLocalDataSource.get();
        mView.showReflections(reflections);
    }
}
