package com.example.fintechMiddleware.service;

import com.example.fintechMiddleware.dto.BankResponse;
import com.example.fintechMiddleware.dto.UserDTO;
import com.example.fintechMiddleware.model.User;


public interface AccountService {
    BankResponse createBankAccount(UserDTO userDTO, User user);
}

//package com.example.fintechMiddleware.service;
//
//public interface AccountService {
//    void createBankAccount();
//}
