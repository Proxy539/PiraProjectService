package com.proxy.pira.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.entity.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDto toProjectDto(Project project);

    List<ProjectDto> toProjectsDto(List<Project> projects);

    Project toProject(SaveProjectDto saveProjectDto);

    Project toProject(UpdateProjectDto updateProjectDto);

    @Mapping(target = "id", ignore = true)
    Project updateProject(UpdateProjectDto updateProjectDto, @MappingTarget Project project);

}
