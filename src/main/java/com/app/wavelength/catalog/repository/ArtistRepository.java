package com.app.wavelength.catalog.repository;

import com.app.wavelength.catalog.domain.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    Optional<Artist> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY a.name")
    Page<Artist> searchByName(String query, Pageable pageable);
}