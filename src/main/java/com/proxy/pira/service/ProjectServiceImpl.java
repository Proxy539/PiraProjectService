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
 * <p>All write operations go through {@link ProjectRepository} or {@link TicketRepository}.
 * MapStruct mappers handle entity↔DTO conversions so no manual field mapping is needed here.
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
     * Returns all projects in the database mapped to their DTO representation.
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
     * @throws ResourceNotFoundException if no project with the given ID exists
     */
    @Override
    public ProjectDto findProjectById(Long projectId) {
        log.info("Finding project by id: {}", projectId);
        return projectRepository.findById(projectId)
                .map(projectMapper::toProjectDto)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + projectId));
    }

    /**
     * Returns all tickets that belong to the given project.
     *
     * @throws ResourceNotFoundException if no project with the given ID exists
     */
    @Override
    public List<TicketDto> findProjectTickets(Long projectId) {
        log.info("Finding project tickets by project id {}", projectId);

        final var projectTickes = ticketRepository.findByProjectId(projectId);

        return ticketMapper.toTicketsDto(projectTickes);
    }

    /**
     * Creates a new project from the given DTO and persists it.
     *
     * @return the persisted project as a DTO (includes the generated ID)
     */
    @Override
    public ProjectDto saveProject(SaveProjectDto saveProjectDto) {
        log.info("Saving project: {}", saveProjectDto);
        final var project = projectMapper.toProject(saveProjectDto);
        final var savedProject = projectRepository.save(project);

        return projectMapper.toProjectDto(savedProject);
    }

    /**
     * Creates a new ticket and associates it with an existing project.
     *
     * <p>The project must exist before the ticket is saved; the foreign-key reference
     * is set explicitly via {@link Ticket#setProject(Project)} before persisting.
     *
     * @throws ResourceNotFoundException if no project with {@code projectId} exists
     */
    @Override
    public TicketDto saveProjectTicket(Long projectId, SaveTicketDto saveTicketDto) {
        log.info("Saving prject ticket: {}", saveTicketDto);

        final var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + projectId));

        final var ticket = ticketMapper.toTicket(saveTicketDto);

        ticket.setProject(project);

        ticketRepository.save(ticket);

        return ticketMapper.toTicketDto(ticket);

    }

    /**
     * Patches an existing project with the fields from {@code updateProjectDto}.
     *
     * <p>MapStruct's {@code @MappingTarget} is used to apply only the supplied fields onto
     * the managed entity, avoiding a full replacement.
     *
     * @throws ResourceNotFoundException if no project with {@code projectId} exists
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
     * Creates or updates a ticket on the given project.
     *
     * <p>Delegates the create-vs-update decision to {@link #saveOrUpdateTicket}.
     *
     * @throws ResourceNotFoundException if the project or ticket is not found
     */
    @Override
    public TicketDto updateProjectTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        log.info("Updating project ticket data: {}", updateTicketDto);

        final var savedTicket = saveOrUpdateTicket(projectId, updateTicketDto);

        return ticketMapper.toTicketDto(savedTicket);

    }

    /**
     * Deletes a project by its ID (cascades to its tickets via the database).
     */
    @Override
    public void deleteProject(Long projectId) {
        log.info("Deleting project by id: {}", projectId);
        projectRepository.deleteById(projectId);
    }

    /**
     * Deletes a specific ticket that belongs to the given project.
     *
     * <p>Both {@code projectId} and {@code ticketId} are required so that a ticket
     * cannot be deleted via a project it does not belong to.
     */
    @Override
    public void deleteProjectTicket(Long projectId, Long ticketId) {
        log.info("Deleting project {} ticket by id {}", projectId, ticketId);
        ticketRepository.deleteByProjectIdAndId(projectId, ticketId);
    }

    /**
     * Creates a new ticket when {@code updateTicketDto.getId()} is {@code null};
     * otherwise fetches the existing ticket and patches it in place.
     *
     * <p>This dual create/update behaviour lets callers use a single PUT endpoint
     * for both scenarios without a separate POST.
     *
     * @throws ResourceNotFoundException if the project or the existing ticket is not found
     */
    private Ticket saveOrUpdateTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        if (updateTicketDto.getId() == null) {
            final var ticket = ticketMapper.toTicket(updateTicketDto);

            final var savedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project was not found by id " + updateTicketDto.getProjectId()));

            savedProject.getTickets().add(ticket);

            return ticket;

        } else {
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
