package com.learn.rabbitmq.direct;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

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
public class Publish {

    private static final String EXCHANGE_NAME = "exchange_direct";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();

            //一个exchange（交换机、转发器）上可以绑定多个队列，定义交换机类型是direct模式，可以根据routKey匹配来分发消息
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String msg = "";

            for (int i = 1; i <= 10; i++) {
                String routingKey = "";
                if (i % 2 == 0) {
                    routingKey = "error";
                    msg = "error message";
                } else {
                    routingKey = "info";
                    msg = "info message";
                }

                channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
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
