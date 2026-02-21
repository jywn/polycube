package com.pay.polycube.service;

import com.pay.polycube.domain.Grade;

public interface DiscountPolicy {
    int discount(Grade grade, int price);
}
