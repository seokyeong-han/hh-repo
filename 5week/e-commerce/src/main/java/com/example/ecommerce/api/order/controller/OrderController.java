package com.example.ecommerce.api.order.controller;

import com.example.ecommerce.api.order.dto.OrderRequest;
import com.example.ecommerce.api.order.facade.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody OrderRequest request) {
        orderFacade.placeOrder(request);
        return ResponseEntity.ok().build();
    }

}
