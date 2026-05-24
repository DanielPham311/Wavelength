package com.app.wavelength.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UploadRequest(

        @NotNull(message = "Audio file is required")
        MultipartFile file,

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        // artistId comes from the JWT, not the request body
        // albumId is optional — song can be a standalone single
        UUID albumId,

        String coverUrl
) {}