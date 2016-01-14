package com.github.wens;

import java.util.Arrays;

/**
 * Created by wens on 16-1-12.
 */
public class MessageContent {

    public final static int TYPE_TEXT = 0;
    public final static int TYPE_IMG = 1;
    public final static int TYPE_VOICE = 2;
    public final static int TYPE_OTHER = 999;

    private String sender;

    private String receiver;

    private byte[] content;

    private int type;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "MessageContent{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content=" + Arrays.toString(content) +
                ", type=" + type +
                '}';
    }
}
