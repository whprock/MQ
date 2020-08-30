package com.learn.rabbitmq.fanout.publishsubscribe;

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
 * 〈订阅者〉
 *
 * @author whp
 * @create 2020-05-31
 * @since 1.0.0
 */
public class Subscribe_1 {

    //定义了一个永久队列、也可以使用临时队列，减少rabbitmq中的队列数，减轻压力
    private static final String QUEUE_NAME = "queue_fanout_email";

    private static final String EXCHANGE_NAME = "test_exchange";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();

//            //声明消息队列，使用永久队列；也可以使用临时队列
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //在消费者里，使用声明交换机的方式也可以，mq能自动识别交换机
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            //使用临时队列
            String tempQueue = channel.queueDeclare().getQueue();

            //将消息队列绑定到交换机上
            channel.queueBind(tempQueue, EXCHANGE_NAME, "");

            //确保每次处理完一条消息后，在给消费者发送下一条消息
            channel.basicQos(1);

            //获取consumer
            Consumer consumer = getConsumer(channel);

            //监听队列 autoAck = false 关闭自动应答，让rabbitmq不会自动发送消息
            channel.basicConsume(QUEUE_NAME, false, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Subscribe is done");
        }
    }

    private static Consumer getConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String receiverMsg = new String(body, "utf-8");
                System.out.println("Subscribe[1]: " + receiverMsg);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //逻辑处理完之后，返回给rabbitmq 一个deliveryTag，只有当rabbitmq接收到deliveryTag后，才会发送下一条消息
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
    }
}
