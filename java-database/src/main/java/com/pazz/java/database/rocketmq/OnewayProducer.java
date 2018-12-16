package com.pazz.java.database.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 单向传输
 */
public class OnewayProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        producer.setNamesrvAddr("47.98.219.97:9876");
        producer.setVipChannelEnabled(false);
        producer.start();
        for (int i = 0; i < 100; i++) {
            Message message = new Message("TopicTest", "TagA", ("Hello word " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 调用发送消息向一个代理传递消息。
            producer.sendOneway(message);
        }
        producer.shutdown();
    }
}
