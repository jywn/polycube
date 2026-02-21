package com.pay.polycube.service;

import com.pay.polycube.domain.Grade;
import com.pay.polycube.domain.Order;
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

    private final DiscountPolicy discountPolicy;
    private final OrderRepository orderRepository;

    private void pay(Order order, Grade grade, PaymentMethod paymentMethod) {

        discountPolicy.discount(grade, order.getOriginalPrice());
        orderRepository.save(order);
    }


}
