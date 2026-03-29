package com.ocauaatdev.contacomigo.exception;

public class DataAlreadyExistsException extends RuntimeException{
    public DataAlreadyExistsException (String message){
        super(message);
    }
}
