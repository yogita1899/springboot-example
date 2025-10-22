package com.integrationninjas.springbootexample.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(String message) {
        super(message);
    }
}
