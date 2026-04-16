package com.proxy.pira.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.dto.UpdateTicketDto;
import com.proxy.pira.entity.Project;
import com.proxy.pira.entity.Ticket;
import com.proxy.pira.exception.ResourceNotFoundException;
import com.proxy.pira.mapper.ProjectMapper;
import com.proxy.pira.mapper.TicketMapper;
import com.proxy.pira.repository.ProjectRepository;
import com.proxy.pira.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of {@link ProjectService}.
 *
 * <p>Handles all CRUD operations for projects and their associated tickets.
 * Dependencies are injected via constructor (Lombok {@code @RequiredArgsConstructor}).
 */
@Service
@Slf4j
@RequiredArgsConstructor
class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final ProjectMapper projectMapper;
    private final TicketMapper ticketMapper;

    /**
     * Retrieves all projects from the database.
     *
     * @return list of all projects as DTOs; empty list if none exist
     */
    @Override
    public List<ProjectDto> findAllProjects() {
        log.info("Finding all projects");
        final var allProjects = projectRepository.findAll();
        return projectMapper.toProjectsDto(allProjects);
    }

    /**
     * Finds a single project by its ID.
     *
     * @param projectId the ID of the project to find
     * @return the matching project as a DTO
     * @throws ResourceNotFoundException if no project exists with the given ID
     */
    @Override
    public ProjectDto findProjectById(Long projectId) {
        log.info("Finding project by id: {}", projectId);
        return projectRepository.findById(projectId)
                .map(projectMapper::toProjectDto)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + projectId));
    }

    /**
     * Returns all tickets belonging to the specified project.
     *
     * @param projectId the ID of the parent project
     * @return list of tickets as DTOs; empty list if the project has no tickets
     */
    @Override
    public List<TicketDto> findProjectTickets(Long projectId) {
        log.info("Finding project tickets by project id {}", projectId);

        final var projectTickes = ticketRepository.findByProjectId(projectId);

        return ticketMapper.toTicketsDto(projectTickes);
    }

    /**
     * Persists a new project.
     *
     * @param saveProjectDto DTO containing the data for the new project
     * @return the saved project as a DTO (with generated ID)
     */
    @Override
    public ProjectDto saveProject(SaveProjectDto saveProjectDto) {
        log.info("Saving project: {}", saveProjectDto);
        final var project = projectMapper.toProject(saveProjectDto);
        final var savedProject = projectRepository.save(project);

        return projectMapper.toProjectDto(savedProject);
    }

    /**
     * Creates a new ticket and associates it with the specified project.
     *
     * @param projectId     the ID of the project to attach the ticket to
     * @param saveTicketDto DTO containing the ticket data
     * @return the saved ticket as a DTO (with generated ID)
     * @throws ResourceNotFoundException if the parent project does not exist
     */
    @Override
    public TicketDto saveProjectTicket(Long projectId, SaveTicketDto saveTicketDto) {
        log.info("Saving prject ticket: {}", saveTicketDto);

        // Verify the parent project exists before creating the ticket
        final var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + projectId));

        final var ticket = ticketMapper.toTicket(saveTicketDto);

        // Link the ticket to its parent project
        ticket.setProject(project);

        ticketRepository.save(ticket);

        return ticketMapper.toTicketDto(ticket);

    }

    /**
     * Updates an existing project with the values provided in the DTO.
     * Uses MapStruct's {@code @MappingTarget} to patch only the supplied fields.
     *
     * @param projectId       the ID of the project to update
     * @param updateProjectDto DTO containing the updated fields
     * @return the updated project as a DTO
     * @throws ResourceNotFoundException if no project exists with the given ID
     */
    @Override
    public ProjectDto updateProject(Long projectId, UpdateProjectDto updateProjectDto) {
        log.info("Updating project data: {}", updateProjectDto);

        final var updatedProject =  projectRepository.findById(projectId)
            .map(savedProject -> projectMapper.updateProject(updateProjectDto, savedProject))
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Project was not found by id " + projectId));

        final var savedProject = projectRepository.save(updatedProject);

        return projectMapper.toProjectDto(savedProject);

    }

    /**
     * Creates or updates a ticket within a project.
     * Delegates to {@link #saveOrUpdateTicket(Long, UpdateTicketDto)} to determine
     * whether to insert a new ticket or patch an existing one.
     *
     * @param projectId       the ID of the parent project
     * @param updateTicketDto DTO containing ticket data; {@code id} field determines create vs. update
     * @return the saved/updated ticket as a DTO
     * @throws ResourceNotFoundException if the project or ticket is not found
     */
    @Override
    public TicketDto updateProjectTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        log.info("Updating project ticket data: {}", updateTicketDto);

        final var savedTicket = saveOrUpdateTicket(projectId, updateTicketDto);

        return ticketMapper.toTicketDto(savedTicket);

    }

    /**
     * Deletes a project and all its associated data by ID.
     *
     * @param projectId the ID of the project to delete
     */
    @Override
    public void deleteProject(Long projectId) {
        log.info("Deleting project by id: {}", projectId);
        projectRepository.deleteById(projectId);
    }

    /**
     * Removes a specific ticket from a project.
     *
     * @param projectId the ID of the parent project
     * @param ticketId  the ID of the ticket to delete
     */
    @Override
    public void deleteProjectTicket(Long projectId, Long ticketId) {
        log.info("Deleting project {} ticket by id {}", projectId, ticketId);
        ticketRepository.deleteByProjectIdAndId(projectId, ticketId);
    }

    /**
     * Decides whether to create a new ticket or update an existing one.
     *
     * <ul>
     *   <li>If {@code updateTicketDto.getId()} is {@code null} — creates a new ticket and
     *       adds it to the project's ticket collection.</li>
     *   <li>If {@code updateTicketDto.getId()} is present — looks up the existing ticket
     *       by project ID + ticket ID and applies the DTO's changes via MapStruct.</li>
     * </ul>
     *
     * @param projectId       the ID of the parent project
     * @param updateTicketDto DTO with ticket data; {@code id == null} means create
     * @return the persisted {@link Ticket} entity
     * @throws ResourceNotFoundException if the project or the existing ticket cannot be found
     */
    private Ticket saveOrUpdateTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        if (updateTicketDto.getId() == null) {
            // No ID supplied — treat this as a new ticket creation
            final var ticket = ticketMapper.toTicket(updateTicketDto);

            final var savedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + updateTicketDto.getProjectId()));

            // Add to the project's collection; cascade will persist the ticket
            savedProject.getTickets().add(ticket);

            return ticket;

        } else {
            // ID present — fetch and patch the existing ticket
            final var ticket = ticketRepository
                    .findByProjectIdAndId(updateTicketDto.getProjectId(), updateTicketDto.getId())
                    .map(savedTicket -> ticketMapper.updateTicket(updateTicketDto, savedTicket))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ticket was not found by id " + updateTicketDto.getId()));

            ticketRepository.save(ticket);

            return ticket;

        }
    }

}
