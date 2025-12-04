package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.OrderDTO;
import com.plenti.plentibackend.entity.*;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.CartRepository;
import com.plenti.plentibackend.repository.OrderRepository;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for order management operations
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public OrderDTO placeOrder(Long userId, String deliveryAddress, String promoCode, Double discountAmount) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new PlentiException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new PlentiException("Cart is empty");
        }

        // Check stock availability
        for (var entry : cart.getItems().entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new PlentiException("Product not found"));
            if (product.getStock() < entry.getValue()) {
                throw new PlentiException("Insufficient stock for product: " + product.getName());
            }
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setProductIds(new ArrayList<>(cart.getItems().keySet().stream().map(String::valueOf).toList()));
        order.setTotalAmount(cart.getTotal() - (discountAmount != null ? discountAmount : 0.0));
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(deliveryAddress);
        order.setTrackingUrl(generateTrackingUrl());
        order.setEstimatedDelivery(LocalDateTime.now().plusHours(1));
        order.setPromoCodeApplied(promoCode);
        order.setDiscountAmount(discountAmount != null ? discountAmount : 0.0);

        // Reduce stock
        for (var entry : cart.getItems().entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new PlentiException("Product not found"));
            product.setStock(product.getStock() - entry.getValue());
            productRepository.save(product);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cart.setTotal(0.0);
        cartRepository.save(cart);

        return mapper.toOrderDTO(savedOrder);
    }

    public OrderDTO getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));
        return mapper.toOrderDTO(order);
    }

    public List<OrderDTO> getOrderHistory(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(mapper::toOrderDTO)
                .toList();
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(mapper::toOrderDTO)
                .toList();
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        // Send notification
        notificationService.sendOrderStatusUpdate(orderId, status);

        return mapper.toOrderDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO assignRider(Long orderId, String riderId, String riderName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));
        order.setRiderId(riderId);
        order.setRiderName(riderName);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        Order updatedOrder = orderRepository.save(order);

        notificationService.sendOrderStatusUpdate(orderId, OrderStatus.OUT_FOR_DELIVERY);

        return mapper.toOrderDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));

        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY || 
            order.getStatus() == OrderStatus.DELIVERED) {
            throw new PlentiException("Cannot cancel order at this stage");
        }

        // Restore stock
        if (order.getProductIds() != null) {
            for (String productIdStr : order.getProductIds()) {
                try {
                    Long productId = Long.parseLong(productIdStr);
                    Product product = productRepository.findById(productId).orElse(null);
                    if (product != null) {
                        product.setStock(product.getStock() + 1);
                        productRepository.save(product);
                    }
                } catch (NumberFormatException e) {
                    // Log and skip invalid product ID
                    System.err.println("Invalid product ID in order: " + productIdStr);
                }
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        notificationService.sendOrderStatusUpdate(orderId, OrderStatus.CANCELLED);

        return mapper.toOrderDTO(updatedOrder);
    }

    private String generateTrackingUrl() {
        return "https://plenti.ng/track/" + UUID.randomUUID().toString();
    }
}
