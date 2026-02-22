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

    private Order(Member member, String productName, int originalPrice) {
        this.member = member;
        this.productName = productName;
        this.originalPrice = originalPrice;
    }

    public static Order create(Member member, String productName, int originalPrice) {
        return new Order(member, productName, originalPrice);
    }

    public void discount(DiscountPolicy discountPolicy) {
        this.finalPrice = discountPolicy.discount(member.getGrade(), originalPrice);
    }

    public void pay(PaymentMethod paymentMethod) {
        if (this.paidAt != null) {
            throw new BusinessException(ErrorCode.PAY_TWICE);
        }
        this.paymentMethod = paymentMethod;
        this.paidAt = LocalDateTime.now();
    }
}
