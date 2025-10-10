package com.example.fintechMiddleware.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillPaymentRequest {
    private String productId;
    private String accountNumber;
    private BigDecimal amount;
}
