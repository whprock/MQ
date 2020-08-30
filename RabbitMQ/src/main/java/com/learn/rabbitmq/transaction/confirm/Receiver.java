package com.learn.rabbitmq.transaction.confirm;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author whp
 * @create 2020-06-01
 * @since 1.0.0
 */
public class Receiver {
    private static final String QUEUE_NAME = "confirm_queue";
    public static void main(String[] args) {

        try {
            //创建连接
            Connection connection = ConnectionUtils.getConnection();
            //创建频道
            Channel channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //创建消费者--重写handleDelivery方法
            Consumer consumer = new DefaultConsumer(channel) {
                int i = 1;
                //获取到达的消息
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String msg = new String(body, "utf-8");
                    System.out.println("receiver[1]" + msg);
                }
            };
            //监听队列
            channel.basicConsume(QUEUE_NAME, true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
