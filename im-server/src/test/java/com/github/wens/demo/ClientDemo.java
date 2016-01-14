package com.github.wens.demo;

import com.github.wens.MessageContent;
import com.github.wens.MessagePacket;
import com.github.wens.util.JsonUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by wens on 16-1-12.
 */
public class ClientDemo {

    public static void main(String[] args) throws IOException, InterruptedException {

        /*int port = 9999 ;
        String token = "token123";
        final String userId = "123";
        final String receiverId = "456";*/

        int port = 8888 ;
        String token = "token456";
        final String userId = "456";
        final String receiverId = "123";

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", port), 1000);

        final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        //packet protocol:Packet Type (4bit)|Flag(4bit)|Payload Len(4bit) | Payload
        //认证
        dataOutputStream.writeInt(MessagePacket.PACKET_TYPE_AUTHORIZE);
        dataOutputStream.writeInt(0);



        dataOutputStream.writeInt(token.getBytes().length);
        dataOutputStream.write(token.getBytes());
        dataOutputStream.flush();

        int packetType = dataInputStream.readInt();
        int flag = dataInputStream.readInt();

        int payloadLen = dataInputStream.readInt();
        byte[] buf = new byte[payloadLen];

        dataInputStream.read(buf);

        if( !"+ok".equals(new String(buf))){
            throw new RuntimeException("Auth fail") ;
        }

        System.out.println("Auth success");

        new Thread(){
            @Override
            public void run() {
                while (true){
                    try{


                        dataOutputStream.writeInt(MessagePacket.PACKET_TYPE_HEARTBEAT);
                        dataOutputStream.writeInt(0);
                        dataOutputStream.writeInt(0);
                        dataOutputStream.flush();

                        Thread.sleep(3000);

                    }catch (Exception e){
                        throw new RuntimeException(e) ;
                    }
                }
            }
        }.start();


        new Thread(){
            @Override
            public void run() {

                while (true){

                    try{
                        int packetType = dataInputStream.readInt();
                        int flag = dataInputStream.readInt();
                        if(flag != 0 ){
                            throw new RuntimeException("unexpected flag : " + flag ) ;
                        }
                        int payloadLen = dataInputStream.readInt();
                        byte[] buf = new byte[payloadLen];

                        dataInputStream.readFully(buf);

                        if(MessagePacket.PACKET_TYPE_HEARTBEAT == packetType ){
                            continue;
                        }

                        MessageContent messageContent = JsonUtils.unmarshalFromByte(buf, MessageContent.class) ;

                        if( MessagePacket.PACKET_TYPE_P2P_MSG == packetType  ){
                            if(messageContent.getType() == MessageContent.TYPE_TEXT ){
                                System.out.println("From:" + messageContent.getSender() +":[text]" + new String( messageContent.getContent()));
                            }else if(messageContent.getType() == MessageContent.TYPE_IMG){
                                System.out.println("From:" + messageContent.getSender() +":[img]..." );
                            }else if(messageContent.getType() == MessageContent.TYPE_VOICE){
                                System.out.println("From:" + messageContent.getSender() +":[voice]..." );
                            }else if(messageContent.getType() == MessageContent.TYPE_OTHER){
                                System.out.println("From:" + messageContent.getSender() +":[other].." );
                            }
                        }
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }


            }
        }.start();


        new Thread(){
            @Override
            public void run() {

                Scanner scanner = new Scanner(System.in);
                while (true){

                    try{

                        System.out.print("input:");
                        String line = scanner.nextLine();

                        dataOutputStream.writeInt(MessagePacket.PACKET_TYPE_P2P_MSG);
                        dataOutputStream.writeInt(0);

                        MessageContent messageContent = new MessageContent();
                        messageContent.setContent(line.getBytes("utf-8"));
                        messageContent.setReceiver(receiverId);
                        messageContent.setSender(userId);
                        messageContent.setType(MessageContent.TYPE_TEXT);

                        byte[] bytes = JsonUtils.marshalToByte(messageContent);
                        dataOutputStream.writeInt(bytes.length);
                        dataOutputStream.write(bytes);
                        dataOutputStream.flush();



                    }catch (Exception e){
                        throw new RuntimeException(e) ;
                    }
                }


            }
        }.start();


    }


}
