package com.app.wavelength.social.repository;

import org.springframework.stereotype.Repository;
import com.app.wavelength.social.domain.PlayHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlayHistoryRepository extends JpaRepository<PlayHistory, UUID> {

    //Recently played - distinct tracks ordered by play time
    @Query("SELECT ph FROM PlayHistory ph WHERE ph.userId = :userId " +
           "ORDER BY ph.playedAt DESC")
    Page<PlayHistory> findRecentPlaysByUserId(UUID userId, Pageable pageable);

    //How many times a song was played in a time window — used by TrendingService
    @Query("SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.songId = :songId " +
           "AND ph.playedAt >= :since")
    long countPlaysSince(UUID songId, Instant since);

    // Top played songs in a window — used by TrendingService
    @Query("SELECT ph.songId, COUNT(ph) as playCount FROM PlayHistory ph " +
           "WHERE ph.playedAt >= :since " +
           "GROUP BY ph.songId ORDER BY playCount DESC")
    List<Object[]> findTopSongsSince(Instant since, Pageable pageable);
}
