package com.dzarembo.binanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Entry point for the Binance Service microservice.
 * Enables scheduling for automatic cache updates.
 */
@SpringBootApplication
@EnableScheduling
public class BinanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinanceServiceApplication.class, args);
    }

}
