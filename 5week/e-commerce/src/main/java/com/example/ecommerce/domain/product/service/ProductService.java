package com.example.ecommerce.domain.product.service;

import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.global.cache.CacheConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;

    public static final String PRODUCT_DETAIL = CacheConstants.PRODUCT_DETAIL;

    //전체 상품 조회
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //단일 상품 조회
    @Cacheable(value = PRODUCT_DETAIL, key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    //사용하지 않으나 사용시 레디스 캐시 삭제 해줘야한다.
    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    public void save(Product product) {
        Product saved = productRepository.save(product);
        //@CacheEvict사용시 최초 상품 등록시 캐시가 없어 오류가 나 저장된 목록을 캐시 삭제 하도록 수정
        cacheManager
                .getCache(PRODUCT_DETAIL)
                .evict(saved.getId());
    }

    //재고 롤백
    @Transactional
    public void rollbackStock(List<OrderItem> orderItems) { // 트랜잭션 내에서 롤백 처리
        for (OrderItem item : orderItems) {
            Product product = findById(item.getProductId());
            product.restoreStock(item.getQuantity());
            Product saved = productRepository.save(product);

            cacheManager
                    .getCache(PRODUCT_DETAIL)
                    .evict(saved.getId());
        }
    }


}
