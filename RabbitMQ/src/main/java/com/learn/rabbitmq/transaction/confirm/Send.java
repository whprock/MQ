package com.learn.rabbitmq.transaction.confirm;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author whp
 * @create 2020-06-01
 * @since 1.0.0
 */
public class Send {
    private static final String QUEUE_NAME = "confirm_queue";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //信道调用confirmSelect  将channel设置为confirm模式
            channel.confirmSelect();

            for (int i = 0; i < 10; i++) {
                String msg = "发送confirm模式的消息";
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            }

            if (channel.waitForConfirms()) {
                System.out.println("send msg success");
            } else {
                System.out.println("send msg failed");
            }

            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
