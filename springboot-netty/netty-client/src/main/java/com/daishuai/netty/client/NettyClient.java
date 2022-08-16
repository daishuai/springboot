package com.daishuai.netty.client;


import com.daishuai.netty.client.encoder.ObjectEncoder;
import com.daishuai.netty.client.handler.SimpleClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description netty客户端
 * @createTime 2022-08-09 22:15:25
 */
public class NettyClient {

    public static void main(String[] args) {
        // 首先，netty通过ServerBootstrap启动服务端
        Bootstrap client = new Bootstrap();
        // 第1步 定义线程组，处理读写和链接事件，没有了accept事件
        EventLoopGroup group = new NioEventLoopGroup();
        client.group(group);
        // 第2步 绑定客户端通道
        client.channel(NioSocketChannel.class);
        //第3步 给NIoSocketChannel初始化handler， 处理读写事件
        client.handler(new ChannelInitializer<NioSocketChannel>() {

            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                //字符串编码器，一定要加在SimpleClientHandler 的上面
                ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new JsonObjectDecoder());
                //找到他的管道 增加他的handler
                ch.pipeline().addLast(new SimpleClientHandler());
            }
        });

        try {
            ChannelFuture future = client.connect("localhost", 8989).sync();
            //发送数据给服务器
            Map<String, Object> map = new HashMap<>();
            map.put("id", 12);
            map.put("type", "messageBus");
            map.put("content", "zhangsan");
            map.put("ack", 1);
            future.channel().writeAndFlush(map);

            for (int i = 0; i < 5; i++) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("msg", "hello World!");
                future.channel().writeAndFlush(dataMap);
            }

            //当通道关闭了，就继续往下走
            future.channel().closeFuture().sync();

            //接收服务端返回的数据
           /* AttributeKey<String> key = AttributeKey.valueOf("ServerData");
            Object result = future.channel().attr(key).get();
            System.out.println(result.toString());*/
        } catch (Exception e) {
            System.out.println("连接失败");
        }
    }
}
