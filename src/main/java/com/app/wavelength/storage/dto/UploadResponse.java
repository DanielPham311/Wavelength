package com.app.wavelength.storage.dto;

import java.util.UUID;

public record UploadResponse(
    UUID songID, 
    String title,
    String status,
    String message
) {

}
