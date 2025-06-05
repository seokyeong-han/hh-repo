package com.example.ecommerce.common.config;

import com.example.ecommerce.api.product.facade.ProductFacade;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer  implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductFacade  productFacade;

    @Override
    public void run(String... args) {
        System.out.println("앱 시작 후 자동 실행됨!");

        //test user setting
        if (userRepository.count() == 0) {
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= 500; i++) {
                users.add(new User(
                        null, "test-user-" + i, 10000L + i, null, LocalDateTime.now(), LocalDateTime.now()
                ));
            }
            userRepository.saveAll(users);
            System.out.println("✅ 테스트 유저 500명 저장 완료");
        }

        //test product setting
        if(productRepository.count() == 0){
            List<Product> products = new ArrayList<>();
            for (int i = 1; i<= 10; i++) {
                products.add(new Product(
                        null, "test-product-"+i, 1L+i, i*50
                ));
            }
            productRepository.saveAll(products);
            System.out.println("✅ 테스트 상품 저장 완료");

        }

        //test 상품 조회수 증가
        if(productRepository.count() > 0){
            for (int i = 1; i<= 100; i++) {
                productFacade.getProductById(1L);
            }
            for (int i = 1; i<= 10; i++) {
                productFacade.getProductById(5L);
            }
            System.out.println("✅ 테스트 상품 조회 완료");
        }




    }
}
