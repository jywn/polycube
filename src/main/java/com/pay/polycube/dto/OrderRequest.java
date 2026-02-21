package com.pay.polycube.dto;

import com.pay.polycube.service.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderRequest {
    PaymentMethod paymentMethod;
    String productName;
    int originalPrice;
}
