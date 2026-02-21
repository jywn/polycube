package com.pay.polycube.controller;

import com.pay.polycube.ApiResponse;
import com.pay.polycube.domain.Member;
import com.pay.polycube.domain.Order;
import com.pay.polycube.dto.OrderRequest;
import com.pay.polycube.dto.OrderResponse;
import com.pay.polycube.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;

    private ResponseEntity<ApiResponse<OrderResponse>> order(Member member, OrderRequest req) {

        Order order = orderCommandService.process(member, req.getPaymentMethod(), req.getProductName(), req.getOriginalPrice());
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(ApiResponse.success("주문을 성공하였습니다.", response));
    }
}
