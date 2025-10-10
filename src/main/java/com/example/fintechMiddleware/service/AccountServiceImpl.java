package com.example.fintechMiddleware.service;

import com.example.fintechMiddleware.config.AccountUtils;
import com.example.fintechMiddleware.dto.AccountInfo;
import com.example.fintechMiddleware.dto.BankResponse;
import com.example.fintechMiddleware.dto.UserDTO;
import com.example.fintechMiddleware.model.Account;
import com.example.fintechMiddleware.model.User;
import com.example.fintechMiddleware.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public BankResponse createBankAccount(UserDTO userDTO, User user) {
        if (accountRepository.findByUserId(user.getId()).isPresent()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Account account = new Account();
        account.setUser(user);
        account.setEmail(userDTO.getEmail());
        account.setPin(userDTO.getPin());
        account.setAccountNumber(AccountUtils.generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setIsVerified(true);

        try {
            Account savedAccount = accountRepository.save(account);
            log.info("Successfully created account --> id={}, email={}", savedAccount.getId(), savedAccount.getEmail());

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(savedAccount.getBalance())
                            .accountNumber(savedAccount.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName())
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("Account creation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create account", e);
        }
    }
}