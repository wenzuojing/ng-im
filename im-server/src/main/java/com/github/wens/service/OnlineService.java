package com.github.wens.service;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wens on 16-1-12.
 */
public class OnlineService {

    private ConcurrentHashMap<String, ChannelWrapper> channelMap = new ConcurrentHashMap<>();

    public void online(String userId, Channel channel) {
        channelMap.put(userId, new ChannelWrapper(channel));
    }

    public void offline(String userId) {
        channelMap.remove(userId);
    }

    public void heartbeat(String userId) {
        ChannelWrapper channelWrapper = channelMap.get(userId);
        if (channelWrapper != null) {
            channelWrapper.setLastHeartbeat(System.currentTimeMillis());
        }
    }

    public ChannelWrapper findChannel(String userId) {
        return channelMap.get(userId);
    }
}
