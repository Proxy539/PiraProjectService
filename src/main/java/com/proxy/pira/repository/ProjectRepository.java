package com.proxy.pira.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proxy.pira.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    
}
