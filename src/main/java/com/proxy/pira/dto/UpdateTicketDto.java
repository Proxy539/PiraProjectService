package com.proxy.pira.dto;

import com.proxy.pira.entity.TicketPriority;
import com.proxy.pira.entity.TicketStatus;
import com.proxy.pira.entity.TicketType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketDto {

    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private TicketType type;
    private TicketPriority priority;
    private TicketStatus status;
    
}
