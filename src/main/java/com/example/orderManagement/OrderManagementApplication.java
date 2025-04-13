package com.example.orderManagement;

import org.apache.catalina.LifecycleException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
//@EnableConfigurationProperties(OrdersConfigurationProps.class)
public class OrderManagementApplication {

    public static void main(String[] args) throws LifecycleException {
        System.out.println("hello");
        SpringApplication.run(OrderManagementApplication.class, args);
    }
}
