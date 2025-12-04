package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.CartDTO;
import com.plenti.plentibackend.entity.Cart;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.CartRepository;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.util.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private Product product;
    private CartDTO cartDTO;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        cart.setItems(new HashMap<>());
        cart.setTotal(0.0);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(50);

        cartDTO = new CartDTO();
        cartDTO.setId(1L);
        cartDTO.setUserId(1L);
        cartDTO.setTotal(0.0);
    }

    @Test
    void addToCart_WhenProductHasStock_ShouldAddItem() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(mapper.toCartDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.addToCart(1L, 1L, 2);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addToCart_WhenInsufficientStock_ShouldThrowException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(PlentiException.class, () -> cartService.addToCart(1L, 1L, 100));
    }

    @Test
    void removeFromCart_ShouldRemoveItem() {
        cart.getItems().put(1L, 2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(mapper.toCartDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.removeFromCart(1L, 1L);

        assertNotNull(result);
        assertFalse(cart.getItems().containsKey(1L));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void clearCart_ShouldClearAllItems() {
        cart.getItems().put(1L, 2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotal());
        verify(cartRepository, times(1)).save(cart);
    }
}
