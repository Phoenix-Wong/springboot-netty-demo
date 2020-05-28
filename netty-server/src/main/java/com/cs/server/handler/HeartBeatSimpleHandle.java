package com.cs.server.handler;

import com.cs.common.model.HeartBeatProtoBuf;
import com.cs.server.util.NettySocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<HeartBeatProtoBuf.HeartBeatPongDTO> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);
    // private static final DeviceControlDTO.DeviceControl HEART_BEAT = DeviceControlDTO.DeviceControl.newBuilder().setDeviceId("001").build();
    // private static final ByteBuf HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("ping", CharsetUtil.UTF_8));
    // private static final ByteBuf HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("ping", CharsetUtil.UTF_8));
    private static final HeartBeatProtoBuf.HeartBeatPingDTO HEART_BEAT = HeartBeatProtoBuf.HeartBeatPingDTO.newBuilder().setId(123).setContent("6666666").build();

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
