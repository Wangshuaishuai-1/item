package com.test.netty.config.two.webSocket;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ Author     ：wangshuaishuai
 * @ Date       ：Created in 17:30 2021/2/3
 * @ Modified By：
 */
public class WebInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private HttpRequestHandler httpRequestHandler;
    @Autowired
    private TextWebSocketFrameHandler textWebSocketFrameHandler;
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // WebSocket 是基于 Http 协议的，要使用 Http 解编码器
        channel.pipeline().addLast("http-codec", new HttpServerCodec());
        // 用于大数据流的分区传输
        channel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        // 将多个消息转换为单一的 request 或者 response 对象，最终得到的是 FullHttpRequest 对象
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        // 创建 WebSocket 之前会有唯一一次 Http 请求 (Header 中包含 Upgrade 并且值为 websocket)
        channel.pipeline().addLast("http-request",httpRequestHandler);
        // 处理所有委托管理的 WebSocket 帧类型以及握手本身
        // 入参是 ws://server:port/context_path 中的 contex_path
        channel.pipeline().addLast("websocket-server", new WebSocketServerProtocolHandler(socketUri));
        // WebSocket RFC 定义了 6 种帧，TextWebSocketFrame 是我们唯一真正需要处理的帧类型
        channel.pipeline().addLast("text-frame",textWebSocketFrameHandler);
    }
}
