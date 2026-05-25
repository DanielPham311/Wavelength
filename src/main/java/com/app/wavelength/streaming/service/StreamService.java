package com.app.wavelength.streaming.service;

import java.util.UUID;

import com.app.wavelength.streaming.dto.StreamURLResponse;


public interface StreamService {
    StreamURLResponse getStreamURL(UUID songID, UUID userID);
    String generatePresignedHlsURL(String s3Key);
}
