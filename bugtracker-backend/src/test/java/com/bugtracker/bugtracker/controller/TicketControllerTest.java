package com.bugtracker.bugtracker.controller;

import com.bugtracker.bugtracker.model.Ticket; // The Ticket class we build fake tickets from
import com.bugtracker.bugtracker.repository.TicketRepository; // The repository we're going to fake
import org.junit.jupiter.api.Test; // Marks a method as a test
import org.springframework.beans.factory.annotation.Autowired; // Lets Spring hand us the MockMvc tool
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; // Starts just the web layer for testing
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Swaps in a fake bean
import org.springframework.test.web.servlet.MockMvc; // Sends fake HTTP requests
import org.springframework.http.MediaType; // Lets us say the request body is JSON

import java.util.List; // Used to make an empty list
import java.util.Optional; // Used because findById returns an Optional
import static org.mockito.ArgumentMatchers.any; // Lets us say "any Ticket"
import static org.mockito.BDDMockito.*; // Gives us given(...).willReturn(...)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // Gives us get(...), post(...), etc.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // Gives us status() and jsonPath()

@WebMvcTest(TicketController.class) // Start only the web layer for TicketController, no database
public class TicketControllerTest {

    @Autowired // Spring provides this for us
    private MockMvc mockMvc; // The tool that sends fake requests

    @MockitoBean // Replace the real repository with a fake one
    private TicketRepository repository; // The fake repository

    private Ticket makeTicket(Long id, String title) { // Helper: builds a fake ticket
        Ticket ticket = new Ticket(); // Create an empty ticket
        ticket.setId(id); // Give it an id
        ticket.setTitle(title); // Give it a title
        return ticket; // Hand it back to the test
    }

    // ---------- GET tests ----------

    @Test // Tests: getting all tickets when none exist returns an empty list
    void getAllTickets_whenEmpty_returnsEmptyList() throws Exception {
        given(repository.findAll()).willReturn(List.of()); // Fake repository says: no tickets exist

        mockMvc.perform(get("/api/tickets")) // Send a fake GET request
                .andExpect(status().isOk()) // Check the status is 200
                .andExpect(jsonPath("$").isEmpty()); // Check the response is an empty list
    }

    @Test // Tests: getting a ticket that exists returns 200 and the ticket
    void getTicketById_whenExists_returnsTicket() throws Exception {
        given(repository.findById(1L)).willReturn(Optional.of(makeTicket(1L, "Login bug"))); // Fake repository says: ticket 1 exists

        mockMvc.perform(get("/api/tickets/1")) // Send a fake GET request for ticket 1
                .andExpect(status().isOk()) // Check the status is 200
                .andExpect(jsonPath("$.title").value("Login bug")); // Check the title matches
    }

    @Test // Tests: getting a ticket that doesn't exist returns 404
    void getTicketById_whenNotFound_returns404() throws Exception {
        given(repository.findById(99L)).willReturn(Optional.empty()); // Fake repository says: ticket 99 doesn't exist

        mockMvc.perform(get("/api/tickets/99")) // Send a fake GET request for ticket 99
                .andExpect(status().isNotFound()); // Check the status is 404
    }

    // ---------- CREATE tests ----------

    @Test // Tests: creating a ticket with valid data returns 201 and the saved ticket
    void createTicket_withValidData_returns201() throws Exception {
        given(repository.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0)); // Fake save: hand back the same ticket it was given

        mockMvc.perform(post("/api/tickets") // Send a fake POST request
                        .contentType(MediaType.APPLICATION_JSON) // Say the body is JSON
                        .content("{\"title\": \"Login bug\", \"description\": \"Cannot log in\"}")) // The ticket we're sending
                .andExpect(status().isCreated()) // Check the status is 201
                .andExpect(jsonPath("$.title").value("Login bug")); // Check the title came back
    }

    @Test // Tests: creating a ticket with a blank title is rejected with 400
    void createTicket_withBlankTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/tickets") // Send a fake POST request
                        .contentType(MediaType.APPLICATION_JSON) // Say the body is JSON
                        .content("{\"title\": \"\", \"description\": \"test\"}")) // Blank title
                .andExpect(status().isBadRequest()) // Check the status is 400
                .andExpect(jsonPath("$.title").exists()); // Check the error mentions the title field
    }

    @Test // Tests: creating a ticket with a title over 100 characters is rejected with 400
    void createTicket_withTooLongTitle_returns400() throws Exception {
        String longTitle = "a".repeat(101); // Build a title that is 101 characters long

        mockMvc.perform(post("/api/tickets") // Send a fake POST request
                        .contentType(MediaType.APPLICATION_JSON) // Say the body is JSON
                        .content("{\"title\": \"" + longTitle + "\"}")) // Use the too-long title
                .andExpect(status().isBadRequest()) // Check the status is 400
                .andExpect(jsonPath("$.title").exists()); // Check the error mentions the title field
    }

    // ---------- UPDATE tests ----------

    @Test // Tests: updating an existing ticket saves the changes and returns 200
    void updateTicket_whenExists_returns200() throws Exception {
        given(repository.findById(1L)).willReturn(Optional.of(makeTicket(1L, "Old title"))); // Fake repository: ticket 1 exists
        given(repository.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0)); // Fake save: hand back the same ticket

        mockMvc.perform(put("/api/tickets/1") // Send a fake PUT request
                        .contentType(MediaType.APPLICATION_JSON) // Say the body is JSON
                        .content("{\"title\": \"New title\"}")) // The new title
                .andExpect(status().isOk()) // Check the status is 200
                .andExpect(jsonPath("$.title").value("New title")); // Check the title changed
    }

    @Test // Tests: updating a ticket that doesn't exist returns 404
    void updateTicket_whenNotFound_returns404() throws Exception {
        given(repository.findById(99L)).willReturn(Optional.empty()); // Fake repository: ticket 99 doesn't exist

        mockMvc.perform(put("/api/tickets/99") // Send a fake PUT request for ticket 99
                        .contentType(MediaType.APPLICATION_JSON) // Say the body is JSON
                        .content("{\"title\": \"New title\"}")) // Any valid body
                .andExpect(status().isNotFound()); // Check the status is 404
    }

    @Test // Tests: updating a ticket with a blank title is rejected with 400
    void updateTicket_withBlankTitle_returns400() throws Exception {
        mockMvc.perform(put("/api/tickets/1") // Send a fake PUT request
                        .contentType(MediaType.APPLICATION_JSON) // Say the body is JSON
                        .content("{\"title\": \"\"}")) // Blank title
                .andExpect(status().isBadRequest()) // Check the status is 400
                .andExpect(jsonPath("$.title").exists()); // Check the error mentions the title field
    }

    // ---------- DELETE tests ----------

    @Test // Tests: deleting an existing ticket returns 204
    void deleteTicket_whenExists_returns204() throws Exception {
        given(repository.existsById(1L)).willReturn(true); // Fake repository: ticket 1 exists

        mockMvc.perform(delete("/api/tickets/1")) // Send a fake DELETE request
                .andExpect(status().isNoContent()); // Check the status is 204
    }

    @Test // Tests: deleting a ticket that doesn't exist returns 404
    void deleteTicket_whenNotFound_returns404() throws Exception {
        given(repository.existsById(99L)).willReturn(false); // Fake repository: ticket 99 doesn't exist

        mockMvc.perform(delete("/api/tickets/99")) // Send a fake DELETE request
                .andExpect(status().isNotFound()); // Check the status is 404
    }
}