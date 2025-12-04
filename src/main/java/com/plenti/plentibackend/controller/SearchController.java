package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.entity.SearchHistory;
import com.plenti.plentibackend.service.SearchAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for search analytics
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search analytics and history")
public class SearchController {

    private final SearchAnalyticsService searchAnalyticsService;

    @PostMapping("/track")
    @Operation(summary = "Track a search query")
    public ResponseEntity<Void> trackSearch(@RequestBody Map<String, Object> searchData) {
        Long userId = searchData.get("userId") != null ? 
            Long.valueOf(searchData.get("userId").toString()) : null;
        String searchTerm = searchData.get("searchTerm").toString();
        int resultCount = Integer.parseInt(searchData.get("resultCount").toString());
        
        searchAnalyticsService.trackSearch(userId, searchTerm, resultCount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get user's search history")
    public ResponseEntity<List<SearchHistory>> getUserSearchHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(searchAnalyticsService.getUserSearchHistory(userId));
    }

    @GetMapping("/recent/{userId}")
    @Operation(summary = "Get recent searches for autocomplete")
    public ResponseEntity<List<String>> getRecentSearches(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchAnalyticsService.getRecentSearches(userId, limit));
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular search terms")
    public ResponseEntity<List<Map<String, Object>>> getPopularSearchTerms(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(searchAnalyticsService.getPopularSearchTerms(days));
    }
}
