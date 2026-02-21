package com.pay.polycube.controller;

import com.pay.polycube.ApiResponse;
import com.pay.polycube.domain.Grade;
import com.pay.polycube.domain.Member;
import com.pay.polycube.dto.OrderRequest;
import com.pay.polycube.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;

    private ResponseEntity<ApiResponse<Void>> order(Member member, OrderRequest orderRequest) {

        orderCommandService.process(
                member, orderRequest.getPaymentMethod(), orderRequest.getProductName(), orderRequest.getOriginalPrice());

        return ResponseEntity.ok().build();
    }
}
