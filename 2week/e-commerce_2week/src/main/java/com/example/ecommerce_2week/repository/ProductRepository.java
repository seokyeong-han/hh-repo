package com.example.ecommerce_2week.repository;

import com.example.ecommerce_2week.entity.Balance;
import com.example.ecommerce_2week.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
