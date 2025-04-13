package com.example.orderManagement.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm") // Optional for individual control
    private LocalDateTime transactionTime;

    private int customerId;

    private int quantity;

    private String productCode;

}
