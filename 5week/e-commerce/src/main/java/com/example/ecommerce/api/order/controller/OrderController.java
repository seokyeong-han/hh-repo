package com.example.ecommerce.api.order.controller;

import com.example.ecommerce.api.order.dto.OrderRequest;
import com.example.ecommerce.api.order.facade.OrderFacade;
import com.example.ecommerce.common.recode.StockReserveRequest;
import com.example.ecommerce.common.recode.StockReservedItem;
import com.example.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderFacade orderFacade;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody OrderRequest request) {
        orderFacade.placeOrder(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/event")
    public ResponseEntity<Void> placeOrderEvent(@RequestBody OrderRequest request) {
        List<StockReserveRequest> reserveRequests = request.getItems().stream()
                .map(i -> new StockReserveRequest(i.getProductId(), i.getQuantity()))
                .toList();

        productService.reserveStockEvent(request.getUserId(), reserveRequests); //재고차감
        return ResponseEntity.ok().build();
    }

}
