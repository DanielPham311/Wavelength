package com.app.wavelength.streaming.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.wavelength.catalog.domain.Song;
import com.app.wavelength.catalog.service.SongService;
import com.app.wavelength.storage.service.S3StorageService;
import com.app.wavelength.streaming.dto.StreamURLResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamServiceImpl implements StreamService {
    private final S3StorageService s3StorageService;
    private final SongService songService;

    private static final Duration STREAM_URL_EXPIRATION = Duration.ofMinutes(15);

    @Override
    public String generatePresignedHlsUrl(String s3Key) {
        return s3StorageService.generatePresignedUrl(s3Key, STREAM_URL_EXPIRATION);
    }

    // Extract the S3 key from the full public URL
    // e.g. https://bucket.s3.region.amazonaws.com/artist/123/hls/index.m3u8
    //   →  artist/123/hls/index.m3u8
    private String extractS3Key(String fullUrl) {
        if (fullUrl == null) throw new IllegalStateException("Song has no HLS URL");
        // If already a relative key (no http), return as-is
        if (!fullUrl.startsWith("http")) return fullUrl;
        // Strip the base URL — everything after the third slash group
        return fullUrl.substring(fullUrl.indexOf(".com/") + 5);
    }

    @Override
    public StreamURLResponse getStreamUrl(UUID songId, UUID userId) {
        Song song = songService.findSongById(songID);

        if(song.getUploadStatus() != Song.UploadStatus.READY) {
            throw new IllegalStateException("Song is not available for streaming yet. Status: " + song.getUploadStatus());
        }

        // hlsUrl stored in DB is the full S3 path e.g.
        // artist/{artistId}/album/{albumId}/{songId}/hls/index.m3u8
        String s3Key = extractS3Key(song.getHlsUrl());
        String signedUrl = s3StorageService.generatePresignedUrl(s3Key, STREAM_URL_EXPIRATION);

        log.info("Generated presigned URL for song {}: {}", songID, signedUrl);

        return new StreamURLResponse(song.getId(), song.getTitle(), signedUrl, song.getCoverUrl(), song.getDurationSeconds(), 
            Instant.now().plus(STREAM_URL_EXPIRATION));
    }

}
