package com.learn.rabbitmq.workqueue.Rotationtrainingdistribution;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈工作队列消息发送者--轮训分发〉
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
