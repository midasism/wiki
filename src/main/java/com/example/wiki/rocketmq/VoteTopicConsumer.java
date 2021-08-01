package com.example.wiki.rocketmq;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(consumerGroup = "default", topic = "VOTE_TOPIC")
public class VoteTopicConsumer implements RocketMQListener<MessageExt> {

    private Logger LOG = LoggerFactory.getLogger(VoteTopicConsumer.class);

    @Override
    public void onMessage(MessageExt message) {
        byte[] body = message.getBody();
        LOG.info("RocketMQ收到消息：{}", new String(body));
    }
}
