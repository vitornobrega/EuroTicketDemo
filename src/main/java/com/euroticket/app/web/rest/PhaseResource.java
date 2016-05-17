package com.euroticket.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.euroticket.app.domain.Phase;
import com.euroticket.app.service.PhaseService;
import com.euroticket.app.web.rest.util.HeaderUtil;
import com.euroticket.app.web.rest.util.PaginationUtil;
import com.euroticket.app.web.rest.dto.PhaseDTO;
import com.euroticket.app.web.rest.mapper.PhaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Phase.
 */
@RestController
@RequestMapping("/api")
public class PhaseResource {

    private final Logger log = LoggerFactory.getLogger(PhaseResource.class);
        
    @Inject
    private PhaseService phaseService;
    
    @Inject
    private PhaseMapper phaseMapper;
    
    /**
     * POST  /phases : Create a new phase.
     *
     * @param phaseDTO the phaseDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new phaseDTO, or with status 400 (Bad Request) if the phase has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/phases",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PhaseDTO> createPhase(@Valid @RequestBody PhaseDTO phaseDTO) throws URISyntaxException {
        log.debug("REST request to save Phase : {}", phaseDTO);
        if (phaseDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("phase", "idexists", "A new phase cannot already have an ID")).body(null);
        }
        PhaseDTO result = phaseService.save(phaseDTO);
        return ResponseEntity.created(new URI("/api/phases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("phase", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /phases : Updates an existing phase.
     *
     * @param phaseDTO the phaseDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated phaseDTO,
     * or with status 400 (Bad Request) if the phaseDTO is not valid,
     * or with status 500 (Internal Server Error) if the phaseDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/phases",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PhaseDTO> updatePhase(@Valid @RequestBody PhaseDTO phaseDTO) throws URISyntaxException {
        log.debug("REST request to update Phase : {}", phaseDTO);
        if (phaseDTO.getId() == null) {
            return createPhase(phaseDTO);
        }
        PhaseDTO result = phaseService.save(phaseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("phase", phaseDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /phases : get all the phases.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of phases in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/phases",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<PhaseDTO>> getAllPhases(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Phases");
        Page<Phase> page = phaseService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/phases");
        return new ResponseEntity<>(phaseMapper.phasesToPhaseDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /phases/:id : get the "id" phase.
     *
     * @param id the id of the phaseDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the phaseDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/phases/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PhaseDTO> getPhase(@PathVariable Long id) {
        log.debug("REST request to get Phase : {}", id);
        PhaseDTO phaseDTO = phaseService.findOne(id);
        return Optional.ofNullable(phaseDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /phases/:id : delete the "id" phase.
     *
     * @param id the id of the phaseDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/phases/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePhase(@PathVariable Long id) {
        log.debug("REST request to delete Phase : {}", id);
        phaseService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("phase", id.toString())).build();
    }

    /**
     * SEARCH  /_search/phases?query=:query : search for the phase corresponding
     * to the query.
     *
     * @param query the query of the phase search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/phases",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<PhaseDTO>> searchPhases(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Phases for query {}", query);
        Page<Phase> page = phaseService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/phases");
        return new ResponseEntity<>(phaseMapper.phasesToPhaseDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
