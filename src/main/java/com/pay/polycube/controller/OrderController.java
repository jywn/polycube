package com.pay.polycube.controller;

import com.pay.polycube.ApiResponse;
import com.pay.polycube.domain.Member;
import com.pay.polycube.domain.Order;
import com.pay.polycube.dto.OrderRequest;
import com.pay.polycube.dto.OrderResponse;
import com.pay.polycube.service.OrderCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;

    @PostMapping("/order")
    public ResponseEntity<ApiResponse<OrderResponse>> order(@Valid @NotNull Member member, @Valid @RequestBody OrderRequest req) {

        Order order = orderCommandService.process(member, req.getPaymentMethod(), req.getProductName(), req.getOriginalPrice());
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(ApiResponse.success("주문을 성공하였습니다.", response));
    }
}
