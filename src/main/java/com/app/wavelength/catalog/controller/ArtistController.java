package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.AlbumResponse;
import com.app.wavelength.catalog.dto.ArtistResponse;
import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.service.AlbumService;
import com.app.wavelength.catalog.service.ArtistService;
import com.app.wavelength.catalog.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;
    private final AlbumService albumService;

    // GET /api/v1/artists/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getArtist(@PathVariable UUID id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    // GET /api/v1/artists/{id}/songs?limit=50
    @GetMapping("/{id}/songs")
    public ResponseEntity<List<SongResponse>> getArtistSongs(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(songService.getSongsByArtist(id, limit));
    }

    // GET /api/v1/artists/{id}/albums
    @GetMapping("/{id}/albums")
    public ResponseEntity<List<AlbumResponse>> getArtistAlbums(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(id));
    }
}