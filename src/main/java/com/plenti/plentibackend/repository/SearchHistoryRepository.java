package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(Long userId);
    
    @Query("SELECT sh.searchTerm, COUNT(sh) as count FROM SearchHistory sh " +
           "WHERE sh.searchedAt >= :since " +
           "GROUP BY sh.searchTerm " +
           "ORDER BY count DESC")
    List<Object[]> findPopularSearchTerms(LocalDateTime since);
}
