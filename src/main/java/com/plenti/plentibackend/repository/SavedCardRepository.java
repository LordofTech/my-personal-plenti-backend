package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.SavedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedCardRepository extends JpaRepository<SavedCard, Long> {
    List<SavedCard> findByUserId(Long userId);
    Optional<SavedCard> findByUserIdAndIsDefaultTrue(Long userId);
}
