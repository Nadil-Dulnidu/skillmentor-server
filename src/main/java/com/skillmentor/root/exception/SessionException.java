package com.skillmentor.root.exception;

public class SessionException extends RuntimeException{
    public SessionException(String message, Throwable throwable){
        super(message, throwable);
    }
}

