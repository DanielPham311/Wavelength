package com.app.wavelength.social.controller;

import com.app.wavelength.social.dto.FollowResponse;
import com.app.wavelength.social.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // POST /api/v1/users/me/follow/{artistId} — toggle follow/unfollow
    @PostMapping("/follow/{artistId}")
    public ResponseEntity<FollowResponse> toggleFollow(
            @PathVariable UUID artistId,
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(followService.toggleFollow(userId, artistId));
    }

    // GET /api/v1/users/me/following — list artist IDs the user follows
    @GetMapping("/following")
    public ResponseEntity<?> getFollowing(
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(followService.getFollowedArtistIds(userId));
    }

    // GET /api/v1/users/me/following/{artistId} — check if following a specific artist
    @GetMapping("/following/{artistId}")
    public ResponseEntity<?> isFollowing(
            @PathVariable UUID artistId,
            @AuthenticationPrincipal UUID userId) {
        boolean following = followService.isFollowing(userId, artistId);
        long count = followService.getFollowerCount(artistId);
        return ResponseEntity.ok(new FollowResponse(artistId, following, count));
    }
}