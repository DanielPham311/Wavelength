package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.AlbumResponse;
import com.app.wavelength.catalog.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
@Tag(name = "Albums", description = "Album metadata and lookup")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/{id}")
    @Operation(summary = "Get album", description = "Return album metadata. Optionally include tracklist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album found"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    public ResponseEntity<AlbumResponse> getAlbum(
            @PathVariable UUID id,
            @RequestParam(name = "include_songs", defaultValue = "false") boolean includeSongs) {
        return ResponseEntity.ok(albumService.getAlbumById(id, includeSongs));
    }
}
