package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    
    List<OrderTracking> findByOrderIdOrderByTimestampDesc(Long orderId);
    
    List<OrderTracking> findByOrderIdOrderByTimestampAsc(Long orderId);
}
