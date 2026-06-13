package com.app.wavelength.catalog.dto;

import java.util.UUID;

import com.app.wavelength.catalog.domain.Artist;

public record ArtistResponse(
    UUID id,
    String name,
    String bio,
    String avatarUrl,
    Boolean verified,
    int songCount,
    int albumCount
) {
    public static ArtistResponse from(Artist artist, int songCount, int albumCount) {
        return new ArtistResponse(artist.getId(), artist.getName(), artist.getBio(), artist.getAvatarUrl(),
                artist.getVerified(), songCount, albumCount);
    }
}
