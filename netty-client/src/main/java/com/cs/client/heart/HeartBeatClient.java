package com.cs.client.heart;

import com.cs.client.init.CustomerHandleInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Spring 管理 netty client端配置, 说白了跟Spring进行集成就是将自己变成一个Bean交给Spring...(个人看法)
 */
@Component
public class HeartBeatClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatClient.class);
    private EventLoopGroup group = new NioEventLoopGroup();
    @Value("${netty.server.port}")
    private int nettyPort;
    @Value("${netty.server.host}")
    private String host;

    private SocketChannel socketChannel;

    private Bootstrap bootstrap;

    private ChannelFuture future;

    /**
     * @PostConstruct 注解; 在bean初始化的时候回执行这方法
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() {
        /**
         * NioSocketChannel用于创建客户端通道，而不是NioServerSocketChannel。
         * 请注意，我们不像在ServerBootstrap中那样使用childOption()，因为客户端SocketChannel没有父服务器。
         */
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new CustomerHandleInitializer());
        /**
         * 启动客户端
         * 我们应该调用connect()方法而不是bind()方法。
         */
        try {
            this.doConnect();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            LOGGER.info("启动失败",e);
        }
    }

    protected void doConnect(){
        if (socketChannel!=null&&socketChannel.isActive()){
            return;
        }
        future = bootstrap.connect(host,nettyPort);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    socketChannel = (SocketChannel) future.channel();
                    LOGGER.info("启动 client Netty 成功");
                }else {
                    LOGGER.info("连接服务端失败,10s后重试");
                    future.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    },10, TimeUnit.SECONDS);
                }
            }
        });
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        group.shutdownGracefully().syncUninterruptibly();
        LOGGER.info("关闭 client Netty 成功");
    }
}
