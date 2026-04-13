package com.proxy.pira.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proxy.pira.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>  {

    List<Ticket> findByProjectId(Long projectId);
    Optional<Ticket> findByProjectIdAndId(Long projectId, Long id);
    void deleteByProjectIdAndId(Long projectId, Long id);
    
}
