package com.test.netty.config.two.webSocket;

import com.test.netty.config.two.test.ChannelSupervise;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends ChannelHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.ServerHandshakeStateEvent) {
            ctx.pipeline().remove(String.valueOf(HttpRequestHandler.class));
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TextWebSocketFrame msg1 = (TextWebSocketFrame) msg;
        String requestMsg = msg1.text();
        String responseMsg = "服务端接收客户端消息：" + requestMsg;
        TextWebSocketFrame resp = new TextWebSocketFrame(responseMsg);
        ctx.writeAndFlush(resp.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        LOGGER.error(ctx.channel().id().asShortText(), cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        ChannelSupervise.removeChannel(ctx.channel());
        LOGGER.info("[%s]断开连接", ctx.channel().id().asShortText());
    }
}