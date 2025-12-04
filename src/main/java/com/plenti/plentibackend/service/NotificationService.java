package com.plenti.plentibackend.service;

import com.plenti.plentibackend.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for real-time notifications via WebSocket
 */
@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendOrderStatusUpdate(Long orderId, OrderStatus status) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", orderId);
        message.put("status", status.toString());
        message.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/orders/" + orderId, message);
    }

    public void sendNotification(String topic, Object message) {
        messagingTemplate.convertAndSend(topic, message);
    }
}
