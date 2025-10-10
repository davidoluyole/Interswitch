package com.example.fintechMiddleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String bvn;
    private String nin;
    private String status;
    private Boolean isVerified;

    public boolean isVerified() {
        return isVerified;
    }
}

//package com.example.fintechMiddleware.model;
//
//import lombok.*;
//import org.springframework.data.annotation.Id;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Date;
//
//@Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class User {
//    @Id
//    private String id;
//    private String firstName;
//    private String lastName;
//    private String otherName;
//    private String gender;
//    private String dateOfBirth;
//    private String address;
//    private String phoneNumber;
//    private String alternativePhoneNumber;
//    private String status;
//    private String email;
//    private String password;
//    private String stateOfOrigin;
//    private String accountNumber;
//    private BigDecimal accountBalance;
//    private String BVN;
//    private String NIN;
////    @CreationTimestamp
//    private LocalDateTime createdAt;
////    @UpdateTimestamp
//    private LocalDateTime modifiedAt;
//    private Boolean isVerified;
//}