package com.proxy.pira.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.entity.Project;

/** MapStruct mapper for converting between {@link Project} entities and their DTOs. */
@Mapper(componentModel = "spring", uses = {TicketMapper.class})
public interface ProjectMapper {

    /** Converts a {@link Project} entity to its response DTO. */
    ProjectDto toProjectDto(Project project);

    /** Converts a list of {@link Project} entities to a list of response DTOs. */
    List<ProjectDto> toProjectsDto(List<Project> projects);

    /** Creates a new {@link Project} entity from a creation request DTO. */
    Project toProject(SaveProjectDto saveProjectDto);

    /** Creates a new {@link Project} entity from an update request DTO. */
    Project toProject(UpdateProjectDto updateProjectDto);

    /**
     * Patches {@code project} in-place with values from {@code updateProjectDto}.
     * The entity {@code id} is intentionally left unchanged.
     */
    @Mapping(target = "id", ignore = true)
    Project updateProject(UpdateProjectDto updateProjectDto, @MappingTarget Project project);

}
