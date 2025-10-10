package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Biller {
    private String billerId;
    private String name; // e.g., AEDC, MTN
    private String categoryId;
}
