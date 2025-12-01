package com.plenti.plenti.backend.service;

import com.plenti.plenti.backend.dto.CartDTO;
import com.plenti.plenti.backend.entity.Cart;
import com.plenti.plenti.backend.entity.Product;
import com.plenti.plenti.backend.repository.CartRepository;
import com.plenti.plenti.backend.repository.ProductRepository;
import com.plenti.plenti.backend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public CartDTO getCart(String userIdStr) {
        Cart cart = cartRepository.findByUserId(userIdStr);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(Long.parseLong(userIdStr));
            cart.setItems(new HashMap<>());
            cart.setTotal(0.0);
            cart = cartRepository.save(cart);
        }
        return Mapper.toCartDTO(cart);
    }
    
    public CartDTO addItem(String userIdStr, String productIdStr, int quantity) {
        Cart cart = cartRepository.findByUserId(userIdStr);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(Long.parseLong(userIdStr));
            cart.setItems(new HashMap<>());
            cart.setTotal(0.0);
        }
        
        Optional<Product> optionalProduct = productRepository.findById(productIdStr);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        Product product = optionalProduct.get();
        Long productId = Long.parseLong(productIdStr);
        
        int currentQuantity = cart.getItems().getOrDefault(productId, 0);
        cart.getItems().put(productId, currentQuantity + quantity);
        cart.setTotal(cart.getTotal() + (product.getPrice() * quantity));
        
        return Mapper.toCartDTO(cartRepository.save(cart));
    }
    
    public CartDTO removeItem(String userIdStr, String itemIdStr) {
        Cart cart = cartRepository.findByUserId(userIdStr);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        
        Long itemId = Long.parseLong(itemIdStr);
        
        if (!cart.getItems().containsKey(itemId)) {
            throw new RuntimeException("Item not found in cart");
        }
        
        int quantity = cart.getItems().get(itemId);
        Optional<Product> optionalProduct = productRepository.findById(itemIdStr);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        double price = optionalProduct.get().getPrice();
        cart.setTotal(cart.getTotal() - (price * quantity));
        cart.getItems().remove(itemId);
        
        return Mapper.toCartDTO(cartRepository.save(cart));
    }
}