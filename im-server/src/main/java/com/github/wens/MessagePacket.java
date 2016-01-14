package com.github.wens;

import java.io.Serializable;

/**
 * Created by wens on 16-1-9.
 */
public class MessagePacket implements Serializable {

    public final static int PACKET_TYPE_HEARTBEAT = 1;
    public final static int PACKET_TYPE_AUTHORIZE = 2;
    public final static int PACKET_TYPE_P2P_MSG = 3;
    public final static int PACKET_TYPE_P2G_MSG = 4;

    private String msgId;
    private int packetType;
    private int flag;
    private byte[] payload;

    public MessagePacket(int packetType, int flag, byte[] payload) {
        this.packetType = packetType;
        this.flag = flag;
        this.payload = payload;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
