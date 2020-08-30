package com.learn.rabbitmq.fanout.publishsubscribe;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈消息发布者〉
 *
 * @author whp
 * @create 2020-05-31
 * @since 1.0.0
 */
public class Publish {
    /**
     * 因为交换机没有存储能力，在rabbitmq里面，只有队列有存储能力
     *
     * 当只声明一个exchange时，此时该exchange上没有绑定任何队列，发布者往exchange里发送的消息会丢失，
     *
     * 因为exchange本身是没有存储能力的。
     *
     * 所以在部署启动服务器的时候，要先启动订阅者（消费者）服务器，这样才能确保消息不丢失
     */
    private static final String EXCHANGE_NAME = "test_exchange";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            //声明通道
            Channel channel = connection.createChannel();
            //声明fanout类型交换机，并定义交换机类型，要用广播模式，第二个参数必须写-fanout
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            for (int i = 1; i < 10; i++) {
                    String msg = "hello exchange_" +i;
                    channel.basicPublish(EXCHANGE_NAME,"",null,msg.getBytes());

                    System.out.println("Send: "+ msg);
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
