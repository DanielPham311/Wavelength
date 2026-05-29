package com.app.wavelength.auth.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"), indexes = {@Index(name = "idx_email", columnList = "email"), @Index(name = "idx_display_name", columnList = "displayName")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ID;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "passwordHash",nullable = false)
    private String passwordHash;

    @Column(name = "displayName", nullable = false, length = 100)
    private String displayName;

    @Column(name = "avatarURL")
    private String avatarURL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.LISTENER;

    @Column(name = "bitratePref")
    private Integer bitratePref;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum Role {
        LISTENER,
        ARTIST,
        ADMIN
    }
    
    public String getAvatarUrl() {
        return avatarURL;
    }
}
