package com.exception;

public class DuplicateLocationException extends RuntimeException {
    public DuplicateLocationException(String message) {
        super(message);
    }
}
