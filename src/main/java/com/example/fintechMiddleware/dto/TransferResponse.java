package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferResponse {
    private String transactionId;
    private String status;
    private String message;
    private BigDecimal newBalance;
}
