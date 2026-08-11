package com.mycompany.myapp.service;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvUploadService {

    private final Logger log = LoggerFactory.getLogger(CsvUploadService.class);

    // Configuration --> application.yml
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
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadToSmbShare(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "upload_" + System.currentTimeMillis() + ".csv";
        }

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
     *
     * @param file
     * @param fileName t
     * @return
     * @throws
     */
    private String uploadToSmbShareInternal(MultipartFile file, String fileName)
        throws IOException {
        String uncPath = String.format(
            "\\\\%s\\%s\\%s",
            smbHost,
            smbShare,
            fileName
        );

        log.info("Uploading file to SMB share: {}", uncPath);
        log.info(
            "SMB Host: {}, Share: {}, User: {}",
            smbHost,
            smbShare,
            smbUsername
        );

        SMBClient smbClient = new SMBClient();
        Connection connection = null;
        com.hierynomus.smbj.session.Session session = null;
        DiskShare diskShare = null;

        try {
            connection = smbClient.connect(smbHost);
            AuthenticationContext authContext = new AuthenticationContext(
                smbUsername,
                smbPassword.toCharArray(),
                null
            );
            session = connection.authenticate(authContext);
            diskShare = (DiskShare) session.connectShare(smbShare);

            try (
                InputStream inputStream = file.getInputStream();
                File smbFile = diskShare.openFile(
                    fileName,
                    EnumSet.of(AccessMask.GENERIC_WRITE),
                    EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                    EnumSet.of(
                        SMB2ShareAccess.FILE_SHARE_READ,
                        SMB2ShareAccess.FILE_SHARE_WRITE,
                        SMB2ShareAccess.FILE_SHARE_DELETE
                    ),
                    SMB2CreateDisposition.FILE_OVERWRITE_IF,
                    EnumSet.noneOf(SMB2CreateOptions.class)
                );
                OutputStream outputStream = smbFile.getOutputStream();
            ) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            log.info("File uploaded successfully to: {}", uncPath);
            return uncPath;
        } catch (Exception e) {
            log.error("Failed to upload to SMB share: {}", e.getMessage(), e);
            log.error(
                "Error details - Host: {}, Share: {}, File: {}, User: {}",
                smbHost,
                smbShare,
                fileName,
                smbUsername
            );
            throw new IOException(
                "Failed to upload file: " + e.getMessage(),
                e
            );
        } finally {
            if (diskShare != null) {
                try {
                    diskShare.close();
                } catch (Exception e) {
                    log.error("Error closing share", e);
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (Exception e) {
                    log.error("Error closing session", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.error("Error closing connection", e);
                }
            }
            smbClient.close();
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
