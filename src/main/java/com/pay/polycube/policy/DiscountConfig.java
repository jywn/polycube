package com.pay.polycube.policy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscountConfig {

    @Bean
    public DiscountPolicy discountPolicy() {
        return new BasicDiscountImpl();
    }
}
