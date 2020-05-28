package com.cs.server.util;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NettySocketHolder {

    /**
     * 保存在线设备 imei作为key
     */
    private static final Map<String, NioSocketChannel> MAP = new ConcurrentHashMap<>(16);

    public static void put(String id, NioSocketChannel socketChannel) {
        MAP.putIfAbsent(id, socketChannel);
    }

    public static NioSocketChannel get(String id) {
        return MAP.get(id);
    }

    public static Map<String, NioSocketChannel> getMAP() {
        return MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> MAP.remove(entry.getKey()));
    }
}
