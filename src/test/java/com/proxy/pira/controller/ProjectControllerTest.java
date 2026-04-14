package com.proxy.pira.controller;

import static com.proxy.pira.utils.ProjectUtils.PROJECT_1_ID;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDtos;
import static com.proxy.pira.utils.ProjectUtils.buildSaveProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildUpdateProjectDtoWithId;
import static com.proxy.pira.utils.ProjectUtils.buildValidationErrorResponseDTO;
import static com.proxy.pira.utils.TicketUtils.TICKET_1_ID;
import static com.proxy.pira.utils.TicketUtils.buildSaveTicketDto;
import static com.proxy.pira.utils.TicketUtils.buildTicketDto;
import static com.proxy.pira.utils.TicketUtils.buildTicketDtos;
import static com.proxy.pira.utils.TicketUtils.buildTicketValidationErrorResponseDTO;
import static com.proxy.pira.utils.TicketUtils.buildUpdateTicketDtoWithId;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.proxy.pira.dto.ErrorResponseDTO;
import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.exception.ResourceNotFoundException;
import com.proxy.pira.service.ProjectService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    private static final String GET_PROJECTS_URL = "/api/v1/projects";
    private static final String GET_PROJECT_BY_ID_URL = "/api/v1/projects/{id}";
    private static final String POST_PROJECTS_URL = "/api/v1/projects";
    private static final String PUT_PROJECTS_URL = "/api/v1/projects";
    private static final String DELETE_PROJECTS_URL = "/api/v1/projects/{id}";
    private static final String GET_PROJECT_TICKETS_URL = "/api/v1/projects/{projectId}/tickets";
    private static final String POST_PROJECT_TICKETS_URL = "/api/v1/projects/{projectId}/tickets";
    private static final String PUT_PROJECT_TICKETS_URL = "/api/v1/projects/{projectId}/tickets";
    private static final String DELETE_PROJECT_TICKET_URL = "/api/v1/projects/{projectId}/tickets/{ticketId}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenNoProjectsInDatabaseWhenFindAllProjectReturnEmptyList() throws Exception {
        final var projectDtos = List.<ProjectDto>of();

        when(projectService.findAllProjects()).thenReturn(projectDtos);

        mockMvc.perform(get(GET_PROJECTS_URL)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectDtos)));

        verify(projectService).findAllProjects();
    }

    @Test
    public void givenProjectsInDatabaseWhenFindAllProjectsReturnProjectsList() throws Exception {
        final var projectDtos = buildProjectDtos();

        when(projectService.findAllProjects()).thenReturn(projectDtos);

        mockMvc.perform(get(GET_PROJECTS_URL)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectDtos)));

        verify(projectService).findAllProjects();

    }

    @Test
    public void givenNoProjectInDatabaseWhenFindByIdThenReturnNotFoundResponse() throws Exception {
        final var notFoundException = new ResourceNotFoundException("Project not found by id " + PROJECT_1_ID);
        final var errorBody = ErrorResponseDTO.builder()
                .service(ControllerAdvice.PIRA_SERVICE_NAME)
                .status(HttpStatus.NOT_FOUND.value())
                .message(notFoundException.getMessage())
                .build();

        when(projectService.findProjectById(PROJECT_1_ID)).thenThrow(notFoundException);

        mockMvc.perform(get(GET_PROJECT_BY_ID_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(errorBody)));

        verify(projectService).findProjectById(PROJECT_1_ID);
    }

    @Test
    public void givenProjectInDatabaseWhenFindByIdThenReturnProject() throws Exception {
        final var projectDto = buildProjectDto();

        when(projectService.findProjectById(PROJECT_1_ID)).thenReturn(projectDto);

        mockMvc.perform(get(GET_PROJECT_BY_ID_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectDto)));

        verify(projectService).findProjectById(PROJECT_1_ID);
    }

    @Test
    public void givenNoProjectInDatabaseWhenSaveProjectThenReturnSavedProject() throws Exception {
        final var saveProjectDto = buildSaveProjectDto();
        final var projectDto = buildProjectDto();

        when(projectService.saveProject(saveProjectDto)).thenReturn(projectDto);

        mockMvc.perform(post(POST_PROJECTS_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveProjectDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(projectDto)));

        verify(projectService).saveProject(saveProjectDto);
    }

    @Test
    public void givenProjectInDatabaseWhenUpdateProjectThenReturnUpdatedProject() throws Exception {
        final var updateProjectDto = buildUpdateProjectDtoWithId();
        final var projectDto = buildProjectDto();

        when(projectService.updateProject(updateProjectDto)).thenReturn(projectDto);

        mockMvc.perform(put(PUT_PROJECTS_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProjectDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectDto)));

        verify(projectService).updateProject(updateProjectDto);
    }

    @Test
    public void givenNoProjectInDatabaseWhenSaveInvalidProjectThenReturnValidationErrors() throws Exception {
        final var saveProjectDto = SaveProjectDto.builder().build();
        final var errorBody = buildValidationErrorResponseDTO();

        mockMvc.perform(post(POST_PROJECTS_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveProjectDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(errorBody)));
    }

    @Test
    public void givenNoProjectInDatabaseWhenUpdateInvalidProjectThenReturnValidationErrors() throws Exception {
        final var updateProjectDto = UpdateProjectDto.builder().build();
        final var errorBody = buildValidationErrorResponseDTO();

        mockMvc.perform(put(PUT_PROJECTS_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProjectDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(errorBody)));
    }

    @Test
    public void givenProjetInDatabaseWhenDeleteProjectThenDeleteProject() throws Exception {

        mockMvc.perform(delete(DELETE_PROJECTS_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(PROJECT_1_ID);
    }

    @Test
    public void givenTicketsForProjectWhenFindProjectTicketsThenReturnTickets() throws Exception {
        final var ticketDtos = buildTicketDtos();

        when(projectService.findProjectTickets(PROJECT_1_ID)).thenReturn(ticketDtos);

        mockMvc.perform(get(GET_PROJECT_TICKETS_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDtos)));

        verify(projectService).findProjectTickets(PROJECT_1_ID);
    }

    @Test
    public void givenProjectInDatabaseWhenSaveTicketThenReturnSavedTicket() throws Exception {
        final var saveTicketDto = buildSaveTicketDto();
        final var ticketDto = buildTicketDto();

        when(projectService.saveProjectTicket(PROJECT_1_ID, saveTicketDto)).thenReturn(ticketDto);

        mockMvc.perform(post(POST_PROJECT_TICKETS_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveTicketDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDto)));

        verify(projectService).saveProjectTicket(PROJECT_1_ID, saveTicketDto);
    }

    @Test
    public void givenInvalidTicketWhenSaveTicketThenReturnValidationErrors() throws Exception {
        final var saveTicketDto = SaveTicketDto.builder().build();
        final var errorBody = buildTicketValidationErrorResponseDTO();

        mockMvc.perform(post(POST_PROJECT_TICKETS_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveTicketDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(errorBody)));
    }

    @Test
    public void givenTicketInDatabaseWhenUpdateTicketThenReturnUpdatedTicket() throws Exception {
        final var updateTicketDto = buildUpdateTicketDtoWithId();
        final var ticketDto = buildTicketDto();

        when(projectService.updateProjectTicket(PROJECT_1_ID, updateTicketDto)).thenReturn(ticketDto);

        mockMvc.perform(put(PUT_PROJECT_TICKETS_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTicketDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ticketDto)));

        verify(projectService).updateProjectTicket(PROJECT_1_ID, updateTicketDto);
    }

    @Test
    public void givenTicketInDatabaseWhenDeleteTicketThenReturnNoContent() throws Exception {

        mockMvc.perform(delete(DELETE_PROJECT_TICKET_URL, PROJECT_1_ID, TICKET_1_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProjectTicket(PROJECT_1_ID, TICKET_1_ID);
    }

}
