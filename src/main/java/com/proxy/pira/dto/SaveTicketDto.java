package com.proxy.pira.dto;

import com.proxy.pira.entity.TicketPriority;
import com.proxy.pira.entity.TicketType;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveTicketDto {

    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private TicketType type;
    @NotNull
    private TicketPriority priority;

}
