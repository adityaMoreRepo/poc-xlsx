package com.poc.xlsx.poc_project.controller;

import com.poc.xlsx.poc_project.service.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class XlsxFileController {

    Logger log = LoggerFactory.getLogger(FileProcessingService.class);

    private static final String BASE_DIRECTORY = "./uploads";

    private static final String DELIMITER = "/";

    @Autowired
    FileProcessingService fileProcessingService;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Invalid file or file is empty. Only .xlsx files are allowed.");
            }
            Path uploadPath = Paths.get(BASE_DIRECTORY);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Directory created successfully");
            }
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File copied successfully");
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterDataByMonth(@RequestParam("sheetName") String sheetName,
                                            @RequestParam("file") String file) {
        try {
            Path filePath = Paths.get(BASE_DIRECTORY + DELIMITER + file);
            List<Map<String, String>> result = fileProcessingService.parseExcelTable(filePath, sheetName);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File parsing failed.");
        }
    }

    @GetMapping("/sheets")
    public ResponseEntity<Object> getSheetNames(@RequestParam("file") String file) {
        try {
            Path filePath = Paths.get(BASE_DIRECTORY + DELIMITER + file);
            List<String> sheetNames = fileProcessingService.getSheetNames(filePath);
            return ResponseEntity.ok(sheetNames);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File parsing failed.");
        }
    }
}
