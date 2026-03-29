package com.ocauaatdev.contacomigo.exception;

public class PasswordFormatException extends RuntimeException{
    public PasswordFormatException(){
        super("The password must be longer than 6 characters, including private characters, numbers, and special characters");
    }

    public PasswordFormatException(String message){
        super(message);
    }
}
