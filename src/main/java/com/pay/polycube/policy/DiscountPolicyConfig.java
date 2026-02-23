package com.pay.polycube.policy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DiscountPolicyConfig {

    @Bean
    public DiscountPolicy discountPolicy() {
        return new CompositeDiscountPolicy(
                List.of(
                        new GradeDiscountPolicy(),
                        new PaymentMethodDiscountPolicy())
        );
    }
}
