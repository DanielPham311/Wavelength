package com.app.wavelength.storage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.app.wavelength.catalog.domain.Song;
import com.app.wavelength.catalog.repository.SongRepository;
import com.app.wavelength.storage.dto.UploadResponse;
import com.app.wavelength.storage.dto.UploadRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {
    private final S3StorageService s3StorageService;
    private final TranscodeService transcodeService;
    private final SongRepository songRepository;

    private static final Set<String> ALLOWED_TYPES = Set.of("audio/mpeg", "audio/wav", "audio/flac", "audio/aac", "audio/ogg");

    private static final long MAX_FILE_SIZE = 500L * 1024 * 1024; // 500MB

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Audio file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the 500MB limit");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + file.getContentType() +
                    ". Allowed: mp3, wav, flac, aac, ogg");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "mp3";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public UploadResponse upload(UploadRequest request, UUID artistID) {
        MultipartFile file = request.file();

        validateFile(file);

        String fileExtension = getExtension(file.getOriginalFilename());
        UUID songId = UUID.randomUUID();

        // Storage path: artist/{artistId}/album/{albumId}/{songId}
        String albumSegment = request.albumId() != null
                ? request.albumId().toString()
                : "singles";
        String storagePath = String.format("artist/%s/album/%s/%s",
                artistID, albumSegment, songId);

        // ── Save to DB as PENDING ──────────────────────────────────────────
        Song song = new Song();
        song.setId(songId);
        song.setArtistId(artistID);
        song.setAlbumId(request.albumId());
        song.setTitle(request.title().trim());
        song.setFileFormat(fileExtension);
        song.setCoverUrl(request.coverUrl());
        song.setStoragePath(storagePath);
        song.setUploadStatus(Song.UploadStatus.PENDING);
        songRepository.save(song);

        // ── Save raw file to temp disk ─────────────────────────────────────
        Path tempDir  = Path.of(System.getProperty("java.io.tmpdir"), "wavelength", songId.toString());
        Path tempFile = tempDir.resolve("raw." + fileExtension);

        try {
            Files.createDirectories(tempDir);
            file.transferTo(tempFile);
        } catch (IOException e) {
            log.error("Failed to save upload to temp disk for song {}", songId, e);
            songRepository.updateStatus(songId, Song.UploadStatus.FAILED);
            throw new RuntimeException("Upload failed — could not save file", e);
        }

        // ── Upload raw file to S3 (private) ───────────────────────────────
        String rawS3Key = storagePath + "/raw." + fileExtension;
        s3StorageService.uploadFile(tempFile, rawS3Key, file.getContentType());

        // Update raw URL on the song record
        Song savedSong = songRepository.findById(songId).orElseThrow();
        savedSong.setRawFileUrl(rawS3Key);
        songRepository.save(savedSong);

        // ── Trigger async FFmpeg transcode ────────────────────────────────
        // Returns immediately — transcode runs on background thread pool
        transcodeService.transcodeToHls(tempFile, songId, storagePath);

        log.info("Upload accepted for song {} — transcoding in background", songId);

        return new UploadResponse(
                songId,
                request.title(),
                "processing",
                "Your track has been uploaded and is being processed. It will be available shortly."
        );
    }
}
