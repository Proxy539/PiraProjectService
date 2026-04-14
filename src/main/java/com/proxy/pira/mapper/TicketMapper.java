package com.proxy.pira.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateTicketDto;
import com.proxy.pira.entity.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketDto toTicketDto(Ticket ticket);

    List<TicketDto> toTicketsDto(List<Ticket> tickets);

    Ticket toTicket(SaveTicketDto saveTicketDto);

    Ticket toTicket(UpdateTicketDto updateTicketDto);

    @Mapping(target = "id", ignore = true)
    Ticket updateTicket(UpdateTicketDto upadteTicketDto, @MappingTarget Ticket ticket);

    
}
