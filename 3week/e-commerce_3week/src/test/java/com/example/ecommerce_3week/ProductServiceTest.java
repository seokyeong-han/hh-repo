package com.example.ecommerce_3week;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import com.example.ecommerce_3week.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    @DisplayName("상품 조회 성공")
    void getProductById_success() {
        // given
        Long productId = 1L;
        Product product = new Product(productId, 5000L, 20);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        Product result = productService.getProductById(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getPrice()).isEqualTo(5000L);
        assertThat(result.getStock()).isEqualTo(20);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 조회 실패 - 존재하지 않는 ID")
    void getProductById_notFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("상품을 찾을 수 없습니다.");

        verify(productRepository, times(1)).findById(productId);
    }

}
