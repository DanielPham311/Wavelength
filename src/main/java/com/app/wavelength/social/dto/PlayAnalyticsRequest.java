package com.app.wavelength.social.dto;

import java.util.Map;

public record PlayAnalyticsRequest(
        // Seconds the user listened before stopping or skipping
        Integer durationPlayed,

        // The signed HLS URL that was used — for debugging
        String signedUrlUsed,

        // Freeform analytics from the app
        // e.g. { "source": "search", "shuffle": false, "quality": "128k" }
        Map<String, Object> analyticsData
) {}