package com.proxy.pira.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proxy.pira.entity.Project;
import com.proxy.pira.exception.ResourceNotFoundException;
import com.proxy.pira.mapper.ProjectMapper;
import com.proxy.pira.repository.ProjectRepository;

import static com.proxy.pira.utils.ProjectUtils.PROJECT_1_ID;
import static com.proxy.pira.utils.ProjectUtils.buildProject;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDtos;
import static com.proxy.pira.utils.ProjectUtils.buildProjects;
import static com.proxy.pira.utils.ProjectUtils.buildSaveProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildUpdateProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildUpdateProjectDtoWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    public void givenProjectsInDatabaseWhenFindAllThenReturnProjects() {
        final var projects = buildProjects();
        final var projectsDto = buildProjectDtos();

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toProjectsDto(projects)).thenReturn(projectsDto);

        final var result = projectService.findAllProjects();

        assertThat(result).isEqualTo(projectsDto);

        verify(projectRepository).findAll();
        verify(projectMapper).toProjectsDto(projects);
    }

    @Test
    public void givenNoProjectsInDatabaseWhenFindAllThenReturnEmptyList() {
        final var emptyProjectsList = List.<Project>of();

        when(projectRepository.findAll()).thenReturn(emptyProjectsList);

        final var result = projectService.findAllProjects();

        assertThat(result).isEmpty();

        verify(projectRepository).findAll();
    }

    @Test
    public void givenProjectInDatabaseWhenFindByIdThenReturnProject() {
        final var project = buildProject();
        final var optionalProject = Optional.of(project);
        final var projectDto = buildProjectDto();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(optionalProject);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.findProjectById(PROJECT_1_ID);

        assertThat(result).isEqualTo(projectDto);

        verify(projectRepository).findById(PROJECT_1_ID);

    }

    @Test
    public void givenNoProjectInDatabaseWhenFindByIdThenThrowResourceNotFoundException() {

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findProjectById(PROJECT_1_ID))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Project was not found by id " + PROJECT_1_ID);

        verify(projectRepository).findById(PROJECT_1_ID);

    }

    @Test
    public void givenNoProjectInDatabaseWhenSaveProjectThenReturnSavedProject() {
        final var saveProjectDto = buildSaveProjectDto();
        final var project = buildProject();
        final var projectDto = buildProjectDto();

        when(projectMapper.toProject(saveProjectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.saveProject(saveProjectDto);

        assertThat(result).isEqualTo(projectDto);

        verify(projectMapper).toProject(saveProjectDto);
        verify(projectRepository).save(project);
        verify(projectMapper).toProjectDto(project);
    }

    @Test
    public void givenNoProjectInDatabaseWhenUpdateProjectWithoutIdThenReturnSavedProject() {
        final var updateProjectDto = buildUpdateProjectDto();
        final var project = buildProject();
        final var projectDto = buildProjectDto();

        when(projectMapper.toProject(updateProjectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.updateProject(updateProjectDto);

        assertThat(result).isEqualTo(projectDto);

        verify(projectMapper).toProject(updateProjectDto);
        verify(projectRepository).save(project);
        verify(projectMapper).toProjectDto(project);
    }

    @Test
    public void givenNoProjectInDatabaseWhenUpdateProjectWithIdThenThrowResouceNotFoundException() {
        final var updateProjectDto = buildUpdateProjectDtoWithId();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProject(updateProjectDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Project was not found by id " + PROJECT_1_ID);

        verify(projectRepository).findById(PROJECT_1_ID);
    }

    @Test
    public void givenProjectInDatabseWhenUpdateProjectWithIdThenReturnUpdatedProject() {
        final var updateProjectDto = buildUpdateProjectDtoWithId();
        final var project = buildProject();
        final var projectDto = buildProjectDto();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.of(project));
        when(projectMapper.updateProject(updateProjectDto, project)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.updateProject(updateProjectDto);

        assertThat(result).isEqualTo(projectDto);

        verify(projectRepository).findById(PROJECT_1_ID);
        verify(projectMapper).updateProject(updateProjectDto, project);
        verify(projectRepository).save(project);
        verify(projectMapper).toProjectDto(project);
     }

    @Test
    public void givenProjectInDatabaseWhenDeleteProjectThenDeleteProject() {

        projectService.deleteProject(PROJECT_1_ID);

        verify(projectRepository).deleteById(PROJECT_1_ID);
    }


}
