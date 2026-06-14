package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.SearchResponse;
import com.app.wavelength.catalog.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Full-text search across songs, artists, and albums")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search", description = "Search songs, artists, albums by query string. Filter by type.")
    @ApiResponse(responseCode = "200", description = "Search results")
    public ResponseEntity<SearchResponse> search(
            @Parameter(description = "Search query", required = true) @RequestParam String q,
            @Parameter(description = "Filter: song, artist, album (comma-separated)") @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(searchService.search(q, type,
                Math.min(limit, 50), offset));
    }
}
