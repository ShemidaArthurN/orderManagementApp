package com.example.orderManagement.processor;

import com.example.orderManagement.repository.entity.Orders;
import com.example.orderManagement.request.OrderTransaction;
import com.example.orderManagement.service.OrderTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

import static com.example.orderManagement.service.OrderTransactionService.convertStringToDateTime;


@Component
@Slf4j
public class OrderInputProcessor<O, O1> implements ItemProcessor<OrderTransaction, Orders> {

    @Autowired
    private OrderTransactionService orderService;

    @Override
    public Orders process(OrderTransaction item) throws Exception {
        if (item == null) {
            log.error("Input is null or empty");
        }

        if (!StringUtils.hasLength(item.getTransactionTime())) {
            log.error("Empty Transaction Date received");
        }

        if (!StringUtils.hasLength(item.getTransactionTime())) {
            log.error("Empty Transaction Date received");
        }

        final LocalDateTime orderTransTime = convertStringToDateTime(item.getTransactionTime());

        final Orders orders = new Orders();
        orders.setQuantity(item.getQuantity());
        orders.setTransactionTime(orderTransTime);
        orders.setPrdCode(item.getProductCode());
        orders.setCustId(item.getCustomerId());
        final StringBuilder errors = new StringBuilder();

        orderService.isValidInput(orders, errors);
        orderService.validateOrderRelations(orders, errors);

        if (errors.length() > 0) {
            log.error("Skipping the processing of the Order Transaction - " + orders + " due to the below errors : - " + errors.toString());
            return null;
        }

        return orders;
    }
}
