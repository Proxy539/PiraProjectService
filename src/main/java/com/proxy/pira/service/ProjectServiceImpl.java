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
 * Spring-managed {@link ProjectService} backed by JPA repositories.
 *
 * <p>All entity-to-DTO conversions are handled by MapStruct mappers injected
 * via constructor. Throws {@link com.proxy.pira.exception.ResourceNotFoundException}
 * whenever a requested resource cannot be found.
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
     * Returns all projects stored in the database.
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
     * Finds a single project by its primary key.
     *
     * @param projectId the ID of the project to retrieve
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
     * Returns all tickets that belong to the specified project.
     *
     * @param projectId the ID of the owning project
     * @return list of tickets as DTOs; empty list if the project has no tickets
     */
    @Override
    public List<TicketDto> findProjectTickets(Long projectId) {
        log.info("Finding project tickets by project id {}", projectId);

        final var projectTickes = ticketRepository.findByProjectId(projectId);

        return ticketMapper.toTicketsDto(projectTickes);
    }

    /**
     * Persists a new project derived from the supplied DTO.
     *
     * @param saveProjectDto validated DTO carrying the title and description
     * @return the newly created project as a DTO, including the generated ID
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
     * @param projectId      the ID of the project that will own the ticket
     * @param saveTicketDto  validated DTO carrying the ticket details
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
     * Applies a partial update to an existing project.
     *
     * <p>The mapper patches only the fields supplied in the DTO, leaving
     * the entity's {@code id} untouched (enforced via {@code @MappingTarget}).
     *
     * @param projectId        the ID of the project to update
     * @param updateProjectDto DTO containing the fields to overwrite
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
     * Creates or updates a ticket within the specified project.
     *
     * <p>Delegates to {@link #saveOrUpdateTicket} to determine whether to insert
     * a new ticket or patch an existing one based on the presence of an ID in the DTO.
     *
     * @param projectId        the ID of the owning project
     * @param updateTicketDto  DTO containing the ticket data; if {@code id} is null a
     *                         new ticket is created, otherwise the existing one is patched
     * @return the saved ticket as a DTO
     * @throws ResourceNotFoundException if the project or ticket cannot be found
     */
    @Override
    public TicketDto updateProjectTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        log.info("Updating project ticket data: {}", updateTicketDto);

        final var savedTicket = saveOrUpdateTicket(projectId, updateTicketDto);

        return ticketMapper.toTicketDto(savedTicket);

    }

    /**
     * Deletes a project and all of its associated tickets.
     *
     * @param projectId the ID of the project to delete; no-op if it does not exist
     */
    @Override
    public void deleteProject(Long projectId) {
        log.info("Deleting project by id: {}", projectId);
        projectRepository.deleteById(projectId);
    }

    /**
     * Deletes a specific ticket from a project.
     *
     * @param projectId the ID of the owning project
     * @param ticketId  the ID of the ticket to delete; no-op if it does not exist
     */
    @Override
    public void deleteProjectTicket(Long projectId, Long ticketId) {
        log.info("Deleting project {} ticket by id {}", projectId, ticketId);
        ticketRepository.deleteByProjectIdAndId(projectId, ticketId);
    }

    /**
     * Inserts a new ticket or patches an existing one, depending on whether the DTO
     * carries an {@code id}.
     *
     * <ul>
     *   <li>No {@code id} — maps the DTO to a new {@link Ticket}, attaches it to the
     *       project, and returns it (the project's cascade setting persists it).</li>
     *   <li>{@code id} present — fetches the existing ticket by project + ticket ID,
     *       applies the DTO fields via the mapper, saves, and returns it.</li>
     * </ul>
     *
     * @param projectId       the ID of the owning project
     * @param updateTicketDto DTO with the ticket data and optional ID
     * @return the persisted {@link Ticket} entity
     * @throws ResourceNotFoundException if the required project or ticket is not found
     */
    private Ticket saveOrUpdateTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        if (updateTicketDto.getId() == null) {
            // No ID supplied — treat as a create request
            final var ticket = ticketMapper.toTicket(updateTicketDto);

            final var savedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + updateTicketDto.getProjectId()));

            savedProject.getTickets().add(ticket);

            return ticket;

        } else {
            // ID supplied — fetch and patch the existing ticket
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
