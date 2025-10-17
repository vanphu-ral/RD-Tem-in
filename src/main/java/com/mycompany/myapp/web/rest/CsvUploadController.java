package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.CsvUploadService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling CSV file uploads to SMB share.
 * This controller provides an HTTP endpoint that the frontend can call
 * to upload CSV files, which are then written to the configured SMB share.
 */
@RestController
@RequestMapping("/api/csv-upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CsvUploadController {

    private final Logger log = LoggerFactory.getLogger(
        CsvUploadController.class
    );

    @Autowired
    private CsvUploadService csvUploadService;

    /**
     * POST /api/csv-upload : Upload a CSV file to the configured SMB share
     *
     * @param file the CSV file to upload
     * @return ResponseEntity with success/error message
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadCsv(
        @RequestParam("file") MultipartFile file
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info(
                "Received CSV upload request: {}",
                file.getOriginalFilename()
            );

            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                response.put("success", false);
                response.put("message", "Only CSV files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            // Upload file to SMB share
            String result = csvUploadService.uploadToSmbShare(file);

            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("fileName", file.getOriginalFilename());
            response.put("filePath", result);

            log.info(
                "CSV file uploaded successfully: {}",
                file.getOriginalFilename()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading CSV file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error uploading file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                response
            );
        }
    }

    /**
     * GET /api/csv-upload/test : Test endpoint to verify the API is working
     *
     * @return ResponseEntity with test message
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "CSV Upload API is working");
        return ResponseEntity.ok(response);
    }
}
