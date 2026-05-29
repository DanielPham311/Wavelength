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
    Page<LikedSong> findByUserIDOrderByLikedAtDesc(UUID userID, Pageable pageable); 

    Optional<LikedSong> findByUserIDAndSongID(UUID userID, UUID songID);  

    boolean existsByUserIDAndSongID(UUID userID, UUID songID);

    @Modifying
    @Query("DELETE FROM LikedSong ls WHERE ls.userId = :userId AND ls.songId = :songId")
    void deleteByUserIdAndSongId(UUID userId, UUID songId);
}
