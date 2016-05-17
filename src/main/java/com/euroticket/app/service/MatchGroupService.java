package com.euroticket.app.service;

import com.euroticket.app.domain.MatchGroup;
import com.euroticket.app.repository.MatchGroupRepository;
import com.euroticket.app.repository.search.MatchGroupSearchRepository;
import com.euroticket.app.web.rest.dto.MatchGroupDTO;
import com.euroticket.app.web.rest.mapper.MatchGroupMapper;
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
 * Service Implementation for managing MatchGroup.
 */
@Service
@Transactional
public class MatchGroupService {

    private final Logger log = LoggerFactory.getLogger(MatchGroupService.class);
    
    @Inject
    private MatchGroupRepository matchGroupRepository;
    
    @Inject
    private MatchGroupMapper matchGroupMapper;
    
    @Inject
    private MatchGroupSearchRepository matchGroupSearchRepository;
    
    /**
     * Save a matchGroup.
     * 
     * @param matchGroupDTO the entity to save
     * @return the persisted entity
     */
    public MatchGroupDTO save(MatchGroupDTO matchGroupDTO) {
        log.debug("Request to save MatchGroup : {}", matchGroupDTO);
        MatchGroup matchGroup = matchGroupMapper.matchGroupDTOToMatchGroup(matchGroupDTO);
        matchGroup = matchGroupRepository.save(matchGroup);
        MatchGroupDTO result = matchGroupMapper.matchGroupToMatchGroupDTO(matchGroup);
        matchGroupSearchRepository.save(matchGroup);
        return result;
    }

    /**
     *  Get all the matchGroups.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<MatchGroup> findAll(Pageable pageable) {
        log.debug("Request to get all MatchGroups");
        Page<MatchGroup> result = matchGroupRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one matchGroup by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public MatchGroupDTO findOne(Long id) {
        log.debug("Request to get MatchGroup : {}", id);
        MatchGroup matchGroup = matchGroupRepository.findOne(id);
        MatchGroupDTO matchGroupDTO = matchGroupMapper.matchGroupToMatchGroupDTO(matchGroup);
        return matchGroupDTO;
    }

    /**
     *  Delete the  matchGroup by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MatchGroup : {}", id);
        matchGroupRepository.delete(id);
        matchGroupSearchRepository.delete(id);
    }

    /**
     * Search for the matchGroup corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MatchGroup> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MatchGroups for query {}", query);
        return matchGroupSearchRepository.search(queryStringQuery(query), pageable);
    }
}
