package com.pay.polycube.policy;

import com.pay.polycube.domain.Grade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BasicDiscountImpl implements DiscountPolicy{

    @Override
    public int discount(Grade grade, int price) {
        if (grade == Grade.VIP) {
            if (price <= 1000) {
                log.warn("1000원 이하인 상품은 할인 정책에서 제외합니다.");
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
}
