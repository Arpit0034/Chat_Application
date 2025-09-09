package com.chat_application.exception;

public class UnAuthorisedException extends RuntimeException {
    public UnAuthorisedException(String message){
        super(message) ;
    }
}
