package com.github.wens.service;

import io.netty.channel.Channel;

/**
 * Created by wens on 16-1-12.
 */
public class ChannelWrapper {

    private final Channel srcChannel;
    private volatile long lastHeartbeat;

    public ChannelWrapper(Channel srcChannel) {
        this.srcChannel = srcChannel;
    }

    public Channel getSrcChannel() {
        return srcChannel;
    }


    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
}
