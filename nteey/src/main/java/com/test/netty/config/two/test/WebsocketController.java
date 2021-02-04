package com.test.netty.config.two.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebsocketController {
    @Autowired
    private ChannelSupervise channelSupervise;
    @RequestMapping("sendToAll")
    public void sendToAll(String msg) {
        channelSupervise.sendToAll(msg);
    }

    @RequestMapping("sendToUser")
    public void sendToUser(String userId, String msg) {
        channelSupervise.sendToUser(userId, msg);
    }
}