package com.cs.server.devicecontrol;

import com.cs.common.model.HeartBeatProtoBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/dc")
public class DeviceController {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private SendMsgToDeviceService sendMsgToDeviceService;

    @GetMapping("/{imei}/{content}")
    public String sendMsg(@PathVariable("imei") String imei, @PathVariable("content") String content) {
//        "78a8656545454a545e"
        HeartBeatProtoBuf.HeartBeatPingDTO.Builder builder = HeartBeatProtoBuf.HeartBeatPingDTO.newBuilder();
        builder.setContent(content);
        builder.setId(new Random().nextInt(60_000));
        return sendMsgToDeviceService.sendMsg(imei,builder.build());
    }

    @GetMapping("/{content}")
    public void sendAllMsg(@PathVariable("content")String content){
        HeartBeatProtoBuf.HeartBeatPingDTO.Builder builder = HeartBeatProtoBuf.HeartBeatPingDTO.newBuilder();
        builder.setContent(content);
        builder.setId(new Random().nextInt(60_000));
        sendMsgToDeviceService.sendAllMsg(builder.build());
    }
}
