package com.app.wavelength.catalog.repository;

import com.app.wavelength.catalog.domain.Song;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    List<Song> findByArtistIdOrderByCreatedAtDesc(UUID artistId);

    @Query("SELECT s FROM Song s WHERE s.artistId = :artistId ORDER BY s.createdAt DESC")
    Page<Song> findByArtistIdOrderByCreatedAtDesc(UUID artistId, Pageable pageable);

    List<Song> findByAlbumIdOrderByCreatedAtAsc(UUID albumId);

    // Trending — ordered by trending_score descending, optionally filtered by genre
    @Query("SELECT s FROM Song s WHERE s.uploadStatus = 'READY' " +
           "ORDER BY s.trendingScore DESC")
    Page<Song> findTrending(Pageable pageable);

    // Full-text search across title
    @Query("SELECT s FROM Song s WHERE s.uploadStatus = 'READY' " +
           "AND LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY s.trendingScore DESC")
    Page<Song> searchByTitle(String query, Pageable pageable);

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

    @Modifying
    @Query("UPDATE Song s SET s.trendingScore = :score WHERE s.id = :songId")
    void updateTrendingScore(UUID songId, Float score);

    // Count total matching songs for pagination metadata
    @Query("SELECT COUNT(s) FROM Song s WHERE s.uploadStatus = 'READY' " +
           "AND LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    long countByTitle(String query);
}