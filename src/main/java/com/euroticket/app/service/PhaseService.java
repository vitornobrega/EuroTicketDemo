package com.euroticket.app.service;

import com.euroticket.app.domain.Phase;
import com.euroticket.app.repository.PhaseRepository;
import com.euroticket.app.repository.search.PhaseSearchRepository;
import com.euroticket.app.web.rest.dto.PhaseDTO;
import com.euroticket.app.web.rest.mapper.PhaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Phase.
 */
@Service
@Transactional
public class PhaseService {

    private final Logger log = LoggerFactory.getLogger(PhaseService.class);
    
    @Inject
    private PhaseRepository phaseRepository;
    
    @Inject
    private PhaseMapper phaseMapper;
    
    @Inject
    private PhaseSearchRepository phaseSearchRepository;
    
    /**
     * Save a phase.
     * 
     * @param phaseDTO the entity to save
     * @return the persisted entity
     */
    public PhaseDTO save(PhaseDTO phaseDTO) {
        log.debug("Request to save Phase : {}", phaseDTO);
        Phase phase = phaseMapper.phaseDTOToPhase(phaseDTO);
        phase = phaseRepository.save(phase);
        PhaseDTO result = phaseMapper.phaseToPhaseDTO(phase);
        phaseSearchRepository.save(phase);
        return result;
    }

    /**
     *  Get all the phases.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Phase> findAll(Pageable pageable) {
        log.debug("Request to get all Phases");
        Page<Phase> result = phaseRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one phase by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public PhaseDTO findOne(Long id) {
        log.debug("Request to get Phase : {}", id);
        Phase phase = phaseRepository.findOne(id);
        PhaseDTO phaseDTO = phaseMapper.phaseToPhaseDTO(phase);
        return phaseDTO;
    }

    /**
     *  Delete the  phase by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Phase : {}", id);
        phaseRepository.delete(id);
        phaseSearchRepository.delete(id);
    }

    /**
     * Search for the phase corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Phase> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Phases for query {}", query);
        return phaseSearchRepository.search(queryStringQuery(query), pageable);
    }
}
