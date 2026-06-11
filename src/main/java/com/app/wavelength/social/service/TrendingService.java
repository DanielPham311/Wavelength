package com.app.wavelength.social.service;

import com.app.wavelength.catalog.service.SongService;
import com.app.wavelength.social.repository.PlayHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingService {

    private final PlayHistoryRepository playHistoryRepository;
    private final SongService songService;  // catalog module interface — no direct repo access

    // Runs every hour — recalculates trending scores for top 200 songs
    // Trending score = weighted play count:
    //   plays in last 24h  × 3.0
    //   plays in last 7d   × 1.5
    //   plays in last 30d  × 0.5
    @Scheduled(fixedRate = 3_600_000)  // every 1 hour in ms
    @Transactional
    public void recalculateTrendingScores() {
        log.info("Recalculating trending scores...");

        Instant now      = Instant.now();
        Instant last24h  = now.minus(1,  ChronoUnit.DAYS);
        Instant last7d   = now.minus(7,  ChronoUnit.DAYS);
        Instant last30d  = now.minus(30, ChronoUnit.DAYS);

        // Get top 200 songs by raw play count in last 30 days
        List<Object[]> topSongs = playHistoryRepository
                .findTopSongsSince(last30d, PageRequest.of(0, 200));

        topSongs.forEach(row -> {
            UUID songId = (UUID) row[0];

            long plays24h = playHistoryRepository.countPlaysSince(songId, last24h);
            long plays7d  = playHistoryRepository.countPlaysSince(songId, last7d);
            long plays30d = playHistoryRepository.countPlaysSince(songId, last30d);

            float score = (plays24h * 3.0f) + (plays7d * 1.5f) + (plays30d * 0.5f);

            // Calls catalog service — social module never writes to songs table directly
            songService.updateTrendingScore(songId, score);
        });

        log.info("Trending scores updated for {} songs", topSongs.size());
    }
}