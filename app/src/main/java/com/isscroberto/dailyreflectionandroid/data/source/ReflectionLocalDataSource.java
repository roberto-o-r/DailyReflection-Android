package com.isscroberto.dailyreflectionandroid.data.source;

import com.isscroberto.dailyreflectionandroid.data.models.Reflection;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class ReflectionLocalDataSource {

    private final Realm realm;

    public ReflectionLocalDataSource() {
        realm = Realm.getDefaultInstance();
    }

    public RealmResults<Reflection> get () {
        return realm.where(Reflection.class).findAllSorted("Id", Sort.DESCENDING);
    }

    public Reflection get(String id) {
        return realm.where(Reflection.class).equalTo("Id", id).findFirst();
    }

    public void put(Reflection reflection) {
        realm.beginTransaction();
        realm.copyToRealm(reflection);
        realm.commitTransaction();
    }

    public void delete (String id) {
        final Reflection reflection = realm.where(Reflection.class).equalTo("Id", id).findFirst();
        if(reflection != null) {
            realm.executeTransaction(realm -> reflection.deleteFromRealm());
        }
    }
    
}
