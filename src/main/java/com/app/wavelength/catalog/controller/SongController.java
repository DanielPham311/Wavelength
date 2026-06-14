package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
@Tag(name = "Songs", description = "Song metadata, trending, upload, and streaming")
public class SongController {

    private final SongService songService;

    @GetMapping("/{id}")
    @Operation(summary = "Get song metadata", description = "Returns song metadata without stream URL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song found"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public ResponseEntity<SongResponse> getSong(@PathVariable UUID id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @GetMapping("/trending")
    @Operation(summary = "Trending songs", description = "Returns top songs by trending score")
    @ApiResponse(responseCode = "200", description = "List of trending songs")
    public ResponseEntity<List<SongResponse>> getTrending(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(songService.getTrending(Math.min(limit, 100)));
    }
}
