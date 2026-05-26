package com.app.wavelength.catalog.repository;

import com.app.wavelength.catalog.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    Optional<Artist> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}