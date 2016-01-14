package com.github.wens;

import com.github.wens.exchange.MessageExchange;
import com.github.wens.handler.LogicHandler;
import com.github.wens.handler.MessagePacketDecoderHandler;
import com.github.wens.handler.MessagePacketEncoderHandler;
import com.github.wens.service.BrokerService;
import com.github.wens.service.OnlineService;
import com.github.wens.service.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by wens on 16-1-9.
 */
public class ServerChannelInitializer extends ChannelInitializer<Channel> {

    private OnlineService onlineService;
    private UserService userService;
    private BrokerService brokerService;
    private MessageExchange messageExchange ;

    public ServerChannelInitializer(OnlineService onlineService, UserService userService, BrokerService brokerService ,MessageExchange messageExchange) {
        this.onlineService = onlineService;
        this.userService = userService;
        this.brokerService = brokerService;
        this.messageExchange = messageExchange ;
    }


    @Override
    protected void initChannel(Channel channel) throws Exception {
        //channel.pipeline().addLast(new LengthFieldPrepender(4, false));
        //channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        channel.pipeline().addLast(new MessagePacketDecoderHandler());
        channel.pipeline().addLast(new MessagePacketEncoderHandler());
        channel.pipeline().addLast(new LogicHandler(onlineService, userService, brokerService,messageExchange));

    }
}
