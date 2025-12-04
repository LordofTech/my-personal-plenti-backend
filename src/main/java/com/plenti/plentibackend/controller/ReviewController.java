package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.dto.ReviewDTO;
import com.plenti.plentibackend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for review operations
 */
@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Product review endpoints")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create review", description = "Submit a product review")
    public ResponseEntity<ResponseDTO<ReviewDTO>> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO created = reviewService.createReview(reviewDTO);
        return ResponseEntity.ok(ResponseDTO.success("Review submitted successfully. You earned 10 Plenti Points!", created));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product reviews", description = "Get all reviews for a product")
    public ResponseEntity<ResponseDTO<List<ReviewDTO>>> getProductReviews(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ResponseDTO.success(reviews));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user reviews", description = "Get all reviews by a user")
    public ResponseEntity<ResponseDTO<List<ReviewDTO>>> getUserReviews(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(ResponseDTO.success(reviews));
    }
}
