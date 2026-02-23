package com.pay.polycube.policy;

import com.pay.polycube.domain.Grade;
import com.pay.polycube.domain.Member;
import com.pay.polycube.domain.Order;
import com.pay.polycube.domain.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountPolicyTest {

    private GradeDiscountPolicy gradeDiscountPolicy;

    @BeforeEach
    void setUp() {
        gradeDiscountPolicy = new GradeDiscountPolicy();
    }

    @Test
    @DisplayName("NORMAL: 할인이 적용되지 않는다")
    void normalNoDiscount() {
        Member member = Member.create(Grade.NORMAL);
        Order order = Order.create(member, "product", 10_000, PaymentMethod.CREDIT_CARD);

        int result = gradeDiscountPolicy.discount(order, 10_000);

        assertThat(result).isEqualTo(10_000);
    }

    @Test
    @DisplayName("VIP: 1000원 초과 상품은 1000원 할인된다")
    void vipDiscountWhenPriceOver1000() {
        Member member = Member.create(Grade.VIP);
        Order order = Order.create(member, "product", 10_000, PaymentMethod.CREDIT_CARD);

        int result = gradeDiscountPolicy.discount(order, 10_000);

        assertThat(result).isEqualTo(9_000);
    }

    @Test
    @DisplayName("VIP: 1000원 이하 상품은 할인이 적용되지 않는다")
    void vipNoDiscountWhenPriceIs1000OrLess() {
        Member member = Member.create(Grade.VIP);
        Order order = Order.create(member, "product", 1_000, PaymentMethod.CREDIT_CARD);

        int result = gradeDiscountPolicy.discount(order, 1_000);

        assertThat(result).isEqualTo(1_000);
    }

    @Test
    @DisplayName("VVIP: 10% 할인이 적용된다")
    void vvipTenPercentDiscount() {
        Member member = Member.create(Grade.VVIP);
        Order order = Order.create(member, "product", 10_000, PaymentMethod.CREDIT_CARD);

        int result = gradeDiscountPolicy.discount(order, 10_000);

        assertThat(result).isEqualTo(9_000);
    }

    @Test
    @DisplayName("VVIP: 소수점 이하는 버림 처리된다")
    void vvipTruncatesDecimal() {
        Member member = Member.create(Grade.VVIP);
        Order order = Order.create(member, "product", 15, PaymentMethod.CREDIT_CARD);

        int result = gradeDiscountPolicy.discount(order, 15);

        assertThat(result).isEqualTo(13);
    }
}