package com.learn.rabbitmq.transaction;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈事务模式发送消息〉
 *
 * @author whp
 * @create 2020-06-01
 * @since 1.0.0
 */
public class TxSend {
    private static final String QUEUE_NAME = "transaction_queue";

    public static void main(String[] args) throws IOException {
        Channel channel = null;
        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnection();
            channel = connection.createChannel();
            //在声明队列中的durable参数，才是对队列的持久化，为true时，重启rabbitmq队列依然存在，但是队列中的消息丢失
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            //开启事务--针对消息的事务，对队列没用用，重启rabbitmq后，队列依然会消失
            channel.txSelect();
            String msg = "hello tx";
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
//            int num = 1 / 0;//模拟rollback场景
            channel.txCommit();

            channel.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("tx rollback");
            channel.txRollback();
            try {
                channel.close();
            } catch (TimeoutException e1) {
                e1.printStackTrace();
            }
            connection.close();
        }
    }
}
