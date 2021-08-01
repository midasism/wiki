package com.example.wiki.rocketmq;

import com.example.wiki.websocket.WebSocketServer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@RocketMQMessageListener(consumerGroup = "default", topic = "VOTE_TOPIC")
public class VoteTopicConsumer implements RocketMQListener<MessageExt> {

    private Logger LOG = LoggerFactory.getLogger(VoteTopicConsumer.class);

    @Resource
    private WebSocketServer webSocketServer;

    @Override
    public void onMessage(MessageExt message) {
        byte[] body = message.getBody();
        String info = new String(body);
        LOG.info("RocketMQ收到消息：{}", info);
        webSocketServer.sendInfo(info);
    }
}
