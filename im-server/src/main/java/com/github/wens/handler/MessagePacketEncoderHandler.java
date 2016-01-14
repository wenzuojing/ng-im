package com.github.wens.handler;

import com.github.wens.MessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by wens on 16-1-9.
 */
public class MessagePacketEncoderHandler extends MessageToByteEncoder<MessagePacket> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessagePacket message, ByteBuf byteBuf) throws Exception {
        // protocol:Packet Type (4bit)|Flag(1bit)|Payload Len(4bit) | Payload
        byteBuf.writeInt(message.getPacketType());
        byteBuf.writeInt(message.getFlag());
        int contentLen = message.getPayload() == null ? 0 : message.getPayload().length;
        byteBuf.writeInt(contentLen);
        if (contentLen > 0) {
            byteBuf.writeBytes(message.getPayload());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        ctx.flush() ;
    }

}
