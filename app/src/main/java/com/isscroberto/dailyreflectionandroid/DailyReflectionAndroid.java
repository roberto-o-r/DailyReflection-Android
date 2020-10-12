package com.isscroberto.dailyreflectionandroid;

import android.app.Application;

import com.github.stkent.amplify.feedback.DefaultEmailFeedbackCollector;
import com.github.stkent.amplify.feedback.GooglePlayStoreFeedbackCollector;
import com.github.stkent.amplify.tracking.Amplify;

import io.realm.Realm;

/**
 * Created by roberto.orozco on 18/12/2017.
 */

public class DailyReflectionAndroid extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Realm.
        Realm.init(this);

        // Feedback.
        Amplify.initSharedInstance(this)
                .setPositiveFeedbackCollectors(new GooglePlayStoreFeedbackCollector())
                .setCriticalFeedbackCollectors(new DefaultEmailFeedbackCollector(getString(R.string.my_email)))
                .applyAllDefaultRules();
        //.setAlwaysShow(BuildConfig.DEBUG);

        // Force a crash
        //throw new RuntimeException("Test Crash");
    }
}
