package com.learn.rabbitmq.workqueue.fairdistribution;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈工作队列消息发送者〉
 *
 * @author whp
 * @create 2020-05-30
 * @since 1.0.0
 */
public class Sender {

    private static final String WORK_QUEUE = "work_queue";

    public static void main(String[] args) {
        try {
            //获取连接
            Connection connection = ConnectionUtils.getConnection();
            Channel channel;
            int i = 1;
            while (true) {
                i++;
                //创建通道
                channel = connection.createChannel();
                //声明队列
                channel.queueDeclare(WORK_QUEUE, false, false, false, null);

                /**
                 * basicQos控制每次发送多少条消息到消费者
                 *
                 * 每个消费者发送确认消息之前，消息队列不会发送下一条消息到消费者，消费者一次只处理一个消息
                 *
                 * 限制发送给同一个消费者不得超过一条消息
                 */
                int prefetchCount = 1;
                channel.basicQos(prefetchCount);

                String msg = "msg:" +i;
                channel.basicPublish("", WORK_QUEUE, null, msg.getBytes());

                if (i==1000)
                    break;
            }
            channel.close();
            connection.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
