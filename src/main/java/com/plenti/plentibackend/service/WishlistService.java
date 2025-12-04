package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.WishlistDTO;
import com.plenti.plentibackend.entity.Wishlist;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.repository.WishlistRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for wishlist operations
 */
@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private Mapper mapper;

    public WishlistDTO getWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> createNewWishlist(userId));
        return mapper.toWishlistDTO(wishlist);
    }

    @Transactional
    public WishlistDTO addToWishlist(Long userId, Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new PlentiException("Product not found");
        }

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> createNewWishlist(userId));

        if (!wishlist.getProductIds().contains(productId)) {
            wishlist.getProductIds().add(productId);
        }

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return mapper.toWishlistDTO(savedWishlist);
    }

    @Transactional
    public WishlistDTO removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("Wishlist not found"));

        wishlist.getProductIds().remove(productId);

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return mapper.toWishlistDTO(savedWishlist);
    }

    @Transactional
    public void moveToCart(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("Wishlist not found"));

        if (!wishlist.getProductIds().contains(productId)) {
            throw new PlentiException("Product not in wishlist");
        }

        cartService.addToCart(userId, productId, 1);
        wishlist.getProductIds().remove(productId);
        wishlistRepository.save(wishlist);
    }

    private Wishlist createNewWishlist(Long userId) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        return wishlistRepository.save(wishlist);
    }
}
