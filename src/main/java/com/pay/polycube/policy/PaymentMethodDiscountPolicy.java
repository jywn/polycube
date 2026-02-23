package com.pay.polycube.policy;

import com.pay.polycube.domain.Order;
import com.pay.polycube.domain.PaymentMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentMethodDiscountPolicy implements DiscountPolicy {

    @Override
    public int discount(Order order, int price) {
        return order.getPaymentMethod() == PaymentMethod.POINT ? price * 95 / 100 : price;
    }

    @Override
    public String getName() {
        return "결제 수단 할인 정책";
    }
}
