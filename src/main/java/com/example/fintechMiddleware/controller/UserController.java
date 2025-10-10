package com.example.fintechMiddleware.controller;

import com.example.fintechMiddleware.dto.BankResponse;
import com.example.fintechMiddleware.dto.UserDTO;
import com.example.fintechMiddleware.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BankResponse registerUser(@RequestBody UserDTO userDTO) {
        return userService.registerUser(userDTO);
    }
}
//package com.example.fintechMiddleware.controller;
//
//import com.example.fintechMiddleware.dto.BankResponse;
//import com.example.fintechMiddleware.dto.UserDTO;
//import com.example.fintechMiddleware.service.UserService;
//import com.mongodb.internal.bulk.UpdateRequest;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/user")
//public class UserController {
//    @Autowired
//    UserService userService;
//
//    @PostMapping("/")
//    public BankResponse createAccount(@Valid @RequestBody UserDTO userDTO){
//        return userService.registerUser(userDTO);
//    }
//}