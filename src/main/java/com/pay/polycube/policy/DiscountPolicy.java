package com.pay.polycube.policy;

import com.pay.polycube.domain.Grade;
import com.pay.polycube.domain.Order;

public interface DiscountPolicy {
    int discount(Order order, int currentPrice);
    String getName();
}
