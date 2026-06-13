package com.app.wavelength.storage.service;

import com.app.wavelength.catalog.domain.Song;
import com.app.wavelength.catalog.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscodeService {

    private final S3StorageService s3StorageService;
    private final SongService songService;

    // Runs on the transcodeExecutor thread pool — non-blocking for the upload request
    @Async("transcodeExecutor")
    public void transcodeToHls(Path rawFilePath, UUID songId, String storagePath) {
        log.info("Starting HLS transcode for song: {}", songId);

        // Mark as processing
        songService.updateSongStatus(songId, Song.UploadStatus.PROCESSING);

        Path hlsOutputDir = rawFilePath.getParent().resolve("hls_" + songId);

        try {
            // Create temp output dir
            Files.createDirectories(hlsOutputDir);

            Path m3u8Path = hlsOutputDir.resolve("index.m3u8");

            // Build FFmpeg command
            // -codec:a aac        — encode audio as AAC (HLS standard)
            // -b:a 128k           — 128kbps bitrate
            // -hls_time 10        — 10 second segments
            // -hls_playlist_type vod — video on demand (not live)
            // -hls_segment_filename — segment naming pattern
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", rawFilePath.toString(),
                    "-codec:a", "aac",
                    "-b:a", "128k",
                    "-hls_time", "10",
                    "-hls_playlist_type", "vod",
                    "-hls_segment_filename",
                    hlsOutputDir.resolve("segment_%03d.ts").toString(),
                    "-y",           // overwrite if exists
                    m3u8Path.toString()
            );

            pb.redirectErrorStream(true);
            pb.redirectOutput(hlsOutputDir.resolve("ffmpeg.log").toFile());

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.error("FFmpeg failed for song {} with exit code {}", songId, exitCode);
                songService.updateSongStatus(songId, Song.UploadStatus.FAILED);
                return;
            }

            log.info("FFmpeg transcode complete for song: {}", songId);

            // Upload all HLS segments + m3u8 to S3
            String hlsS3Prefix = storagePath + "/hls";
            s3StorageService.uploadDirectory(hlsOutputDir, hlsS3Prefix);

            // Build the public HLS URL
            String hlsUrl = s3StorageService.uploadFile(
                    m3u8Path,
                    hlsS3Prefix + "/index.m3u8",
                    "application/vnd.apple.mpegurl"
            );

            // Mark song as ready with HLS URL
            songService.markSongReady(songId, Song.UploadStatus.READY, hlsUrl, storagePath);

            log.info("Song {} is now READY at {}", songId, hlsUrl);

        } catch (IOException | InterruptedException e) {
            log.error("Transcode failed for song: {}", songId, e);
            songService.updateSongStatus(songId, Song.UploadStatus.FAILED);
            Thread.currentThread().interrupt();

        } finally {
            // Clean up local temp files
            deleteDirectory(hlsOutputDir);
            deleteFile(rawFilePath);
        }
    }

    private void deleteDirectory(Path dir) {
        try {
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(java.util.Comparator.reverseOrder())
                        .forEach(p -> {
                            try { Files.delete(p); }
                            catch (IOException e) { log.warn("Could not delete temp file: {}", p); }
                        });
            }
        } catch (IOException e) {
            log.warn("Could not clean up temp directory: {}", dir);
        }
    }

    private void deleteFile(Path file) {
        try { Files.deleteIfExists(file); }
        catch (IOException e) { log.warn("Could not delete temp file: {}", file); }
    }
}