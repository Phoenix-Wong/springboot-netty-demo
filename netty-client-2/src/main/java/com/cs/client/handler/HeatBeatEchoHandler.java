package com.cs.client.handler;

import com.cs.common.model.HeartBeatProtoBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端发送心跳到服务端
 * @author
 */
public class HeatBeatEchoHandler extends SimpleChannelInboundHandler<HeartBeatProtoBuf.HeartBeatPingDTO> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeatBeatEchoHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                // 客户端60秒内(writeIdleTimeSeconds)没有给服务端发送消息的话, 会触发这个IdleStateEvent事件, 是时候给服务端发消息说一声:"我还活着", 或者发送个状态信息什么的
                LOGGER.info("过了 60秒, 是时候给服务器发个Pong心跳消息");
                // 向服务端发送消息
                HeartBeatProtoBuf.HeartBeatPongDTO.Builder heatBeatBuilder = HeartBeatProtoBuf.HeartBeatPongDTO.newBuilder();
                // 设置设备ip,
                heatBeatBuilder.setDeviceIp("113.65.205.92");
                // 摄像头运行状态 0：异常 1：正常'
                heatBeatBuilder.setCameraWorkStatus(1);
                // HEART_BEAT(3,"心跳检测"),
                heatBeatBuilder.setMsgType(3);
                // 分辨率
                heatBeatBuilder.setDeviceResolution("1920*1080");
                // imei
                heatBeatBuilder.setImei("78a8656545454a545e");
                // 设置是否背光（0：否 1：是）
                heatBeatBuilder.setIsBlacklight(0);
                // 屏幕亮度 默认20
                heatBeatBuilder.setScreenBrightness("60");
                // 是否开机自启（0：否 1：是）
                heatBeatBuilder.setIsRootStart(1);
                // 是否开启进程守护（0 否 1：是）
                heatBeatBuilder.setIsGuard(1);
                // 是否显示状态栏（0：否 1：是）
                heatBeatBuilder.setIsStatus(1);
                // 设置监听器,关闭连接? TODO
                ctx.writeAndFlush(heatBeatBuilder.build()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     *  每当从服务端接收到新数据时，都会使用收到的消息调用此方法 channelRead0(),在此示例中，接收消息的类型是ByteBuf。
     * @param channelHandlerContext
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HeartBeatProtoBuf.HeartBeatPingDTO byteBuf) throws Exception {
        //从服务端收到消息时被调用
        // LOGGER.info("客户端心跳handler收到消息={}", byteBuf.toString());
        LOGGER.info("客户端心跳handler收到服务端的Ping消息: {},{}", byteBuf.getId(),byteBuf.getContent());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.fireExceptionCaught(cause);
        LOGGER.error("client catcher Exception: {}", cause.getMessage());
    }


}
