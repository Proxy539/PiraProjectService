package com.proxy.pira.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.entity.Project;
import com.proxy.pira.exception.ResourceNotFoundException;
import com.proxy.pira.mapper.ProjectMapper;
import com.proxy.pira.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectDto> findAllProjects() {
        log.info("Finding all projects");
        final var allProjects = projectRepository.findAll();
        return projectMapper.toProjectsDto(allProjects);
    }

    @Override
    public ProjectDto findProjectById(Long projectId) {
        log.info("Finding project by id: {}", projectId);
        return projectRepository.findById(projectId)
                .map(projectMapper::toProjectDto)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + projectId));
    }

    @Override
    public ProjectDto saveProject(SaveProjectDto saveProjectDto) {
        log.info("Saving project: {}", saveProjectDto);
        final var project = projectMapper.toProject(saveProjectDto);
        final var savedProject = projectRepository.save(project);

        return projectMapper.toProjectDto(savedProject);
    }

    @Override
    public ProjectDto updateProject(UpdateProjectDto updateProjectDto) {
        log.info("Updating project data: {}", updateProjectDto);

        final var saveOrUpdateProject = saveOrUpdateProject(updateProjectDto);

        final var savedProject = projectRepository.save(saveOrUpdateProject);

        return projectMapper.toProjectDto(savedProject);

    }

    @Override
    public void deleteProject(Long projectId) {
        log.info("Deleting project by id: {}", projectId);
        projectRepository.deleteById(projectId);
    }

    private Project saveOrUpdateProject(UpdateProjectDto updateProjectDto) {
        if (updateProjectDto.getId() == null) {
            return projectMapper.toProject(updateProjectDto);
        } else {
            return projectRepository.findById(updateProjectDto.getId())
                .map(savedProject -> projectMapper.updateProject(updateProjectDto, savedProject))
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + updateProjectDto.getId()));
        }

    }

}
