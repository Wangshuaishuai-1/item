package com.test.netty.config.two.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebsocketController {
    @Autowired
    private ChannelSupervise channelSupervise;
    @Value("${netty.server-socket.url}")
    private String socketUri;
    @RequestMapping("sendToAll")
    public void sendToAll(String msg) {
        System.out.println(socketUri);
        channelSupervise.sendToAll(msg);
    }

    @RequestMapping("sendToUser")
    public void sendToUser(String userId, String msg) {
        channelSupervise.sendToUser(userId, msg);
    }
}