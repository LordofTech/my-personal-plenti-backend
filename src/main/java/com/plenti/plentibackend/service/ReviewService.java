package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.ReviewDTO;
import com.plenti.plentibackend.entity.Review;
import com.plenti.plentibackend.entity.User;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.ReviewRepository;
import com.plenti.plentibackend.repository.UserRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for review and rating operations
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private Mapper mapper;

    private static final double PLENTI_POINTS_FOR_REVIEW = 10.0;

    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = mapper.toReviewEntity(reviewDTO);
        Review savedReview = reviewRepository.save(review);

        // Update product rating
        updateProductRating(reviewDTO.getProductId());

        // Reward user with Plenti Points
        rewardUserForReview(reviewDTO.getUserId());

        return mapper.toReviewDTO(savedReview);
    }

    public List<ReviewDTO> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(mapper::toReviewDTO)
                .toList();
    }

    public List<ReviewDTO> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(mapper::toReviewDTO)
                .toList();
    }

    private void updateProductRating(Long productId) {
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.countByProductId(productId);

        if (averageRating != null && reviewCount != null) {
            productService.updateProductRating(productId, averageRating, reviewCount.intValue());
        }
    }

    private void rewardUserForReview(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PlentiException("User not found"));
        user.setMetaCoins(user.getMetaCoins() + PLENTI_POINTS_FOR_REVIEW);
        userRepository.save(user);
    }
}
