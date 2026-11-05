package com.bugtracker.bugtracker.dto;

import com.bugtracker.bugtracker.model.Ticket;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketDTO {

    private Long id;
    private String title;
    private String description;
    private Ticket.Status status;
    private Ticket.Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

