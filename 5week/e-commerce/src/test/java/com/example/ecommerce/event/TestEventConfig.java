package com.example.ecommerce.event;

import com.example.ecommerce.common.recode.ProceedOrderEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@TestConfiguration
public class TestEventConfig {
    @Bean
    public TestProceedOrderEventListener listener() {
        return new TestProceedOrderEventListener();
    }
}


