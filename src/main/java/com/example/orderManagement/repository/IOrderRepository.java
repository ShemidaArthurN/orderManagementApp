package com.example.orderManagement.repository;

import com.example.orderManagement.repository.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByCustomerCustomerId(Integer customerId);

    List<Orders> findByProductProductCode(String productCode);

    @Query("SELECT SUM(o.quantity) FROM Orders o WHERE o.customer.location = :location GROUP BY o.customer.location")
    Integer findTotalQuantityByCustomerLocation(@Param("location") String location);
}

