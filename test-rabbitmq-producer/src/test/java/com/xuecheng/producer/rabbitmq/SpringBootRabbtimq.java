package com.xuecheng.producer.rabbitmq;

import com.xuecheng.producer.config.RabbitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : superjinge
 * @date : 2019/07/18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringBootRabbtimq {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendEmail() {
        String message = "send email message to user";
        /**
         * 参数
         * 1.交换机名称
         * 2.routingKey
         * 3.消息内容
         */
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_TOPICS_INFORM
                , "inform.email", message);
    }

}
