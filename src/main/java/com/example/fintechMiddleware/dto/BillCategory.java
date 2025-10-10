package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillCategory {
    private String categoryId;
    private String name; // e.g., Electricity, Internet
}
