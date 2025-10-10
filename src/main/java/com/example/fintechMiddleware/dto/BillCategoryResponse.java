package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class BillCategoryResponse {
    private List<BillCategory> categories;
}

