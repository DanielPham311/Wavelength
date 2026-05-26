package com.app.wavelength.catalog.dto;

import java.util.List;

public record SearchResponse(
    List<SongResponse> songs,
    List<ArtistResponse> artists,
    List<AlbumResponse> albums,
    int totalSongs,
    int totalArtists,
    int totalAlbums,
    int limit,
    int offset
) {

}
