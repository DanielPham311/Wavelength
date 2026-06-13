package com.app.wavelength.streaming.controller;

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
public class StreamController {
    private final StreamService streamService;

    @GetMapping("/{id}/stream")
    public ResponseEntity<StreamURLResponse> stream(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {

        StreamURLResponse response = streamService.getStreamUrl(id, userId);
        return ResponseEntity.ok(response);
    }
    
}
