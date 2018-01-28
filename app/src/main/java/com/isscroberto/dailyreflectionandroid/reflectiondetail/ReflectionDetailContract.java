package com.isscroberto.dailyreflectionandroid.reflectiondetail;

import com.isscroberto.dailyreflectionandroid.BasePresenter;
import com.isscroberto.dailyreflectionandroid.BaseView;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public interface ReflectionDetailContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        void deleteReflection(String id);
    }
}
