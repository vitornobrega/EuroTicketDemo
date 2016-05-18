package com.euroticket.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.euroticket.app.domain.SaleStatus;
import com.euroticket.app.repository.SaleStatusRepository;
import com.euroticket.app.repository.search.SaleStatusSearchRepository;
import com.euroticket.app.web.rest.util.HeaderUtil;
import com.euroticket.app.web.rest.dto.SaleStatusDTO;
import com.euroticket.app.web.rest.mapper.SaleStatusMapper;
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
 * REST controller for managing SaleStatus.
 */
@RestController
@RequestMapping("/api")
public class SaleStatusResource {

    private final Logger log = LoggerFactory.getLogger(SaleStatusResource.class);
        
    @Inject
    private SaleStatusRepository saleStatusRepository;
    
    @Inject
    private SaleStatusMapper saleStatusMapper;
    
    @Inject
    private SaleStatusSearchRepository saleStatusSearchRepository;
    
    /**
     * POST  /sale-statuses : Create a new saleStatus.
     *
     * @param saleStatusDTO the saleStatusDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new saleStatusDTO, or with status 400 (Bad Request) if the saleStatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sale-statuses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SaleStatusDTO> createSaleStatus(@RequestBody SaleStatusDTO saleStatusDTO) throws URISyntaxException {
        log.debug("REST request to save SaleStatus : {}", saleStatusDTO);
        if (saleStatusDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("saleStatus", "idexists", "A new saleStatus cannot already have an ID")).body(null);
        }
        SaleStatus saleStatus = saleStatusMapper.saleStatusDTOToSaleStatus(saleStatusDTO);
        saleStatus = saleStatusRepository.save(saleStatus);
        SaleStatusDTO result = saleStatusMapper.saleStatusToSaleStatusDTO(saleStatus);
        saleStatusSearchRepository.save(saleStatus);
        return ResponseEntity.created(new URI("/api/sale-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("saleStatus", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sale-statuses : Updates an existing saleStatus.
     *
     * @param saleStatusDTO the saleStatusDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated saleStatusDTO,
     * or with status 400 (Bad Request) if the saleStatusDTO is not valid,
     * or with status 500 (Internal Server Error) if the saleStatusDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sale-statuses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SaleStatusDTO> updateSaleStatus(@RequestBody SaleStatusDTO saleStatusDTO) throws URISyntaxException {
        log.debug("REST request to update SaleStatus : {}", saleStatusDTO);
        if (saleStatusDTO.getId() == null) {
            return createSaleStatus(saleStatusDTO);
        }
        SaleStatus saleStatus = saleStatusMapper.saleStatusDTOToSaleStatus(saleStatusDTO);
        saleStatus = saleStatusRepository.save(saleStatus);
        SaleStatusDTO result = saleStatusMapper.saleStatusToSaleStatusDTO(saleStatus);
        saleStatusSearchRepository.save(saleStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("saleStatus", saleStatusDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sale-statuses : get all the saleStatuses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of saleStatuses in body
     */
    @RequestMapping(value = "/sale-statuses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<SaleStatusDTO> getAllSaleStatuses() {
        log.debug("REST request to get all SaleStatuses");
        List<SaleStatus> saleStatuses = saleStatusRepository.findAll();
        return saleStatusMapper.saleStatusesToSaleStatusDTOs(saleStatuses);
    }

    /**
     * GET  /sale-statuses/:id : get the "id" saleStatus.
     *
     * @param id the id of the saleStatusDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the saleStatusDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sale-statuses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SaleStatusDTO> getSaleStatus(@PathVariable Long id) {
        log.debug("REST request to get SaleStatus : {}", id);
        SaleStatus saleStatus = saleStatusRepository.findOne(id);
        SaleStatusDTO saleStatusDTO = saleStatusMapper.saleStatusToSaleStatusDTO(saleStatus);
        return Optional.ofNullable(saleStatusDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sale-statuses/:id : delete the "id" saleStatus.
     *
     * @param id the id of the saleStatusDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sale-statuses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSaleStatus(@PathVariable Long id) {
        log.debug("REST request to delete SaleStatus : {}", id);
        saleStatusRepository.delete(id);
        saleStatusSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("saleStatus", id.toString())).build();
    }

    /**
     * SEARCH  /_search/sale-statuses?query=:query : search for the saleStatus corresponding
     * to the query.
     *
     * @param query the query of the saleStatus search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/sale-statuses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<SaleStatusDTO> searchSaleStatuses(@RequestParam String query) {
        log.debug("REST request to search SaleStatuses for query {}", query);
        return StreamSupport
            .stream(saleStatusSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(saleStatusMapper::saleStatusToSaleStatusDTO)
            .collect(Collectors.toList());
    }

}
