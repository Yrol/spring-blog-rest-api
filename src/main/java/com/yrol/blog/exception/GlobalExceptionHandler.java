package com.yrol.blog.exception;

import com.yrol.blog.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Global class for exception handling
 * @ControllerAdvice - specialization of the @Component annotation which allows to handle exceptions.
 * Output consist only the fields (format): timestamp, message and details.
 * */
@ControllerAdvice
public class GlobalExceptionHandler {

    // handle specific exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {

        // getDescription will send the request URL
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // handle specific exception
    @ExceptionHandler(BlogAPIException.class)
    public ResponseEntity<ErrorDetails> handleBlogAPIException(BlogAPIException exception, WebRequest webRequest) {

        // getDescription will send the request URL
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handling "spring-boot-starter-validation" bean validation messages (customised response).
     * Mapping in field validation level
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        // Using lambda expression to get fields and the corresponding errors.
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handling all the other exceptions (except the above 2) thrown by the "Exception" class. Ex: 400 bad request & etc.
     * Test with: http://localhost:8080/api/posts/blah
     * */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception, WebRequest webRequest) {

        // getDescription will send the request URL
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
