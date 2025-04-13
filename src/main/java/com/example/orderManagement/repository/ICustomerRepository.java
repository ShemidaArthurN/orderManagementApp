package com.example.orderManagement.repository;

import com.example.orderManagement.repository.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ICustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c from Customer c where c.customerId = :customerId")
    Customer findByCustomerId(Integer customerId);
}

