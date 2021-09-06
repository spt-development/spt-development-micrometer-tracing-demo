package com.spt.development.demo.cucumber.config;

import com.spt.development.test.integration.HttpTestManager;
import org.springframework.context.annotation.Bean;

public class TestManagerConfig {

    @Bean
    public HttpTestManager httpTestManager() {
        return new HttpTestManager();
    }
}
