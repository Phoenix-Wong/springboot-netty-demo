package com.cs.server.init;

import com.cs.common.encode.CustomProtobufDecoder;
import com.cs.common.encode.CustomProtobufEncoder;
import com.cs.server.handler.HeartBeatSimpleHandle;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;


public class HeartbeatInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                //五秒没有收到消息 将IdleStateHandler 添加到 ChannelPipeline 中
                // .addLast(new IdleStateHandler(5, 0, 0))
                // .addLast(new HeartBeatSimpleHandle())
/**
                // 用protobuf做心跳 5秒发送一次给客户端
                .addLast(new IdleStateHandler(5, 0, 0))
                // 用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
                .addLast(new ProtobufVarint32FrameDecoder())
                // ProtobufDecoder：反序列化指定的Probuf字节数组为protobuf类型
                .addLast(new ProtobufDecoder(DeviceControlProtoBuf.HeartBeatDTO.getDefaultInstance()))
                .addLast(new ProtobufDecoder(DeviceControlProtoBuf.PingDTO.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                // 用于对Probuf类型序列化。
                .addLast(new ProtobufEncoder())
                // 心跳handler
                .addLast(new HeartBeatSimpleHandle())
 **/

                //      自定义protobuf解码器, 能解析多种protobuf格式 用法来着 https://www.cnblogs.com/Binhua-Liu/p/5577622.html
                // 第二个参数60是指, 客户端60秒内(writeIdleTimeSeconds)没有给服务端发送消息的话, 会触发这个IdleStateEvent事件, 是时候给服务端发消息说一声:"我还活着", 或者发送个状态信息什么的
                // 如果是服务端的话,应该设置第一个参数是60(readIdleTimeSeconds),即60秒没有读到信息来自客户端的信息就触发IdleStateEvent事件, 给客户端发送消息"你还活着吗??"
                .addLast(new IdleStateHandler(30, 0, 0))
                .addLast("decoder", new CustomProtobufDecoder())
                .addLast("encoder", new CustomProtobufEncoder())
                .addLast(new HeartBeatSimpleHandle())
        ;
    }
}
