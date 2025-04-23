package com.example.ecommerce.api.order.facade;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.api.order.dto.OrderRequest;
import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.service.ProductService;
import com.example.ecommerce.domain.user.model.User;
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

        // 상품 조회, 재고 차감, 준비
        PreparedOrderItems prepared = productService.prepareOrderItems(requestItems);

        // 트랜잭션이 포함된 핵심 주문 처리 호출
        orderService.placeOrder(request.getUserId(), prepared);

    }
}
