package com.app.wavelength.library.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.wavelength.library.dto.AddSongRequest;
import com.app.wavelength.library.dto.CreatePlaylistRequest;
import com.app.wavelength.library.dto.PlaylistResponse;
import com.app.wavelength.library.service.PlaylistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    
    //POST /api/v1/playlists - Create a new playlist
    @PostMapping
    public ResponseEntity<PlaylistResponse> create(
        @Valid @RequestBody CreatePlaylistRequest request,
        @AuthenticationPrincipal UUID userID
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.createPlaylist(request, userID));
    }
    
    //GET /api/v1/playlists/{id}?includeSongs=true - Get playlist details (with optional songs)
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylist(
        @AuthenticationPrincipal UUID userID,
        @PathVariable UUID id,
        @RequestParam(name="include_songs", defaultValue = "false") boolean includeSongs
    ) {
        return ResponseEntity.ok(playlistService.getByID(id, includeSongs, userID));
    }

    // PUT /api/v1/playlists/{id}/songs
    @PutMapping("/{id}/songs")
    public ResponseEntity<PlaylistResponse> addSongs(
            @PathVariable UUID id,
            @Valid @RequestBody AddSongRequest request,
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(playlistService.addSongsToPlaylist(id, request, userId));
    }

    // DELETE /api/v1/playlists/{id}/songs/{songId}
    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Void> removeSong(
            @PathVariable UUID id,
            @PathVariable UUID songId,
            @AuthenticationPrincipal UUID userId) {
        playlistService.removeSong(id, songId, userId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/playlists/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        playlistService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
