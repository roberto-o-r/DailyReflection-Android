package com.isscroberto.dailyreflectionandroid.reflection;

import com.isscroberto.dailyreflectionandroid.BasePresenter;
import com.isscroberto.dailyreflectionandroid.BaseView;
import com.isscroberto.dailyreflectionandroid.data.models.Item;
import com.isscroberto.dailyreflectionandroid.data.models.Reflection;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public interface ReflectionContract {

    interface View extends BaseView<Presenter> {
        void showReflection(Item reflection);
        void showError();
        void logError(String Message);
        void showImage(String url);
        void setLoadingIndicator(boolean active);
    }

    interface Presenter extends BasePresenter<View> {
        void reload();
        void loadReflection();
        void loadImage();
        void saveReflection(Reflection reflection);
        void deleteReflection(String id);
    }

}
