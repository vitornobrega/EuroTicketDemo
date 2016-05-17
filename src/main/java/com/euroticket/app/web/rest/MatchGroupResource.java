package com.euroticket.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.euroticket.app.domain.MatchGroup;
import com.euroticket.app.service.MatchGroupService;
import com.euroticket.app.web.rest.util.HeaderUtil;
import com.euroticket.app.web.rest.util.PaginationUtil;
import com.euroticket.app.web.rest.dto.MatchGroupDTO;
import com.euroticket.app.web.rest.mapper.MatchGroupMapper;
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
 * REST controller for managing MatchGroup.
 */
@RestController
@RequestMapping("/api")
public class MatchGroupResource {

    private final Logger log = LoggerFactory.getLogger(MatchGroupResource.class);
        
    @Inject
    private MatchGroupService matchGroupService;
    
    @Inject
    private MatchGroupMapper matchGroupMapper;
    
    /**
     * POST  /match-groups : Create a new matchGroup.
     *
     * @param matchGroupDTO the matchGroupDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new matchGroupDTO, or with status 400 (Bad Request) if the matchGroup has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/match-groups",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MatchGroupDTO> createMatchGroup(@Valid @RequestBody MatchGroupDTO matchGroupDTO) throws URISyntaxException {
        log.debug("REST request to save MatchGroup : {}", matchGroupDTO);
        if (matchGroupDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("matchGroup", "idexists", "A new matchGroup cannot already have an ID")).body(null);
        }
        MatchGroupDTO result = matchGroupService.save(matchGroupDTO);
        return ResponseEntity.created(new URI("/api/match-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("matchGroup", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /match-groups : Updates an existing matchGroup.
     *
     * @param matchGroupDTO the matchGroupDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated matchGroupDTO,
     * or with status 400 (Bad Request) if the matchGroupDTO is not valid,
     * or with status 500 (Internal Server Error) if the matchGroupDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/match-groups",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MatchGroupDTO> updateMatchGroup(@Valid @RequestBody MatchGroupDTO matchGroupDTO) throws URISyntaxException {
        log.debug("REST request to update MatchGroup : {}", matchGroupDTO);
        if (matchGroupDTO.getId() == null) {
            return createMatchGroup(matchGroupDTO);
        }
        MatchGroupDTO result = matchGroupService.save(matchGroupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("matchGroup", matchGroupDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /match-groups : get all the matchGroups.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of matchGroups in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/match-groups",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<MatchGroupDTO>> getAllMatchGroups(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of MatchGroups");
        Page<MatchGroup> page = matchGroupService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/match-groups");
        return new ResponseEntity<>(matchGroupMapper.matchGroupsToMatchGroupDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /match-groups/:id : get the "id" matchGroup.
     *
     * @param id the id of the matchGroupDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the matchGroupDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/match-groups/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MatchGroupDTO> getMatchGroup(@PathVariable Long id) {
        log.debug("REST request to get MatchGroup : {}", id);
        MatchGroupDTO matchGroupDTO = matchGroupService.findOne(id);
        return Optional.ofNullable(matchGroupDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /match-groups/:id : delete the "id" matchGroup.
     *
     * @param id the id of the matchGroupDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/match-groups/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMatchGroup(@PathVariable Long id) {
        log.debug("REST request to delete MatchGroup : {}", id);
        matchGroupService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("matchGroup", id.toString())).build();
    }

    /**
     * SEARCH  /_search/match-groups?query=:query : search for the matchGroup corresponding
     * to the query.
     *
     * @param query the query of the matchGroup search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/match-groups",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<MatchGroupDTO>> searchMatchGroups(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of MatchGroups for query {}", query);
        Page<MatchGroup> page = matchGroupService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/match-groups");
        return new ResponseEntity<>(matchGroupMapper.matchGroupsToMatchGroupDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
