package com.proxy.pira.dto;

import com.proxy.pira.entity.TicketPriority;
import com.proxy.pira.entity.TicketType;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for creating a new ticket inside a project. All fields are mandatory. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveTicketDto {

    /** The ticket title; must not be blank. */
    @NotBlank
    private String title;

    /** A description of the work to be done; must not be blank. */
    @NotBlank
    private String description;

    /** The category of work (e.g. BUG, FEATURE); must not be null. */
    @NotNull
    private TicketType type;

    /** The urgency level of this ticket; must not be null. */
    @NotNull
    private TicketPriority priority;

}
