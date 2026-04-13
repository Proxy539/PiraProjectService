package com.proxy.pira.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateTicketDto;
import com.proxy.pira.service.TicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<TicketDto> findAllTickets() {
        return ticketService.findAll();
    }

    @GetMapping("/{ticketId}")
    public TicketDto findTicketById(@PathVariable Long ticketId) {
        return ticketService.findById(ticketId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto saveTicket(@RequestBody @Valid SaveTicketDto saveTicketDto) {
        return ticketService.save(saveTicketDto);
    }

    @PutMapping
    public TicketDto updateTicket(@RequestBody @Valid UpdateTicketDto updateTicketDto) {
        return ticketService.update(updateTicketDto);
    }

    @DeleteMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable Long ticketId) {
        ticketService.delete(ticketId);
    }
    
}
