package com.plenti.plentibackend.service;

import com.plenti.plentibackend.entity.SearchHistory;
import com.plenti.plentibackend.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for tracking and analyzing search behavior
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchAnalyticsService {

    private final SearchHistoryRepository searchHistoryRepository;

    /**
     * Track a search query
     */
    public void trackSearch(Long userId, String searchTerm, int resultCount) {
        log.info("Tracking search: userId={}, term={}, results={}", userId, searchTerm, resultCount);
        
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserId(userId);
        searchHistory.setSearchTerm(searchTerm.toLowerCase().trim());
        searchHistory.setResultCount(resultCount);
        
        searchHistoryRepository.save(searchHistory);
    }

    /**
     * Get user's search history
     */
    public List<SearchHistory> getUserSearchHistory(Long userId) {
        log.info("Fetching search history for user: {}", userId);
        return searchHistoryRepository.findByUserIdOrderBySearchedAtDesc(userId);
    }

    /**
     * Get popular search terms
     */
    public List<Map<String, Object>> getPopularSearchTerms(int days) {
        log.info("Fetching popular search terms for last {} days", days);
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> results = searchHistoryRepository.findPopularSearchTerms(since);
        
        return results.stream()
                .limit(20) // Top 20 search terms
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("searchTerm", result[0]);
                    map.put("count", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get recent unique searches for a user (for autocomplete)
     */
    public List<String> getRecentSearches(Long userId, int limit) {
        log.info("Fetching recent searches for user: {}", userId);
        return searchHistoryRepository.findByUserIdOrderBySearchedAtDesc(userId)
                .stream()
                .map(SearchHistory::getSearchTerm)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
