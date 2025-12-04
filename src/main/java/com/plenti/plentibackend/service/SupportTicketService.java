package com.plenti.plentibackend.service;

import com.plenti.plentibackend.entity.SupportTicket;
import com.plenti.plentibackend.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing customer support tickets
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    /**
     * Create a new support ticket
     */
    public SupportTicket createTicket(SupportTicket ticket) {
        log.info("Creating support ticket for user: {}", ticket.getUserId());
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return supportTicketRepository.save(ticket);
    }

    /**
     * Get ticket by ID
     */
    public SupportTicket getTicketById(Long id) {
        log.info("Fetching ticket with ID: {}", id);
        return supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));
    }

    /**
     * Get all tickets for a user
     */
    public List<SupportTicket> getUserTickets(Long userId) {
        log.info("Fetching tickets for user: {}", userId);
        return supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get tickets by status
     */
    public List<SupportTicket> getTicketsByStatus(String status) {
        log.info("Fetching tickets with status: {}", status);
        return supportTicketRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Update ticket status
     */
    public SupportTicket updateTicketStatus(Long id, String status, String resolution) {
        log.info("Updating ticket {} status to: {}", id, status);
        SupportTicket ticket = getTicketById(id);
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        if (resolution != null && !resolution.isEmpty()) {
            ticket.setResolution(resolution);
        }
        
        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        
        return supportTicketRepository.save(ticket);
    }

    /**
     * Assign ticket to an admin user
     */
    public SupportTicket assignTicket(Long ticketId, Long adminUserId) {
        log.info("Assigning ticket {} to admin user: {}", ticketId, adminUserId);
        SupportTicket ticket = getTicketById(ticketId);
        ticket.setAssignedToUserId(adminUserId);
        ticket.setStatus("IN_PROGRESS");
        ticket.setUpdatedAt(LocalDateTime.now());
        return supportTicketRepository.save(ticket);
    }

    /**
     * Get all tickets (admin)
     */
    public List<SupportTicket> getAllTickets() {
        log.info("Fetching all support tickets");
        return supportTicketRepository.findAll();
    }

    /**
     * Update ticket priority
     */
    public SupportTicket updateTicketPriority(Long id, String priority) {
        log.info("Updating ticket {} priority to: {}", id, priority);
        SupportTicket ticket = getTicketById(id);
        ticket.setPriority(priority);
        ticket.setUpdatedAt(LocalDateTime.now());
        return supportTicketRepository.save(ticket);
    }
}
