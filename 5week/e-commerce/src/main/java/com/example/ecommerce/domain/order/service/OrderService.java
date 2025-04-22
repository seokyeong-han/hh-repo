package com.example.ecommerce.domain.order.service;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductService productService;

    public void prepareOrderItems(List<OrderCommand> itemRequests) {//이거 지우기
        Map<Long, Integer> quantityMap = itemRequests.stream()
                .collect(Collectors.toMap(OrderCommand::getProductId, OrderCommand::getQuantity));

        Map<Long, Long> productCouponMap = itemRequests.stream()
                .filter(req -> req.getCouponId() != null)
                .collect(Collectors.toMap(OrderCommand::getProductId, OrderCommand::getCouponId));

        List<Product> products = new ArrayList<>();

        for (OrderCommand request : itemRequests) {
            Product product = productService.findById(request.getProductId());
            product.deductStock(request.getQuantity());
            products.add(product);
            productService.save(product);
        }

        productService.saveAll(products); //재고차감 저장
    }

    public List<OrderItem> createOrderItemList (List<Product> products,
                                                List<OrderCommand> itemRequests) {
        // quantity, couponId 정리
        Map<Long, OrderCommand> commandMap = itemRequests.stream()
                .collect(Collectors.toMap(OrderCommand::getProductId, c -> c));

       //일단쿠폰 빼고
        return null;
    }


}
