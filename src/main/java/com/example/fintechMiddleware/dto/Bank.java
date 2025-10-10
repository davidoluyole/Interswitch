package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bank {
    private String bankId;
    private String name; // e.g., GTBank, Zenith Bank
    private String bankCode; // e.g., 058, 057
}
