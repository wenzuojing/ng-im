package com.github.wens.exchange;

import com.github.wens.MessageContent;
import com.github.wens.MessagePacket;
import com.github.wens.service.BrokerService;
import com.github.wens.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by wens on 16-1-12.
 */
public class MessageExchangeTest {

    private MessageExchange messageExchange1;
    private MessageExchange messageExchange2;
    private MessageExchange messageExchange3;

    @Before
    public void before() {
        MyBrokerService myBrokerService = new MyBrokerService();
        messageExchange1 = new MessageExchange(myBrokerService);
        messageExchange2 = new MessageExchange(myBrokerService);
        messageExchange3 = new MessageExchange(myBrokerService);
        messageExchange1.start();
        messageExchange2.start();
        messageExchange3.start();
    }

    @After
    public void after() {
        messageExchange1.stop();
        messageExchange2.stop();
        messageExchange3.stop();
    }


    @Test
    public void test_1() throws InterruptedException {

        final AtomicReference<String> onlineUserId = new AtomicReference<>();

        messageExchange1.setMessageListener(new MessageListener() {
            @Override
            public void onReceiveUserOnline(String userId, String address) {
                onlineUserId.set(userId);
            }

            @Override
            public void onReceiveUserOffline(String userId, String address) {

            }

            @Override
            public void onReceiveBroadcastMsg(MessageContent messagePacket) {

            }
        });

        messageExchange2.broadcastUserOnline("123456");

        Thread.sleep(1000);

        Assert.assertEquals("123456", onlineUserId.get());


    }


    @Test
    public void test_2() throws InterruptedException {

        test_1();

        final AtomicReference<String> offlineUserId = new AtomicReference<>();

        messageExchange1.setMessageListener(new MessageListener() {
            @Override
            public void onReceiveUserOnline(String userId, String address) {
            }

            @Override
            public void onReceiveUserOffline(String userId, String address) {

                offlineUserId.set(userId);
            }

            @Override
            public void onReceiveBroadcastMsg(MessageContent messagePacket) {

            }
        });

        messageExchange2.broadcastUserOffline("123456");

        Thread.sleep(1000);

        Assert.assertEquals("123456", offlineUserId.get());


    }


    @Test
    public void test_3() throws InterruptedException {

        test_1();

        final AtomicReference<MessageContent> baseMessage1 = new AtomicReference<>();
        final AtomicReference<MessageContent> baseMessage2 = new AtomicReference<>();

        messageExchange1.setMessageListener(new MessageListener() {
            @Override
            public void onReceiveUserOnline(String userId, String address) {
            }

            @Override
            public void onReceiveUserOffline(String userId, String address) {

            }

            @Override
            public void onReceiveBroadcastMsg(MessageContent msg) {
                baseMessage1.set(msg);
            }
        });

        messageExchange2.setMessageListener(new MessageListener() {
            @Override
            public void onReceiveUserOnline(String userId, String address) {
            }

            @Override
            public void onReceiveUserOffline(String userId, String address) {

            }

            @Override
            public void onReceiveBroadcastMsg(MessageContent msg) {
                baseMessage2.set(msg);
            }
        });
        MessageContent messageContent = new MessageContent();
        messageContent.setContent("hi".getBytes());
        messageContent.setReceiver("123456");
        messageContent.setSender("123456");
        messageContent.setType(MessageContent.TYPE_TEXT);
        messageExchange3.transportP2PMessage(messageContent);

        Thread.sleep(100);

        Assert.assertNull(baseMessage1.get());
        Assert.assertNotNull(baseMessage2.get());


    }

    static class MyBrokerService extends BrokerService {


        public MyBrokerService() {
            super();
        }


        @Override
        public boolean transportP2P(MessagePacket messagePacket, MessageContent message) {
            return true ;
        }

        @Override
        public boolean transportP2G(MessagePacket messagePacket, MessageContent message) {
            return true;
        }

        @Override
        public boolean canRoute(String userId) {
            return true ;
        }
    }


}
