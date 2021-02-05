package com.test.netty.config.two.webSocket;

import com.sun.istack.logging.Logger;
import com.test.netty.config.two.test.ChannelSupervise;
import com.test.netty.constants.DefaultConstants;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;

/**
 * @ Author     ：wangshuaishuai
 * @ Date       ：Created in 17:30 2021/2/3
 * @ Modified By：
 */
@Component
public class WebInitializer extends ChannelInitializer<SocketChannel> {
    @Value("${netty.server-socket.url}")
    private String socketUri;

    private HttpRequestHandler httpRequestHandler=new HttpRequestHandler();
    private NioWebSocketHandler nioWebSocketHandler=new NioWebSocketHandler();
    private TextWebSocketFrameHandler textWebSocketFrameHandler=new TextWebSocketFrameHandler();

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        // WebSocket 是基于 Http 协议的，要使用 Http 解编码器
        channel.pipeline().addLast("http-codec", new HttpServerCodec());
        // 用于大数据流的分区传输
        channel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        // 将多个消息转换为单一的 request 或者 response 对象，最终得到的是 FullHttpRequest 对象
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        channel.pipeline().addLast("handler",nioWebSocketHandler);
        // 创建 WebSocket 之前会有唯一一次 Http 请求 (Header 中包含 Upgrade 并且值为 websocket)
        channel.pipeline().addLast("http-request", httpRequestHandler);
        // 处理所有委托管理的 WebSocket 帧类型以及握手本身
        // 入参是 ws://server:port/context_path 中的 contex_path
        channel.pipeline().addLast("websocket-server", new WebSocketServerProtocolHandler(DefaultConstants.SOCKET_IP));
        // WebSocket RFC 定义了 6 种帧，TextWebSocketFrame 是我们唯一真正需要处理的帧类型
        channel.pipeline().addLast("text-frame", textWebSocketFrameHandler);
    }
}
