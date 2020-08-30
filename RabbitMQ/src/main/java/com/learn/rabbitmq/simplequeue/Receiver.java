package com.learn.rabbitmq.simplequeue;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.impl.AMQBasicProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈rabbitmq-简单队列-消息接收者〉
 * <p>
 * rabbitmq 服务重启，或者关闭服务，消息队列会被清除
 *
 * @author whp
 * @create 2020-05-29
 * @since 1.0.0
 */
public class Receiver {

    private static final String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) {

        try {
            //创建连接
            Connection connection = ConnectionUtils.getConnection();
            //创建频道
            Channel channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //创建消费者--重写handleDelivery方法
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
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
                    System.out.println("new api receive msg[" + i++ + "]" + msg);
                }
            };
            //监听队列
            channel.basicConsume(QUEUE_NAME, true, defaultConsumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    //废弃的api-QueueingConsumer，在新版本RabbitMQ已不再使用
    private static void oldConsumerApi() {
        try {
            Connection connection = ConnectionUtils.getConnection();
            //创建频道
            Channel channel = connection.createChannel();
            //定义队列的消费者
            QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

            //监听队列
            channel.basicConsume(QUEUE_NAME, true, queueingConsumer);
            while (true) {
                Thread.sleep(500);
                QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
                String body = new String(delivery.getBody());
                System.out.println("接收到的消息：" + body);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
