package com.example.orderManagement.service;

import com.example.orderManagement.repository.IOrderRepository;
import com.example.orderManagement.repository.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HtmlGeneratorService {

    private final SpringTemplateEngine templateEngine;

    @Autowired
    private IOrderRepository orderRepository;

    public HtmlGeneratorService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String renderTotalTransByCustomerId(int custId) {
        BigDecimal total = BigDecimal.ZERO;
        final List<Orders> orders = orderRepository.findByCustomerCustomerId(custId);
        if (orders != null && !orders.isEmpty()) {
            total = orders.stream().map(o -> o.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        Context context = new Context();
        context.setVariable("title", "Total Transactions By CustomerId");
        context.setVariable("message", "Total Transactions By CustomerId - " + custId + " :: $" + total);

        return templateEngine.process("TotalTransactions", context);
    }

    public String renderTotalTransByProductCode(String productCode) {
        BigDecimal total = BigDecimal.ZERO;
        final List<Orders> orders = orderRepository.findByProductProductCode(productCode);
        if (orders != null && !orders.isEmpty()) {
            total = orders.stream().map(o -> o.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        Context context = new Context();
        context.setVariable("title", "Total Transactions By Product Code");
        context.setVariable("message", "Total Transactions By Product Code - " + productCode + " :: $" + total);

        return templateEngine.process("TotalTransactions", context);
    }

    public String renderNumberOfTransByCustomerLocation(String location) {
        final int totalQty = orderRepository.findTotalQuantityByCustomerLocation(location);

        Context context = new Context();
        context.setVariable("title", "Number of Transactions By Customer Location");
        context.setVariable("message", "Number of Transactions By Customer Location - " + location + " :: " + totalQty);

        return templateEngine.process("TotalTransactions", context);
    }
}