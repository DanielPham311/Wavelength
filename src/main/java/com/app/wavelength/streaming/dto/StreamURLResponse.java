package com.app.wavelength.streaming.dto;

import java.time.Instant;
import java.util.UUID;

public record StreamURLResponse(UUID songID,
    String title,
    String signedHlsURL,
    String coverURL,
    Integer durationSeconds,
    Instant expiresAt) {}


