package com.example.orderManagement.service;

import com.example.orderManagement.OrderManagementBaseTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderTransactionServiceTest extends OrderManagementBaseTest {

    @Autowired
    private OrderTransactionService orderTransactionService;

    @Test
    void validateOrderRelations() throws Exception {
        setUp();

        // Check for invalid Customer and Products set to Orders
        order.setCustId(12);
        order.setPrdCode("13");
        order.setTransactionTime(LocalDateTime.now());
        orderTransactionService.validateOrderRelations(order, errors);
        checkErrorMsg(new String[]{"No Customer", "No Product"});
        assertNull(order.getCustomer());
        assertNull(order.getProduct());

        // Check for inactive products
        order.setPrdCode(INACTIVE_PRODUCT_CODE);
        orderTransactionService.validateOrderRelations(order, errors);
        checkErrorMsg("Order is NOT ACTIVE");
        assertNull(order.getCustomer());
        assertNull(order.getProduct());

        // Check for Active products and set the Product, valid Customer
        constructValidOrder();
        orderTransactionService.validateOrderRelations(order, errors);
        assertTrue(StringUtils.isBlank(errors.toString()));
        assertNotNull(order.getCustomer());
        assertNotNull(order.getProduct());
        assertEquals(order.getCustomer(), mockCustomer);
        assertEquals(order.getProduct(), mockActiveProduct);
        final BigDecimal totalExp = new BigDecimal(1000);
        assertTrue(totalExp.compareTo(order.getTotal()) == 0);

        //Check if total exceeds $5000
        order.setQuantity(55);
        orderTransactionService.validateOrderRelations(order, errors);
        checkErrorMsg("exceeds $5000");
    }

    @Test
    void isValidInput() {
        setUp();

        order.setTransactionTime(LocalDateTime.now().minusDays(2));
        order.setQuantity(0);

        //Test invalid TransactionTime and Quantity
        orderTransactionService.isValidInput(order, errors);
        checkErrorMsg(new String[]{"Invalid Transaction Date", "Invalid Quantity"});
    }

    private void checkErrorMsg(final String... errorMsg) {
        assertNotNull(errors);
        for (String error : errorMsg) {
            assertTrue(errors.toString().contains(error));
        }
        errors = new StringBuilder();
    }
}