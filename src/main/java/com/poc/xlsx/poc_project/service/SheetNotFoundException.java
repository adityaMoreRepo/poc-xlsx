package com.poc.xlsx.poc_project.service;

public class SheetNotFoundException extends RuntimeException {
    public SheetNotFoundException(String message) {
        super(message);
    }
}
