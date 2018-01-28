package com.isscroberto.dailyreflectionandroid.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

public class Reflection extends RealmObject {

    @PrimaryKey
    private String Id;
    private String Title;
    private String Description;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}

