package com.test.netty.config.two.test;

import com.test.netty.config.two.webSocket.ChannelSupervise;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebsocketController {
    @RequestMapping("sendToAll")
    public void sendToAll(String msg) {
        ChannelSupervise.sendToAll(msg);
    }

    @RequestMapping("sendToUser")
    public void sendToUser(String userId, String msg) {
        ChannelSupervise.sendToUser(userId, msg);
    }
}