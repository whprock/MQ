package com.learn.rabbitmq.transaction.confirm.AsyncConfirm;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author whp
 * @create 2020-06-01
 * @since 1.0.0
 */
public class send {
    private static final String QUEUE_NAME = "aysnc_confirm_queue";

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionUtils.getConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //信道调用confirmSelect  将channel设置为confirm模式
            channel.confirmSelect();

            final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<>());

            //添加一个监听
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    //multiple 返回为true时，说明在deliveryTag序号之前的所有消息都成功处理了，
                    if (multiple) {
                        System.out.println("handleAck multiple = false");
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        System.out.println("handleAck multiple = false");
                        confirmSet.remove(deliveryTag);
                    }
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    if (multiple) {
                        System.out.println("handleNack multiple = true");
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        System.out.println("handleNack multiple = false");
                        confirmSet.remove(deliveryTag);
                    }
                }
            });


            String msg = "发送confirm模式的消息";
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());


            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}