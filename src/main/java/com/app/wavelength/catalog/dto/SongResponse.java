package com.app.wavelength.catalog.dto;

import java.time.Instant;
import java.util.UUID;

import com.app.wavelength.catalog.domain.Song;

public record SongResponse(
    UUID id,
    String title,
    UUID artistId,
    String artistName,
    UUID albumId,
    String albumTitle,
    Integer durationSeconds,
    Integer bitrate,
    String coverUrl,
    Integer playCount,
    String uploadStatus,
    String signedHlsUrl,
    Instant streamUrlExpiresAt,
    Instant createdAt
) {

    public static SongResponse from(Song song, String artistName, String albumTitle) {
        return new SongResponse(song.getId(), song.getTitle(), song.getArtistId(), artistName, song.getAlbumId(), albumTitle,
                song.getDurationSeconds(), song.getBitrate(), song.getCoverUrl(), song.getPlayCount(),
                song.getUploadStatus().name(), null, null, song.getCreatedAt());
    }

    public SongResponse withStreamUrl(String hlsUrl, Instant expiresAt) {
        return new SongResponse(this.id, this.title, this.artistId, this.artistName, this.albumId, this.albumTitle,
                this.durationSeconds, this.bitrate, this.coverUrl, this.playCount, this.uploadStatus,
                hlsUrl, expiresAt, this.createdAt);
    }
}
