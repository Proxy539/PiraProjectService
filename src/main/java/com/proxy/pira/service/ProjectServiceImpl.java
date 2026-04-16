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
     * @return list of all projects as DTOs
     */
    @Override
    public List<ProjectDto> findAllProjects() {
        log.info("Finding all projects");
        final var allProjects = projectRepository.findAll();
        return projectMapper.toProjectsDto(allProjects);
    }

    /**
     * Retrieves a single project by its ID.
     *
     * @param projectId the ID of the project to retrieve
     * @return the project as a DTO
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
     * Retrieves all tickets belonging to the specified project.
     *
     * @param projectId the ID of the project whose tickets to retrieve
     * @return list of tickets for the project as DTOs
     */
    @Override
    public List<TicketDto> findProjectTickets(Long projectId) {
        log.info("Finding project tickets by project id {}", projectId);

        final var projectTickes = ticketRepository.findByProjectId(projectId);

        return ticketMapper.toTicketsDto(projectTickes);
    }

    /**
     * Creates and persists a new project.
     *
     * @param saveProjectDto DTO containing the project data to save
     * @return the newly created project as a DTO
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
     * @param saveTicketDto DTO containing the ticket data to save
     * @return the newly created ticket as a DTO
     * @throws ResourceNotFoundException if no project exists with the given ID
     */
    @Override
    public TicketDto saveProjectTicket(Long projectId, SaveTicketDto saveTicketDto) {
        log.info("Saving prject ticket: {}", saveTicketDto);

        final var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + projectId));

        final var ticket = ticketMapper.toTicket(saveTicketDto);

        // Link the ticket to its parent project before persisting
        ticket.setProject(project);

        ticketRepository.save(ticket);

        return ticketMapper.toTicketDto(ticket);

    }

    /**
     * Updates an existing project with the data provided in the DTO.
     *
     * @param projectId       the ID of the project to update
     * @param updateProjectDto DTO containing the updated project data
     * @return the updated project as a DTO
     * @throws ResourceNotFoundException if no project exists with the given ID
     */
    @Override
    public ProjectDto updateProject(Long projectId, UpdateProjectDto updateProjectDto) {
        log.info("Updating project data: {}", updateProjectDto);

        // Fetch, patch in-place via MapStruct @MappingTarget, then save
        final var updatedProject =  projectRepository.findById(projectId)
            .map(savedProject -> projectMapper.updateProject(updateProjectDto, savedProject))
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Project was not found by id " + projectId));

        final var savedProject = projectRepository.save(updatedProject);

        return projectMapper.toProjectDto(savedProject);

    }

    /**
     * Creates or updates a ticket on the specified project.
     * Delegates to {@link #saveOrUpdateTicket} to decide whether to insert or update.
     *
     * @param projectId       the ID of the project that owns the ticket
     * @param updateTicketDto DTO containing the ticket data; if {@code id} is null a new ticket is created
     * @return the saved or updated ticket as a DTO
     */
    @Override
    public TicketDto updateProjectTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        log.info("Updating project ticket data: {}", updateTicketDto);

        final var savedTicket = saveOrUpdateTicket(projectId, updateTicketDto);

        return ticketMapper.toTicketDto(savedTicket);

    }

    /**
     * Deletes a project by its ID.
     *
     * @param projectId the ID of the project to delete
     */
    @Override
    public void deleteProject(Long projectId) {
        log.info("Deleting project by id: {}", projectId);
        projectRepository.deleteById(projectId);
    }

    /**
     * Deletes a specific ticket from a project.
     *
     * @param projectId the ID of the project that owns the ticket
     * @param ticketId  the ID of the ticket to delete
     */
    @Override
    public void deleteProjectTicket(Long projectId, Long ticketId) {
        log.info("Deleting project {} ticket by id {}", projectId, ticketId);
        ticketRepository.deleteByProjectIdAndId(projectId, ticketId);
    }

    /**
     * Inserts a new ticket or updates an existing one depending on whether the DTO carries an ID.
     * If {@code updateTicketDto.getId()} is null, a new ticket is created and added to the project's
     * ticket collection. Otherwise, the existing ticket is fetched, patched in-place, and persisted.
     *
     * @param projectId       the ID of the owning project (used when creating a new ticket)
     * @param updateTicketDto DTO with the ticket data; {@code id} being null signals a create operation
     * @return the persisted {@link Ticket} entity
     * @throws ResourceNotFoundException if the project or ticket cannot be found
     */
    private Ticket saveOrUpdateTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        if (updateTicketDto.getId() == null) {
            // No ID supplied — create a new ticket and attach it to the project
            final var ticket = ticketMapper.toTicket(updateTicketDto);

            final var savedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + updateTicketDto.getProjectId()));

            savedProject.getTickets().add(ticket);

            return ticket;

        } else {
            // ID supplied — fetch the existing ticket, apply the patch, then save
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
