package com.app.wavelength.catalog.dto;

import com.app.wavelength.catalog.domain.Album;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AlbumResponse(
        UUID id,
        String title,
        UUID artistId,
        String artistName,
        String coverUrl,
        LocalDate releaseDate,
        String type,
        int trackCount,
        // Included when ?include_songs=true
        List<SongResponse> songs
) {
    public static AlbumResponse from(Album album, String artistName,
                                     int trackCount, List<SongResponse> songs) {
        return new AlbumResponse(
                album.getId(),
                album.getTitle(),
                album.getArtistID(),
                artistName,
                album.getCoverUrl(),
                album.getReleaseDate(),
                album.getType().name(),
                trackCount,
                songs
        );
    }
}