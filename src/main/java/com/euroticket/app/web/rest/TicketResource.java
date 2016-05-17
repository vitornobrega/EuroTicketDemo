package com.euroticket.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.euroticket.app.domain.Ticket;
import com.euroticket.app.repository.TicketRepository;
import com.euroticket.app.repository.search.TicketSearchRepository;
import com.euroticket.app.web.rest.util.HeaderUtil;
import com.euroticket.app.web.rest.dto.TicketDTO;
import com.euroticket.app.web.rest.mapper.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Ticket.
 */
@RestController
@RequestMapping("/api")
public class TicketResource {

    private final Logger log = LoggerFactory.getLogger(TicketResource.class);
        
    @Inject
    private TicketRepository ticketRepository;
    
    @Inject
    private TicketMapper ticketMapper;
    
    @Inject
    private TicketSearchRepository ticketSearchRepository;
    
    /**
     * POST  /tickets : Create a new ticket.
     *
     * @param ticketDTO the ticketDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ticketDTO, or with status 400 (Bad Request) if the ticket has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tickets",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketDTO ticketDTO) throws URISyntaxException {
        log.debug("REST request to save Ticket : {}", ticketDTO);
        if (ticketDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ticket", "idexists", "A new ticket cannot already have an ID")).body(null);
        }
        Ticket ticket = ticketMapper.ticketDTOToTicket(ticketDTO);
        ticket = ticketRepository.save(ticket);
        TicketDTO result = ticketMapper.ticketToTicketDTO(ticket);
        ticketSearchRepository.save(ticket);
        return ResponseEntity.created(new URI("/api/tickets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("ticket", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tickets : Updates an existing ticket.
     *
     * @param ticketDTO the ticketDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ticketDTO,
     * or with status 400 (Bad Request) if the ticketDTO is not valid,
     * or with status 500 (Internal Server Error) if the ticketDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tickets",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TicketDTO> updateTicket(@RequestBody TicketDTO ticketDTO) throws URISyntaxException {
        log.debug("REST request to update Ticket : {}", ticketDTO);
        if (ticketDTO.getId() == null) {
            return createTicket(ticketDTO);
        }
        Ticket ticket = ticketMapper.ticketDTOToTicket(ticketDTO);
        ticket = ticketRepository.save(ticket);
        TicketDTO result = ticketMapper.ticketToTicketDTO(ticket);
        ticketSearchRepository.save(ticket);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("ticket", ticketDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tickets : get all the tickets.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tickets in body
     */
    @RequestMapping(value = "/tickets",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<TicketDTO> getAllTickets() {
        log.debug("REST request to get all Tickets");
        List<Ticket> tickets = ticketRepository.findAll();
        return ticketMapper.ticketsToTicketDTOs(tickets);
    }

    /**
     * GET  /tickets/:id : get the "id" ticket.
     *
     * @param id the id of the ticketDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ticketDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tickets/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TicketDTO> getTicket(@PathVariable Long id) {
        log.debug("REST request to get Ticket : {}", id);
        Ticket ticket = ticketRepository.findOne(id);
        TicketDTO ticketDTO = ticketMapper.ticketToTicketDTO(ticket);
        return Optional.ofNullable(ticketDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tickets/:id : delete the "id" ticket.
     *
     * @param id the id of the ticketDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tickets/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        log.debug("REST request to delete Ticket : {}", id);
        ticketRepository.delete(id);
        ticketSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ticket", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tickets?query=:query : search for the ticket corresponding
     * to the query.
     *
     * @param query the query of the ticket search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/tickets",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<TicketDTO> searchTickets(@RequestParam String query) {
        log.debug("REST request to search Tickets for query {}", query);
        return StreamSupport
            .stream(ticketSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(ticketMapper::ticketToTicketDTO)
            .collect(Collectors.toList());
    }

}
