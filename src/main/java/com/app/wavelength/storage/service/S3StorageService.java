package com.app.wavelength.storage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {
    private S3Client s3Client;
    private S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.public-url}")
    private String publicURL;

    //Upload files to S3 bucket
    public String uploadFile(Path filePath, String s3Key, String contentType) {
        try {
           PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromFile(filePath));
            log.info("Uploaded file to S3: {}", s3Key);
            return publicURL + "/" + s3Key;
        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    //Upload raw byte data to S3 bucket
    public String uploadBytes(byte[] data, String s3Key, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(data));
            log.info("Uploaded byte data to S3: {}", s3Key);
            return publicURL + "/" + s3Key;
        } catch (Exception e) {
            log.error("Error uploading byte data to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload byte data to S3", e);
        }
    }

    // ── Upload entire directory (HLS segments folder) ─────────────────────

    public void uploadDirectory(Path localDir, String s3Prefix) throws IOException {
        Files.walk(localDir)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String relativePath = localDir.relativize(file).toString()
                            .replace("\\", "/"); // Windows path fix
                    String s3Key = s3Prefix + "/" + relativePath;
                    String contentType = relativePath.endsWith(".m3u8")
                            ? "application/vnd.apple.mpegurl"
                            : "video/MP2T";
                    uploadFile(file, s3Key, contentType);
                });

        log.info("Uploaded HLS directory to S3 prefix: {}", s3Prefix);
    }

    // ── Generate pre-signed URL for private files ─────────────────────────

    public String generatePresignedUrl(String s3Key, Duration expiry) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .getObjectRequest(r -> r.bucket(bucketName).key(s3Key))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // ── Delete a file ──────────────────────────────────────────────────────

    public void deleteFile(String s3Key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build());
            log.info("Deleted S3 file: {}", s3Key);
        } catch (S3Exception e) {
            log.warn("Failed to delete S3 file: {}", s3Key, e);
        }
    }
}
