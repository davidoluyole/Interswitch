package com.example.fintechMiddleware.service;

import com.example.fintechMiddleware.config.AccountUtils;
import com.example.fintechMiddleware.dto.AccountInfo;
import com.example.fintechMiddleware.dto.BankResponse;
import com.example.fintechMiddleware.dto.UserDTO;
import com.example.fintechMiddleware.exception.InvalidInputException;
import com.example.fintechMiddleware.model.User;
import com.example.fintechMiddleware.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper = new ModelMapper();

    public boolean existByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isStrongPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?]).{8,}$";
        return Pattern.compile(passwordRegex).matcher(password).matches();
    }

    private boolean isValidBVN(String bvn) {
        // Robust BVN validation: Must be exactly 11 digits, allowing leading zeros
        if (bvn == null || bvn.trim().isEmpty()) {
            log.error("BVN validation failed: BVN is null or empty");
            return false;
        }
        // Remove any whitespace and check for exactly 11 digits
        String cleanedBvn = bvn.trim();
        log.info("Validating BVN: {}", cleanedBvn);
        if (!Pattern.matches("^\\d{11}$", cleanedBvn)) {
            log.error("BVN validation failed: BVN '{}' does not match exactly 11 digits", cleanedBvn);
            return false;
        }
        return true;
    }

    private boolean isValidNIN(String nin) {
    // Robust BVN validation: Must be exactly 11 digits, allowing leading zeros
        if (nin == null || nin.trim().isEmpty()) {
            log.error("BVN validation failed: BVN is null or empty");
            return false;
        }
        // Remove any whitespace and check for exactly 11 digits
        String cleanedBvn = nin.trim();
            log.info("Validating BVN: {}", cleanedBvn);
            if (!Pattern.matches("^\\d{11}$", cleanedBvn)) {
            log.error("BVN validation failed: BVN '{}' does not match exactly 11 digits", cleanedBvn);
            return false;
        }
            return true;
    }
//    private boolean isValidNIN(String nin) {
//        if (nin == null || !nin.matches("^\\d{11}$")) {
//            log.error("NIN validation failed: NIN '{}' does not match exactly 11 digits", nin);
//            return false;
//        }
//
//        int[][] d = {
//                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
//                {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
//                {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
//                {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
//                {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
//                {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
//                {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
//                {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
//                {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
//                {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
//        };
//
//        int[][] p = {
//                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
//                {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
//                {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
//                {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
//                {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
//                {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
//                {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
//                {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
//        };
//
//        int c = 0;
//        for (int k = 1; k <= 11; k++) {
//            int idx = 11 - k;
//            int digit = nin.charAt(idx) - '0';
//            int pos = k % 8;
//            int perm = p[pos][digit];
//            c = d[c][perm];
//            log.info("NIN step k={}, digit={}, pos={}, perm={}, c={}", k, digit, pos, perm, c);
//        }
//        if (c != 0) {
//            log.error("NIN validation failed: Checksum for NIN '{}' is {}, expected 0", nin, c);
//            return false;
//        }
//        return true;
//    }

    private boolean isValidPin(int pin) {
        return String.valueOf(pin).matches("^\\d{4}$");
    }

    @Override
    public BankResponse registerUser(UserDTO userDTO) {
        if (!isStrongPassword(userDTO.getPassword())) {
            throw new InvalidInputException(
                    "Password is too weak. Must contain at least 8 characters, one digit, one lowercase, and one special character.",
                    new Throwable("Invalid password strength")
            );
        }

        if (!isValidBVN(userDTO.getBvn())) {
            throw new InvalidInputException("Invalid BVN. Must be exactly 11 digits.");
        }

        if (!isValidNIN(userDTO.getNin())) {
            throw new InvalidInputException("Invalid NIN. Must be 11 digits with valid checksum.");
        }

        if (!isValidPin(userDTO.getPin())) {
            throw new InvalidInputException("Invalid PIN. Must be exactly 4 digits.");
        }

        if (existByEmail(userDTO.getEmail())) {
            User existingUser = userRepository.findByEmail(userDTO.getEmail()).orElse(null);
            if (existingUser != null && existingUser.getIsVerified()) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage("User exists but is unverified. Please check your email or log in.")
                    .accountInfo(null)
                    .build();
        }

        User user = new User();
        modelMapper.map(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setStatus("ACTIVE");
        user.setIsVerified(true);

        try {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            User savedUser = userRepository.save(user);
            log.info("Successfully saved user --> id={}, email={}", savedUser.getId(), savedUser.getEmail());

            BankResponse accountResponse = accountService.createBankAccount(userDTO, savedUser);
            if (!accountResponse.getResponseCode().equals(AccountUtils.ACCOUNT_CREATION_SUCCESS)) {
                userRepository.delete(savedUser);
                log.error("Account creation failed for user: {}", userDTO.getEmail());
                throw new RuntimeException("Failed to create account for user");
            }

            return BankResponse.builder()
                    .responseCode(AccountUtils.USER_CREATION_SUCCESS)
                    .responseMessage(AccountUtils.USER_CREATION_MESSAGE)
                    .accountInfo(accountResponse.getAccountInfo())
                    .build();
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }
}

//package com.example.fintechMiddleware.service;
//
//import com.example.fintechMiddleware.config.AccountUtils;
////import com.example.fintechMiddleware.config.PasswordUtils;
//import com.example.fintechMiddleware.dto.AccountInfo;
//import com.example.fintechMiddleware.dto.BankResponse;
//import com.example.fintechMiddleware.dto.UserDTO;
//import com.example.fintechMiddleware.exception.InvalidInputException;
//import com.example.fintechMiddleware.model.User;
//import com.example.fintechMiddleware.repository.UserRepository.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.regex.Pattern;
//
//@Service
//@Slf4j
//public class UserServiceImpl implements UserService{
//    @Autowired
//    private UserRepository userRepository;
//
//    private final ModelMapper modelMapper = new ModelMapper();
//
//    public boolean existByEmail(String email) {
//        return userRepository.findByEmail(email).isPresent();
//    }
////    private boolean isValidEmail(String email) {
////        return !email.contains("@") || !email.contains(".");
////    }
//
//    private boolean isStrongPassword(String password) {
//        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?]).{8,}$";
//        return Pattern.compile(passwordRegex).matcher(password).matches();
//    }
//
//    @Override
//    public BankResponse registerUser(UserDTO userDTO) {
//        if (!isStrongPassword(userDTO.getPassword())) {
//            throw new InvalidInputException(
//                    "Password is too weak. Must contain at least 8 characters, one digit, one lowercase, and one uppercase letter.",
//                    new Throwable("Invalid password strength")
//            );
//        }
//        if (existByEmail(userDTO.getEmail())) {
//            User existingUser = (User) userRepository.findByEmail(userDTO.getEmail()).orElse(null);
//
//            if (existingUser != null && existingUser.getIsVerified() == true) {
//                return BankResponse.builder()
//                        .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
//                        .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
//                        .accountInfo(null)
//                        .build();
//            }
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
//                    .responseMessage("User exists but is unverified. Please check your email or log in.")
//                    .accountInfo(null)
//                    .build();
//        }
//        User user = new User();
//        modelMapper.map(userDTO, user);
//
//        user.setAccountNumber(AccountUtils.generateAccountNumber());
//        user.setAccountBalance(BigDecimal.ZERO);
//        user.setStatus("ACTIVE");
//        user.setIsVerified(false);
//        try {
//            User savedUser = userRepository.save(user);
//            log.info("Successfully saved user --> id={}, email={}", savedUser.getId(), savedUser.getEmail());
//
//            // Success Response
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
//                    .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
//                    .accountInfo(AccountInfo.builder()
//                            .accountBalance(savedUser.getAccountBalance())
//                            .accountNumber(savedUser.getAccountNumber())
//                            .accountName(savedUser.getFirstName() +" "+ savedUser.getLastName())
//                            .build()
//                    )
//                    .build();
//        } catch (Exception e) {
//            log.error("Registration failed: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to save user", e);
//        }
//    }
//}
