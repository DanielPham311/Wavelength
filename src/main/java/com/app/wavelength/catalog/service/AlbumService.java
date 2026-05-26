package com.app.wavelength.catalog.service;

import com.app.wavelength.catalog.domain.Album;
import com.app.wavelength.catalog.domain.Artist;
import com.app.wavelength.catalog.dto.AlbumResponse;
import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.repository.AlbumRepository;
import com.app.wavelength.catalog.repository.ArtistRepository;
import com.app.wavelength.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    @Transactional(readOnly = true)
    public AlbumResponse getAlbumById(UUID albumId, boolean includeSongs) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Album not found: " + albumId));

        String artistName = artistRepository.findById(album.getArtistID())
                .map(Artist::getName).orElse("Unknown Artist");

        List<SongResponse> songs = includeSongs
                ? songRepository.findByAlbumIdOrderByCreatedAtAsc(albumId)
                        .stream()
                        .map(song -> SongResponse.from(song, artistName, album.getTitle()))
                        .toList()
                : List.of();

        int trackCount = includeSongs
                ? songs.size()
                : songRepository.findByAlbumIdOrderByCreatedAtAsc(albumId).size();

        return AlbumResponse.from(album, artistName, trackCount, songs);
    }

    @Transactional(readOnly = true)
    public List<AlbumResponse> getAlbumsByArtist(UUID artistId) {
        String artistName = artistRepository.findById(artistId)
                .map(Artist::getName).orElse("Unknown Artist");

        return albumRepository.findByArtistIdOrderByReleaseDateDesc(artistId)
                .stream()
                .map(album -> AlbumResponse.from(album, artistName, 0, List.of()))
                .toList();
    }
}