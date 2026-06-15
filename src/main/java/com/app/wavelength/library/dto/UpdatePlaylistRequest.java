package com.app.wavelength.library.dto;

import jakarta.validation.constraints.Size;

public record UpdatePlaylistRequest(
        @Size(min = 1, max = 255, message = "Playlist name must be between 1 and 255 characters")
        String name,

        String description,

        Boolean isPublic
) {}
