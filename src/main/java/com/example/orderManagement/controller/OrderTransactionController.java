package com.example.orderManagement.controller;

import com.example.orderManagement.repository.entity.Orders;
import com.example.orderManagement.request.OrderRequest;
import com.example.orderManagement.service.OrderTransactionService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(BaseController.BASE_PATH + "/order")
@Getter
@Setter
public class OrderTransactionController {

    @Autowired
    private OrderTransactionService orderService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Orders> createOrders(@RequestBody OrderRequest orderRequest) throws Exception {
        Orders orders = null;
        try {
            orders = getOrdersFromRequest(orderRequest);
            orderService.save(orders);
        } catch (Exception e) {
            return (ResponseEntity) ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.created(new URI("/" + orders.getId())).body(orders);
    }

    private Orders getOrdersFromRequest(final OrderRequest orderRequest) throws Exception {
        final Orders orders = new Orders();
        orders.setQuantity(orderRequest.getQuantity());
        orders.setTransactionTime(orderRequest.getTransactionTime());
        orders.setCustId(orderRequest.getCustomerId());
        orders.setPrdCode(orderRequest.getProductCode());
        return orders;
    }

}
