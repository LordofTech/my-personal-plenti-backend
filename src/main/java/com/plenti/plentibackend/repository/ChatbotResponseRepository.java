package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.ChatbotResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotResponseRepository extends JpaRepository<ChatbotResponse, Long> {
    
    List<ChatbotResponse> findByIsActiveTrueOrderByPriorityDescUsageCountDesc();
    
    List<ChatbotResponse> findByCategoryAndIsActiveTrueOrderByPriorityDesc(String category);
    
    List<ChatbotResponse> findByIntentAndIsActiveTrueOrderByPriorityDesc(String intent);
    
    @Query("SELECT c FROM ChatbotResponse c WHERE c.isActive = true AND " +
           "(LOWER(c.questionPattern) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.priority DESC, c.usageCount DESC")
    List<ChatbotResponse> searchByKeyword(@Param("keyword") String keyword);
}
