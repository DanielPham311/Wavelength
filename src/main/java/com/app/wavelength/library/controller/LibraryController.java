package com.app.wavelength.library.controller;

import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.library.dto.PlaylistResponse;
import com.app.wavelength.library.service.LikedSongService;
import com.app.wavelength.library.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Library", description = "User library: playlists, liked songs, and personal catalog")
public class LibraryController {

    private final PlaylistService playlistService;
    private final LikedSongService likedSongService;

    @GetMapping("/users/me/playlists")
    @Operation(summary = "Get my playlists", description = "Return all playlists owned by the authenticated user.")
    @ApiResponse(responseCode = "200", description = "List of playlists")
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists(
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(playlistService.getUsersPlaylists(userId));
    }

    @GetMapping("/users/me/liked_songs")
    @Operation(summary = "Get liked songs", description = "Return songs liked by the authenticated user.")
    @ApiResponse(responseCode = "200", description = "List of liked songs")
    public ResponseEntity<List<SongResponse>> getLikedSongs(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(likedSongService.getLikedSongs(
                userId, Math.min(limit, 100), offset));
    }

    @PostMapping("/songs/{id}/like")
    @Operation(summary = "Toggle like", description = "Like or unlike a song. Returns new like state.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Like toggled"),
            @ApiResponse(responseCode = "404", description = "Song not found")
    })
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        boolean liked = likedSongService.toggleLike(userId, id);
        return ResponseEntity.ok(Map.of(
                "songId", id,
                "liked", liked
        ));
    }
}
