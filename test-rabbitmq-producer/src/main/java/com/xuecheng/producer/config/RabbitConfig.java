package com.xuecheng.producer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : superjinge
 * @date : 2019/07/17
 */
@Configuration
public class RabbitConfig {

    //队列名称
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    public static final String ROUTINGKEY_EMAIL = "inform.#.email.#";
    public static final String ROUTINGKEY_SMS = "inform.#.sms.#";


    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        //durable(true)持久化
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }
    //声明队列

    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL() {
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS() {
        return new Queue(QUEUE_INFORM_SMS);
    }

    //队列绑定交换机

    /**
     * 队列绑定交换机指定通配符：
     * 统配符规则：
     * 中间以“.”分隔。
     * 符号#可以匹配多个词，符号*可以匹配一个词语。
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingEmail(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
                                @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTINGKEY_EMAIL)
                .noargs();
    }

    @Bean
    public Binding bindingSms(@Qualifier(QUEUE_INFORM_SMS) Queue queue,
                              @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTINGKEY_SMS)
                .noargs();
    }
}
