package com.xuecheng.consumer.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author : superjinge
 * @date : 2019/07/17
 */
public class Consumer01 {
    public static final String QUEUE = "helloword";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE, true, false, false, null);

        //定义消费方法
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            /**
             *
             * @param consumerTag  消费者标签
             * @param envelope  消息包内容
             * @param properties
             * @param body
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                super.handleDelivery(consumerTag, envelope, properties, body);

                String exchange = envelope.getExchange();

                String routingKey = envelope.getRoutingKey();

                long deliveryTag = envelope.getDeliveryTag();

                String msg = new String(body, "utf-8");

                System.out.println("msg===" + msg);
            }
        };


        channel.basicConsume(QUEUE, true, consumer);


    }
}
