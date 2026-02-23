package com.pay.polycube.policy;

import com.pay.polycube.domain.Grade;
import com.pay.polycube.domain.Order;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradeDiscountPolicy implements DiscountPolicy {

    @Override
    public int discount(Order order, int price) {
        Grade grade = order.getMember().getGrade();

        if (grade == Grade.VIP) {
            if (price <= 1000) {
                log.info("1000원 이하인 상품은 할인 정책에서 제외합니다.");
                return price;
            }
            return price - 1000;
        }

        if (grade == Grade.VVIP) {
            // 고민: 정수 가격의 실수화
            return price * 90 / 100;
        }

        return price;
    }

    @Override
    public String getName() {
        return "등급 할인 정책";
    }
}
