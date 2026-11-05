package com.bugtracker.bugtracker.controller;

import com.bugtracker.bugtracker.model.Ticket;
import com.bugtracker.bugtracker.repository.TicketRepository;
import com.bugtracker.bugtracker.dto.TicketDTO;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:4200")

public class TicketController {

    private final TicketRepository repository;

    public TicketController(TicketRepository repository) {
        this.repository = repository;
    }

    private TicketDTO convertToDto(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        return dto;
    }

    @GetMapping
    public List<TicketDTO> getAllTickets() {
        return repository.findAll()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
}

    // Gets the Ticket ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        Optional<Ticket> found = repository.findById(id);

        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDto(found.get()));
    }

    // Creates the Ticket in database
    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody Ticket ticket) {
        Ticket saved = repository.save(ticket);
        return ResponseEntity.status(201).body(convertToDto(saved));
    }

    // Updates the Ticket (title and desc update to be done in ts)
    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable Long id, @Valid @RequestBody Ticket updated) {
        Optional<Ticket> found = repository.findById(id);

        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ticket existing = found.get();
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setPriority(updated.getPriority());

        Ticket saved = repository.save(existing);
        return ResponseEntity.ok(convertToDto(saved));
    }

    // Deletes Ticket (works)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}