package com.example.fintechMiddleware.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankResponse {
    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;
}

//package com.example.fintechMiddleware.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class BankResponse {
//    public static BankResponse builder;
//    private String responseCode;
//    private String responseMessage;
//    private AccountInfo accountInfo;
//}
