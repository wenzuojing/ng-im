package com.github.wens;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wens on 16-1-9.
 */
public class ServerConf {

    private String bindHost;

    private int bindPort;

    private int bossThreads;

    private int workerThreads;
    private int tcpSendBufferSize;
    private boolean tcpNoDelay;
    private int tcpReceiveBufferSize;
    private boolean tcpKeepAlive;
    private boolean reuseAddress;
    private int acceptBackLog;


    public String getBindHost() {
        return bindHost;
    }

    public void setBindHost(String bindHost) {
        this.bindHost = bindHost;
    }

    public int getBindPort() {
        return bindPort;
    }

    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public int getTcpSendBufferSize() {
        return tcpSendBufferSize;
    }

    public void setTcpSendBufferSize(int tcpSendBufferSize) {
        this.tcpSendBufferSize = tcpSendBufferSize;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getTcpReceiveBufferSize() {
        return tcpReceiveBufferSize;
    }

    public void setTcpReceiveBufferSize(int tcpReceiveBufferSize) {
        this.tcpReceiveBufferSize = tcpReceiveBufferSize;
    }

    public boolean isTcpKeepAlive() {
        return tcpKeepAlive;
    }

    public void setTcpKeepAlive(boolean tcpKeepAlive) {
        this.tcpKeepAlive = tcpKeepAlive;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public int getAcceptBackLog() {
        return acceptBackLog;
    }

    public void setAcceptBackLog(int acceptBackLog) {
        this.acceptBackLog = acceptBackLog;
    }

    @Override
    public String toString() {
        return "ServerConf{" +
                "bindHost='" + bindHost + '\'' +
                ", bindPort=" + bindPort +
                ", bossThreads=" + bossThreads +
                ", workerThreads=" + workerThreads +
                ", tcpSendBufferSize='" + tcpSendBufferSize + '\'' +
                ", tcpNoDelay=" + tcpNoDelay +
                ", tcpReceiveBufferSize=" + tcpReceiveBufferSize +
                ", tcpKeepAlive=" + tcpKeepAlive +
                ", reuseAddress=" + reuseAddress +
                ", acceptBackLog=" + acceptBackLog +
                '}';
    }

    public final static String CLASS_PATH_PREFIX = "classpath:";

    public static ServerConf parse(String confFilePath) {

        if (confFilePath.startsWith(CLASS_PATH_PREFIX)) {
            confFilePath = Thread.currentThread().getContextClassLoader().getResource(confFilePath.substring(CLASS_PATH_PREFIX.length())).getPath();
        }

        InputStream confInputStream = null;

        try {
            confInputStream = new FileInputStream(confFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("can not find configure :" + confFilePath);
        }

        Properties prop = new Properties();
        try {
            prop.load(confInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                confInputStream.close();
            } catch (IOException e) {
                //
            }
        }

        ServerConf serverConf = new ServerConf();

        serverConf.setBindHost(prop.getProperty("bindHost", "0.0.0.0"));
        serverConf.setBindPort(Integer.parseInt(prop.getProperty("bindPort", "9999")));

        serverConf.setBossThreads(Integer.parseInt(prop.getProperty("bossThreads", "2")));
        serverConf.setWorkerThreads(Integer.parseInt(prop.getProperty("workerThreads", "16")));

        serverConf.setTcpSendBufferSize(Integer.parseInt(prop.getProperty("tcpSendBufferSize", "1024")));
        serverConf.setTcpNoDelay(Boolean.parseBoolean(prop.getProperty("tcpNoDelay", "true")));
        serverConf.setTcpReceiveBufferSize(Integer.parseInt(prop.getProperty("tcpReceiveBufferSize", "1024")));
        serverConf.setTcpKeepAlive(Boolean.parseBoolean(prop.getProperty("tcpKeepAlive", "true")));
        serverConf.setReuseAddress(Boolean.parseBoolean(prop.getProperty("reuseAddress", "true")));
        serverConf.setAcceptBackLog(Integer.parseInt(prop.getProperty("acceptBackLog", "10240")));

        return serverConf;
    }
}
