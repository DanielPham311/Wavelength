package com.app.wavelength.catalog.service;

import com.app.wavelength.catalog.domain.Artist;
import com.app.wavelength.catalog.dto.AlbumResponse;
import com.app.wavelength.catalog.dto.ArtistResponse;
import com.app.wavelength.catalog.dto.SearchResponse;
import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.repository.AlbumRepository;
import com.app.wavelength.catalog.repository.ArtistRepository;
import com.app.wavelength.catalog.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    private static final Set<String> VALID_TYPES = Set.of("song", "artist", "album");

    @Transactional(readOnly = true)
    public SearchResponse search(String query, String type, int limit, int offset) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        String q = query.trim();
        boolean searchAll = type == null || type.isBlank();

        List<SongResponse> songs = List.of();
        List<ArtistResponse> artists = List.of();
        List<AlbumResponse> albums = List.of();

        PageRequest page = PageRequest.of(offset / limit, limit);

        if (searchAll || type.contains("song")) {
            songs = songRepository.searchByTitle(q, page)
                    .stream()
                    .map(song -> {
                        String artistName = artistRepository.findById(song.getArtistId())
                                .map(Artist::getName).orElse("Unknown Artist");
                        return SongResponse.from(song, artistName, null);
                    })
                    .toList();
        }

        if (searchAll || type.contains("artist")) {
            artists = artistRepository.searchByName(q, page)
                    .stream()
                    .map(a -> ArtistResponse.from(a, 0, 0))
                    .toList();
        }

        if (searchAll || type.contains("album")) {
            albums = albumRepository.searchByTitle(q, page)
                    .stream()
                    .map(a -> AlbumResponse.from(a, null, 0, List.of()))
                    .toList();
        }

        return new SearchResponse(
                songs, artists, albums,
                songs.size(), artists.size(), albums.size(),
                limit, offset
        );
    }
}
