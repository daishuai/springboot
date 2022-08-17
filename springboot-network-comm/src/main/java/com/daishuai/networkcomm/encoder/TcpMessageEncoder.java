package com.daishuai.networkcomm.encoder;

import com.alibaba.fastjson.JSON;
import com.daishuai.networkcomm.model.TcpMessageModel;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description Tcp消息编码器
 * @createTime 2022年08月17日 13:18:00
 */
public class TcpMessageEncoder extends MessageToMessageEncoder<TcpMessageModel> {



    private final Charset charset;

    public TcpMessageEncoder() {
        this(StandardCharsets.UTF_8);
    }

    public TcpMessageEncoder(Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, TcpMessageModel msg, List<Object> out) throws Exception {
        if (msg == null) {
            return;
        }
        CharSequence charSequence = JSON.toJSONString(msg);
        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(charSequence), charset));
    }
}
