package org.example.lab7.service;


public class ServiceException extends RuntimeException{
    public ServiceException() {}

    public ServiceException(String message) {
        super(message);
    }
}
