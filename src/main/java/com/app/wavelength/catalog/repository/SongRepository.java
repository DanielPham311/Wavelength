package com.app.wavelength.catalog.repository;

import com.app.wavelength.catalog.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    List<Song> findByArtistIdOrderByCreatedAtDesc(UUID artistId);

    @Modifying
    @Query("UPDATE Song s SET s.uploadStatus = :status, s.hlsUrl = :hlsUrl, " +
           "s.storagePath = :storagePath WHERE s.id = :songId")
    void markReady(UUID songId, Song.UploadStatus status, String hlsUrl, String storagePath);

    @Modifying
    @Query("UPDATE Song s SET s.uploadStatus = :status WHERE s.id = :songId")
    void updateStatus(UUID songId, Song.UploadStatus status);

    @Modifying
    @Query("UPDATE Song s SET s.playCount = s.playCount + 1 WHERE s.id = :songId")
    void incrementPlayCount(UUID songId);
}