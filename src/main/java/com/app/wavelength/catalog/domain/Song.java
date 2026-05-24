package com.app.wavelength.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "songs", indexes = {
        @Index(name = "idx_songs_artist_id", columnList = "artist_id"),
        @Index(name = "idx_songs_album_id",  columnList = "album_id"),
        @Index(name = "idx_songs_status",    columnList = "upload_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "album_id")
    private UUID albumId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "bitrate")
    private Integer bitrate;

    @Column(name = "file_format", length = 10)
    private String fileFormat;

    // Private — raw uploaded file, never served directly
    @Column(name = "raw_file_url")
    private String rawFileUrl;

    // Public — HLS master playlist URL served to the app
    @Column(name = "hls_url")
    private String hlsUrl;

    // Path structure: artist/{artistId}/album/{albumId}/{songId}/
    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "play_count", nullable = false)
    @Builder.Default
    private Integer playCount = 0;

    @Column(name = "trending_score", nullable = false)
    @Builder.Default
    private Float trendingScore = 0f;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, length = 20)
    @Builder.Default
    private UploadStatus uploadStatus = UploadStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public enum UploadStatus {
        PENDING,      // file received, waiting for transcode
        PROCESSING,   // FFmpeg running
        READY,        // HLS available, song is streamable
        FAILED        // transcode failed
    }
}