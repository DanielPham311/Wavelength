package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    // GET /api/v1/songs/{id} — metadata only, no stream URL
    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSong(@PathVariable UUID id) {
        return ResponseEntity.ok(songService.getSongByID(id));
    }

    // GET /api/v1/songs/trending?limit=50
    @GetMapping("/trending")
    public ResponseEntity<List<SongResponse>> getTrending(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(songService.getTrending(Math.min(limit, 100)));
    }
}