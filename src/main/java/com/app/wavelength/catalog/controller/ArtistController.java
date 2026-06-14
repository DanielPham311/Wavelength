package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.AlbumResponse;
import com.app.wavelength.catalog.dto.ArtistResponse;
import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.service.AlbumService;
import com.app.wavelength.catalog.service.ArtistService;
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
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
@Tag(name = "Artists", description = "Artist metadata, discography, and catalog lookup")
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;
    private final AlbumService albumService;

    @GetMapping("/{id}")
    @Operation(summary = "Get artist", description = "Return artist metadata.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artist found"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public ResponseEntity<ArtistResponse> getArtist(@PathVariable UUID id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @GetMapping("/{id}/songs")
    @Operation(summary = "Get artist songs", description = "Return songs by this artist.")
    @ApiResponse(responseCode = "200", description = "List of songs")
    public ResponseEntity<List<SongResponse>> getArtistSongs(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(songService.getSongsByArtist(id, limit));
    }

    @GetMapping("/{id}/albums")
    @Operation(summary = "Get artist albums", description = "Return albums by this artist.")
    @ApiResponse(responseCode = "200", description = "List of albums")
    public ResponseEntity<List<AlbumResponse>> getArtistAlbums(@PathVariable UUID id) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(id));
    }
}
