package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.OrderDTO;
import com.plenti.plentibackend.entity.*;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.CartRepository;
import com.plenti.plentibackend.repository.OrderRepository;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Mapper mapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private Cart cart;
    private Product product;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        cart.setItems(new HashMap<>());
        cart.getItems().put(1L, 2);
        cart.setTotal(200.0);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(50);

        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalAmount(200.0);
        order.setStatus(OrderStatus.PENDING);

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setTotalAmount(200.0);
    }

    @Test
    void placeOrder_WithValidCart_ShouldCreateOrder() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapper.toOrderDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.placeOrder(1L, "Test Address", null, 0.0);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void placeOrder_WithEmptyCart_ShouldThrowException() {
        cart.getItems().clear();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(PlentiException.class, 
            () -> orderService.placeOrder(1L, "Test Address", null, 0.0));
    }

    @Test
    void cancelOrder_WhenOrderIsPending_ShouldCancelSuccessfully() {
        order.setStatus(OrderStatus.PENDING);
        order.getProductIds().add("1");
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapper.toOrderDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.cancelOrder(1L);

        assertNotNull(result);
        verify(notificationService, times(1)).sendOrderStatusUpdate(1L, OrderStatus.CANCELLED);
    }
}
