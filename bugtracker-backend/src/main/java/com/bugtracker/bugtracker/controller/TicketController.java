package com.bugtracker.bugtracker.controller;

import com.bugtracker.bugtracker.model.Ticket;
import com.bugtracker.bugtracker.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:4200")

public class TicketController {

    private final TicketRepository repository;

    public TicketController(TicketRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return repository.findAll();
    }

    // Gets the Ticket ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Optional<Ticket> found = repository.findById(id);

        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(found.get());
    }

    // Creates the Ticket in database
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) {
        Ticket saved = repository.save(ticket);
        return ResponseEntity.status(201).body(saved);
    }

    // Updates the Ticket (title and desc update to be done in ts)
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @Valid @RequestBody Ticket updated) {
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
        return ResponseEntity.ok(saved);
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