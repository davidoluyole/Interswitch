package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BillerResponse {
    private List<Biller> billers;
}
