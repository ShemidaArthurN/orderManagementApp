package com.example.orderManagement;

import com.example.orderManagement.repository.ICustomerRepository;
import com.example.orderManagement.repository.IOrderRepository;
import com.example.orderManagement.repository.IProductRepository;
import com.example.orderManagement.repository.entity.Customer;
import com.example.orderManagement.repository.entity.Orders;
import com.example.orderManagement.repository.entity.Product;
import lombok.Getter;
import lombok.Setter;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderManagementBaseTest {
    protected static final String ACTIVE_PRODUCT_CODE = "PRD-1";
    protected static final String INACTIVE_PRODUCT_CODE = "PRD-2";
    protected static final int VALID_CUSTOMER_ID = 777;
    protected Product mockActiveProduct;

    protected Product mockInActiveProduct;

    protected Customer mockCustomer;

    protected Orders order;

    protected static StringBuilder errors;

    @MockBean
    protected IProductRepository mockProductRepository;

    @MockBean
    protected ICustomerRepository mockCustomerRepository;

    @MockBean
    protected IOrderRepository mockOrderRepository;

    @Autowired
    protected IOrderRepository iOrderRepository;

    @Autowired
    protected IProductRepository iProductRepository;

    @Autowired
    protected ICustomerRepository iCustomerRepository;

    protected void setUp() {
        mockActiveProduct = new Product(ACTIVE_PRODUCT_CODE, BigDecimal.valueOf(100.0), "ACTIVE");
        Mockito.when(mockProductRepository.findByProductCode(ACTIVE_PRODUCT_CODE)).thenReturn(mockActiveProduct);

        mockInActiveProduct = new Product(INACTIVE_PRODUCT_CODE, BigDecimal.valueOf(100.0), "INACTIVE");
        Mockito.when(mockProductRepository.findByProductCode(INACTIVE_PRODUCT_CODE)).thenReturn(mockInActiveProduct);

        mockCustomer = new Customer(VALID_CUSTOMER_ID, "Steve", "Bob", "steve.bob@gmail.com", "Australia");
        Mockito.when(mockCustomerRepository.findByCustomerId(VALID_CUSTOMER_ID)).thenReturn(mockCustomer);

        order = new Orders();
        errors = new StringBuilder();
    }

    protected void constructValidOrder() {
        // Check for Active products and set the Product, valid Customer
        order.setCustId(VALID_CUSTOMER_ID);
        order.setPrdCode(ACTIVE_PRODUCT_CODE);
        order.setQuantity(10);
        order.setTransactionTime(LocalDateTime.now());
    }

}
