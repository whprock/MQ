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
 * 〈消息订阅者2〉
 *
 * @author whp
 * @create 2020-05-31
 * @since 1.0.0
 */
public class Subscribe_2 {

    private static final String QUEUE_NAME = "queue_fanout_sms";

    private static final String EXCHANGE_NAME = "test_exchange";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);

            //将消息队列绑定到交换机上
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

            //保证每次之分发个消息，收到返回的确认消息之后，再发下一条消息
            channel.basicQos(1);

            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String msg = new String(body, "utf-8");
                    System.out.println("Subscribe_2 msg: "+ msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //给rabbitmq返回一个deliveryTag，以确保，每次处理完消息后，在让rabbitmq发送下一条消息过来
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            };

            //autoAck = true :自动回执； false :手动回执；
            // autoAck设置false的时候，这时候，消费者只会处理一条数据，因为没有给rabbitmq返回deliveryTay，
            // 而rabbitmq有在等待队列给我返回deliveryTag，rabbitmq拿不到deliveryTag，就会认为，消费者还没处理完消息，
            // 不能发送下一条
            //
            // 所以，autoAck设置false的时候，必须设置basicAck的值，否则rabbitmq将一直等待队列返回处理成功的deliveryTag。
            channel.basicConsume(QUEUE_NAME,true,consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            System.out.println(" Subscribe[2] is done");
        }
    }
}
