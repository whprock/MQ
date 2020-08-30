package com.learn.rabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈一句话功能简述〉<br>
 * 〈连接rabbitmq工具类〉
 *
 * @author whp
 * @create 2020-05-29
 * @since 1.0.0
 */
public class ConnectionUtils {

    public static Connection getConnection() throws IOException, TimeoutException {
        //创建一个连接
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接地址
        factory.setHost("127.0.0.1");
        //设置端口号
        factory.setPort(5672);
        //设置virtual host
        factory.setVirtualHost("/vhost_whp");
        //设置用户名
        factory.setUsername("whp");
        //设置密码
        factory.setPassword("guest");

        //返回一个Connection对象
        Connection connection = factory.newConnection();

        return connection;
    }

    public static void close(){

    }
}
