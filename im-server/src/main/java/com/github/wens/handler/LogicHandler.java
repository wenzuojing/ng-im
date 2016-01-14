package com.github.wens.handler;

import com.github.wens.MessageContent;
import com.github.wens.MessagePacket;
import com.github.wens.exchange.MessageExchange;
import com.github.wens.service.BrokerService;
import com.github.wens.service.OnlineService;
import com.github.wens.service.UserService;
import com.github.wens.util.JsonUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by wens on 16-1-9.
 */
public class LogicHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(LogicHandler.class);

    private final static AttributeKey<String> userInfo = new AttributeKey<>("userInfo");

    private BrokerService brokerService;

    private UserService userService;

    private OnlineService onlineService;

    private MessageExchange messageExchange ;

    public LogicHandler(OnlineService onlineService, UserService userService, BrokerService brokerService , MessageExchange messageExchange ) {
        this.onlineService = onlineService;
        this.userService = userService;
        this.brokerService = brokerService;
        this.messageExchange = messageExchange ;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof MessagePacket) {

            MessagePacket messagePacket = (MessagePacket) msg;
            switch (messagePacket.getPacketType()) {
                case MessagePacket.PACKET_TYPE_AUTHORIZE: {
                    String token = new String(messagePacket.getPayload(), "utf-8");
                    String userId = userService.authorize(token);

                    String resp = "+ok";
                    if (userId == null) {
                        logger.warn("authorize fail : {} ", token);
                        resp = "+fail";
                    } else {
                        ctx.channel().attr(userInfo).set(userId);
                        onlineService.online(userId, ctx.channel());
                    }

                    messagePacket.setPayload(resp.getBytes(Charset.forName("utf-8")));
                    ctx.write(messagePacket);
                    break;
                }
                case MessagePacket.PACKET_TYPE_HEARTBEAT: {
                    ctx.write(messagePacket);
                    onlineService.heartbeat(getUserId(ctx));
                    break;
                }
                case MessagePacket.PACKET_TYPE_P2P_MSG: {
                    String userId = getUserId(ctx);
                    if (userId == null) {
                        ctx.channel().close();
                        break;
                    }
                    MessageContent message = JsonUtils.unmarshalFromByte(messagePacket.getPayload(), MessageContent.class);
                    if( !brokerService.transportP2P(messagePacket, message)){
                        messageExchange.transportP2PMessage(message);
                    }
                    break;
                }
                case MessagePacket.PACKET_TYPE_P2G_MSG: {
                    String userId = getUserId(ctx);
                    if (userId == null) {
                        ctx.channel().close();
                        break;
                    }
                    MessageContent message = JsonUtils.unmarshalFromByte(messagePacket.getPayload(), MessageContent.class);
                    brokerService.transportP2G(messagePacket, message);
                    messageExchange.transportP2GMessage(message);
                    break;
                }
                default:
                    logger.warn("Unknown packet type : {} ", messagePacket.getPacketType());
            }


        }


    }

    private String getUserId(ChannelHandlerContext ctx) {
        Attribute<String> userInfoAttribute = ctx.channel().attr(userInfo);
        return userInfoAttribute.get();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Some error,force close" ,cause);
        onlineService.offline(getUserId(ctx));
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        onlineService.offline(getUserId(ctx));
    }
}
