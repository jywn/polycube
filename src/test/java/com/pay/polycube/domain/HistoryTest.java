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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class HistoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("정책 수정/삭제 후에도 과거 결제 데이터와 이력이 보존된다")
    void historyPreservedAfterPolicyChange() {
        DiscountPolicy originalPolicy = new CompositeDiscountPolicy(
                List.of(new GradeDiscountPolicy(), new PaymentMethodDiscountPolicy())
        );
        OrderCommandService doubleDiscountService = new OrderCommandService(orderRepository, originalPolicy);

        Member member = Member.create(Grade.VIP);
        memberRepository.save(member);

        Order order = doubleDiscountService.process(member, PaymentMethod.POINT, "product", 10_000);
        Long orderId = order.getId();

        OrderCommandService gradeDiscountService = new OrderCommandService(orderRepository, new GradeDiscountPolicy());

        Order savedOrder = orderRepository.findById(orderId).orElseThrow();

        assertThat(savedOrder.getFinalPrice()).isEqualTo(8_550);  // 기존: (10000-1000)*0.95
        assertThat(savedOrder.getDiscountPrice()).isEqualTo(1_450);
    }
}