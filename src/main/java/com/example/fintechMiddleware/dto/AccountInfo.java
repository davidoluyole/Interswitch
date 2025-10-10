package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountInfo {
    private String accountName;
    private String accountNumber;
    private BigDecimal accountBalance;
}

//package com.example.fintechMiddleware.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AccountInfo {
//    private String accountName;
//    private String accountNumber;
//    private BigDecimal accountBalance;
//}
