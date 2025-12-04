package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<SupportTicket> findByStatusOrderByCreatedAtDesc(String status);
    List<SupportTicket> findByAssignedToUserIdOrderByCreatedAtDesc(Long assignedToUserId);
}
