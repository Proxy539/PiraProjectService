package com.proxy.pira.service;

import java.util.List;

import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.dto.UpdateTicketDto;

public interface ProjectService {

    List<ProjectDto> findAllProjects();

    List<TicketDto> findProjectTickets(Long projectId);

    ProjectDto findProjectById(Long projectId);

    ProjectDto saveProject(SaveProjectDto saveProjectDto);

    ProjectDto updateProject(Long projectId, UpdateProjectDto updateProjectDto);

    void deleteProject(Long projectId);

    void deleteProjectTicket(Long projectId, Long ticketId);

    TicketDto updateProjectTicket(Long projectId, UpdateTicketDto updateTicketDto);

    TicketDto saveProjectTicket(Long projectId, SaveTicketDto saveTicketDto);

}
