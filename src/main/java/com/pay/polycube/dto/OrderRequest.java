package com.pay.polycube.dto;

import com.pay.polycube.domain.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@NoArgsConstructor
public class OrderRequest {
    @NotNull
    private PaymentMethod paymentMethod;

    @NotBlank
    private String productName;

    @NotNull
    @Positive
    private Integer originalPrice;
}
