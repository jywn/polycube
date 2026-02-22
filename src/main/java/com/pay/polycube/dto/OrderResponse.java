package com.pay.polycube.dto;

import com.pay.polycube.domain.Order;
import com.pay.polycube.domain.PaymentMethod;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {
    private PaymentMethod paymentMethod;
    private LocalDateTime paidAt;
    private String productName;
    private int finalPrice;

    @Builder
    private OrderResponse(PaymentMethod paymentMethod, LocalDateTime paidAt, String productName, int finalPrice) {
        this.paymentMethod = paymentMethod;
        this.paidAt = paidAt;
        this.productName = productName;
        this.finalPrice = finalPrice;
    }

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .paymentMethod(order.getPaymentMethod())
                .paidAt(order.getPaidAt())
                .productName(order.getProductName())
                .finalPrice(order.getFinalPrice())
                .build();
    }
}
