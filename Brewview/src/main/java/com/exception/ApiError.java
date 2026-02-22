package com.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ApiError {
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;

    private Map<String, List<String>> fieldErrors;

    public ApiError(){}

    //constructor
    public ApiError(int status, String error, String message, String path) {
        this.timeStamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    //Getters
    public LocalDateTime getTimestamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, List<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
