package com.app.wavelength.library.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.library.domain.Playlist;

public record PlaylistResponse(
    UUID id,
    String name,
    String desription,
    String coverURL,
    boolean isPublic,
    UUID ownerID,
    int trackCount,
    List<SongResponse> songs,
    Instant createdAt,
    Instant updatedAt
) {
    public static PlaylistResponse from(Playlist playlist,
                                        int trackCount,
                                        List<SongResponse> songs) {
        return new PlaylistResponse(
                playlist.getID(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getCoverURL(),
                playlist.getIsPublic(),
                playlist.getOwnerID(),
                trackCount,
                songs,
                playlist.getCreatedAt(),
                playlist.getUpdatedAt()
        );
}
}
