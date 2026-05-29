package com.app.wavelength.library.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.wavelength.library.domain.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findByOwnerIdOrderByUpdatedAtDesc(UUID ownerId);

    List<Playlist> findByOwnerIdAndIsPublicTrueOrderByUpdatedAtDesc(UUID ownerId);
}
