package com.test.netty.config.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
@Component
public class NettyServer {

    public void start() throws UnknownHostException {

        InetSocketAddress address = InetSocketAddress.createUnresolved("127.0.0.1",8888);
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)  // 绑定线程池
                    .channel(NioServerSocketChannel.class)
                    .localAddress(address)
                    .childHandler(new NettyServerChannelInitializer())//编码解码
                    .option(ChannelOption.SO_BACKLOG, 128)  //服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  //保持长连接，2小时无数据激活心跳机制

            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind(address).sync();
            log.info("netty服务器开始监听端口：" + address.getPort());
            //关闭channel和块，直到它被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private InetSocketAddress getInetAddress() throws UnknownHostException {
        // 使用getLocalHost方法为InetAddress创建对象；

        //获得本机的InetAddress对象
        InetAddress add = InetAddress.getLocalHost();
        //根据域名得到InetAddress对象
        add = InetAddress.getByName("www.baidu.com");

        //根据ip得到InetAddress对象；
        add = InetAddress.getByName("111.13.100.91");

        //www.baidu.com，不给你解析就会返回这个IP本身；
        return InetSocketAddress.createUnresolved("127.0.0.1",8888);
    }

}