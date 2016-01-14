package com.github.wens.service;

import com.github.wens.MessageContent;
import com.github.wens.MessagePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wens on 16-1-12.
 */
public class BrokerService {

    private final static Logger logger = LoggerFactory.getLogger(BrokerService.class);

    private OnlineService onlineService;
    private UserService userService;

    public BrokerService() {
    }

    public BrokerService(OnlineService onlineService, UserService userService) {
        this.onlineService = onlineService;
        this.userService = userService;
    }

    public boolean transportP2P(MessagePacket messagePacket, MessageContent message) {
        //protocol:Content Type(4bit)|Receiver(Group) Len (4bit)|Receiver(Group)|Content Len(4bit)|Content
        String receiver = message.getReceiver();
        logger.info("[transport][p2p]:sender={},receiver={}", message.getSender(), receiver);
        ChannelWrapper channelWrapper = onlineService.findChannel(receiver);
        if (channelWrapper != null) {
            channelWrapper.getSrcChannel().write(messagePacket);
            return true;
        } else {
            return false;
        }
    }

    public boolean transportP2G(MessagePacket messagePacket, MessageContent message) {
        //protocol:Content Type(4bit)|Receiver(Group) Len (4bit)|Receiver(Group)|Content Len(4bit)|Content
        String group = message.getReceiver();
        logger.info("[transport][p2g]:sender={},group={}", message.getSender(), group);
        List<String> groupUsers = userService.findUserByGroup(group);
        for (String u : groupUsers) {
            ChannelWrapper channelWrapper = onlineService.findChannel(u);
            if (channelWrapper != null) {
                channelWrapper.getSrcChannel().write(messagePacket);
            }
        }
        return true;
    }

    public void setOnlineService(OnlineService onlineService) {
        this.onlineService = onlineService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public boolean canRoute(String userId) {
        return onlineService.findChannel(userId) != null;
    }
}
