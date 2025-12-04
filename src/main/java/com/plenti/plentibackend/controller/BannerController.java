package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.entity.Banner;
import com.plenti.plentibackend.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for banner management
 */
@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@Tag(name = "Banners", description = "Promotional banner management")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "Get all active banners")
    public ResponseEntity<List<Banner>> getActiveBanners() {
        return ResponseEntity.ok(bannerService.getActiveBanners());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all banners (admin)")
    public ResponseEntity<List<Banner>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get banner by ID")
    public ResponseEntity<Banner> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.getBannerById(id));
    }

    @PostMapping
    @Operation(summary = "Create new banner (admin)")
    public ResponseEntity<Banner> createBanner(@RequestBody Banner banner) {
        return ResponseEntity.ok(bannerService.createBanner(banner));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update banner (admin)")
    public ResponseEntity<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        return ResponseEntity.ok(bannerService.updateBanner(id, banner));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete banner (admin)")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle")
    @Operation(summary = "Toggle banner active status (admin)")
    public ResponseEntity<Banner> toggleBannerStatus(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.toggleBannerStatus(id));
    }
}
