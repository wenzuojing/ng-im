package com.github.wens.exchange;

import com.github.wens.MessageContent;
import com.github.wens.MessagePacket;
import com.github.wens.service.BrokerService;
import com.github.wens.util.ByteUtils;
import com.github.wens.util.JsonUtils;
import org.jgroups.*;
import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wens on 16/1/9.
 */
public class MessageExchange extends ReceiverAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MessageExchange.class);

    private final static int MSG_TYPE_USER_ONLINE = 1;
    private final static int MSG_TYPE_USER_OFFLINE = 2;
    private final static int MSG_TYPE_BROADCAST_P2P_MSG = 3;
    private final static int MSG_TYPE_BROADCAST_P2G_MSG = 4;

    private JChannel channel;

    private String address;

    private Serializer serializer;

    private MessageListener messageListener;

    private ConcurrentHashMap<String, Address> userAddressMap = new ConcurrentHashMap<>();

    private volatile Map<String, Address> clusterAddress;

    private BrokerService brokerService;


    public MessageExchange(BrokerService brokerService) {
        this.brokerService = brokerService;
        try {
            this.channel = new JChannel();
            channel.setReceiver(this);
            channel.connect("im-server");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.address = channel.getAddress().toString();
        this.serializer = new Hessian2Serializer();
    }

    public void start() {

    }

    public void stop() {
        channel.close();
    }

    @Override
    public void receive(Message msg) {
        //不处理发送给自己的消息
        if (msg.getSrc().equals(channel.getAddress()))
            return;

        byte[] buffers = msg.getBuffer();

        if (buffers.length < 1) {
            logger.warn("MessageContent is empty.");
            return;
        }


        ByteBuffer byteBuffer = ByteBuffer.wrap(buffers);
        int msgType = byteBuffer.get();

        System.out.println("---------------" + address + "---------" + msgType + "----------------------------");

        switch (msgType) {
            case MSG_TYPE_USER_ONLINE: {
                String userId = ByteUtils.readString(byteBuffer);
                String address = ByteUtils.readString(byteBuffer);
                synchronized (userAddressMap) {
                    userAddressMap.put(userId, clusterAddress.get(address));
                }
                if (messageListener != null) {
                    messageListener.onReceiveUserOnline(userId, address);
                }
                break;
            }
            case MSG_TYPE_USER_OFFLINE: {
                String userId = ByteUtils.readString(byteBuffer);
                String address = ByteUtils.readString(byteBuffer).intern();
                synchronized (userAddressMap) {
                    Address address1 = userAddressMap.get(userId);
                    if (address1.toString().equals(address)) {
                        userAddressMap.remove(userId);
                    }
                }
                if (messageListener != null) {
                    messageListener.onReceiveUserOffline(userId, address);
                }
                break;
            }
            case MSG_TYPE_BROADCAST_P2P_MSG:{
                byte[] buf = new byte[byteBuffer.getInt()];
                byteBuffer.get(buf);
                String receiver = new String(buf , Charset.forName("utf-8"));
                if( brokerService.canRoute(receiver)){
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    MessageContent messageContent = JsonUtils.unmarshalFromByte(bytes, MessageContent.class);
                    boolean ok = brokerService.transportP2P( new MessagePacket(MessagePacket.PACKET_TYPE_P2P_MSG,0,bytes ) ,  messageContent );
                    if (ok && msg.getDest() == null) {
                        broadcastUserOnline(receiver);
                    }

                    if (messageListener != null) {
                        messageListener.onReceiveBroadcastMsg(messageContent);
                    }
                }

                break;
            }
            case MSG_TYPE_BROADCAST_P2G_MSG: {
                byte[] buf = new byte[byteBuffer.getInt()];
                byteBuffer.get(buf);
                String receiver = new String(buf , Charset.forName("utf-8"));
                byte[] bytes = byteBuffer.slice().array();
                MessageContent messageContent = JsonUtils.unmarshalFromByte(bytes, MessageContent.class);
                brokerService.transportP2G( new MessagePacket(MessagePacket.PACKET_TYPE_P2P_MSG,0,bytes ) ,  messageContent );
                if (messageListener != null) {
                    messageListener.onReceiveBroadcastMsg(messageContent);
                }
                break;
            }
            default:
                logger.warn("Unknown msg type : {} " , msgType );
        }
    }


    @Override
    public void viewAccepted(View view) {
        Map<String, Address> addressMap = new HashMap<>();
        for (Address address : view.getMembers()) {
            addressMap.put(address.toString(), address);
        }
        clusterAddress = addressMap;
    }

    public void transportP2PMessage(MessageContent messageContent) {
        transportMessage(MSG_TYPE_BROADCAST_P2P_MSG , messageContent );
    }

    public void transportP2GMessage(MessageContent messageContent) {
        transportMessage(MSG_TYPE_BROADCAST_P2P_MSG, messageContent);
    }

    private void transportMessage(int msgType , MessageContent messageContent) {
        byte[] bytes = JsonUtils.marshalToByte(messageContent) ;
        Address address = userAddressMap.get(messageContent.getReceiver());
        byte[] receiverBytes = messageContent.getReceiver().getBytes(Charset.forName("utf-8"));
        ByteBuffer buffer = ByteBuffer.allocate( 1 + 4 + receiverBytes.length + bytes.length);
        buffer.put((byte) msgType);
        buffer.putInt(receiverBytes.length);
        buffer.put(receiverBytes);
        buffer.put(bytes) ;
        doSend(address, buffer.array());
    }

    private void doSend(Address address, byte[] bytes) {
        try {
            channel.send(address, bytes);
        } catch (Exception e) {
            throw new RuntimeException("Fail to send ", e);
        }
    }

    public void broadcastUserOnline(String userId) {
        ByteBuffer buffer = ByteBuffer.allocate(userId.length() + address.length() + 1 + 8);
        buffer.put((byte) MSG_TYPE_USER_ONLINE);
        ByteUtils.writeString(buffer, userId);
        ByteUtils.writeString(buffer, address);
        doSend(null, buffer.array());

    }

    public void broadcastUserOffline(String userId) {

        ByteBuffer buffer = ByteBuffer.allocate((userId.length() + address.length() + 1 + 8));
        buffer.put((byte) MSG_TYPE_USER_OFFLINE);
        ByteUtils.writeString(buffer, userId);
        ByteUtils.writeString(buffer, address);
        doSend(null, buffer.array());

    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public static void main(String[] args) {
        System.out.println(UUID.getByName("wens-33818"));
    }
}
