package com.learn.rabbitmq.topic;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈消息生产者〉
 *
 * @author whp
 * @create 2020-06-01
 * @since 1.0.0
 */
public class Send {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME,"topic");

            String msg = "商品。。。";
            channel.basicPublish(EXCHANGE_NAME,"good.add",null,msg.getBytes());

            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
