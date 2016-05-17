package com.euroticket.app.repository;

import com.euroticket.app.domain.Ticket;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
public interface TicketRepository extends JpaRepository<Ticket,Long> {

}
