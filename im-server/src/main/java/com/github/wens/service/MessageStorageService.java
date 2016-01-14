package com.github.wens.service;

import com.github.wens.MessagePacket;
import com.github.wens.bigqueue.BigQueueImpl;
import com.github.wens.bigqueue.IBigQueue;
import com.github.wens.serializer.Hessian2Serializer;
import com.github.wens.serializer.Hessian2SerializerFactory;
import com.github.wens.serializer.Serializer;

import java.io.IOException;

/**
 * Created by wens on 16/1/14.
 */
public class MessageStorageService {

    private IBigQueue bigQueue ;

    private Serializer serializer ;

    public MessageStorageService(){
        try {
            bigQueue = new BigQueueImpl(System.getProperty("java.io.tmpdir"), "ng-im-message");
        } catch (IOException e) {
            throw new RuntimeException(e) ;
        }
        serializer = new Hessian2Serializer();
    }

    public void enqueue(MessagePacket messagePacket){
        /*messagePacket.getMsgId();
        messagePacket.get
        messagePacket.getPayload();
        byte[] buf  = new byte[8 + messagePacket.getPayload().length ] ;

        bigQueue.enqueue();*/
    }




}
