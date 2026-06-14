package com.app.wavelength.social.controller;

import com.app.wavelength.social.dto.FollowResponse;
import com.app.wavelength.social.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "Follow/unfollow artists and query follow state")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{artistId}")
    @Operation(summary = "Toggle follow", description = "Follow or unfollow an artist. Returns new follow state.")
    @ApiResponse(responseCode = "200", description = "Follow toggled")
    public ResponseEntity<FollowResponse> toggleFollow(
            @PathVariable UUID artistId,
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(followService.toggleFollow(userId, artistId));
    }

    @GetMapping("/following")
    @Operation(summary = "Get following", description = "List artist IDs the authenticated user follows.")
    @ApiResponse(responseCode = "200", description = "List followed artists")
    public ResponseEntity<?> getFollowing(
            @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(followService.getFollowedArtistIds(userId));
    }

    @GetMapping("/following/{artistId}")
    @Operation(summary = "Is following", description = "Check if the user follows a specific artist. Returns follow state and follower count.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Follow state returned"),
            @ApiResponse(responseCode = "404", description = "Artist not found")
    })
    public ResponseEntity<?> isFollowing(
            @PathVariable UUID artistId,
            @AuthenticationPrincipal UUID userId) {
        boolean following = followService.isFollowing(userId, artistId);
        long count = followService.getFollowerCount(artistId);
        return ResponseEntity.ok(new FollowResponse(artistId, following, count));
    }
}
