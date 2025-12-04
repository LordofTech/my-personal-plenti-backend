package com.plenti.plentibackend.service;

import com.plenti.plentibackend.entity.Banner;
import com.plenti.plentibackend.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing promotional banners
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BannerService {

    private final BannerRepository bannerRepository;

    /**
     * Get all active banners
     */
    public List<Banner> getActiveBanners() {
        log.info("Fetching active banners");
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByDisplayOrderAsc(now, now);
    }

    /**
     * Get all banners (admin)
     */
    public List<Banner> getAllBanners() {
        log.info("Fetching all banners");
        return bannerRepository.findAll();
    }

    /**
     * Get banner by ID
     */
    public Banner getBannerById(Long id) {
        log.info("Fetching banner with ID: {}", id);
        return bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));
    }

    /**
     * Create new banner
     */
    public Banner createBanner(Banner banner) {
        log.info("Creating new banner: {}", banner.getTitle());
        return bannerRepository.save(banner);
    }

    /**
     * Update banner
     */
    public Banner updateBanner(Long id, Banner bannerDetails) {
        log.info("Updating banner with ID: {}", id);
        Banner banner = getBannerById(id);
        
        banner.setTitle(bannerDetails.getTitle());
        banner.setDescription(bannerDetails.getDescription());
        banner.setImageUrl(bannerDetails.getImageUrl());
        banner.setLinkUrl(bannerDetails.getLinkUrl());
        banner.setDisplayOrder(bannerDetails.getDisplayOrder());
        banner.setIsActive(bannerDetails.getIsActive());
        banner.setStartDate(bannerDetails.getStartDate());
        banner.setEndDate(bannerDetails.getEndDate());
        
        return bannerRepository.save(banner);
    }

    /**
     * Delete banner
     */
    public void deleteBanner(Long id) {
        log.info("Deleting banner with ID: {}", id);
        bannerRepository.deleteById(id);
    }

    /**
     * Toggle banner active status
     */
    public Banner toggleBannerStatus(Long id) {
        log.info("Toggling banner status with ID: {}", id);
        Banner banner = getBannerById(id);
        banner.setIsActive(!banner.getIsActive());
        return bannerRepository.save(banner);
    }
}
