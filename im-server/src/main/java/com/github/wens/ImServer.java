package com.github.wens;

import com.github.wens.exchange.MessageExchange;
import com.github.wens.service.BrokerService;
import com.github.wens.service.OnlineService;
import com.github.wens.service.UserService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * Created by wens on 16-1-9.
 */
public class ImServer {

    private final static Logger logger = LoggerFactory.getLogger(ImServer.class);

    private ServerConf serverConf;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private MessageExchange messageExchange;
    private OnlineService onlineService;
    private UserService userService;
    private BrokerService brokerService;

    public ImServer(ServerConf serverConf) {
        this.serverConf = serverConf;
        this.onlineService = new OnlineService();
        this.userService = new UserService();
        this.brokerService = new BrokerService(onlineService, userService);
        this.messageExchange = new MessageExchange(this.brokerService);
    }

    public void start() {
        logger.info("IM server conf : " + serverConf);
        initGroups();
        ServerBootstrap b = new ServerBootstrap();
        (b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)).childHandler(new ServerChannelInitializer(onlineService, userService, brokerService , messageExchange ));
        applyConnectionOptions(b);
        InetSocketAddress addr = new InetSocketAddress(serverConf.getBindHost(), serverConf.getBindPort());

        b.bind(addr).addListener(new FutureListener<Void>() {
            @Override
            public void operationComplete(Future<Void> future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("IM server started at port: {}", serverConf.getBindPort());
                } else {
                    logger.error("IM server start failed at port: {}!", serverConf.getBindPort());
                }
            }
        });
        messageExchange.start();

    }

    protected void initGroups() {
        bossGroup = new NioEventLoopGroup(serverConf.getBossThreads());
        workerGroup = new NioEventLoopGroup(serverConf.getWorkerThreads());
    }

    protected void applyConnectionOptions(ServerBootstrap bootstrap) {
        bootstrap.childOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(serverConf.isTcpNoDelay()));
        bootstrap.childOption(ChannelOption.SO_SNDBUF, Integer.valueOf(serverConf.getTcpSendBufferSize()));
        bootstrap.childOption(ChannelOption.SO_RCVBUF, Integer.valueOf(serverConf.getTcpReceiveBufferSize()));
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(serverConf.isTcpKeepAlive()));
        bootstrap.option(ChannelOption.SO_REUSEADDR, Boolean.valueOf(serverConf.isReuseAddress()));
        bootstrap.option(ChannelOption.SO_BACKLOG, Integer.valueOf(serverConf.getAcceptBackLog()));
    }

    public void stop() {

        messageExchange.stop();
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();

        logger.info("IM server stop at : {} ", new Date());
    }

    public static void main(String[] args) {
        String confFilePath = System.getProperty("conf", "classpath:server.conf");
        final ImServer imServer = new ImServer(ServerConf.parse(confFilePath));
        imServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                imServer.stop();
            }
        });
    }
}
