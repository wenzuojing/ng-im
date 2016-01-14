package com.github.wens.util;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by wens on 16/1/9.
 */
public class ByteUtils {

    private static final int MAX_BYTE = 10 * 1024 * 1024;

    public static String readString(ByteBuffer byteBuffer) {
        return new String(readBytes(byteBuffer), Charset.forName("utf-8"));
    }

    public static byte[] readBytes(ByteBuffer byteBuffer) {
        int len = byteBuffer.getInt();
        if (len > MAX_BYTE) {
            throw new RuntimeException("Over max byte : " + len);
        }
        byte[] bytes = new byte[len];
        byteBuffer.get(bytes);
        return bytes;
    }

    public static void writeString(ByteBuffer byteBuffer, String src) {
        writeBytes(byteBuffer, src.getBytes(Charset.forName("utf-8")));
    }

    public static void writeBytes(ByteBuffer byteBuffer, byte[] bytes) {
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
    }

    //
    public static String readString(ByteBuf byteBuf) {
        return new String(readBytes(byteBuf), Charset.forName("utf-8"));
    }

    public static byte[] readBytes(ByteBuf byteBuf) {
        int len = byteBuf.readInt();
        if (len > MAX_BYTE) {
            throw new RuntimeException("Over max byte : " + len);
        }
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public static void writeString(ByteBuf byteBuf, String src) {
        writeBytes(byteBuf, src.getBytes(Charset.forName("utf-8")));
    }

    public static void writeBytes(ByteBuf byteBuf, byte[] bytes) {
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
