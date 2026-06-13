package com.app.wavelength.catalog.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.wavelength.catalog.domain.Album;
import com.app.wavelength.catalog.domain.Artist;
import com.app.wavelength.catalog.domain.Song;
import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.repository.AlbumRepository;
import com.app.wavelength.catalog.repository.ArtistRepository;
import com.app.wavelength.catalog.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    @Transactional(readOnly = true)
    public SongResponse getSongById(UUID songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found: " + songId));

        String artistName = artistRepository.findById(song.getArtistId())
                .map(Artist::getName).orElse("Unknown Artist");

        String albumTitle = song.getAlbumId() != null
                ? albumRepository.findById(song.getAlbumId())
                        .map(Album::getTitle).orElse(null)
                : null;

        return SongResponse.from(song, artistName, albumTitle);
    }

    @Transactional(readOnly = true)
    public List<SongResponse> getTrending(int limit) {
        return songRepository.findTrending(PageRequest.of(0, limit))
                .stream()
                .map(song -> {
                    String artistName = artistRepository.findById(song.getArtistId())
                            .map(Artist::getName).orElse("Unknown Artist");
                    return SongResponse.from(song, artistName, null);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SongResponse> getSongsByArtist(UUID artistId, int limit) {
        return songRepository.findByArtistIdOrderByCreatedAtDesc(artistId, PageRequest.of(0, limit))
                .stream()
                .map(song -> SongResponse.from(song, null, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SongResponse> getSongsByAlbum(UUID albumId) {
        return songRepository.findByAlbumIdOrderByCreatedAtAsc(albumId)
                .stream()
                .map(song -> SongResponse.from(song, null, null))
                .toList();
    }

    // Called by social module's TrendingService — no direct repo access from social
    @Transactional
    public void updateTrendingScore(UUID songId, Float score) {
        songRepository.updateTrendingScore(songId, score);
    }

    // Called by social module after play event recorded
    @Transactional
    public void incrementPlayCount(UUID songId) {
        songRepository.incrementPlayCount(songId);
    }

    // ── Methods for storage module (no direct SongRepository access) ──

    @Transactional
    public void saveSong(Song song) {
        songRepository.save(song);
    }

    @Transactional
    public void updateSongStatus(UUID songId, Song.UploadStatus status) {
        songRepository.updateStatus(songId, status);
    }

    @Transactional
    public void markSongReady(UUID songId, Song.UploadStatus status, String hlsUrl, String storagePath) {
        songRepository.markReady(songId, status, hlsUrl, storagePath);
    }

    @Transactional
    public void updateRawFileUrl(UUID songId, String rawFileUrl) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found: " + songId));
        song.setRawFileUrl(rawFileUrl);
        songRepository.save(song);
    }

    // ── Method for streaming module (no direct SongRepository access) ──

    @Transactional(readOnly = true)
    public Song findSongById(UUID songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found: " + songId));
    }
}
