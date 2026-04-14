package com.proxy.pira.dto;

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
public class TicketDto {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketType type;
    
}
