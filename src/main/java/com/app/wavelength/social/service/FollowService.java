package com.app.wavelength.social.service;

import com.app.wavelength.social.domain.UserArtistFollow;
import com.app.wavelength.social.dto.FollowResponse;
import com.app.wavelength.social.repository.UserArtistFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserArtistFollowRepository followRepository;

    // Toggle follow — follow if not following, unfollow if already following
    @Transactional
    public FollowResponse toggleFollow(UUID followerId, UUID artistId) {
        boolean alreadyFollowing = followRepository
                .existsByFollowerIdAndArtistId(followerId, artistId);

        if (alreadyFollowing) {
            followRepository.deleteByFollowerIdAndArtistId(followerId, artistId);
        } else {
            UserArtistFollow follow = UserArtistFollow.builder()
                    .followerId(followerId)
                    .artistId(artistId)
                    .build();
            followRepository.save(follow);
        }

        long followerCount = followRepository.countByArtistId(artistId);

        return new FollowResponse(artistId, !alreadyFollowing, followerCount);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(UUID followerId, UUID artistId) {
        return followRepository.existsByFollowerIdAndArtistId(followerId, artistId);
    }

    @Transactional(readOnly = true)
    public List<UUID> getFollowedArtistIds(UUID userId) {
        return followRepository.findByFollowerIdOrderByFollowedAtDesc(userId)
                .stream()
                .map(UserArtistFollow::getArtistId)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(UUID artistId) {
        return followRepository.countByArtistId(artistId);
    }
}