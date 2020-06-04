package com.cs.client.init;

import com.cs.client.handler.HeatBeatEchoHandler;
import com.cs.common.encode.CustomProtobufDecoder;
import com.cs.common.encode.CustomProtobufEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 *自定义protobuf解码器
 */
public class CustomerHandleInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
                //10 秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
                // .addLast(new IdleStateHandler(0, 10, 0))
                // .addLast(new EchoClientHandle());
/**
                // 用protobuf做心跳试试,如果使用这方法的话, 只能传输一种格式的protobuf,多种格式需要自定义的protobuf解码器
//                .addLast(new IdleStateHandler(0, 10, 0))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(DeviceControlProtoBuf.HeartBeatDTO.getDefaultInstance()))
                .addLast(new ProtobufDecoder(DeviceControlProtoBuf.PingDTO.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new HeatBeatEchoHandlerProtoBuf())
 **/
                //      自定义protobuf解码器, 能解析多种protobuf格式 用法来着 https://www.cnblogs.com/Binhua-Liu/p/5577622.html
                // 第二个参数60是指, 客户端60秒内(writeIdleTimeSeconds)没有给服务端发送消息的话, 会触发这个IdleStateEvent事件, 是时候给服务端发消息说一声:"我还活着", 或者发送个状态信息什么的
                // 如果是服务端的话,应该设置第一个参数是60(readIdleTimeSeconds),即60秒没有读到信息来自客户端的信息就触发IdleStateEvent事件, 给客户端发送消息"你还活着吗??"
                .addLast(new IdleStateHandler(0, 60, 0))
                .addLast("decoder", new CustomProtobufDecoder())
                .addLast("encoder", new CustomProtobufEncoder())
                .addLast(new HeatBeatEchoHandler())
        ;
    }
}
