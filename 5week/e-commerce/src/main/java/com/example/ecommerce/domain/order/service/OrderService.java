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

        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : products) {
            OrderCommand command = commandMap.get(product.getId());
            int quantity = command.getQuantity();
            Long couponId = command.getCouponId();

            Long pricePerItem = product.getPrice(); //상품 1개당 가격
            Long totalOriginalPrice = pricePerItem * quantity;   // 할인 전 총액
            Long discount = 0L;

            if (couponId != null) {
                discount = couponService.getDiscountAmount(couponId, pricePerItem); // 한 개당만 할인
            }
            // 실제 결제 총액 = 할인된 1개 가격 + 나머지는 원가
            Long finalTotalPrice = (pricePerItem * quantity) - discount;
            Long finalPricePerItem = finalTotalPrice / quantity;

            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    quantity,
                    pricePerItem,
                    totalOriginalPrice,
                    discount,
                    finalPricePerItem,
                    finalTotalPrice,
                    couponId
            );

            orderItems.add(orderItem);
            //여기에 save 추가
        }

        return null;
    }


}
