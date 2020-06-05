package com.cs.server.handler;

import com.cs.common.model.HeartBeatProtoBuf;
import com.cs.server.util.NettySocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * TODO 如何判断是正常断开 以及异常断开(走降级策略)
 */
public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<HeartBeatProtoBuf.HeartBeatPongDTO> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);


    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        LOGGER.info("链接报告开始");
        LOGGER.info("链接报告信息：有一客户端链接到本服务端");
        LOGGER.info("链接报告IP:" + channel.localAddress().getHostString());
        LOGGER.info("链接报告Port:" + channel.localAddress().getPort());
        LOGGER.info("链接报告完毕");
        //通知客户端链接建立成功
        String str = "通知客户端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
        ctx.writeAndFlush(str);
    }

    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
        LOGGER.error("移除设备:{}", ctx.channel().id());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
//                LOGGER.info("已经5秒没有收到信息！给客户端发个信息线");
                //向客户端发送消息 ,并且添加一个监听器, 如果ta关闭了则服务端也关闭对其监听,并在设备在线列表移除
//                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                LOGGER.info("触发idleStateEvent");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatProtoBuf.HeartBeatPongDTO heartBeat) throws Exception {
        LOGGER.info("收到心跳信息:{}", heartBeat.toString());
        //我们调用writeAndFlush（Object）来逐字写入接收到的消息并刷新线路
        //ctx.writeAndFlush(customProtocol);
        //保存客户端与 Channel 之间的关系
         NettySocketHolder.put(heartBeat.getImei(), (NioSocketChannel) ctx.channel());
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("心跳handler发生Exception:{}",cause.getMessage());
    }
}
