package com.app.wavelength.social.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "user_artist_follows",
         uniqueConstraints = @UniqueConstraint(
                columnNames = {"follower_id", "artist_id"}),
        indexes = {
                @Index(name = "idx_follows_follower_id", columnList = "follower_id"),
                @Index(name = "idx_follows_artist_id",   columnList = "artist_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserArtistFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @CreationTimestamp
    @Column(name = "followed_at", updatable = false)
    private Instant followedAt;
}
