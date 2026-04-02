package com.proxy.pira.controller;

import static com.proxy.pira.utils.ProjectUtils.PROJECT_1_ID;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDtos;
import static com.proxy.pira.utils.ProjectUtils.buildSaveProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildUpdateProjectDtoWithId;
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

    // @TODO add the validation error test when the validation is added

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

    // @TODO add the validation eerror test when the validation is added

    @Test
    public void givenProjetInDatabaseWhenDeleteProjectThenDeleteProject() throws Exception {

        mockMvc.perform(delete(DELETE_PROJECTS_URL, PROJECT_1_ID)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(PROJECT_1_ID);
    }

}
