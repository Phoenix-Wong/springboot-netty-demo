package com.cs.server.devicecontrol;

import com.cs.common.model.HeartBeatProtoBuf;
import com.cs.server.util.NettySocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 发送消息给客户端server
 */
@Service
//@EnableScheduling
public class SendMsgToDeviceService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SendMsgToDeviceService.class);


    //    @Scheduled(fixedRate = 35_000L)
    public String sendMsg(String deviceImei, HeartBeatProtoBuf.HeartBeatPingDTO controlDTO) {

        Map<String, NioSocketChannel> map = NettySocketHolder.getMAP();
        NioSocketChannel nioSocketChannel = map.get(deviceImei);
        if (Objects.nonNull(nioSocketChannel)) {
            nioSocketChannel.writeAndFlush(controlDTO);
            return String.format("已经发送消息给该客户端:%s", deviceImei);
        } else {
            return ("该设备离线, sorry");
        }
    }
}
