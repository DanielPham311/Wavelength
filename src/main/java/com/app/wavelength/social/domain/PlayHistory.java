package com.app.wavelength.social.domain;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "play_history", indexes = {
    @Index(name = "idx_play_history_user_id", columnList = "user_id"),
    @Index(name = "idx_play_history_song_id", columnList = "song_id"),
    @Index(name = "idx_play_history_played_at", columnList = "played_at")
}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ID;

    @Column(name = "user_id", nullable = false)
    private UUID userID;

    @Column(name = "song_id", nullable = false)
    private UUID songID;

    // How many seconds the user actually listened before skipping
    @Column(name = "duration_played")
    private Integer durationPlayed;

    // The signed URL used — useful for debugging stream issues
    @Column(name = "signed_url_used", length = 2048)
    private String signedURLused;

    // Flexible analytics payload from the app
    // e.g. { "source": "home_feed", "shuffle": true, "quality": "128k" }
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> analyticsData;

    @CreationTimestamp
    @Column(name = "played_at", updatable = false)
    private Instant playedAt;
}
