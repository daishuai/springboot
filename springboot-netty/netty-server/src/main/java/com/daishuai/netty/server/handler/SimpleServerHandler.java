package com.daishuai.netty.server.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description 服务端处理器
 * @createTime 2022-08-09 22:12:16
 */
@Slf4j
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * handlerAdded() ：指的是当检测到新连接之后，调用 ch.pipeline().addLast(new LifeCyCleTestHandler());
     * 之后的回调，表示在当前的 channel 中，已经成功添加了一个 handler 处理器。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("逻辑处理器被添加：handlerAdded()");
        super.handlerAdded(ctx);
    }

    /**
     * channelRegistered()：这个回调方法，表示当前的 channel 的所有的逻辑处理已经和某个 NIO 线程建立了绑定关系，
     * 类似 BIO 编程中，accept 到新的连接，然后创建一个线程来处理这条连接的读写，只不过 Netty 里面是使用了线程池的方式，
     * 只需要从线程池里面去抓一个线程绑定在这个 channel 上即可，这里的 NIO 线程通常指的是 NioEventLoop,不理解没关系，后面我们还会讲到。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel 绑定到线程(NioEventLoop)：channelRegistered()");
        super.channelRegistered(ctx);
    }

    /**
     * channelActive() ：当 channel 的所有的业务逻辑链准备完毕
     * （也就是说 channel 的 pipeline 中已经添加完所有的 handler）以及绑定好一个 NIO 线程之后，
     * 这条连接算是真正激活了，接下来就会回调到此方法。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel 准备就绪：channelActive()");
        super.channelActive(ctx);
    }

    /**
     * channelRead()：客户端向服务端发来数据，每次都会回调此方法，表示有数据可读。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channel 有数据可读：channelRead()");
        //可以在这里面写一套类似SpringMVC的框架
        //让SimpleServerHandler不跟任何业务有关，可以封装一套框架
        if(msg instanceof ByteBuf){
            System.out.println(((ByteBuf)msg).toString(Charset.defaultCharset()));
        }

        //业务逻辑代码处理框架。。。

        //返回给客户端的数据，告诉我已经读到你的数据了
        Map<String, Object> dataMap = new HashMap<>();
        String result = "hello client ";
        dataMap.put("result", result);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(JSON.toJSONString(dataMap).getBytes(Charset.defaultCharset()));
        ctx.channel().writeAndFlush(buf);
    }

    /**
     * channelReadComplete()：服务端每次读完一次完整的数据之后，回调该方法，表示数据读取完毕。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channel 某次数据读完：channelReadComplete()");
        super.channelReadComplete(ctx);
    }

    /**
     * channelInactive(): 表面这条连接已经被关闭了，这条连接在 TCP 层面已经不再是 ESTABLISH 状态了
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel 被关闭：channelInactive()");
        super.channelInactive(ctx);
    }

    /**
     * channelUnregistered(): 既然连接已经被关闭，那么与这条连接绑定的线程就不需要对这条连接负责了，
     * 这个回调就表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel 取消线程(NioEventLoop) 的绑定: channelUnregistered()");
        super.channelUnregistered(ctx);
    }

    /**
     * chandlerRemoved()：最后，我们给这条连接上添加的所有的业务逻辑处理器都给移除掉。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("逻辑处理器被移除：handlerRemoved()");
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("出现异常: {}", cause.getMessage(), cause);
    }

    // ChannelHandler回调方法执行顺序
    // handlerAdded() -> channelRegistered() -> channelActive() -> channelRead() -> channelReadComplete()

    // 客户端关闭，这个时候对于服务端来说，其实就是 channel 被关闭，这时候 ChannelHandler 回调方法的执行顺序为
    // channelInactive() -> channelUnregistered() -> handlerRemoved()
}
