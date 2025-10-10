package com.example.fintechMiddleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    private User user;
    private String email;
    private int pin;
    private String accountNumber;
    private BigDecimal balance;
    private String status;
    private Boolean isVerified;

    public boolean isVerified() {
        return isVerified;
    }
}

//package com.example.fintechMiddleware.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigInteger;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class Account {
//    private String id;
//    private User user;
//    private String email;
//
//    private int pin;
//    private String accountNumber;
//    private BigInteger balance;
//}
