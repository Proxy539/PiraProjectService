package com.proxy.pira.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.proxy.pira.entity.Project;
import com.proxy.pira.entity.Ticket;
import com.proxy.pira.exception.ResourceNotFoundException;
import com.proxy.pira.mapper.ProjectMapper;
import com.proxy.pira.mapper.TicketMapper;
import com.proxy.pira.repository.ProjectRepository;
import com.proxy.pira.repository.TicketRepository;

import static com.proxy.pira.utils.ProjectUtils.PROJECT_1_ID;
import static com.proxy.pira.utils.ProjectUtils.buildProject;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildProjectDtos;
import static com.proxy.pira.utils.ProjectUtils.buildProjects;
import static com.proxy.pira.utils.ProjectUtils.buildSaveProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildUpdateProjectDto;
import static com.proxy.pira.utils.ProjectUtils.buildUpdateProjectDtoWithId;
import static com.proxy.pira.utils.TicketUtils.TICKET_1_ID;
import static com.proxy.pira.utils.TicketUtils.buildProjectWithTickets;
import static com.proxy.pira.utils.TicketUtils.buildSaveTicketDto;
import static com.proxy.pira.utils.TicketUtils.buildTicket;
import static com.proxy.pira.utils.TicketUtils.buildTicketDto;
import static com.proxy.pira.utils.TicketUtils.buildTicketDtos;
import static com.proxy.pira.utils.TicketUtils.buildTickets;
import static com.proxy.pira.utils.TicketUtils.buildUpdateTicketDto;
import static com.proxy.pira.utils.TicketUtils.buildUpdateTicketDtoWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    public void givenProjectsInDatabaseWhenFindAllThenReturnProjects() {
        final var projects = buildProjects();
        final var projectsDto = buildProjectDtos();

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toProjectsDto(projects)).thenReturn(projectsDto);

        final var result = projectService.findAllProjects();

        assertThat(result).isEqualTo(projectsDto);

        verify(projectRepository).findAll();
        verify(projectMapper).toProjectsDto(projects);
    }

    @Test
    public void givenNoProjectsInDatabaseWhenFindAllThenReturnEmptyList() {
        final var emptyProjectsList = List.<Project>of();

        when(projectRepository.findAll()).thenReturn(emptyProjectsList);

        final var result = projectService.findAllProjects();

        assertThat(result).isEmpty();

        verify(projectRepository).findAll();
    }

    @Test
    public void givenProjectInDatabaseWhenFindByIdThenReturnProject() {
        final var project = buildProject();
        final var optionalProject = Optional.of(project);
        final var projectDto = buildProjectDto();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(optionalProject);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.findProjectById(PROJECT_1_ID);

        assertThat(result).isEqualTo(projectDto);

        verify(projectRepository).findById(PROJECT_1_ID);

    }

    @Test
    public void givenNoProjectInDatabaseWhenFindByIdThenThrowResourceNotFoundException() {

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findProjectById(PROJECT_1_ID))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Project was not found by id " + PROJECT_1_ID);

        verify(projectRepository).findById(PROJECT_1_ID);

    }

    @Test
    public void givenNoProjectInDatabaseWhenSaveProjectThenReturnSavedProject() {
        final var saveProjectDto = buildSaveProjectDto();
        final var project = buildProject();
        final var projectDto = buildProjectDto();

        when(projectMapper.toProject(saveProjectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.saveProject(saveProjectDto);

        assertThat(result).isEqualTo(projectDto);

        verify(projectMapper).toProject(saveProjectDto);
        verify(projectRepository).save(project);
        verify(projectMapper).toProjectDto(project);
    }

    @Test
    public void givenNoProjectInDatabaseWhenUpdateProjectWithoutIdThenReturnSavedProject() {
        final var updateProjectDto = buildUpdateProjectDto();
        final var project = buildProject();
        final var projectDto = buildProjectDto();

        when(projectMapper.toProject(updateProjectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.updateProject(updateProjectDto);

        assertThat(result).isEqualTo(projectDto);

        verify(projectMapper).toProject(updateProjectDto);
        verify(projectRepository).save(project);
        verify(projectMapper).toProjectDto(project);
    }

    @Test
    public void givenNoProjectInDatabaseWhenUpdateProjectWithIdThenThrowResouceNotFoundException() {
        final var updateProjectDto = buildUpdateProjectDtoWithId();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProject(updateProjectDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Project was not found by id " + PROJECT_1_ID);

        verify(projectRepository).findById(PROJECT_1_ID);
    }

    @Test
    public void givenProjectInDatabseWhenUpdateProjectWithIdThenReturnUpdatedProject() {
        final var updateProjectDto = buildUpdateProjectDtoWithId();
        final var project = buildProject();
        final var projectDto = buildProjectDto();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.of(project));
        when(projectMapper.updateProject(updateProjectDto, project)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        final var result = projectService.updateProject(updateProjectDto);

        assertThat(result).isEqualTo(projectDto);

        verify(projectRepository).findById(PROJECT_1_ID);
        verify(projectMapper).updateProject(updateProjectDto, project);
        verify(projectRepository).save(project);
        verify(projectMapper).toProjectDto(project);
     }

    @Test
    public void givenProjectInDatabaseWhenDeleteProjectThenDeleteProject() {

        projectService.deleteProject(PROJECT_1_ID);

        verify(projectRepository).deleteById(PROJECT_1_ID);
    }

    @Test
    public void givenTicketsInDatabaseWhenFindProjectTicketsThenReturnTickets() {
        final var tickets = buildTickets();
        final var ticketDtos = buildTicketDtos();

        when(ticketRepository.findByProjectId(PROJECT_1_ID)).thenReturn(tickets);
        when(ticketMapper.toTicketsDto(tickets)).thenReturn(ticketDtos);

        final var result = projectService.findProjectTickets(PROJECT_1_ID);

        assertThat(result).isEqualTo(ticketDtos);

        verify(ticketRepository).findByProjectId(PROJECT_1_ID);
        verify(ticketMapper).toTicketsDto(tickets);
    }

    @Test
    public void givenNoTicketsInDatabaseWhenFindProjectTicketsThenReturnEmptyList() {
        final var emptyTicketsList = List.<Ticket>of();

        when(ticketRepository.findByProjectId(PROJECT_1_ID)).thenReturn(emptyTicketsList);

        final var result = projectService.findProjectTickets(PROJECT_1_ID);

        assertThat(result).isEmpty();

        verify(ticketRepository).findByProjectId(PROJECT_1_ID);
    }

    @Test
    public void givenProjectInDatabaseWhenSaveTicketThenReturnSavedTicket() {
        final var saveTicketDto = buildSaveTicketDto();
        final var project = buildProjectWithTickets();
        final var ticket = buildTicket();
        final var ticketDto = buildTicketDto();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.of(project));
        when(ticketMapper.toTicket(saveTicketDto)).thenReturn(ticket);
        when(ticketMapper.toTicketDto(ticket)).thenReturn(ticketDto);

        final var result = projectService.saveProjectTicket(PROJECT_1_ID, saveTicketDto);

        assertThat(result).isEqualTo(ticketDto);

        verify(projectRepository).findById(PROJECT_1_ID);
        verify(ticketMapper).toTicket(saveTicketDto);
        verify(projectRepository).save(project);
        verify(ticketMapper).toTicketDto(ticket);
    }

    @Test
    public void givenNoProjectInDatabaseWhenSaveTicketThenThrowResourceNotFoundException() {
        final var saveTicketDto = buildSaveTicketDto();

        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.saveProjectTicket(PROJECT_1_ID, saveTicketDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project was not found by id " + PROJECT_1_ID);

        verify(projectRepository).findById(PROJECT_1_ID);
    }

    @Test
    public void givenProjectInDatabaseWhenUpdateTicketWithoutIdThenReturnSavedTicket() {
        final var updateTicketDto = buildUpdateTicketDto();
        final var project = buildProjectWithTickets();
        final var ticket = buildTicket();
        final var ticketDto = buildTicketDto();

        when(ticketMapper.toTicket(updateTicketDto)).thenReturn(ticket);
        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.of(project));
        when(ticketMapper.toTicketDto(ticket)).thenReturn(ticketDto);

        final var result = projectService.updateProjectTicket(PROJECT_1_ID, updateTicketDto);

        assertThat(result).isEqualTo(ticketDto);

        verify(ticketMapper).toTicket(updateTicketDto);
        verify(projectRepository).findById(PROJECT_1_ID);
        verify(ticketMapper).toTicketDto(ticket);
    }

    @Test
    public void givenNoProjectInDatabaseWhenUpdateTicketWithoutIdThenThrowResourceNotFoundException() {
        final var updateTicketDto = buildUpdateTicketDto();

        when(ticketMapper.toTicket(updateTicketDto)).thenReturn(buildTicket());
        when(projectRepository.findById(PROJECT_1_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProjectTicket(PROJECT_1_ID, updateTicketDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project was not found by id " + PROJECT_1_ID);

        verify(projectRepository).findById(PROJECT_1_ID);
    }

    @Test
    public void givenTicketInDatabaseWhenUpdateTicketWithIdThenReturnUpdatedTicket() {
        final var updateTicketDto = buildUpdateTicketDtoWithId();
        final var ticket = buildTicket();
        final var ticketDto = buildTicketDto();

        when(ticketRepository.findByProjectIdAndId(PROJECT_1_ID, TICKET_1_ID)).thenReturn(Optional.of(ticket));
        when(ticketMapper.updateTicket(updateTicketDto, ticket)).thenReturn(ticket);
        when(ticketMapper.toTicketDto(ticket)).thenReturn(ticketDto);

        final var result = projectService.updateProjectTicket(PROJECT_1_ID, updateTicketDto);

        assertThat(result).isEqualTo(ticketDto);

        verify(ticketRepository).findByProjectIdAndId(PROJECT_1_ID, TICKET_1_ID);
        verify(ticketMapper).updateTicket(updateTicketDto, ticket);
        verify(ticketRepository).save(ticket);
        verify(ticketMapper).toTicketDto(ticket);
    }

    @Test
    public void givenNoTicketInDatabaseWhenUpdateTicketWithIdThenThrowResourceNotFoundException() {
        final var updateTicketDto = buildUpdateTicketDtoWithId();

        when(ticketRepository.findByProjectIdAndId(PROJECT_1_ID, TICKET_1_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProjectTicket(PROJECT_1_ID, updateTicketDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ticket was not found by id {}" + TICKET_1_ID);

        verify(ticketRepository).findByProjectIdAndId(PROJECT_1_ID, TICKET_1_ID);
    }

    @Test
    public void givenTicketInDatabaseWhenDeleteTicketThenDeleteTicket() {

        projectService.deleteProjectTicket(PROJECT_1_ID, TICKET_1_ID);

        verify(ticketRepository).deleteByProjectIdAndId(PROJECT_1_ID, TICKET_1_ID);
    }

}
