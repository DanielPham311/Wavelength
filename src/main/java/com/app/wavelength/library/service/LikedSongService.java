package com.app.wavelength.library.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.wavelength.catalog.dto.SongResponse;
import com.app.wavelength.catalog.service.SongService;
import com.app.wavelength.library.domain.LikedSong;
import com.app.wavelength.library.repository.LikedSongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikedSongService {
    private final LikedSongRepository likedSongRepository;
    private final SongService songService; 

    //Toggle like/unlike a song
    public boolean toggleLike(UUID userId, UUID songId) {
        if (likedSongRepository.existsByUserIdAndSongId(userId, songId)) {
            likedSongRepository.deleteByUserIdAndSongId(userId, songId);
            return false; // Unliked
        }

        LikedSong likedSong = LikedSong.builder()
                .userId(userId)
                .songId(songId)
                .build();
        likedSongRepository.save(likedSong);
        return true; // Liked
    }

    @Transactional(readOnly = true)
    public List<SongResponse> getLikedSongs(UUID userId, int limit, int offset) {
        Page<LikedSong> page = likedSongRepository
                .findByUserIdOrderByLikedAtDesc(userId,
                        PageRequest.of(offset / limit, limit));

        return page.getContent()
                .stream()
                .map(ls -> songService.getSongById(ls.getSongId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isLiked(UUID userId, UUID songId) {
        return likedSongRepository.existsByUserIdAndSongId(userId, songId);
    }

    @Transactional(readOnly = true)
    public long getLikedSongsCount(UUID userId) {
        return likedSongRepository.countByUserId(userId);
    }
}
