package com.example.ecommerce_2week.service;

import com.example.ecommerce_2week.DTO.PurchaseRequest;
import com.example.ecommerce_2week.DTO.PurchaseResponse;
import com.example.ecommerce_2week.entity.Product;
import com.example.ecommerce_2week.entity.Transaction;
import com.example.ecommerce_2week.entity.User;
import com.example.ecommerce_2week.repository.BalanceRepository;
import com.example.ecommerce_2week.repository.ProductRepository;
import com.example.ecommerce_2week.repository.TransactionRepository;
import com.example.ecommerce_2week.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    ProductService productService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    TransactionRepository transactionRepository;

    private static final String USER_NOT_FOUND_MSG = "사용자를 찾을 수 없습니다.";
    @Autowired
    private ProductRepository productRepository;

    public User getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MSG));

        return user;
    }

    public User chargeBalance(Long id, Long amount) {
        User user = getUser(id);
        user.chargeBalance(amount);
        balanceRepository.save(user.getBalance());
        return user;
    }

    @Transactional
    public PurchaseResponse purchase(PurchaseRequest request) {
        Long userId = request.getUserId();
        Long prosuctId = request.getProductId();
        int quantity = request.getQuantity();

        //유저 조회
        User user = getUser(userId);

        //상품 조회
        Product product = productService.getProduct(prosuctId);

        // 상품 가격 총액 계산
        Long totalPrice = product.getPrice() * quantity;

        //상품 재고 감소 (product 엔티티)
        product.updateStock(-quantity);
        productRepository.save(product);

        //잔액 차감 (user 엔티티)
        user.deductBalance(totalPrice);
        balanceRepository.save(user.getBalanceEntity());

        //거래 내역 저장
        Transaction transaction = new Transaction(user, product, quantity, totalPrice);
        transactionRepository.save(transaction);

        //거래 내역 저장
        return new PurchaseResponse(transaction);
    }
}
