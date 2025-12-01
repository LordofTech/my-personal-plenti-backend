package com.plenti.plenti.backend.service;

import com.plenti.plenti.backend.dto.OrderDTO;
import com.plenti.plenti.backend.entity.Order;
import com.plenti.plenti.backend.repository.OrderRepository;
import com.plenti.plenti.backend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ProductService productService;

    public OrderDTO placeOrder(OrderDTO orderDTO) {
        // Check stock to ensure zero stockouts as per Plenti's key promises
        orderDTO.getProductIds().forEach(id -> productService.checkStock(id, 1)); // Assume quantity 1 for simplicity; enhance for actual quantities
        // Assign to nearest store for hybrid fulfillment (dark stores + partners)
        String storeId = storeService.assignStore(orderDTO.getUserId());
        Order order = Mapper.toOrder(orderDTO);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Placed");
        // Placeholder for tracking URL with 60-min ETA logic; integrate real maps API for production
        order.setTrackingUrl("https://maps.google.com/?q=store_location&key=YOUR_GOOGLE_MAPS_KEY");
        return Mapper.toOrderDTO(orderRepository.save(order));
    }

    public OrderDTO trackOrder(String id) {
        return orderRepository.findById(id).map(Mapper::toOrderDTO).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderDTO> getHistory(String userId) {
        return StreamSupport.stream(orderRepository.findByUserId(userId).spliterator(), false)
            .map(Mapper::toOrderDTO)
            .collect(Collectors.toList());
    }
}