package com.app.wavelength.library.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.service.SongService;
import com.app.wavelength.library.domain.Playlist;
import com.app.wavelength.library.domain.PlaylistSong;
import com.app.wavelength.library.dto.AddSongRequest;
import com.app.wavelength.library.dto.CreatePlaylistRequest;
import com.app.wavelength.library.dto.PlaylistResponse;
import com.app.wavelength.library.dto.UpdatePlaylistRequest;
import com.app.wavelength.library.repository.PlaylistRepository;
import com.app.wavelength.library.repository.PlaylistSongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongService songService;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Playlist findAndAuthorize(UUID playlistId, UUID userId) {
        Playlist playlist = playlistRepository.findByIdAndDeletedAtIsNull(playlistId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Playlist not found: " + playlistId));

        // Public playlists are readable by anyone
        // Private playlists only by owner
        if (!playlist.getIsPublic() && !playlist.getOwnerId().equals(userId)) {
            throw new SecurityException("You do not have access to this playlist");
        }

        return playlist;
    }

    private void shiftPositionsDown(UUID playlistId, int fromPosition, int shiftBy) {
        playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId)
                .stream()
                .filter(ps -> ps.getPosition() >= fromPosition)
                .forEach(ps -> {
                    ps.setPosition(ps.getPosition() + shiftBy);
                    playlistSongRepository.save(ps);
                });
    }

    //Create
    @Transactional
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request, UUID ownerId) {
        Playlist playlist = Playlist.builder()
                .ownerId(ownerId)
                .name(request.name().trim())
                .description(request.description())
                .coverUrl(request.coverUrl())
                .isPublic(request.isPublic())
                .build();

        playlistRepository.save(playlist);
        return PlaylistResponse.from(playlist, 0, List.of());
    }

    //Get single playlist
    @Transactional(readOnly = true)
    public PlaylistResponse getById(UUID playlistId, boolean includeSongs, UUID requesterID) {
        Playlist playlist = findAndAuthorize(playlistId, requesterID);

        List<SongResponse> songs = List.of();
        if (includeSongs) {
            songs = playlistSongRepository
                    .findByPlaylistIdOrderByPositionAsc(playlistId)
                    .stream()
                    .map(ps -> songService.getSongById(ps.getSongId()))
                    .toList();
        }
        int trackCount = playlistSongRepository.countByPlaylistId(playlistId);
        return PlaylistResponse.from(playlist, trackCount, songs);
    }

    // Get all playlists for a user (excludes soft-deleted) — legacy full list
    @Transactional(readOnly = true)
    public List<PlaylistResponse> getUsersPlaylists(UUID userId) {
        return playlistRepository.findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(userId)
                .stream()
                .map(p -> PlaylistResponse.from(p,
                        playlistSongRepository.countByPlaylistId(p.getId()),
                        List.of()))
                .toList();
    }

    // Get paginated playlists for a user
    @Transactional(readOnly = true)
    public com.app.wavelength.common.dto.PaginatedResponse<PlaylistResponse> getUsersPlaylistsPaginated(UUID userId, int limit, int offset) {
        org.springframework.data.domain.Pageable page = org.springframework.data.domain.PageRequest.of(offset / limit, limit);
        long total = playlistRepository.countByOwnerIdAndDeletedAtIsNull(userId);
        List<PlaylistResponse> content = playlistRepository.findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(userId, page)
                .stream()
                .map(p -> PlaylistResponse.from(p,
                        playlistSongRepository.countByPlaylistId(p.getId()),
                        List.of()))
                .toList();
        return new com.app.wavelength.common.dto.PaginatedResponse<>(content, total, limit, offset);
    }

    // Add songs (bulk, with position handling)
    @Transactional
    public PlaylistResponse addSongsToPlaylist(UUID playlistId, AddSongRequest request, UUID userId) {
        Playlist playlist = findAndAuthorize(playlistId, userId);

        int startPosition = request.position() != null
                ? request.position()
                : playlistSongRepository.countByPlaylistId(playlistId);

        //Shift songs down if inserting in the middle to make space
        if (request.position() != null) {
            shiftPositionsDown(playlistId, startPosition, request.songIds().size());
        }

        AtomicInteger positionCounter = new AtomicInteger(startPosition);
        request.songIds().forEach(songId -> {
            //Skip duplicates
            if (!playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, songId)) {
                PlaylistSong ps = new PlaylistSong();
                ps.setPlaylistId(playlistId);
                ps.setSongId(songId);
                ps.setPosition(positionCounter.getAndIncrement());
                playlistSongRepository.save(ps);
            }
        });
        int trackCount = playlistSongRepository.countByPlaylistId(playlistId);
        return PlaylistResponse.from(playlist, trackCount, List.of());
    }

    //Remove song
    @Transactional
    public void removeSong(UUID playlistId, UUID songId, UUID userId) {
        findAndAuthorize(playlistId, userId);
        playlistSongRepository.deleteByPlaylistIdAndSongId(playlistId, songId);
    }

    //Update playlist
    @Transactional
    public PlaylistResponse updatePlaylist(UUID playlistId, UpdatePlaylistRequest request, UUID userId) {
        Playlist playlist = findAndAuthorize(playlistId, userId);

        if (request.name() != null && !request.name().isBlank()) {
            playlist.setName(request.name().trim());
        }
        if (request.description() != null) {
            playlist.setDescription(request.description());
        }
        if (request.isPublic() != null) {
            playlist.setIsPublic(request.isPublic());
        }

        playlistRepository.save(playlist);
        int trackCount = playlistSongRepository.countByPlaylistId(playlistId);
        return PlaylistResponse.from(playlist, trackCount, List.of());
    }

    //Delete playlist (soft delete)
    @Transactional
    public void delete(UUID playlistId, UUID userId) {
        Playlist playlist = findAndAuthorize(playlistId, userId);
        playlist.softDelete();
        playlistRepository.save(playlist);
    }
}
