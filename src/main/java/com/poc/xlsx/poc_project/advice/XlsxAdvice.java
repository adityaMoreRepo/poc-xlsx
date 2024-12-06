package com.poc.xlsx.poc_project.advice;

import com.poc.xlsx.poc_project.service.SheetNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class XlsxAdvice {
    @ExceptionHandler(SheetNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException() {
        return ResponseEntity.status(500).body("Sheet not found. Please check the file name and try again.");
    }
}
