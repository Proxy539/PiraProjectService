package com.proxy.pira.service;

import java.util.List;

import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.UpdateProjectDto;

public interface ProjectService {

    List<ProjectDto> findAllProjects();

    ProjectDto findProjectById(Long projectId);

    ProjectDto saveProject(SaveProjectDto saveProjectDto);

    ProjectDto updateProject(UpdateProjectDto updateProjectDto);

    void deleteProject(Long projectId);

}
