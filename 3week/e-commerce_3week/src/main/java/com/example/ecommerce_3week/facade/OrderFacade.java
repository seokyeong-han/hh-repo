package com.example.ecommerce_3week.facade;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderItem;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;

import com.example.ecommerce_3week.service.order.OrderService;
import com.example.ecommerce_3week.service.orderhistory.OrderHistoryService;
import com.example.ecommerce_3week.service.product.ProductService;
import com.example.ecommerce_3week.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;

    public void placeOrder(OrderRequest request){
        //유저 조회
        User user = userService.findUserById(request.getUserId());

        //controller Dto -> facade Dto
        List<Product> products = new ArrayList<>();

        //상품조회 및 재고차감
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    Product product = productService.getProductById(item.getProductId());
                    product.deductStock(item.getQuantity()); //재고 차감
                    products.add(product);
                    return new OrderItem(product.getId(), item.getQuantity(), product.getPrice());
                }).collect(Collectors.toList());

        //주문생성
        Order order = orderService.createOrder(user, orderItems);
        //주문저장
        orderService.save(order);
        //유저저장
        userService.save(user);
        //재고감소 -> 변경된 상품 저장
        productService.save(products);
        //주문 히스토리저장
        orderHistoryService.save(user, orderItems);

    }
}
