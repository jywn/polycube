package com.pay.polycube.service;

import com.pay.polycube.domain.Grade;
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

    private void pay(PaymentMethod paymentMethod) {

    }


}
