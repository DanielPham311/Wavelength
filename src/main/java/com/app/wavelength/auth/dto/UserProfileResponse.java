package com.app.wavelength.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String displayName,
        String avatarUrl,
        String role,
        Integer bitratePref,
        Instant createdAt,
        Instant updatedAt
) {}
