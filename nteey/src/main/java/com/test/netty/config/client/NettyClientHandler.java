package com.test.netty.config.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


import java.util.Date;

/**
 * @program: qingcheng
 * @author: XIONG CHUAN
 * @create: 2019-04-28 19:37
 * @description: 客户端处理类
 **/

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx.channel().remoteAddress());
        System.out.println("client output"+msg);
        ctx.writeAndFlush("from client:"+new Date());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("来自客户端的问候");
        ctx.writeAndFlush("from client:"+new Date());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}