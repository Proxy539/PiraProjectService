package com.proxy.pira.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.proxy.pira.dto.SaveProjectDto;
import com.proxy.pira.dto.SaveTicketDto;
import com.proxy.pira.dto.TicketDto;
import com.proxy.pira.dto.ProjectDto;
import com.proxy.pira.dto.UpdateProjectDto;
import com.proxy.pira.dto.UpdateTicketDto;
import com.proxy.pira.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectDto> findAllProjects() {
        return projectService.findAllProjects();
    }

    @GetMapping("/{projectId}")
    public ProjectDto findProjectById(@PathVariable Long projectId) {
        return projectService.findProjectById(projectId);
    }

    @GetMapping("/{projectId}/tickets")
    public List<TicketDto> findProjectTickets(@PathVariable Long projectId) {
        return projectService.findProjectTickets(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto saveProject(@RequestBody @Valid SaveProjectDto saveProjectDto) {
        return projectService.saveProject(saveProjectDto);
    }

    @PostMapping("/{projectId}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto saveTicket(@PathVariable Long projectId, @RequestBody @Valid SaveTicketDto saveTicketDto) {
        return projectService.saveProjectTicket(projectId, saveTicketDto);
    }

    @PutMapping
    public ProjectDto updateProject(@RequestBody @Valid UpdateProjectDto updateProjectDto) {
        return projectService.updateProject(updateProjectDto);
    }

    @PutMapping("/{projectId}/tickets")
    public TicketDto updateTicket(@PathVariable Long projectId, @RequestBody @Valid UpdateTicketDto updateTicektDto) {
        return projectService.updateProjectTicket(projectId, updateTicektDto);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }

    @DeleteMapping("/{projectId}/tickets/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTickcet(@PathVariable Long projectId, @PathVariable Long ticketId) {
        projectService.deleteProjectTicket(projectId, ticketId);
    }

}
