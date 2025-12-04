package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Order;
import com.plenti.plentibackend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}
