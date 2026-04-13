package com.proxy.pira.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateTicketDto;
import com.proxy.pira.entity.Ticket;
import com.proxy.pira.exception.ResourceNotFoundException;
import com.proxy.pira.mapper.TicketMapper;
import com.proxy.pira.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    public List<TicketDto> findAll() {
        log.info("Finding all tickets");
        final var allTickets = ticketRepository.findAll();
        return ticketMapper.toTicketsDto(allTickets);
    }

    @Override
    public TicketDto findById(Long ticketId) {
        log.info("Finding ticket by id {}", ticketId);

        return ticketRepository.findById(ticketId)
                .map(ticketMapper::toTicketDto)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket was not found by id " + ticketId));
    }

    @Override
    public List<TicketDto> findByProjectId(Long projectId) {
        log.info("Finding tickets by project id {}", projectId);

        final var projectTickets = ticketRepository.findByProjectId(projectId);

        log.info("{} project tickets were found by project id {}", projectTickets.size(), projectId);

        return ticketMapper.toTicketsDto(projectTickets);
    }

    @Override
    public TicketDto save(SaveTicketDto saveTicketDto) {
        log.info("Saving ticket: {}", saveTicketDto);
        final var ticket = ticketMapper.toTicket(saveTicketDto);
        final var savedTicket = ticketRepository.save(ticket);

        return ticketMapper.toTicketDto(savedTicket);
    }

    @Override
    public TicketDto update(UpdateTicketDto updateTicketDto) {
        log.info("Updating ticket: {}", updateTicketDto);

        final var saveOrUpdateTicket = saveOrUpdateTicket(updateTicketDto);
        final var savedTicket = ticketRepository.save(saveOrUpdateTicket);

        return ticketMapper.toTicketDto(savedTicket);
    }

    @Override
    public void delete(Long ticketId) {
        log.info("Deleting ticket by id: {}", ticketId);

        ticketRepository.deleteById(ticketId);
    }

    private Ticket saveOrUpdateTicket(UpdateTicketDto updateTicketDto) {
        if (updateTicketDto.getId() == null) {
            return ticketMapper.toTicket(updateTicketDto);
        } else {
            return ticketRepository.findById(updateTicketDto.getId())
                    .map(savedTicket -> ticketMapper.updateTicket(updateTicketDto, savedTicket))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ticket was not found by id " + updateTicketDto.getId()));
        }
    }

}
