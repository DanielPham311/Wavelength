package com.app.wavelength.storage.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UploadRequest(
        MultipartFile file,
        String title,
        UUID albumId,
        String coverUrl
) {}