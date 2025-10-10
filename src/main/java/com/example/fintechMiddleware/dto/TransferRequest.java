package com.example.fintechMiddleware.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String fromAccountNumber;
    private String toBankCode;
    private String toAccountNumber;
    private BigDecimal amount;
}
