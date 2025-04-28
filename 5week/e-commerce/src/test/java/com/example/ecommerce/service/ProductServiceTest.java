package com.example.ecommerce.service;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceTest.class);

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품이 존재하지 않으면 예외 발생")
    void prepareOrderItems_productNotFound_throwsException() {
        // given
        List<OrderCommand> commands = List.of(new OrderCommand(99L, 1)); //id가 99인 상품 구매

        when(productRepository.findById(99L)).thenReturn(Optional.empty());//빈 optional값이 왔음

        // when & then
        assertThatThrownBy(() -> productService.prepareOrderItems(commands))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("재고가 부족하면 예외 발생")
    void prepareOrderItems_insufficientStock_throwsException() {
        // given
        Product product = new Product(1L, 1000L, 1); // 재고 1개

        List<OrderCommand> commands = List.of(new OrderCommand(1L, 2)); // 2개 주문

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.prepareOrderItems(commands))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 재고가 부족합니다.");

        log.info("product => id : {}, 재고 : {}", product.getId(), product.getStock());
    }

    @Test
    @DisplayName("prepareOrderItems: 재고 차감 및 주문 아이템 생성 성공")
    void prepareOrderItems_success() {
        // given
        Product product1 = new Product(1L, 1000L, 10); // 재고 10개짜리
        Product product2 = new Product(2L, 2000L, 5);  // 재고 5개짜리

        List<OrderCommand> commands = List.of(
                new OrderCommand(1L, 2), // 2개 주문
                new OrderCommand(2L, 1)  // 1개 주문
        );

        // when
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        PreparedOrderItems result = productService.prepareOrderItems(commands);

        // then
        assertThat(result.getOrderItems()).hasSize(2);
        assertThat(result.getOrderItems().get(0).getProductId()).isEqualTo(1L);
        assertThat(result.getOrderItems().get(0).getTotalPrice()).isEqualTo(2000L); // 1000 * 2

        assertThat(result.getOrderItems().get(1).getProductId()).isEqualTo(2L);
        assertThat(result.getOrderItems().get(1).getTotalPrice()).isEqualTo(2000L); // 2000 * 1
        //prepareOrderItems안에 재고 차감이 있어서 재고 차감됨
        assertThat(product1.getStock()).isEqualTo(8); // 10 - 2
        assertThat(product2.getStock()).isEqualTo(4); // 5 - 1

        verify(productRepository).save(product1);
        verify(productRepository).save(product2);

        log.info("product1 => id : {}, 재고 : {}", product1.getId(), product1.getStock());
        log.info("product2 => id : {}, 재고 : {}", product2.getId(), product2.getStock());
    }



}
