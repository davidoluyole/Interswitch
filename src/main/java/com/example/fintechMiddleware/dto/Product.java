package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Product {
    private String productId;
    private String name; // e.g., Prepaid Electricity, 1GB Data
    private String billerId;
    private BigDecimal amount;
}
