package com.app.wavelength.auth.service;

import com.app.wavelength.auth.domain.RefreshToken;
import com.app.wavelength.auth.domain.User;
import com.app.wavelength.auth.dto.AuthResponse;
import com.app.wavelength.auth.dto.LoginRequest;
import com.app.wavelength.auth.dto.RegisterRequest;
import com.app.wavelength.auth.dto.UpdateProfileRequest;
import com.app.wavelength.auth.dto.UserProfileResponse;
import com.app.wavelength.auth.repository.RefreshTokenRepository;
import com.app.wavelength.auth.repository.UserRepository;
import com.app.wavelength.common.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    private static final int MAX_SESSIONS = 5;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(req.email())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = User.builder()
                .email(req.email().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(req.password()))
                .displayName(req.displayName().trim())
                .role(User.Role.LISTENER)
                .build();

        userRepository.save(user);
        return issueTokenPair(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(req.email().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return issueTokenPair(user);
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        if (!jwtUtil.isTokenValid(rawRefreshToken) || !jwtUtil.isRefreshToken(rawRefreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String hash = hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not recognised"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new BadCredentialsException("Refresh token expired, please log in again");
        }

        refreshTokenRepository.delete(storedToken);
        return issueTokenPair(storedToken.getUser());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest req) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (req.displayName() != null && !req.displayName().isBlank()) {
            user.setDisplayName(req.displayName().trim());
        }
        if (req.avatarUrl() != null) {
            user.setAvatarURL(req.avatarUrl().trim());
        }
        if (req.bitratePref() != null) {
            user.setBitratePref(req.bitratePref());
        }

        userRepository.save(user);
        return toProfileResponse(user);
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        refreshTokenRepository.deleteAllByUser(user);
    }

    private AuthResponse issueTokenPair(User user) {
        String accessToken  = jwtUtil.generateAccessToken(
                user.getID(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getID());
        persistRefreshToken(user, refreshToken);
        return new AuthResponse(accessToken, refreshToken, AuthResponse.UserSummary.from(user));
    }

    private void persistRefreshToken(User user, String rawToken) {
        if (refreshTokenRepository.countByUser(user) >= MAX_SESSIONS) {
            refreshTokenRepository.deleteAllByUser(user);
        }
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hashToken(rawToken))
                .user(user)
                .expiresAt(jwtUtil.extractExpiration(rawToken).toInstant())
                .build();
        refreshTokenRepository.save(entity);
    }

    private UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.getID(), user.getEmail(), user.getDisplayName(),
                user.getAvatarUrl(), user.getRole().name(),
                user.getBitratePref(), user.getCreatedAt(), user.getUpdatedAt()
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}