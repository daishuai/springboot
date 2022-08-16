package com.daishuai.netty.server;

import com.daishuai.netty.server.handler.SimpleServerHandler;
import com.daishuai.netty.server.initializer.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;

/**
 * @author admin
 * @version 1.0.0
 * @description netty实现服务端
 * @createTime 2022-08-09 21:39:47
 */
public class NettyServer {

    public static void main(String[] args) {
        // 首先，netty通过ServerBootstrap启动客户端
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        // 第一步定义两个线程组，用来处理客户端通道的accept和读写事件
        // parentGroup用来处理accept时间，childGroup用来处理通道的读写事件
        server.group(parentGroup, childGroup);
        // 用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。
        // 用来初始化服务端可连接队列
        // 服务端处理客户端连接请求是按顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
        // 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
        server.option(ChannelOption.SO_BACKLOG, 128);
        // 第二步绑定服务端通道
        server.channel(NioServerSocketChannel.class);

        // 第三步绑定handler，处理读写事件，ChannelInitializer是给通道初始化
        server.childHandler(new ServerChannelInitializer());
        // 第四步绑定端口
        try {
            ChannelFuture future = server.bind(8989).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
