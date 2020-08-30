package com.learn.rabbitmq.workqueue.fairdistribution;

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
 * 〈工作队列消费者〉
 *
 * @author whp
 * @create 2020-05-30
 * @since 1.0.0
 */
public class Receiver_1 {

    private static final String WORK_QUEUE = "work_queue";

    public static void main(String[] args) {
        try {
            //获取连接
            Connection connection = ConnectionUtils.getConnection();
            //创建渠道
            Channel channel = connection.createChannel();

            /**
             * 消息的持久化
             *
             *  参数：durable = true
             *
             * 如果消息队列已经定义好了，再把durable = false改成 true的话，
             * 代码虽然不报错，但是运行时会报错：
             * Caused by: com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=406, reply-text=PRECONDITION_FAILED - inequivalent arg 'durable' for queue 'work_queue' in vhost '/vhost_whp': received 'true' but current is 'false', class-id=50, method-id=10)
             *
             * 意思是：已定义的work_queue的durable参数值当前是false，不能修改成true。
             *
             * 解决办法：
             *          1、重新定义一个queue，直接把durable设置成true
             *          2、把原来的queue从rabbitmq中删除，再把它的durable参数设置为true
             *
             * durable 的值必须在用在新定义的队列上，已存在的队列，该值不能修改，修改运行即报错；
             *      已存在的队列，如果想改durable值，将其从rabbitmq中删除即可。
             *
             * rabbitmq不允许重新定义（参数不同）一个已存在的同名队列。
             *
             * 例如：队列queue1的durable=false； 又定义一个queue1的durable=true；这时候rabbitmq就会报错
             */
            //声明队列
            channel.queueDeclare(WORK_QUEUE, true, false, false, null);

            //保证一次分发一个
            channel.basicQos(1);

            //定义消费者
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String msg = new String(body, "utf-8");
                    System.out.println("Receiver_1  conform msg:" + msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //手动回执，处理完之后通知消息队列，发送下一条消息
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };
            /**
             * autoAck = true(自动确认模式)
             *     自动确认模式： 一旦rabbitmq将消息分发给消费者，就会将消息从内存中删除。
             *                  这种情况下，如果杀死正在执行的消费者，就会丢失正在处理的数据。
             *
             * autoAck = false(手动确认模式)
             *      手动确认模式： 如果有一个消费者挂掉，就会把消息交付给其他消费者，rabbitmq支持消息应答，消费者发送一个消息应答，
             *                  告诉rabbitmq这条消息已经处理完了，你可以删除了，然后rabbitmq就删除内存中的数据。
             */
            //监听队列
            channel.basicConsume(WORK_QUEUE, false, consumer);//autoAck = false 自动应答关闭


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally{
            System.out.println("receiver [1] is done");
        }
    }
}
