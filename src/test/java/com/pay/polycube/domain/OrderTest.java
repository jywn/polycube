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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderTest {

    private final DiscountPolicyConfig discountPolicyConfig = new DiscountPolicyConfig();
    private final DiscountPolicy discountPolicy = discountPolicyConfig.discountPolicy();
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderCommandService orderCommandService = new OrderCommandService(orderRepository, discountPolicy);

    @Test
    @DisplayName("등급 할인 이후 결제 수단 할인이 적용된다.")
    void pointAfterVIP() {
        Member member = Member.create(Grade.VIP);
        PaymentMethod point = PaymentMethod.POINT;
        String productName = "product001";
        int originalPrice = 10_000;
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Order result = orderCommandService.process(member, point, productName, originalPrice);

        assertThat(result.getFinalPrice()).isEqualTo(8_550);
    }

    @Test
    @DisplayName("중복 결제 시 예외가 발생한다")
    void throwsExceptionWhenPayTwice() {
        Member member = Member.create(Grade.VIP);
        Order order = Order.create(member, "product", 10_000, PaymentMethod.POINT);
        order.discount(discountPolicy);
        order.pay();

        assertThatThrownBy(() -> order.pay())
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getCode()).isEqualTo(ErrorCode.PAY_TWICE);
                });
    }

    @Test
    @DisplayName("결제 금액이 0원이면 예외가 발생한다")
    void throwsExceptionWhenPriceIsZero() {
        Member member = Member.create(Grade.VIP);
        Order order = Order.create(member, "product", 0, PaymentMethod.POINT);

        assertThatThrownBy(() -> order.pay())
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getCode()).isEqualTo(ErrorCode.NO_PRICE);
                });
    }
}