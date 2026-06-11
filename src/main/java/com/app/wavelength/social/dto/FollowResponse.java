package com.app.wavelength.social.dto;

import java.util.UUID;

public record FollowResponse(
        UUID artistId,
        boolean following,
        long followerCount
) {}