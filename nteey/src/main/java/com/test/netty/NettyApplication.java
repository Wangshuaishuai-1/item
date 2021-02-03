package com.test.netty;

import com.test.netty.config.one.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ Author     ：wangshuaishuai
 * @ Date       ：Created in 15:47 2021/2/2
 * @ Modified By：
 */
@SpringBootApplication
public class NettyApplication {
    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
        NettyServer nettyServer=new NettyServer();
    }
}
