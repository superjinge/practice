package com.xuecheng.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : superjinge
 * @date : 2019/07/17
 */
@SpringBootApplication
public class RabbitmqProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RabbitmqProducerApplication.class, args);
        System.out.println("RabbitmqProducer is running");
    }
}
