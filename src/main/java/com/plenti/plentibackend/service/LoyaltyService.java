package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.PlentiPointsDTO;
import com.plenti.plentibackend.dto.PointsTransactionDTO;
import com.plenti.plentibackend.dto.PriceLockDTO;
import com.plenti.plentibackend.entity.PlentiPoints;
import com.plenti.plentibackend.entity.PointsTransaction;
import com.plenti.plentibackend.entity.PriceLock;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.PlentiPointsRepository;
import com.plenti.plentibackend.repository.PointsTransactionRepository;
import com.plenti.plentibackend.repository.PriceLockRepository;
import com.plenti.plentibackend.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for loyalty points and price lock management
 */
@Service
@Slf4j
public class LoyaltyService {

    @Autowired
    private PlentiPointsRepository plentiPointsRepository;

    @Autowired
    private PointsTransactionRepository pointsTransactionRepository;

    @Autowired
    private PriceLockRepository priceLockRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Add points to user's balance
     */
    @Transactional
    public PlentiPointsDTO addPoints(Long userId, int points, String reason) {
        PlentiPoints userPoints = plentiPointsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PlentiPoints newPoints = new PlentiPoints();
                    newPoints.setUserId(userId);
                    newPoints.setBalance(0);
                    newPoints.setTotalEarned(0);
                    newPoints.setTotalRedeemed(0);
                    newPoints.setTier("BRONZE");
                    return newPoints;
                });

        userPoints.setBalance(userPoints.getBalance() + points);
        userPoints.setTotalEarned(userPoints.getTotalEarned() + points);
        userPoints = plentiPointsRepository.save(userPoints);

        // Create transaction record
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setPoints(points);
        transaction.setType("EARN");
        transaction.setReason(reason);
        pointsTransactionRepository.save(transaction);

        log.info("Added {} points to user {}: {}", points, userId, reason);

        return toPlentiPointsDTO(userPoints);
    }

    /**
     * Get user's points balance
     */
    public PlentiPointsDTO getPointsBalance(Long userId) {
        PlentiPoints userPoints = plentiPointsRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("User points not found"));
        return toPlentiPointsDTO(userPoints);
    }

    /**
     * Redeem points for order
     */
    @Transactional
    public PlentiPointsDTO redeemPoints(Long userId, int points, Long orderId) {
        PlentiPoints userPoints = plentiPointsRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("User points not found"));

        if (userPoints.getBalance() < points) {
            throw new PlentiException("Insufficient points balance");
        }

        userPoints.setBalance(userPoints.getBalance() - points);
        userPoints.setTotalRedeemed(userPoints.getTotalRedeemed() + points);
        userPoints = plentiPointsRepository.save(userPoints);

        // Create transaction record
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setPoints(points);
        transaction.setType("REDEEM");
        transaction.setReason("Order redemption");
        transaction.setRelatedOrderId(orderId);
        pointsTransactionRepository.save(transaction);

        log.info("Redeemed {} points for user {} on order {}", points, userId, orderId);

        return toPlentiPointsDTO(userPoints);
    }

    /**
     * Apply price lock for a product
     */
    @Transactional
    public PriceLockDTO applyPricelock(Long userId, Long productId) {
        // Check if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new PlentiException("Product not found"));

        // Check if user already has an active price lock for this product
        priceLockRepository.findByUserIdAndProductIdAndIsActiveTrueAndExpiryDateAfter(
                userId, productId, LocalDateTime.now())
                .ifPresent(lock -> {
                    throw new PlentiException("Active price lock already exists for this product");
                });

        // Create new price lock (30 days validity)
        PriceLock priceLock = new PriceLock();
        priceLock.setUserId(userId);
        priceLock.setProductId(productId);
        priceLock.setLockedPrice(product.getPrice());
        priceLock.setExpiryDate(LocalDateTime.now().plusDays(30));
        priceLock.setIsActive(true);
        priceLock = priceLockRepository.save(priceLock);

        log.info("Applied price lock for user {} on product {} at price {}", 
                userId, productId, product.getPrice());

        return toPriceLockDTO(priceLock);
    }

    /**
     * Get all active price locks for user
     */
    public List<PriceLockDTO> getActivePricelocks(Long userId) {
        return priceLockRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .filter(lock -> lock.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(this::toPriceLockDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get points transaction history
     */
    public List<PointsTransactionDTO> getPointsHistory(Long userId) {
        return pointsTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toPointsTransactionDTO)
                .collect(Collectors.toList());
    }

    // Helper methods for DTO conversion
    private PlentiPointsDTO toPlentiPointsDTO(PlentiPoints points) {
        PlentiPointsDTO dto = new PlentiPointsDTO();
        dto.setId(points.getId());
        dto.setUserId(points.getUserId());
        dto.setBalance(points.getBalance());
        dto.setTotalEarned(points.getTotalEarned());
        dto.setTotalRedeemed(points.getTotalRedeemed());
        dto.setTier(points.getTier());
        dto.setLastUpdated(points.getLastUpdated());
        return dto;
    }

    private PriceLockDTO toPriceLockDTO(PriceLock priceLock) {
        PriceLockDTO dto = new PriceLockDTO();
        dto.setId(priceLock.getId());
        dto.setUserId(priceLock.getUserId());
        dto.setProductId(priceLock.getProductId());
        dto.setLockedPrice(priceLock.getLockedPrice());
        dto.setExpiryDate(priceLock.getExpiryDate());
        dto.setIsActive(priceLock.getIsActive());
        dto.setCreatedAt(priceLock.getCreatedAt());
        return dto;
    }

    private PointsTransactionDTO toPointsTransactionDTO(PointsTransaction transaction) {
        PointsTransactionDTO dto = new PointsTransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUserId());
        dto.setPoints(transaction.getPoints());
        dto.setType(transaction.getType());
        dto.setReason(transaction.getReason());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setRelatedOrderId(transaction.getRelatedOrderId());
        return dto;
    }
}
