package com.proxy.pira.service;

import java.util.List;

import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.dto.UpdateTicketDto;

/** Service contract for managing projects and their nested tickets. */
public interface ProjectService {

    /** Returns all projects stored in the database. */
    List<ProjectDto> findAllProjects();

    /**
     * Returns all tickets belonging to the given project.
     *
     * @throws com.proxy.pira.exception.ResourceNotFoundException if the project does not exist
     */
    List<TicketDto> findProjectTickets(Long projectId);

    /**
     * Returns the project with the given id.
     *
     * @throws com.proxy.pira.exception.ResourceNotFoundException if the project does not exist
     */
    ProjectDto findProjectById(Long projectId);

    /** Persists a new project and returns the saved representation. */
    ProjectDto saveProject(SaveProjectDto saveProjectDto);

    /**
     * Updates an existing project's title and description.
     *
     * @throws com.proxy.pira.exception.ResourceNotFoundException if the project does not exist
     */
    ProjectDto updateProject(Long projectId, UpdateProjectDto updateProjectDto);

    /**
     * Deletes the project and all its tickets.
     *
     * @throws com.proxy.pira.exception.ResourceNotFoundException if the project does not exist
     */
    void deleteProject(Long projectId);

    /**
     * Deletes a single ticket that belongs to the given project.
     *
     * @throws com.proxy.pira.exception.ResourceNotFoundException if the project or ticket does not exist
     */
    void deleteProjectTicket(Long projectId, Long ticketId);

    /**
     * Updates an existing ticket that belongs to the given project.
     *
     * @throws com.proxy.pira.exception.ResourceNotFoundException if the project or ticket does not exist
     */
    TicketDto updateProjectTicket(Long projectId, UpdateTicketDto updateTicketDto);

    /**
     * Creates a new ticket under the given project.
     *
     * @throws com.proxy.pira.exception.ResourceNotFoundException if the project does not exist
     */
    TicketDto saveProjectTicket(Long projectId, SaveTicketDto saveTicketDto);

}
