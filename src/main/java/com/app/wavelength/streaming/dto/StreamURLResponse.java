package com.app.wavelength.streaming.dto;

import java.time.Instant;
import java.util.UUID;

public record StreamURLResponse(UUID songId,
    String title,
    String signedHlsUrl,
    String coverUrl,
    Integer durationSeconds,
    Instant expiresAt) {}


