package com.test.netty.config.two.webSocket;

import com.test.netty.config.two.test.ChannelSupervise;
import com.test.netty.util.UriUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class HttpRequestHandler extends ChannelHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

    @Value(value = "${netty.server-socket.url}")
    private String socketUri;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest msg1 = (FullHttpRequest) msg;

        if (msg1.uri().startsWith(socketUri)) {
            String userId = UriUtil.getParam(msg1.uri(), "userId");
            if (userId != null) {
                // todo: 用户校验，重复登录判断
                ChannelSupervise.addChannel(userId, ctx.channel());
                ctx.fireChannelRead(msg1.setUri(socketUri).retain());
            } else {
                ctx.close();
            }
        } else {
            ctx.close();
        }
    }


}