package com.example.orderManagement.repository;

import com.example.orderManagement.OrderManagementBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CustomerRepositoryTest extends OrderManagementBaseTest {

    @Test
    public void testFindByCustomerId() {
        setUp();
        assertEquals(mockCustomer, mockCustomerRepository.findByCustomerId(VALID_CUSTOMER_ID));

    }
}
