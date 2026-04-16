package com.proxy.pira.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateTicketDto;
import com.proxy.pira.entity.Ticket;

/** MapStruct mapper for converting between {@link Ticket} entities and their DTOs. */
@Mapper(componentModel = "spring")
public interface TicketMapper {

    /** Converts a {@link Ticket} entity to its response DTO. */
    TicketDto toTicketDto(Ticket ticket);

    /** Converts a list of {@link Ticket} entities to a list of response DTOs. */
    List<TicketDto> toTicketsDto(List<Ticket> tickets);

    /**
     * Creates a new {@link Ticket} from a creation request DTO.
     * The initial status is always set to {@code TO_DO}.
     */
    @Mapping(target = "status", expression = "java(com.proxy.pira.entity.TicketStatus.TO_DO)")
    Ticket toTicket(SaveTicketDto saveTicketDto);

    /** Creates a new {@link Ticket} entity from an update request DTO. */
    Ticket toTicket(UpdateTicketDto updateTicketDto);

    /**
     * Patches {@code ticket} in-place with values from {@code updateTicketDto}.
     * The entity {@code id} is intentionally left unchanged.
     */
    @Mapping(target = "id", ignore = true)
    Ticket updateTicket(UpdateTicketDto upadteTicketDto, @MappingTarget Ticket ticket);

}
