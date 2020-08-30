package com.learn.rabbitmq.workqueue.Rotationtrainingdistribution;

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
 * 〈工作队列消费者--轮训分发〉
 *
 * @author whp
 * @create 2020-05-30
 * @since 1.0.0
 */
public class Receiver_2 {

    private static final String WORK_QUEUE = "work_queue";

    public static void main(String[] args) {
        try {

            //创建连接
            Connection connection = ConnectionUtils.getConnection();
            //声明渠道
            Channel channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(WORK_QUEUE,false,false,false,null);
            //定义消费者
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String msg = new String(body,"utf-8");
                    System.out.println("Receiver_1  conform msg:" + msg);
                }
            };
            //监听队列
            channel.basicConsume(WORK_QUEUE,true,consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
