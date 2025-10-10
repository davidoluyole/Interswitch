package com.example.fintechMiddleware.config;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account.";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been created successfully!";
    public static final String USER_CREATION_SUCCESS = "003";
    public static final String USER_CREATION_MESSAGE = "User registered successfully!";
    public static final String USER_NOT_FOUND_CODE = "004";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found or not verified.";

    public static String generateAccountNumber() {
        return String.valueOf((long) (Math.random() * 10000000000L));
    }
}

//package com.example.fintechMiddleware.config;
//
//import java.time.Year;
//
//public class AccountUtils {
//    public static final String ACCOUNT_EXISTS_CODE = "001";
//    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already exists";
//    public static final String ACCOUNT_CREATION_SUCCESS = "002";
//    public static final String ACCOUNT_CREATION_MESSAGE = "Account Created successfully";
//    public static String generateAccountNumber(){
//        //currentYear+randomSixDigits
//        Year currentYear = Year.now();
//        int min = 100_000;
//        int max = 999_999;
//        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
//        //convert currentYear and randNumber to string and concatenate
//        String year = String.valueOf(currentYear);
//        String randomNumber = String.valueOf(randNumber);
//        StringBuilder accountNumber = new StringBuilder();
//        return accountNumber.append(year).append(randomNumber).toString();
//    }
//
//
//}
