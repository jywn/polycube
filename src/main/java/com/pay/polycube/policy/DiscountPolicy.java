package com.pay.polycube.policy;

import com.pay.polycube.domain.Grade;

public interface DiscountPolicy {
    int discount(Grade grade, int price);
}
