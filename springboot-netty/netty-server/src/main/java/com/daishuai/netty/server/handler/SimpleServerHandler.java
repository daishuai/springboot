package com.daishuai.netty.server.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @description 服务端处理器
 * @createTime 2022-08-09 22:12:16
 */
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取客户端通道的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
}
