package com.poc.xlsx.poc_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class XlsxFileController {

    private static final String BASE_DIRECTORY = "./uploads";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if(file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Invalid file. Only .xlsx files are allowed.");
            }
//            String yearMonthDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
//            Path uploadPath = Paths.get(BASE_DIRECTORY, yearMonthDir);
            Path uploadPath = Paths.get(BASE_DIRECTORY);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            file.transferTo(filePath.toFile());
            return  ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

//    @GetMapping("/filter")
//    public ResponseEntity filterDataByMonth(@RequestParam("fileName") String month) {
//        try {
//            Path tempFile = Files.createTempFile("uploaded-", ".xlsx");
//
//            return ResponseEntity.ok("To be updated");
//        } catch ()
//    }
}
