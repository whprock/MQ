package com.learn.rabbitmq.direct;

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
 * 〈根据路由键准确发送消息到队列〉
 *
 * @author whp
 * @create 2020-06-01
 * @since 1.0.0
 */
public class Subscribe_RoutingKey {

    private static final String QUEUE_NAME = "test_queue";

    private static final String EXCHANGE_NAME = "exchange_direct";


    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //设置多个路由键，可以让一个队列绑定多个routingKey，这样一个队列就能接收多个路由的信息
            // 其中消息路由为，info，error，warring的消息，该队列全部都能接收到
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "info");
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "error");
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "warring");

            channel.basicQos(1);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String infoMsg = new String(body, "utf-8");
                    System.out.println("get info message: " + infoMsg);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            channel.basicConsume(QUEUE_NAME, false, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
