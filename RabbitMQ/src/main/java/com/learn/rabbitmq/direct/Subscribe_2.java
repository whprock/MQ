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
 * 〈〉
 *
 * @author whp
 * @create 2020-06-01
 * @since 1.0.0
 */
public class Subscribe_2 {

    private static final String DIRECT_QUEUE_NAME = "test_direct_queue";

    private static final String EXCHANGE_DIRECT = "exchange_direct";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();

            Channel channel = connection.createChannel();

            channel.queueDeclare(DIRECT_QUEUE_NAME, false, false, false, null);

            channel.queueBind(DIRECT_QUEUE_NAME, EXCHANGE_DIRECT, "");

            channel.basicQos(1);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String msg = new String(body, "utf-8");
                    System.out.println("接收到error消息：" + msg);

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            channel.basicConsume(DIRECT_QUEUE_NAME, false, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
