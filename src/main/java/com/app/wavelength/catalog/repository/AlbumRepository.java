package com.app.wavelength.catalog.repository;

import com.app.wavelength.catalog.domain.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    List<Album> findByArtistIdOrderByReleaseDateDesc(UUID artistId);

    @Query("SELECT a FROM Album a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY a.title")
    Page<Album> searchByTitle(String query, Pageable pageable);
}