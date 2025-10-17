package com.mycompany.myapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for uploading CSV files to SMB share.
 * This service handles the actual file writing to the network share location.
 */
@Service
public class CsvUploadService {

    private final Logger log = LoggerFactory.getLogger(CsvUploadService.class);

    // Configuration from application.yml
    @Value("${csv.upload.path:C:/Upload_software}")
    private String uploadPath;

    @Value("${csv.upload.smb.enabled:false}")
    private boolean smbEnabled;

    @Value("${csv.upload.smb.host:192.168.68.16}")
    private String smbHost;

    @Value("${csv.upload.smb.share:Upload_software}")
    private String smbShare;

    @Value("${csv.upload.smb.username:rangdong}")
    private String smbUsername;

    @Value("${csv.upload.smb.password:R@NGd0ng}")
    private String smbPassword;

    /**
     * Upload a CSV file to the configured SMB share or local path
     *
     * @param file the MultipartFile to upload
     * @return the path where the file was saved
     * @throws IOException if an error occurs during file upload
     */
    public String uploadToSmbShare(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "upload_" + System.currentTimeMillis() + ".csv";
        }

        // Add timestamp to filename to avoid conflicts
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );
        String fileName = timestamp + "_" + originalFilename;

        try {
            if (smbEnabled) {
                // Upload to SMB share using JCIFS or similar library
                return uploadToSmbShareInternal(file, fileName);
            } else {
                // Upload to local path (fallback or development mode)
                return uploadToLocalPath(file, fileName);
            }
        } catch (Exception e) {
            log.error(
                "Error uploading file to SMB share: {}",
                e.getMessage(),
                e
            );
            throw new IOException(
                "Failed to upload file: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Upload file to SMB share using network path
     * For Windows, this uses UNC path: \\host\share\file
     *
     * @param file     the file to upload
     * @param fileName the target filename
     * @return the full path where file was saved
     * @throws IOException if upload fails
     */
    private String uploadToSmbShareInternal(MultipartFile file, String fileName)
        throws IOException {
        // Construct UNC path for Windows: \\LAPTOPCUAXUAN\Log_Printer\filename.csv
        String uncPath = String.format(
            "\\\\%s\\%s\\%s",
            smbHost,
            smbShare,
            fileName
        );

        log.info("Uploading file to SMB share: {}", uncPath);
        log.info("SMB Host: {}, Share: {}", smbHost, smbShare);

        try {
            Path targetPath = Paths.get(uncPath);

            log.info("Target path: {}", targetPath.toString());

            // Ensure parent directory exists (the share itself)
            Path parentDir = targetPath.getParent();
            if (parentDir != null) {
                log.info("Parent directory: {}", parentDir.toString());
                if (!Files.exists(parentDir)) {
                    log.warn(
                        "Parent directory does not exist, attempting to create: {}",
                        parentDir
                    );
                    try {
                        Files.createDirectories(parentDir);
                    } catch (IOException e) {
                        log.error(
                            "Cannot create parent directory: {}",
                            e.getMessage()
                        );
                        // Continue anyway, maybe the share exists but Java can't detect it
                    }
                }
            }

            // Copy file to SMB share
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                    inputStream,
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
                );
            }

            log.info("File uploaded successfully to: {}", uncPath);
            return uncPath;
        } catch (IOException e) {
            log.error("Failed to upload to SMB share: {}", e.getMessage(), e);
            log.error(
                "Error details - Host: {}, Share: {}, File: {}",
                smbHost,
                smbShare,
                fileName
            );
            // Fallback to local path if SMB fails
            log.warn("Falling back to local path upload");
            return uploadToLocalPath(file, fileName);
        }
    }

    /**
     * Upload file to local filesystem path
     * This is used as fallback or in development mode
     *
     * @param file     the file to upload
     * @param fileName the target filename
     * @return the full path where file was saved
     * @throws IOException if upload fails
     */
    private String uploadToLocalPath(MultipartFile file, String fileName)
        throws IOException {
        Path uploadDir = Paths.get(uploadPath);

        // Create directory if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("Created upload directory: {}", uploadDir);
        }

        Path targetPath = uploadDir.resolve(fileName);

        log.info("Uploading file to local path: {}", targetPath);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                inputStream,
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
            );
        }

        log.info("File uploaded successfully to: {}", targetPath);
        return targetPath.toString();
    }

    /**
     * Get the configured upload path
     *
     * @return the upload path
     */
    public String getUploadPath() {
        if (smbEnabled) {
            return String.format("\\\\%s\\%s", smbHost, smbShare);
        }
        return uploadPath;
    }
}
