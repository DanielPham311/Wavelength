package com.app.wavelength.library.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;

public record AddSongRequest(
    @NotEmpty(message = "Song IDs list must not be empty")
    List<UUID> songIds,
    Integer position
) {
}
