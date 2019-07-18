package com.xuecheng.consumer.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.consumer.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author : superjinge
 * @date : 2019/07/18
 */
@Component
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitConfig.QUEUE_INFORM_EMAIL})
    public void receiveMessage(String msg, Message message, Channel channel) {
        System.out.println("recevice msg is :" + msg);

    }
}
