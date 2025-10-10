package com.example.fintechMiddleware.repository;

import com.example.fintechMiddleware.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUserId(String userId);
    Optional<Account> findByAccountNumberAndEmail(String accountNumber, String email);
}
