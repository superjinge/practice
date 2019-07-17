package com.xuecheng.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : superjinge
 * @date : 2019/07/17
 */
@SpringBootApplication
public class RabbitmqConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RabbitmqConsumerApplication.class, args);
        System.out.println("RabbitmqConsumer is running");
    }
}
