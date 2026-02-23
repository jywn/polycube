package com.pay.polycube.domain;

import com.pay.polycube.policy.CompositeDiscountPolicy;
import com.pay.polycube.policy.DiscountPolicy;
import com.pay.polycube.policy.GradeDiscountPolicy;
import com.pay.polycube.policy.PaymentMethodDiscountPolicy;
import com.pay.polycube.repository.MemberRepository;
import com.pay.polycube.repository.OrderRepository;
import com.pay.polycube.service.OrderCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@Commit
@Transactional
@SpringBootTest
class HistoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("정책 수정/삭제 후에도 과거 결제 데이터와 이력이 보존된다")
    void historyPreservedAfterPolicyChange() {
        // given: 기존 정책 (등급 + 결제수단 할인)
        DiscountPolicy originalPolicy = new CompositeDiscountPolicy(
                List.of(new GradeDiscountPolicy(), new PaymentMethodDiscountPolicy())
        );
        OrderCommandService originalService = new OrderCommandService(orderRepository, originalPolicy);

        Member member = Member.create(Grade.VIP);
        memberRepository.save(member);

        // when: 기존 정책으로 결제
        Order order = originalService.process(member, PaymentMethod.POINT, "product", 10_000);
        Long orderId = order.getId();

        // 정책 변경: 등급 할인만 적용하는 정책으로 변경
        OrderCommandService changedService = new OrderCommandService(orderRepository, new GradeDiscountPolicy());

        // then: DB에서 조회한 과거 주문은 기존 정책 기준 값 유지
        Order savedOrder = orderRepository.findById(orderId).orElseThrow();

        assertThat(savedOrder.getGrade()).isEqualTo(Grade.VIP);
        assertThat(savedOrder.getPolicy()).isEqualTo("복합 할인 정책");
        assertThat(savedOrder.getOriginalPrice()).isEqualTo(10_000);
        assertThat(savedOrder.getFinalPrice()).isEqualTo(8_550);  // (10000-1000)*0.95
        assertThat(savedOrder.getDiscountPrice()).isEqualTo(1_450);
        assertThat(savedOrder.getPaymentMethod()).isEqualTo(PaymentMethod.POINT);
        assertThat(savedOrder.getPaidAt()).isNotNull();
    }
}