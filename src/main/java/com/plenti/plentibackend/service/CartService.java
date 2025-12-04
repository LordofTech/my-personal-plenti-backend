package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.CartDTO;
import com.plenti.plentibackend.entity.Cart;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.CartRepository;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for shopping cart operations
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Mapper mapper;

    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
        return mapper.toCartDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new PlentiException("Product not found"));

        if (product.getStock() < quantity) {
            throw new PlentiException("Insufficient stock");
        }

        cart.getItems().put(productId, cart.getItems().getOrDefault(productId, 0) + quantity);
        calculateTotal(cart);

        Cart savedCart = cartRepository.save(cart);
        return mapper.toCartDTO(savedCart);
    }

    @Transactional
    public CartDTO removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("Cart not found"));

        cart.getItems().remove(productId);
        calculateTotal(cart);

        Cart savedCart = cartRepository.save(cart);
        return mapper.toCartDTO(savedCart);
    }

    @Transactional
    public CartDTO updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new PlentiException("Product not found"));

        if (product.getStock() < quantity) {
            throw new PlentiException("Insufficient stock");
        }

        if (quantity <= 0) {
            cart.getItems().remove(productId);
        } else {
            cart.getItems().put(productId, quantity);
        }

        calculateTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapper.toCartDTO(savedCart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("Cart not found"));
        cart.getItems().clear();
        cart.setTotal(0.0);
        cartRepository.save(cart);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotal(0.0);
        return cartRepository.save(cart);
    }

    private void calculateTotal(Cart cart) {
        double total = 0.0;
        for (var entry : cart.getItems().entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new PlentiException("Product not found"));
            total += product.getPrice() * entry.getValue();
        }
        cart.setTotal(total);
    }
}
