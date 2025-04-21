package com.example.ecommerce.api.order.facade;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.api.order.dto.OrderRequest;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final ProductService productService;
    private final OrderService orderService;

    public void placeOrder(OrderRequest request){
        //facade -> service Dto변경
        List<OrderCommand> requestItems = OrderRequest.toCommand(request);

        //상품조회, 재고차감, 재고저장
        orderService.prepareOrderItems(requestItems);

        //재고 차감
        List<Product> orderProducts = productService.deductStocks(requestItems);

        //상품 item 생성
        List<OrderItem> orderItems = orderService.createOrderItemList(orderProducts, requestItems);

    }
}
