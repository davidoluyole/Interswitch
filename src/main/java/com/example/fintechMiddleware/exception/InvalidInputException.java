package com.example.fintechMiddleware.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}

//package com.example.fintechMiddleware.exception;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import java.util.HashMap;
//import java.util.Map;
//
//public class InvalidInputException extends RuntimeException{
//    public InvalidInputException(String message, Throwable invalidPasswordStrength){
//        super(message);
//    }
//}
