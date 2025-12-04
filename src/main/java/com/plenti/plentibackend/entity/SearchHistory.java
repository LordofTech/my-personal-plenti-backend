package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing search history for analytics
 */
@Entity
@Table(name = "search_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String searchTerm;

    private Integer resultCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime searchedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        searchedAt = LocalDateTime.now();
        if (resultCount == null) {
            resultCount = 0;
        }
    }
}
