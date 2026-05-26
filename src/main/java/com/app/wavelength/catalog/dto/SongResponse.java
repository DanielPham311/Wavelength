package com.app.wavelength.catalog.dto;

import java.time.Instant;
import java.util.UUID;

import com.app.wavelength.catalog.domain.Song;

public record SongResponse(
    UUID id,
    String title,
    UUID artistID,
    String artistName,
    UUID albumID,
    String albumTitle,
    Integer durationSeconds,
    Integer bitrate,
    String coverURL,
    Integer playCount,
    String uploadStatus,
    String signedHlsURL,
    Instant streamURLExpiresAt,
    Instant createdAt
) {

    public static SongResponse from(Song song, String artistName, String albumTitle) {
        return new SongResponse(song.getId(), song.getTitle(), song.getArtistId(), artistName, song.getAlbumId(), albumTitle,
                song.getDurationSeconds(), song.getBitrate(), song.getCoverUrl(), song.getPlayCount(),
                song.getUploadStatus().name(), null, null, song.getCreatedAt());
    }

    public SongResponse withStreamURL(String hlsURL, Instant expiresAt) {
        return new SongResponse(this.id, this.title, this.artistID, this.artistName, this.albumID, this.albumTitle,
                this.durationSeconds, this.bitrate, this.coverURL, this.playCount, this.uploadStatus,
                hlsURL, expiresAt, this.createdAt);
    }
}
