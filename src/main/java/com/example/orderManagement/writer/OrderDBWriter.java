package com.example.orderManagement.writer;

import com.example.orderManagement.repository.IOrderRepository;
import com.example.orderManagement.repository.entity.Orders;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDBWriter implements ItemWriter<Orders> {

    @Autowired
    private IOrderRepository orderRepository;

    @Override
    public void write(List<? extends Orders> items) throws Exception {
        // Save all items to the database
        orderRepository.saveAll(items);
    }
}
