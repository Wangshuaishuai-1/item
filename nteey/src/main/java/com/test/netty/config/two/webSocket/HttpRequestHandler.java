package com.test.netty.config.two.webSocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

    @Value("${server.socket-uri}")
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

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

    }

}