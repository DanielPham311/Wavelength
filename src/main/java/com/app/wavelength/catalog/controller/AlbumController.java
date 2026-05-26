package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.AlbumResponse;
import com.app.wavelength.catalog.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    // GET /api/v1/albums/{id}?include_songs=true
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbum(
            @PathVariable UUID id,
            @RequestParam(name = "include_songs", defaultValue = "false") boolean includeSongs) {
        return ResponseEntity.ok(albumService.getAlbumById(id, includeSongs));
    }
}