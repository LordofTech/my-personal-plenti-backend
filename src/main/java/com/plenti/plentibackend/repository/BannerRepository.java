package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Banner> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByDisplayOrderAsc(
        LocalDateTime start, LocalDateTime end);
}
