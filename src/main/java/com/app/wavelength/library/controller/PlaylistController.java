package com.app.wavelength.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Playlists", description = "Playlist CRUD and song management")
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping
    @Operation(summary = "Create playlist", description = "Create a new playlist for the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Playlist created")
    public ResponseEntity<PlaylistResponse> create(
        @Valid @RequestBody CreatePlaylistRequest request,
        @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.createPlaylist(request, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get playlist", description = "Return playlist metadata. Optionally include tracklist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Playlist found"),
            @ApiResponse(responseCode = "404", description = "Playlist not found")
    })
    public ResponseEntity<PlaylistResponse> getPlaylist(
        @AuthenticationPrincipal UUID userId,
        @PathVariable UUID id,
        @RequestParam(name="include_songs", defaultValue = "false") boolean includeSongs
    ) {
        return ResponseEntity.ok(playlistService.getById(id, includeSongs, userId));
    }

    @PutMapping("/{id}/songs")
    @Operation(summary = "Add songs", description = "Add one or more songs to a playlist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Songs added"),
            @ApiResponse(responseCode = "404", description = "Playlist not found"),
            @ApiResponse(responseCode = "403", description = "Not playlist owner")
    })
    public ResponseEntity<PlaylistResponse> addSongs(
            @PathVariable UUID id,
            @Valid @RequestBody AddSongRequest request,
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(playlistService.addSongsToPlaylist(id, request, userId));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    @Operation(summary = "Remove song", description = "Remove a song from a playlist.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Song removed"),
            @ApiResponse(responseCode = "404", description = "Playlist or song not found"),
            @ApiResponse(responseCode = "403", description = "Not playlist owner")
    })
    public ResponseEntity<Void> removeSong(
            @PathVariable UUID id,
            @PathVariable UUID songId,
            @AuthenticationPrincipal UUID userId) {
        playlistService.removeSong(id, songId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete playlist", description = "Delete a playlist. Must be the owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Playlist deleted"),
            @ApiResponse(responseCode = "404", description = "Playlist not found"),
            @ApiResponse(responseCode = "403", description = "Not playlist owner")
    })
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        playlistService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
