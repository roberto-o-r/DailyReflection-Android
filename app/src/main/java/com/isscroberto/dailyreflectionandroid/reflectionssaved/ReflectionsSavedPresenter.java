package com.isscroberto.dailyreflectionandroid.reflectionssaved;

import com.isscroberto.dailyreflectionandroid.data.models.Reflection;
import com.isscroberto.dailyreflectionandroid.data.source.ReflectionLocalDataSource;

import io.realm.RealmResults;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionsSavedPresenter implements ReflectionsSavedContract.Presenter {

    private final ReflectionLocalDataSource reflectionLocalDataSource;
    private ReflectionsSavedContract.View view;

    public ReflectionsSavedPresenter(ReflectionLocalDataSource reflectionLocalDataSource) {
        this.reflectionLocalDataSource = reflectionLocalDataSource;
    }

    @Override
    public void loadReflections() {
        RealmResults<Reflection> reflections = reflectionLocalDataSource.get();
        view.showReflections(reflections);
    }

    @Override
    public void takeView(ReflectionsSavedContract.View view) {
        this.view = view;
        loadReflections();
    }

    @Override
    public void dropView() {
        this.view = null;
    }
}
