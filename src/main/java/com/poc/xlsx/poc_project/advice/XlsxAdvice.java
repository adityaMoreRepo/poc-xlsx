package com.poc.xlsx.poc_project.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

@ControllerAdvice
public class XlsxAdvice {
    @ExceptionHandler(FileNotFoundException.class) //Handling IOException
    public ResponseEntity<String> handleFileNotFoundException() {
        return ResponseEntity.status(500).body("File not found. Please check the file name and try again.");
    }
}
