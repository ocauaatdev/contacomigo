package com.ocauaatdev.contacomigo.infra;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class RestErrorMessage {

    //Padronizando retornos de exceções para JSON com status do erro e a message
    private HttpStatus status;
    private String message;

    public RestErrorMessage(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
