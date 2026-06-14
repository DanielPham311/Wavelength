package com.app.wavelength.streaming.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.wavelength.streaming.dto.StreamURLResponse;
import com.app.wavelength.streaming.service.StreamService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
@Tag(name = "Streaming", description = "Song streaming endpoint")
public class StreamController {
    private final StreamService streamService;

    @GetMapping("/{id}/stream")
    @Operation(summary = "Stream song", description = "Return a pre-signed URL for streaming a song.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stream URL generated"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public ResponseEntity<StreamURLResponse> stream(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {

        StreamURLResponse response = streamService.getStreamUrl(id, userId);
        return ResponseEntity.ok(response);
    }

}
