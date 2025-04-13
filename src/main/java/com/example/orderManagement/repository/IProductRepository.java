package com.example.orderManagement.repository;

import com.example.orderManagement.repository.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p from Product p where p.productCode = :productCode")
    Product findByProductCode(String productCode);

    @Query("SELECT CASE WHEN LOWER(p.status) = 'active' THEN TRUE ELSE FALSE END FROM Product p WHERE p.productCode = :productCode")
    Boolean isProductActive(String productCode);
}

