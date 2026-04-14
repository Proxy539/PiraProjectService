package com.proxy.pira.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.proxy.pira.controller.ControllerAdvice;
import com.proxy.pira.dto.ErrorResponseDTO;
import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateTicketDto;
import com.proxy.pira.entity.Project;
import com.proxy.pira.entity.Ticket;
import com.proxy.pira.entity.TicketStatus;
import com.proxy.pira.entity.TicketType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TicketUtils {

    public static final Long TICKET_1_ID = 10L;
    public static final String TICKET_1_TITLE = "Ticket 1 title";
    public static final String TICKET_1_DESCRIPTION = "Ticket 1 description";
    public static final TicketType TICKET_1_TYPE = TicketType.STORY;
    public static final TicketStatus TICKET_1_STATUS = TicketStatus.NEW;

    private static final String TITLE_MUST_NOT_BE_BLANK = "title: must not be blank";
    private static final String DESCTIPION_MUST_NOT_BE_BLANK = "desctipion: must not be blank";

    public static Ticket buildTicket() {
        return Ticket.builder()
                .id(TICKET_1_ID)
                .title(TICKET_1_TITLE)
                .description(TICKET_1_DESCRIPTION)
                .type(TICKET_1_TYPE)
                .status(TICKET_1_STATUS)
                .build();
    }

    public static List<Ticket> buildTickets() {
        return List.of(buildTicket());
    }

    public static TicketDto buildTicketDto() {
        return TicketDto.builder()
                .id(TICKET_1_ID)
                .title(TICKET_1_TITLE)
                .description(TICKET_1_DESCRIPTION)
                .type(TICKET_1_TYPE)
                .status(TICKET_1_STATUS)
                .build();
    }

    public static List<TicketDto> buildTicketDtos() {
        return List.of(buildTicketDto());
    }

    public static SaveTicketDto buildSaveTicketDto() {
        return SaveTicketDto.builder()
                .title(TICKET_1_TITLE)
                .desctipion(TICKET_1_DESCRIPTION)
                .build();
    }

    // No id set — used for create-via-update path
    public static UpdateTicketDto buildUpdateTicketDto() {
        return UpdateTicketDto.builder()
                .projectId(ProjectUtils.PROJECT_1_ID)
                .title(TICKET_1_TITLE)
                .description(TICKET_1_DESCRIPTION)
                .type(TICKET_1_TYPE)
                .status(TICKET_1_STATUS)
                .build();
    }

    // Id set — used for update-existing path
    public static UpdateTicketDto buildUpdateTicketDtoWithId() {
        return UpdateTicketDto.builder()
                .id(TICKET_1_ID)
                .projectId(ProjectUtils.PROJECT_1_ID)
                .title(TICKET_1_TITLE)
                .description(TICKET_1_DESCRIPTION)
                .type(TICKET_1_TYPE)
                .status(TICKET_1_STATUS)
                .build();
    }

    public static Project buildProjectWithTickets() {
        return Project.builder()
                .id(ProjectUtils.PROJECT_1_ID)
                .title(ProjectUtils.PROJECT_1_TITLE)
                .description(ProjectUtils.PROJECT_1_DESCRIPTION)
                .tickets(new ArrayList<>())
                .build();
    }

    public static ErrorResponseDTO buildTicketValidationErrorResponseDTO() {
        return ErrorResponseDTO.builder()
                .service(ControllerAdvice.PIRA_SERVICE_NAME)
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ProjectUtils.VALIDATION_FAILED_ERROR_MESSAGE)
                .validationErrors(List.of(TITLE_MUST_NOT_BE_BLANK, DESCTIPION_MUST_NOT_BE_BLANK))
                .build();
    }

}
