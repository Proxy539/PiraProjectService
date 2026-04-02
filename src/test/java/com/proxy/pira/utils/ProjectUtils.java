package com.proxy.pira.utils;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.proxy.pira.controller.ControllerAdvice;
import com.proxy.pira.dto.ErrorResponseDTO;
import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.entity.Project;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProjectUtils {

    private static final String DESCRIPTION_MUST_NOT_BE_BLANK_VALIDATION_ERROR_MESSAGE = "description: must not be blank";
    private static final String TITLE_MUST_NOT_BE_BLANK_VALIDATION_ERROR_MESSAGE = "title: must not be blank";
    public static final String VALIDATION_FAILED_ERROR_MESSAGE = "Validation failed";
    public static final Long PROJECT_1_ID = 1L;
    public static final Long PROJECT_2_ID = 2L;
    public static final Long PROJECT_3_ID = 3L;
    public static final String PROJECT_1_TITLE = "Project 1 title";
    public static final String PROJECT_2_TITLE = "Project 2 title";
    public static final String PROJECT_3_TITLE = "Project 3 title";
    public static final String PROJECT_1_DESCRIPTION = "Project 1 description";
    public static final String PROJECT_2_DESCRIPTION = "Project 2 description";
    public static final String PROJECT_3_DESCRIPTION = "Project 3 description";

    public static Project buildProject() {
        return Project.builder()
            .id(PROJECT_1_ID)
            .title(PROJECT_1_TITLE)
            .description(PROJECT_1_DESCRIPTION)
            .build();
    }
    
    public static Project buildProject(Long projectId, String projectTitle, String projectDescription) {
        return Project.builder()
            .id(projectId)
            .title(projectTitle)
            .description(projectDescription)
            .build(); 
    }

    public static List<Project> buildProjects() {
        return List.of(
                buildProject(PROJECT_1_ID, PROJECT_2_TITLE, PROJECT_2_DESCRIPTION),
                buildProject(PROJECT_2_ID, PROJECT_2_TITLE, PROJECT_2_DESCRIPTION),
                buildProject(PROJECT_3_ID, PROJECT_3_TITLE, PROJECT_2_DESCRIPTION)
                );
    }

    public static ProjectDto buildProjectDto() {
        return ProjectDto.builder()
            .id(PROJECT_1_ID)
            .title(PROJECT_1_TITLE)
            .description(PROJECT_1_TITLE)
            .build();
    }

    public static ProjectDto buildProjectDto(Long projectId, String projectTitle, String projectDescription) {
        return ProjectDto.builder()
            .id(projectId)
            .title(projectTitle)
            .description(projectDescription)
            .build();
    }

    public static List<ProjectDto> buildProjectDtos() {
        return List.of(
                buildProjectDto(PROJECT_1_ID, PROJECT_1_TITLE, PROJECT_1_DESCRIPTION),
                buildProjectDto(PROJECT_2_ID, PROJECT_2_TITLE, PROJECT_2_DESCRIPTION),
                buildProjectDto(PROJECT_3_ID, PROJECT_3_TITLE, PROJECT_3_DESCRIPTION)
                );
    
    }

    public static SaveProjectDto buildSaveProjectDto() {
        return SaveProjectDto.builder()
            .title(PROJECT_1_TITLE)
            .description(PROJECT_1_DESCRIPTION)
            .build();
    }

    public static SaveProjectDto buildSaveProjectDto(String projectTitle, String projectDescription) {
        return SaveProjectDto.builder()
            .title(projectTitle)
            .description(projectDescription)
            .build();
    }

    public static UpdateProjectDto buildUpdateProjectDto() {
        return UpdateProjectDto.builder()
            .title(PROJECT_1_TITLE)
            .description(PROJECT_1_DESCRIPTION)
            .build();
    }

    public static UpdateProjectDto buildUpdateProjectDtoWithId() {
        return UpdateProjectDto.builder()
            .id(PROJECT_1_ID)
            .title(PROJECT_1_TITLE)
            .description(PROJECT_1_DESCRIPTION)
            .build();
    }

    public static UpdateProjectDto buildUpdateProjectDto(String projectTitle, String projectDescrtiption) {
        return UpdateProjectDto.builder()
            .title(projectTitle)
            .description(projectDescrtiption)
            .build();
    }

    public static UpdateProjectDto buildUpdateProjectDto(Long projectId, String projectTitle, String projectDescription) {
        return UpdateProjectDto.builder()
            .id(projectId)
            .title(projectTitle)
            .description(projectDescription)
            .build();
    }

    public static ErrorResponseDTO buildValidationErrorResponseDTO() {
        return ErrorResponseDTO.builder()
            .service(ControllerAdvice.PIRA_SERVICE_NAME)
            .status(HttpStatus.BAD_REQUEST.value())
            .message(VALIDATION_FAILED_ERROR_MESSAGE)
            .validationErrors(List.of(TITLE_MUST_NOT_BE_BLANK_VALIDATION_ERROR_MESSAGE, DESCRIPTION_MUST_NOT_BE_BLANK_VALIDATION_ERROR_MESSAGE))
            .build();
    }

}
