package com.github.wens.exchange;


import com.github.wens.MessageContent;
import com.github.wens.MessagePacket;

/**
 * Created by wens on 16-1-12.
 */
public interface MessageListener {

    void onReceiveUserOnline(String userId, String address);

    void onReceiveUserOffline(String userId, String address);

    void onReceiveBroadcastMsg(MessageContent messageContent);

}
