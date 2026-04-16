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
    public List<TicketDto> findProjectTickets(Long projectId) {
        log.info("Finding project tickets by project id {}", projectId);

        final var projectTickes = ticketRepository.findByProjectId(projectId);

        return ticketMapper.toTicketsDto(projectTickes);
    }

    @Override
    public ProjectDto saveProject(SaveProjectDto saveProjectDto) {
        log.info("Saving project: {}", saveProjectDto);
        final var project = projectMapper.toProject(saveProjectDto);
        final var savedProject = projectRepository.save(project);

        return projectMapper.toProjectDto(savedProject);
    }

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

    @Override
    public TicketDto updateProjectTicket(Long projectId, UpdateTicketDto updateTicketDto) {
        log.info("Updating project ticket data: {}", updateTicketDto);
        
        final var savedTicket = saveOrUpdateTicket(projectId, updateTicketDto);

        return ticketMapper.toTicketDto(savedTicket);

    }

    @Override
    public void deleteProject(Long projectId) {
        log.info("Deleting project by id: {}", projectId);
        projectRepository.deleteById(projectId);
    }

    @Override
    public void deleteProjectTicket(Long projectId, Long ticketId) {
        log.info("Deleting project {} ticket by id {}", projectId, ticketId);
        ticketRepository.deleteByProjectIdAndId(projectId, ticketId);
    }

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
