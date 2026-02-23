package com.pay.polycube.service;

import com.pay.polycube.domain.Grade;
import com.pay.polycube.domain.Member;
import com.pay.polycube.domain.Order;
import com.pay.polycube.domain.PaymentMethod;
import com.pay.polycube.policy.GradeDiscountPolicy;
import com.pay.polycube.policy.DiscountPolicy;
import com.pay.polycube.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private DiscountPolicy discountPolicy;
    private OrderCommandService orderCommandService;

    @BeforeEach
    void setUp() {
        discountPolicy = new GradeDiscountPolicy();
        orderCommandService = new OrderCommandService(orderRepository, discountPolicy);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("VIP + POINT: 등급 할인(-1000) 후 결제 수단 할인(5%) 순서로 적용된다")
    void vipWithPoint_gradeDiscountBeforePaymentDiscount() {
        // given
        Member member = Member.create(Grade.VIP);

        // when
        Order result = orderCommandService.process(
                member, PaymentMethod.POINT, "product", 10_000
        );

        // then
        // Grade first: 10,000 - 1,000 = 9,000
        // Payment second: 9,000 * 0.95 = 8,550
        // (Wrong order would be: 10,000 * 0.95 - 1,000 = 8,500)
        assertThat(result.getFinalPrice()).isEqualTo(8_550);
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.POINT);
        assertThat(result.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("VIP + CREDIT_CARD: 등급 할인만 적용된다")
    void vipWithCreditCard_onlyGradeDiscount() {
        // given
        Member member = Member.create(Grade.VIP);

        // when
        Order result = orderCommandService.process(
                member, PaymentMethod.CREDIT_CARD, "product", 10_000
        );

        // then
        assertThat(result.getFinalPrice()).isEqualTo(9_000);
    }

    @Test
    @DisplayName("VVIP + POINT: 등급 할인(10%) 후 결제 수단 할인(5%) 순서로 적용된다")
    void vvipWithPoint_gradeDiscountBeforePaymentDiscount() {
        // given
        Member member = Member.create(Grade.VVIP);

        // when
        Order result = orderCommandService.process(
                member, PaymentMethod.POINT, "product", 10_000
        );

        // then
        // Grade first: 10,000 * 0.9 = 9,000
        // Payment second: 9,000 * 0.95 = 8,550
        assertThat(result.getFinalPrice()).isEqualTo(8_550);
    }

    @Test
    @DisplayName("VVIP + CREDIT_CARD: 등급 할인만 적용된다")
    void vvipWithCreditCard_onlyGradeDiscount() {
        // given
        Member member = Member.create(Grade.VVIP);

        // when
        Order result = orderCommandService.process(
                member, PaymentMethod.CREDIT_CARD, "product", 10_000
        );

        // then
        assertThat(result.getFinalPrice()).isEqualTo(9_000);
    }

    @Test
    @DisplayName("NORMAL + POINT: 결제 수단 할인만 적용된다")
    void normalWithPoint_onlyPaymentDiscount() {
        // given
        Member member = Member.create(Grade.NORMAL);

        // when
        Order result = orderCommandService.process(
                member, PaymentMethod.POINT, "product", 10_000
        );

        // then
        assertThat(result.getFinalPrice()).isEqualTo(9_500);
    }

    @Test
    @DisplayName("NORMAL + CREDIT_CARD: 할인 없음")
    void normalWithCreditCard_noDiscount() {
        // given
        Member member = Member.create(Grade.NORMAL);

        // when
        Order result = orderCommandService.process(
                member, PaymentMethod.CREDIT_CARD, "product", 10_000
        );

        // then
        assertThat(result.getFinalPrice()).isEqualTo(10_000);
    }
}