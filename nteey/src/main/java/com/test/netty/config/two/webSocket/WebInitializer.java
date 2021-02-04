package com.test.netty.config.two.webSocket;

import com.sun.istack.logging.Logger;
import com.test.netty.config.two.test.ChannelSupervise;
import com.test.netty.util.UriUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;

/**
 * @ Author     ：wangshuaishuai
 * @ Date       ：Created in 17:30 2021/2/3
 * @ Modified By：
 */
public class WebInitializer extends ChannelInitializer<SocketChannel> {
    @Value(value = "${netty.server-socket.url}")
    private String socketUri;
    @Autowired
    private HttpRequestHandler httpRequestHandler;
    @Autowired
    private NioWebSocketHandler nioWebSocketHandler;
    @Autowired
    private TextWebSocketFrameHandler textWebSocketFrameHandler;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // WebSocket 是基于 Http 协议的，要使用 Http 解编码器
        channel.pipeline().addLast("http-codec", new HttpServerCodec());
        // 用于大数据流的分区传输
        channel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        // 将多个消息转换为单一的 request 或者 response 对象，最终得到的是 FullHttpRequest 对象
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        channel.pipeline().addLast("handler", new ChannelHandlerAdapter() {

            private final Logger logger = Logger.getLogger(this.getClass());

            private WebSocketServerHandshaker handshaker;

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                logger.info("收到消息：" + msg);
                if (msg instanceof FullHttpRequest) {
                    //以http请求形式接入，但是走的是websocket
                    handleHttpRequest(ctx, (FullHttpRequest) msg);
                } else if (msg instanceof WebSocketFrame) {
                    //处理websocket客户端的消息
                    handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
                }
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                //添加连接
                logger.info("客户端加入连接：" + ctx.channel());
                ChannelSupervise.addChannel(ctx.channel());
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                //断开连接
                logger.info("客户端断开连接：" + ctx.channel());
                ChannelSupervise.removeChannel(ctx.channel());
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                ctx.flush();
            }

            private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
                // 判断是否关闭链路的指令
                if (frame instanceof CloseWebSocketFrame) {
                    handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                    return;
                }
                // 判断是否ping消息
                if (frame instanceof PingWebSocketFrame) {
                    ctx.channel().write(
                            new PongWebSocketFrame(frame.content().retain()));
                    return;
                }
                // 本例程仅支持文本消息，不支持二进制消息
                if (!(frame instanceof TextWebSocketFrame)) {
                    logger.info("本例程仅支持文本消息，不支持二进制消息");
                    throw new UnsupportedOperationException(String.format(
                            "%s frame types not supported", frame.getClass().getName()));
                }
                // 返回应答消息
                String request = ((TextWebSocketFrame) frame).text();
                logger.info("服务端收到：" + request);
                TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                        + ctx.channel().id() + "：" + request);
                // 群发
                ChannelSupervise.send2All(tws);
                // 返回【谁发的发给谁】
                // ctx.channel().writeAndFlush(tws);
            }

            /**
             * 唯一的一次http请求，用于创建websocket
             * */
            private void handleHttpRequest(ChannelHandlerContext ctx,
                                           FullHttpRequest req) {
                //要求Upgrade为websocket，过滤掉get/Post
                if (!req.decoderResult().isSuccess()
                        || (!"websocket".equals(req.headers().get("Upgrade")))) {
                    //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
                    sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                            HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                    return;
                }
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                        "ws://localhost:8081/websocket", null, false);
                handshaker = wsFactory.newHandshaker(req);
                if (handshaker == null) {
                    WebSocketServerHandshakerFactory
                            .sendUnsupportedVersionResponse(ctx.channel());
                } else {
                    handshaker.handshake(ctx.channel(), req);
                }
            }

            /**
             * 拒绝不合法的请求，并返回错误信息
             * */
            private void sendHttpResponse(ChannelHandlerContext ctx,
                                          FullHttpRequest req, DefaultFullHttpResponse res) {
                // 返回应答给客户端
                if (res.status().code() != 200) {
                    ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                            CharsetUtil.UTF_8);
                    res.content().writeBytes(buf);
                    buf.release();
                }
                ChannelFuture f = ctx.channel().writeAndFlush(res);
                // 如果是非Keep-Alive，关闭连接
                if (!isKeepAlive(req) || res.status().code() != 200) {
                    f.addListener(ChannelFutureListener.CLOSE);
                }
            }
        });
        // 创建 WebSocket 之前会有唯一一次 Http 请求 (Header 中包含 Upgrade 并且值为 websocket)
        channel.pipeline().addLast("http-request", new ChannelHandlerAdapter() {
            private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HttpRequestHandler.class);

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
        });

        // 处理所有委托管理的 WebSocket 帧类型以及握手本身
        // 入参是 ws://server:port/context_path 中的 contex_path
        channel.pipeline().addLast("websocket-server", new WebSocketServerProtocolHandler(socketUri));
        // WebSocket RFC 定义了 6 种帧，TextWebSocketFrame 是我们唯一真正需要处理的帧类型
        channel.pipeline().addLast("text-frame", new ChannelHandlerAdapter() {
            private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof WebSocketServerProtocolHandler.ServerHandshakeStateEvent) {
                    ctx.pipeline().remove(String.valueOf(org.apache.http.protocol.HttpRequestHandler.class));
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
        });
    }
}
