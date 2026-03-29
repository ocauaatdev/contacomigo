package com.ocauaatdev.contacomigo.infra;

import com.ocauaatdev.contacomigo.exception.BusinessException;
import com.ocauaatdev.contacomigo.exception.DataAlreadyExistsException;
import com.ocauaatdev.contacomigo.exception.PasswordFormatException;
import com.ocauaatdev.contacomigo.exception.TokenGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PasswordFormatException.class)
    private ResponseEntity<RestErrorMessage> passwordFormatHandler(PasswordFormatException exception){
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<RestErrorMessage> businessErrorHandler(BusinessException exception){
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

    @ExceptionHandler(DataAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> dataAlreadyExistsHandler(DataAlreadyExistsException exception){
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

    @ExceptionHandler(TokenGenerationException.class)
    private ResponseEntity<RestErrorMessage> tokenGenerationErrorHandler(TokenGenerationException exception){
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(threatResponse);
    }
}
