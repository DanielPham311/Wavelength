package com.app.wavelength.library.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.wavelength.library.domain.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(UUID ownerId);

    List<Playlist> findByOwnerIdAndIsPublicTrueAndDeletedAtIsNullOrderByUpdatedAtDesc(UUID ownerId);

    List<Playlist> findByOwnerIdAndDeletedAtIsNotNullOrderByUpdatedAtDesc(UUID ownerId);

    Optional<Playlist> findByIdAndDeletedAtIsNull(UUID id);

    Page<Playlist> findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(UUID ownerId, Pageable pageable);

    long countByOwnerIdAndDeletedAtIsNull(UUID ownerId);
}
