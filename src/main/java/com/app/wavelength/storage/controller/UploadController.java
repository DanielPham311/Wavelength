package com.app.wavelength.storage.controller;

import com.app.wavelength.storage.dto.UploadRequest;
import com.app.wavelength.storage.dto.UploadResponse;
import com.app.wavelength.storage.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Upload", description = "Song file upload and transcode status")
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload song", description = "Upload an audio file for streaming. Requires ARTIST role. Returns 202 — processing is async.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Upload accepted, processing"),
            @ApiResponse(responseCode = "400", description = "Invalid file or missing fields"),
            @ApiResponse(responseCode = "403", description = "Requires ARTIST role")
    })
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
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Upload status", description = "Poll upload/transcode status for a song.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status returned"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public ResponseEntity<UploadResponse> getStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(new UploadResponse(
                id, "unknown", "processing", "Status endpoint coming in catalog module"
        ));
    }
}
