package com.example.orderManagement.service;

import com.example.orderManagement.repository.ICustomerRepository;
import com.example.orderManagement.repository.IOrderRepository;
import com.example.orderManagement.repository.IProductRepository;
import com.example.orderManagement.repository.entity.Customer;
import com.example.orderManagement.repository.entity.Orders;
import com.example.orderManagement.repository.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderTransactionService {

    private final IOrderRepository orderRepository;

    @Autowired
    ICustomerRepository customerRepository;

    @Autowired
    IProductRepository productRepository;

    public OrderTransactionService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Orders save(Orders order) throws Exception {
        final StringBuilder errors = new StringBuilder();
        isValidInput(order, errors);
        validateOrderRelations(order, errors);
        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
        return orderRepository.save(order);
    }

    public void validateOrderRelations(final Orders order, final StringBuilder errors) throws Exception {
        final Customer customer = customerRepository.findByCustomerId(order.getCustId());
        if (customer == null) {
            errors.append(String.format("No Customer - '%s' found in the system , unable to process the " +
                            "Transaction for Product Code - '%s' and Transaction Time - '%s' ,\n", order.getCustId(),
                    order.getPrdCode(), order.getTransactionTime().toString()));
        } else {
            order.setCustomer(customer);
        }

        final Product product = productRepository.findByProductCode(order.getPrdCode());
        if (product == null) {
            errors.append(String.format("No Product - '%s' found in the system , unable to process the " +
                            "Transaction for Customer - '%s' and Transaction Time - '%s' , \n", order.getPrdCode(),
                    order.getCustId(), order.getTransactionTime().toString()));
        } else if (!product.getStatus().equalsIgnoreCase("Active")) {
            errors.append(String.format("Product - '%s' received for this Order is NOT ACTIVE, , unable to process the " +
                            "Transaction for Customer - '%s' and Transaction Time - '%s', \n", order.getPrdCode(),
                    order.getCustId(), order.getTransactionTime().toString()));
        } else {
            order.setProduct(product);
            BigDecimal total = BigDecimal.ZERO;
            if (product.getCost() != null) {
                final BigDecimal intAsBigDecimal = BigDecimal.valueOf(order.getQuantity());
                total = product.getCost().multiply(intAsBigDecimal);

                if (total.compareTo(BigDecimal.valueOf(5000)) > 0) {
                    errors.append("Total Transaction cost : " + total + " exceeds $5000, rejecting the Order, \n");
                }
            }
            order.setTotal(total);
        }
    }

    public void isValidInput(final Orders order, final StringBuilder errors) {
        if (order.getTransactionTime().toLocalDate().isBefore(LocalDate.now())) {
            errors.append("Invalid Transaction Date : " + order.getTransactionTime() + ", received an older date, \n");
        }

        if (order.getQuantity() <= 0) {
            errors.append("Invalid Quantity : " + order.getQuantity() + ", received, expecting a positive value, \n");
        }
    }

    public static LocalDateTime convertStringToDateTime(final String transactionTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(transactionTime, formatter);
    }
}
