package com.app.wavelength.catalog.service;

import com.app.wavelength.catalog.domain.Artist;
import com.app.wavelength.catalog.dto.ArtistResponse;
import com.app.wavelength.catalog.repository.AlbumRepository;
import com.app.wavelength.catalog.repository.ArtistRepository;
import com.app.wavelength.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

    @Transactional(readOnly = true)
    public ArtistResponse getArtistById(UUID artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Artist not found: " + artistId));

        int songCount  = songRepository.findByArtistIdOrderByCreatedAtDesc(artistId).size();
        int albumCount = albumRepository.findByArtistIdOrderByReleaseDateDesc(artistId).size();

        return ArtistResponse.from(artist, songCount, albumCount);
    }

    // Promote a user to artist — called when user registers as an artist
    @Transactional
    public ArtistResponse createArtistProfile(UUID userId, String name, String bio) {
        if (artistRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Artist profile already exists for this user");
        }

        Artist artist = Artist.builder()
                .userId(userId)
                .name(name)
                .bio(bio)
                .verified(false)
                .build();

        artistRepository.save(artist);
        return ArtistResponse.from(artist, 0, 0);
    }
}