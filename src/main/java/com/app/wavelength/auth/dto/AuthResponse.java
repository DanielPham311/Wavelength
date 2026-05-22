package com.app.wavelength.auth.dto;

import com.app.wavelength.auth.domain.User;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserSummary user
) {
    public record UserSummary(
            UUID id,
            String email,
            String displayName,
            String avatarUrl,
            String role
    ) {
        public static UserSummary from(User user) {
            return new UserSummary(
                    user.getId(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getAvatarUrl(),
                    user.getRole().name()
            );
        }
    }
}