package com.app.wavelength.library.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.wavelength.library.domain.PlaylistSong;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, UUID>{

    List<PlaylistSong> findByPlaylistIDOrderByPositionAsc(UUID playlistID);

    Optional<PlaylistSong> findByPlaylistIDAndSongID(UUID playlistID, UUID songID);

    boolean existsByPlaylistIDAndSongID(UUID playlistID, UUID songID);

    @Modifying
    @Query("DELETE FROM PlaylistSong ps WHERE ps.playlistId = :playlistId " +
           "AND ps.songId = :songId")
    void deleteByPlaylistIdAndSongId(UUID playlistId, UUID songId);

    // Get max position in a playlist — used when appending songs
    @Query("SELECT COALESCE(MAX(ps.position), 0) FROM PlaylistSong ps " +
           "WHERE ps.playlistId = :playlistId")
    int findMaxPosition(UUID playlistId);

    int countByPlaylistId(UUID playlistId);
}
