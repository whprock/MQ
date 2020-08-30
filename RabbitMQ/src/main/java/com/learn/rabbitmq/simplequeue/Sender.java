package com.learn.rabbitmq.simplequeue;

import com.learn.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈简单队列--发送消息〉
 *
 * @author whp
 * @create 2020-05-29
 * @since 1.0.0
 */
public class Sender {

    //队列名称，用于保存在rabbitmq的virtual hosts中，而这个队列里保存了消息数据，
    // 发送方：往这个队列里发送消息；接收方：从这个队列取消息
    private static final String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) {
        try {
            while(true){
                //获取一个连接
                Connection connection = ConnectionUtils.getConnection();

                /**
                 * createChannel是一个同步方法，其中创建完ChannelManager对象，
                 * 在调用createChannel方法时，是被synchronized修饰代码块
                 *
                 * synchronized (this.monitor) {
                 *             int channelNumber = channelNumberAllocator.allocate();
                 *             if (channelNumber == -1) {
                 *                 return null;
                 *             } else {
                 *                 ch = addNewChannel(connection, channelNumber);
                 *             }
                 *         }
                 */
                //从连接获取一个通道
                Channel channel = connection.createChannel();

                //声明一个队列
                channel.queueDeclare(QUEUE_NAME,false,false,false,null);

                String msg = "hell rabbitmq: simple queue";

                //向队列里面发布消息，将消息保存到定义的队列中，并提交到rabbitmq的 virtual hosts中
                channel.basicPublish("",QUEUE_NAME,null,msg.getBytes());

                //关闭通道
                channel.close();
                //关闭连接
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
