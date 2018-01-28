package com.isscroberto.dailyreflectionandroid.data.source;

import com.isscroberto.dailyreflectionandroid.data.models.Reflection;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionLocalDataSource {

    private Realm mRealm;

    public ReflectionLocalDataSource() {
        mRealm = Realm.getDefaultInstance();
    }

    public RealmResults<Reflection> get () {
        return mRealm.where(Reflection.class).findAllSorted("Id", Sort.DESCENDING);
    }

    public Reflection get(String id) {
        return mRealm.where(Reflection.class).equalTo("Id", id).findFirst();
    }

    public Reflection put(Reflection reflection) {
        mRealm.beginTransaction();
        Reflection managedReflection = mRealm.copyToRealm(reflection);
        mRealm.commitTransaction();
        return managedReflection;
    }

    public void delete (String id) {
        final Reflection reflection = mRealm.where(Reflection.class).equalTo("Id", id).findFirst();
        if(reflection != null) {
            mRealm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    reflection.deleteFromRealm();
                }

            });
        }
    }
    
}
