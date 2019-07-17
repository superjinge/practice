package com.xuecheng.producer.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * .rabbitm
 * rabbitmqr入门程序
 *
 * @author : superjinge
 * @date : 2019/07/14
 */
public class Producer01 {

    //队列
    private static final String QUEUE = "helloword";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //端口
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        //设置虚拟机  ->一个mq的服务可以设置多个虚拟机,每个虚拟机相当于独立的mq
        factory.setVirtualHost("/");
        Connection connection = null;
        Channel channel = null;
        try {
            //j建立新连接
            connection = factory.newConnection();
            //创建会话,生产者和mq通信在channelt通道中完成
            channel = connection.createChannel();
            //声明队列
            /**
             * * param1:队列名称
             * * param2:是否持久化
             * * param3:队列是否独占此连接
             * * param4:队列不再使用时是否自动删除此队列
             * * param5:队列参数
             */
            channel.queueDeclare(QUEUE, true, false, false, null);

            String message = "helloWord===" + System.currentTimeMillis();

            /**
             * 消息发布方法
             * param1：Exchange的名称，如果没有指定，则使用Default Exchange
             * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
             * param3:消息包含的属性
             * param4：消息体
             */
            channel.basicPublish("", QUEUE, null, message.getBytes());

            System.out.println("send message is :" + message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {

            if (channel != null) {
                channel.close();
            }

            if (connection != null) {
                connection.close();
            }
        }

    }
}
