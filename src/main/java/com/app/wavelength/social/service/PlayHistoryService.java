package com.app.wavelength.social.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.wavelength.catalog.service.SongService;
import com.app.wavelength.social.domain.PlayHistory;
import com.app.wavelength.social.dto.PlayAnalyticsRequest;
import com.app.wavelength.social.repository.PlayHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayHistoryService {
    private final PlayHistoryRepository playHistoryRepository;
    private final SongService songService;

    @Transactional
    public void recordPlay(UUID userID, UUID songID, PlayAnalyticsRequest request) {
        // Implementation for recording play history
        PlayHistory history = new PlayHistory();
        history.setUserID(userID);
        history.setSongID(songID);
        history.setDurationPlayed(request.durationPlayed());
        history.setSignedURLused(request.signedUrlUsed());
        history.setAnalyticsData(request.analyticsData());
        playHistoryRepository.save(history);

        // Increment play count on the song — calls catalog module via service interface
        // Never touches SongRepository directly from social module
        songService.incrementPlayCount(songID);

        log.info("Recorded play event — user: {}, song: {}, duration: {}s",
                userID, songID, request.durationPlayed());
    }

    @Transactional(readOnly = true)
    public List<UUID> getRecentlyPlayedSongIds(UUID userId, int limit) {
        return playHistoryRepository
                .findRecentPlaysByUserId(userId, PageRequest.of(0, limit))
                .stream()
                .map(PlayHistory::getSongID)
                .distinct()
                .toList();
    }
}
