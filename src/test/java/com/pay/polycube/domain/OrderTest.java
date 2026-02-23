package com.pay.polycube.domain;

import com.pay.polycube.exception.BusinessException;
import com.pay.polycube.exception.ErrorCode;
import com.pay.polycube.policy.DiscountPolicy;
import com.pay.polycube.policy.DiscountPolicyConfig;
import com.pay.polycube.repository.OrderRepository;
import com.pay.polycube.service.OrderCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderTest {

    private final DiscountPolicyConfig discountPolicyConfig = new DiscountPolicyConfig();
    private final DiscountPolicy discountPolicy = discountPolicyConfig.discountPolicy();
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderCommandService orderCommandService = new OrderCommandService(orderRepository, discountPolicy);

    @Test
    @DisplayName("중복 결제 시 예외가 발생한다")
    void throwsExceptionWhenPayTwice() {
        Member member = Member.create(Grade.VIP);
        Order order = Order.create(member, "product", 10_000, PaymentMethod.POINT);
        discountPolicy.discount(order, 10_000);
        order.pay();

        BusinessException ex = assertThrows(BusinessException.class, order::pay);
        assertThat(ex.getCode()).isEqualTo(ErrorCode.PAY_TWICE);
    }

    @Test
    @DisplayName("결제 금액이 0원이면 예외가 발생한다")
    void throwsExceptionWhenPriceIsZero() {
        Member member = Member.create(Grade.VIP);
        Order order = Order.create(member, "product", 0, PaymentMethod.POINT);

        BusinessException ex = assertThrows(BusinessException.class, order::pay);
        assertThat(ex.getCode()).isEqualTo(ErrorCode.NO_PRICE);
    }
}