package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching //캐시 사용 설정
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

}
