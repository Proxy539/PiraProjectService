package com.proxy.pira.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proxy.pira.entity.Ticket;

/** JPA repository for {@link Ticket} entities, scoped to their owning project. */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>  {

    /** Returns all tickets that belong to the given project. */
    List<Ticket> findByProjectId(Long projectId);

    /** Returns the ticket with the given id if it belongs to the given project. */
    Optional<Ticket> findByProjectIdAndId(Long projectId, Long id);

    /** Deletes the ticket with the given id only if it belongs to the given project. */
    void deleteByProjectIdAndId(Long projectId, Long id);

}
