package com.example.wiki.service;

import com.example.wiki.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WsService {

    private static final Logger LOG = LoggerFactory.getLogger(WsService.class);

    @Resource
    WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String info) {
        webSocketServer.sendInfo(info);
        LOG.info("推送消息" + info);
    }
}
