package com.pay.polycube.policy;

import com.pay.polycube.domain.Order;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CompositeDiscountPolicy implements DiscountPolicy {

    private final List<DiscountPolicy> discountPolicies;

    @Override
    public int discount(Order order, int price) {

        for (DiscountPolicy discountPolicy : discountPolicies) {
            price = discountPolicy.discount(order, price);
        }

        return price;
    }

    @Override
    public String getName() {
        return "복합 할인 정책";
    }
}
