package com.pay.polycube.dto;

import com.pay.polycube.domain.Order;
import com.pay.polycube.service.PaymentMethod;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {
    PaymentMethod paymentMethod;
    LocalDateTime paidAt;
    String productName;
    int finalPrice;

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
