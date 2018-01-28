package com.isscroberto.dailyreflectionandroid.reflectionssaved;

import com.isscroberto.dailyreflectionandroid.BasePresenter;
import com.isscroberto.dailyreflectionandroid.BaseView;
import com.isscroberto.dailyreflectionandroid.data.models.Reflection;

import io.realm.RealmResults;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public interface ReflectionsSavedContract {

    interface View extends BaseView<Presenter> {
        void showReflections(RealmResults<Reflection> reflections);
    }

    interface Presenter extends BasePresenter {
        void loadReflections();
    }

}
