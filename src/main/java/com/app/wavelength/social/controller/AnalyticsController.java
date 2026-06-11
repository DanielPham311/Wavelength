package com.app.wavelength.social.controller;

import com.app.wavelength.social.dto.PlayAnalyticsRequest;
import com.app.wavelength.social.service.PlayHistoryService;
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
public class AnalyticsController {

    private final PlayHistoryService playHistoryService;

    // POST /api/v1/songs/{id}/play/{analytics}
    // Called by app when user finishes or skips a song
    @PostMapping("/songs/{id}/play/analytics")
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

    // GET /api/v1/users/me/recently_played
    @GetMapping("/users/me/recently_played")
    public ResponseEntity<List<UUID>> getRecentlyPlayed(
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UUID userId) {

        return ResponseEntity.ok(
                playHistoryService.getRecentlyPlayedSongIds(userId, Math.min(limit, 50)));
    }
}