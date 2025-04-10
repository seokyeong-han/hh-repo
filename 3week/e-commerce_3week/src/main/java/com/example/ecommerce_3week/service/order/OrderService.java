package com.example.ecommerce_3week.service.order;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.order.OrderItem;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;
import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import com.example.ecommerce_3week.dto.order.facade.PreparedOrderItems;
import com.example.ecommerce_3week.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final ProductService productService;
    private final OrderRepository orderRepository;


    public PreparedOrderItems prepareOrderItems(List<OrderFacadeRequest> itemRequests) {
        List<Product> products = new ArrayList<>();
        List<OrderItem> orderItems = itemRequests.stream()
                .map(request -> {
                    Product product = productService.getProductById(request.getProductId());
                    product.deductStock(request.getQuantity());
                    products.add(product);
                    return new OrderItem(product.getId(), request.getQuantity(), product.getPrice());
                })
                .collect(Collectors.toList());
        return new PreparedOrderItems(products, orderItems);
    }

    public Order createOrder(User user, List<OrderItem> items) {
        Order order = new Order(user.getId(), items);

        return order;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}
