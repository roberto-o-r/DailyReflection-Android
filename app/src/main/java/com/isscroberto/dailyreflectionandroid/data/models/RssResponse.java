package com.isscroberto.dailyreflectionandroid.data.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by roberto.orozco on 22/12/2017.
 */

@Root(name = "rss", strict = false)
public class RssResponse {

    @Element
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
