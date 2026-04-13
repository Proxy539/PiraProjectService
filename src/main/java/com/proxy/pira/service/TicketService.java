package com.proxy.pira.service;

import java.util.List;

import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateTicketDto;

public interface TicketService {

    List<TicketDto> findAll();

    TicketDto findById(Long ticketId);

    List<TicketDto> findByProjectId(Long projectId);

    TicketDto save(SaveTicketDto saveTicketDto);

    TicketDto update(UpdateTicketDto updateTicketDto);

    void delete(Long ticketId);

}
