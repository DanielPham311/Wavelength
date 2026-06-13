package com.app.wavelength.streaming.service;

import java.util.UUID;

import com.app.wavelength.streaming.dto.StreamURLResponse;


public interface StreamService {
    StreamURLResponse getStreamUrl(UUID songId, UUID userId);
    String generatePresignedHlsUrl(String s3Key);
}
