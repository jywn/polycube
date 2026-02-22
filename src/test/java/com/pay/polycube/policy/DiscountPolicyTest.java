package com.pay.polycube.policy;

import com.pay.polycube.domain.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DiscountPolicyTest {

    private BasicDiscountImpl discountPolicy;

    @BeforeEach
    void setUp() {
        discountPolicy = new BasicDiscountImpl();
    }

    @Test
    @DisplayName("NORMAL: 할인이 적용되지 않는다")
    void noDiscount() {
        int price = 10000;

        int result = discountPolicy.discount(Grade.NORMAL, price);

        assertThat(result).isEqualTo(10000);
    }

    @Test
    @DisplayName("VIP: 1000원 초과 상품은 1000원 할인된다")
    void discountWhenPriceOver1000() {
        int price = 10000;

        int result = discountPolicy.discount(Grade.VIP, price);

        assertThat(result).isEqualTo(9000);
    }

    @Test
    @DisplayName("VIP: 1000원 이하 상품은 할인이 적용되지 않는다")
    void noDiscountWhenPriceIs1000OrLess() {
        int price = 1000;

        int result = discountPolicy.discount(Grade.VIP, price);

        assertThat(result).isEqualTo(1000);
    }

    @Test
    @DisplayName("VVIP: 10% 할인이 적용된다")
    void tenPercentDiscount() {
        int price = 10000;

        int result = discountPolicy.discount(Grade.VVIP, price);

        assertThat(result).isEqualTo(9000);
    }

    @Test
    @DisplayName("VVIP: 소수점 이하는 버림 처리된다")
    void truncatesDecimal() {
        int price = 15;

        int result = discountPolicy.discount(Grade.VVIP, price);

        assertThat(result).isEqualTo(13);
    }

}
