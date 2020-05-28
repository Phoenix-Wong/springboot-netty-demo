package com.cs.common.encode;

import com.cs.common.model.HeartBeatProtoBuf;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 参考ProtobufVarint32LengthFieldPrepender 和 ProtobufEncoder
 * @author james
 */
@Sharable
public class CustomProtobufEncoder extends MessageToByteEncoder<MessageLite> {

    // HangqingEncoder hangqingEncoder;
    //
    // public CustomProtobufEncoder(HangqingEncoder hangqingEncoder)
    // {
    //     this.hangqingEncoder = hangqingEncoder;
    // }

    @Override
    protected void encode(
            ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {


        byte[] body = msg.toByteArray();
        byte[] header = encodeHeader(msg, (short)body.length);

        out.writeBytes(header);
        out.writeBytes(body);

        return;
    }

    private byte[] encodeHeader(MessageLite msg, short bodyLength) {
        byte messageType = 0x0f;

        if (msg instanceof HeartBeatProtoBuf.HeartBeatPingDTO) {
            messageType = 0x00;
        } else if (msg instanceof HeartBeatProtoBuf.HeartBeatPongDTO) {
            messageType = 0x01;
        }

        byte[] header = new byte[4];
        header[0] = (byte) (bodyLength & 0xff);
        header[1] = (byte) ((bodyLength >> 8) & 0xff);
        // 保留字段
        header[2] = 0;
        header[3] = messageType;

        return header;

    }
}