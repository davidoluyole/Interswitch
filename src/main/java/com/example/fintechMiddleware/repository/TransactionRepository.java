package com.example.fintechMiddleware.repository;

import com.example.fintechMiddleware.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Optional<Transaction> findByTxnRef(String txnRef);
}