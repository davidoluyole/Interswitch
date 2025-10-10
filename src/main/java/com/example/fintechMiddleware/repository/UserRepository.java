package com.example.fintechMiddleware.repository;

import com.example.fintechMiddleware.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}

//package com.example.fintechMiddleware.repository.UserRepository;
//
//import com.example.fintechMiddleware.model.User;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends MongoRepository<User, String> {
//
//    Optional<Object> findByEmail(String email);
//
//}
