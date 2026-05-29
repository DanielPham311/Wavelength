package com.app.wavelength.library.domain;

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
@Table(name = "liked_songs",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_id"}),
       indexes = @Index(name = "idx_liked_songs_user_id", columnList = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedSong {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ID;
    
    @Column(name = "user_id", nullable = false)
    private UUID userID;

    @Column(name = "song_id", nullable = false)
    private UUID songID;

    @CreationTimestamp
    @Column(name = "liked_at", updatable = false)
    private Instant likedAt;
}
