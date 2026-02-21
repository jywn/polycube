package com.pay.polycube.dto;

import com.pay.polycube.service.PaymentMethod;
import lombok.*;

@Getter
@NoArgsConstructor
public class OrderRequest {
    PaymentMethod paymentMethod;
    String productName;
    int originalPrice;
}
