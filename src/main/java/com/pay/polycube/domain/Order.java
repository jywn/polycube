package com.pay.polycube.domain;

import com.pay.polycube.exception.BusinessException;
import com.pay.polycube.exception.ErrorCode;
import com.pay.polycube.policy.DiscountPolicy;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column (name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "original_price")
    private int originalPrice;

    @Column(name = "final_price")
    private int finalPrice;

    /**
     * 결제 완료 주문은 적용된 할인(등급, 적용 정책명, 할인율, 할인 금액 등)을 데이터베이스에 기록
     */
    @Column(name = "grade")
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "policy")
    private String policy;

    @Column(name = "discount_rate")
    private int discountRate;

    @Column(name = "discount_price")
    private int discountPrice;

    private Order(Member member, String productName, int originalPrice, PaymentMethod paymentMethod) {
        this.member = member;
        this.grade = member.getGrade();
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.finalPrice = originalPrice;
        this.paymentMethod = paymentMethod;
    }

    public static Order create(Member member, String productName, int originalPrice, PaymentMethod paymentMethod) {
        return new Order(member, productName, originalPrice, paymentMethod);
    }

    public void discount(DiscountPolicy discountPolicy) {
        this.policy = discountPolicy.getName();
        this.finalPrice = discountPolicy.discount(this, this.getOriginalPrice());
        this.discountPrice = this.originalPrice - this.finalPrice;
        this.discountRate = 100 - this.finalPrice / this.originalPrice * 100;
    }

    public void pay() {
        if (this.paidAt != null) {
            throw new BusinessException(ErrorCode.PAY_TWICE);
        }
        if (finalPrice == 0) {
            throw new BusinessException(ErrorCode.NO_PRICE);
        }
        this.paidAt = LocalDateTime.now();
    }
}
