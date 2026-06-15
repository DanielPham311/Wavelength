package com.app.wavelength.library.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.wavelength.library.domain.LikedSong;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong,UUID> {
    Page<LikedSong> findByUserIdOrderByLikedAtDesc(UUID userId, Pageable pageable);

    Optional<LikedSong> findByUserIdAndSongId(UUID userId, UUID songId);

    boolean existsByUserIdAndSongId(UUID userId, UUID songId);

    @Modifying
    @Query("DELETE FROM LikedSong ls WHERE ls.userId = :userId AND ls.songId = :songId")
    void deleteByUserIdAndSongId(UUID userId, UUID songId);

    long countByUserId(UUID userId);
}
