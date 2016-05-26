package com.euroticket.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.euroticket.app.config.Constants;
import com.euroticket.app.domain.Payment;
import com.euroticket.app.domain.Sale;
import com.euroticket.app.domain.SaleStatus;
import com.euroticket.app.domain.User;
import com.euroticket.app.repository.PaymentRepository;
import com.euroticket.app.repository.SaleRepository;
import com.euroticket.app.repository.SaleStatusRepository;
import com.euroticket.app.repository.UserRepository;
import com.euroticket.app.repository.search.SaleSearchRepository;
import com.euroticket.app.security.SecurityUtils;
import com.euroticket.app.service.SaleService;
import com.euroticket.app.service.TeamService;
import com.euroticket.app.web.rest.util.HeaderUtil;
import com.euroticket.app.web.rest.dto.SaleDTO;
import com.euroticket.app.web.rest.dto.TeamDTO;
import com.euroticket.app.web.rest.mapper.PaymentMapper;
import com.euroticket.app.web.rest.mapper.SaleMapper;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Sale.
 */
@RestController
@RequestMapping("/api")
public class SaleResource {

    private final Logger log = LoggerFactory.getLogger(SaleResource.class);
    
    @Inject
    private SaleService saleService;
        
    @Inject
    private SaleRepository saleRepository;
    
    @Inject
    private PaymentRepository paymentRepository;
    
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private SaleStatusRepository saleStatusRepository;
    
    @Inject
    private SaleMapper saleMapper;
    
    @Inject
    private PaymentMapper paymentMapper;
    
    @Inject
    private SaleSearchRepository saleSearchRepository;
    
    /**
     * POST  /sales : Create a new sale.
     *
     * @param saleDTO the saleDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new saleDTO, or with status 400 (Bad Request) if the sale has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sales",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SaleDTO> createSale(@RequestBody SaleDTO saleDTO) throws URISyntaxException {
    	log.debug("REST request to save Sale : {}", saleDTO);
        if (saleDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("team", "idexists", "A new sale cannot already have an ID")).body(null);
        }
        SaleDTO result = saleService.save(saleDTO);
        return ResponseEntity.created(new URI("/api/sales/" + result.getId()))
            .headers(null)
            .body(result);
    }

    /**
     * PUT  /sales : Updates an existing sale.
     *
     * @param saleDTO the saleDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated saleDTO,
     * or with status 400 (Bad Request) if the saleDTO is not valid,
     * or with status 500 (Internal Server Error) if the saleDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sales",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SaleDTO> updateSale(@RequestBody SaleDTO saleDTO) throws URISyntaxException {
        log.debug("REST request to update Sale : {}", saleDTO);
        if (saleDTO.getId() == null) {
            return createSale(saleDTO);
        }
        Sale sale = saleMapper.saleDTOToSale(saleDTO);
        sale = saleRepository.save(sale);
        SaleDTO result = saleMapper.saleToSaleDTO(sale);
        saleSearchRepository.save(sale);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sale", saleDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sales : get all the sales.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of sales in body
     */
    @RequestMapping(value = "/sales",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<SaleDTO> getAllSales() {
        log.debug("REST request to get all Sales");
        List<Sale> sales = saleRepository.findByUserIsCurrentUser();
        return saleMapper.salesToSaleDTOs(sales);
    }

    /**
     * GET  /sales/:id : get the "id" sale.
     *
     * @param id the id of the saleDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the saleDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sales/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SaleDTO> getSale(@PathVariable Long id) {
    	try {
            log.debug("REST request to get Sale : {}", id);
            SaleDTO saleDTO = saleService.get(id);

            return Optional.ofNullable(saleDTO)
                .map(result -> new ResponseEntity<>(
                    result,
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    	} catch(Exception e) {
    		 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}

    }

    /**
     * DELETE  /sales/:id : delete the "id" sale.
     *
     * @param id the id of the saleDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sales/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        log.debug("REST request to delete Sale : {}", id);
        saleRepository.delete(id);
        saleSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sale", id.toString())).build();
    }

    /**
     * SEARCH  /_search/sales?query=:query : search for the sale corresponding
     * to the query.
     *
     * @param query the query of the sale search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/sales",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<SaleDTO> searchSales(@RequestParam String query) {
        log.debug("REST request to search Sales for query {}", query);
        return StreamSupport
            .stream(saleSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(saleMapper::saleToSaleDTO)
            .collect(Collectors.toList());
    }

}
