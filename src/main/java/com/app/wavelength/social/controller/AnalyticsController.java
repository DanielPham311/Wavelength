package com.app.wavelength.social.controller;

import com.app.wavelength.social.dto.PlayAnalyticsRequest;
import com.app.wavelength.social.service.PlayHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Analytics", description = "Play history and listening analytics")
public class AnalyticsController {

    private final PlayHistoryService playHistoryService;

    @PostMapping("/songs/{id}/play/analytics")
    @Operation(summary = "Record play", description = "Record a play/skip event for analytics. Called by client after playback ends or skips.")
    @ApiResponse(responseCode = "200", description = "Play recorded")
    public ResponseEntity<Map<String, Object>> recordPlay(
            @PathVariable UUID id,
            @RequestBody PlayAnalyticsRequest request,
            @AuthenticationPrincipal UUID userId) {

        playHistoryService.recordPlay(userId, id, request);

        return ResponseEntity.ok(Map.of(
                "recorded", true,
                "songId", id
        ));
    }

    @GetMapping("/users/me/recently_played")
    @Operation(summary = "Recently played", description = "Return song IDs the user recently played, newest first.")
    @ApiResponse(responseCode = "200", description = "List of song IDs")
    public ResponseEntity<List<UUID>> getRecentlyPlayed(
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UUID userId) {

        return ResponseEntity.ok(
                playHistoryService.getRecentlyPlayedSongIds(userId, Math.min(limit, 50)));
    }
}
