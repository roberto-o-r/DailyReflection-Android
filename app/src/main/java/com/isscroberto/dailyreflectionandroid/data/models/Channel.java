package com.isscroberto.dailyreflectionandroid.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

@Root(name = "channel", strict = false)
public class Channel {

    @Element
    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
