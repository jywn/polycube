package com.pay.polycube.service;

import com.pay.polycube.domain.Member;
import com.pay.polycube.domain.Order;
import com.pay.polycube.domain.PaymentMethod;
import com.pay.polycube.policy.DiscountPolicy;
import com.pay.polycube.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final DiscountPolicy discountPolicy;

    public Order process(Member member, PaymentMethod paymentMethod, String productName, int originalPrice) {
        // 1. create basic order
        Order order = Order.create(member, productName, originalPrice, paymentMethod);

        // 2. apply discount
        int discountedPrice = discountPolicy.discount(order, originalPrice);
        order.applyDiscount(discountPolicy.getName(), discountedPrice);

        // 3. pay
        order.pay();

        // 4. save and return
        return orderRepository.save(order);
    }


}
