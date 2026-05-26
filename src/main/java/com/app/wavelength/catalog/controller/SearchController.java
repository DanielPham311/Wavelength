package com.app.wavelength.catalog.controller;

import com.app.wavelength.catalog.dto.SearchResponse;
import com.app.wavelength.catalog.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    // GET /api/v1/search?q=night&type=song,artist&limit=20&offset=0
    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam String q,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(searchService.search(q, type,
                Math.min(limit, 50), offset));
    }
}