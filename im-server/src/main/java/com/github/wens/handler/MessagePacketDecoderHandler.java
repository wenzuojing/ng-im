package com.github.wens.handler;

import com.github.wens.MessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wens on 16-1-9.
 */
public class MessagePacketDecoderHandler extends ByteToMessageDecoder {

    private final static Logger logger = LoggerFactory.getLogger(MessagePacketDecoderHandler.class);

    private final static int MAX_LEN = 20 * 1024 * 1024;

    public static int HEAD_SIZE = 4 + 4 + 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        if (byteBuf.readableBytes() >= HEAD_SIZE) {
            int msgType = byteBuf.readInt();
            int flag = byteBuf.readInt();
            int contentLen = byteBuf.readInt();

            if (contentLen > MAX_LEN) {
                logger.warn("Over max len {} ,force close ", MAX_LEN);
                channelHandlerContext.close();
                return;
            }

            if (byteBuf.readableBytes() >= contentLen) {
                byte[] buf = new byte[contentLen];
                byteBuf.readBytes(buf);
                list.add(new MessagePacket(msgType, flag, buf));
                return;
            }
        }
        byteBuf.resetReaderIndex();


    }
}
