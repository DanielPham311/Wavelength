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
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Playlist not found: " + playlistId));

        // Public playlists are readable by anyone
        // Private playlists only by owner
        if (!playlist.getIsPublic() && !playlist.getOwnerID().equals(userId)) {
            throw new SecurityException("You do not have access to this playlist");
        }

        return playlist;
    }

    private void shiftPositionsDown(UUID playlistId, int fromPosition, int shiftBy) {
        playlistSongRepository.findByPlaylistIDOrderByPositionAsc(playlistId)
                .stream()
                .filter(ps -> ps.getPosition() >= fromPosition)
                .forEach(ps -> {
                    ps.setPosition(ps.getPosition() + shiftBy);
                    playlistSongRepository.save(ps);
                });
    }

    //Create
    @Transactional
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request, UUID ownerID) {
        Playlist playlist = Playlist.builder()
                .ownerID(ownerID)
                .name(request.name().trim())
                .description(request.description())
                .coverURL(request.coverURL())
                .isPublic(request.isPublic())
                .build();

        playlistRepository.save(playlist);
        return PlaylistResponse.from(playlist, 0, List.of());
    }

    //Get single playlist
    @Transactional(readOnly = true)
    public PlaylistResponse getByID(UUID playlistID, boolean includeSongs, UUID requesterID) {
        Playlist playlist = findAndAuthorize(playlistID, requesterID);

        List<SongResponse> songs = List.of();
        if (includeSongs) {
            songs = playlistSongRepository
                    .findByPlaylistIDOrderByPositionAsc(playlistID)
                    .stream()
                    .map(ps -> songService.getSongByID(ps.getSongID()))
                    .toList();
        }
        int trackCount = playlistSongRepository.countByPlaylistId(playlistID);
        return PlaylistResponse.from(playlist, trackCount, songs);
    }

    // Get all playlists for a user
    @Transactional(readOnly = true)
    public List<PlaylistResponse> getUsersPlaylists(UUID userID) {
        return playlistRepository.findByOwnerIdOrderByUpdatedAtDesc(userID)
                .stream()
                .map(p -> PlaylistResponse.from(p,
                        playlistSongRepository.countByPlaylistId(p.getID()),
                        List.of()))
                .toList();
    }

    // Add songs (bulk, with position handling)
    @Transactional
    public PlaylistResponse addSongsToPlaylist(UUID playlistID, AddSongRequest request, UUID userID) {
        Playlist playlist = findAndAuthorize(playlistID, userID);

        int startPosition = request.position() != null
                ? request.position()
                : playlistSongRepository.countByPlaylistId(playlistID);

        //Shift songs down if inserting in the middle to make space
        if (request.position() != null) {
            shiftPositionsDown(playlistID, startPosition, request.songIDs().size());
        }

        AtomicInteger positionCounter = new AtomicInteger(startPosition);
        request.songIDs().forEach(songID -> {
            //Skip duplicates
            if (!playlistSongRepository.existsByPlaylistIDAndSongID(playlistID, songID)) {
                PlaylistSong ps = new PlaylistSong();
                ps.setPlaylistID(playlistID);
                ps.setSongID(songID);
                ps.setPosition(positionCounter.getAndIncrement());
                playlistSongRepository.save(ps);
            }
        });
        int trackCount = playlistSongRepository.countByPlaylistId(playlistID);
        return PlaylistResponse.from(playlist, trackCount, List.of());
    }

    //Remove song
    @Transactional
    public void removeSong(UUID playlistId, UUID songId, UUID userId) {
        findAndAuthorize(playlistId, userId);
        playlistSongRepository.deleteByPlaylistIdAndSongId(playlistId, songId);
    }

    //Delete playlist
    @Transactional
    public void delete(UUID playlistId, UUID userId) {
        Playlist playlist = findAndAuthorize(playlistId, userId);
        playlistRepository.delete(playlist);
    }
}
