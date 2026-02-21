package com.pay.polycube.domain;

import com.pay.polycube.service.PaymentMethod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    private Order(Member member, PaymentMethod paymentMethod, String productName, int originalPrice, int finalPrice) {
        this.member = member;
        this.paymentMethod = paymentMethod;
        this.paidAt = LocalDateTime.now();
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
    }

    public static Order create(Member member, PaymentMethod paymentMethod, String productName, int originalPrice, int finalPrice) {
        return Order.builder()
                .member(member)
                .paymentMethod(paymentMethod)
                .productName(productName)
                .originalPrice(originalPrice)
                .finalPrice(finalPrice)
                .build();
    }
}
