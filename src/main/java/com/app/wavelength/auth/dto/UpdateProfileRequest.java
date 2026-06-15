package com.app.wavelength.auth.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
        String displayName,

        String avatarUrl,

        Integer bitratePref
) {}
