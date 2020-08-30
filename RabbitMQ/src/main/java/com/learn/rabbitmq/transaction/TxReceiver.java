package com.learn.rabbitmq.transaction;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
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
public class TxReceiver {
    private static final String QUEUE_NAME = "transaction_queue";
    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);

            channel.basicConsume(QUEUE_NAME,true,new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String msg = new String(body, "utf-8");
                    System.out.println(msg);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
