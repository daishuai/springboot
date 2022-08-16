package com.daishuai.netty.server.encoder;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author admin
 * @version 1.0.0
 * @description 自定义
 * @createTime 2022-08-16 22:21:57
 */
public class ObjectEncoder extends MessageToMessageEncoder<Object> {

    private final Charset charset;

    public ObjectEncoder() {
        this(StandardCharsets.UTF_8);
    }

    public ObjectEncoder(Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg == null) {
            return;
        }
        CharSequence charSequence = msg instanceof CharSequence ? (CharSequence) msg : JSON.toJSONString(msg);
        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(charSequence), charset));
    }
}
