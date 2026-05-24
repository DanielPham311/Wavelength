package com.app.wavelength.storage.controller;

import com.app.wavelength.storage.dto.UploadRequest;
import com.app.wavelength.storage.dto.UploadResponse;
import com.app.wavelength.storage.service.UploadService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    // POST /api/songs/upload
    // Requires ARTIST role — already enforced in SecurityConfig
    // Accepts multipart/form-data
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("title") @NotBlank String title,
            @RequestPart(value = "albumId",   required = false) String albumId,
            @RequestPart(value = "coverUrl",  required = false) String coverUrl,
            @AuthenticationPrincipal UUID artistId) {

        UploadRequest request = new UploadRequest(
                file,
                title,
                albumId != null ? UUID.fromString(albumId) : null,
                coverUrl
        );

        UploadResponse response = uploadService.upload(request, artistId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);  // 202
    }

    // GET /api/songs/{id}/status — poll upload/transcode status
    @GetMapping("/{id}/status")
    public ResponseEntity<UploadResponse> getStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {

        // We'll flesh this out fully in the catalog module
        // For now returns a placeholder — prevents 404 during testing
        return ResponseEntity.ok(new UploadResponse(
                id, "unknown", "processing", "Status endpoint coming in catalog module"
        ));
    }
}