package com.example.fintechMiddleware.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String bvn;
    private String nin;
    private int pin;
}

//package com.example.fintechMiddleware.dto;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class UserDTO {
//    @NotBlank(message = "FirstName is required")
//    @Size(min = 1, max = 20, message = "Firstname must be between 3 and 20 characters")
//    private String firstName;
//
//    @NotBlank(message = "LastName is required")
//    @Size(min = 1, max = 20, message = "Lastname must be between 3 and 20 characters")
//    private String lastName;
//
//    @NotBlank(message = "email is required")
//    @Email(message = "Email must be valid")
//    private String email;
//
//    @NotBlank(message = "Password is required")
//    @Size(min = 8, message = "Password must be 8 characters")
//    private String password;
//
//    @NotBlank(message = "BVN is required")
//    @Size(min = 11, max = 11, message = "BVN must be 11 digits")
//    private String BVN;
//
//    @NotBlank(message = "NIN is required")
//    @Size(min = 11, max = 11, message = "NIN must be 11 digits")
//    private String NIN;
//
//    private String dateOfBirth;
//    private String otherName;
//    private String gender;
//    private String address;
//    private String phoneNumber;
//    private String alternativePhoneNumber;
//    private String stateOfOrigin;
//}
