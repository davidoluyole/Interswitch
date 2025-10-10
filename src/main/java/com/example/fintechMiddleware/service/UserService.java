package com.example.fintechMiddleware.service;

import com.example.fintechMiddleware.dto.BankResponse;
import com.example.fintechMiddleware.dto.UserDTO;

public interface UserService {
    BankResponse registerUser(UserDTO userDTO);
}

//package com.example.fintechMiddleware.service;
//
//import com.example.fintechMiddleware.dto.BankResponse;
//import com.example.fintechMiddleware.dto.UserDTO;
//
//public interface UserService {
////    void registerUser(UserDTO userDTO);
//    BankResponse registerUser(UserDTO userDTO);
//
//}