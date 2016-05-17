package com.euroticket.app.service;

import com.euroticket.app.domain.Team;
import com.euroticket.app.repository.TeamRepository;
import com.euroticket.app.repository.search.TeamSearchRepository;
import com.euroticket.app.web.rest.dto.TeamDTO;
import com.euroticket.app.web.rest.mapper.TeamMapper;
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
 * Service Implementation for managing Team.
 */
@Service
@Transactional
public class TeamService {

    private final Logger log = LoggerFactory.getLogger(TeamService.class);
    
    @Inject
    private TeamRepository teamRepository;
    
    @Inject
    private TeamMapper teamMapper;
    
    @Inject
    private TeamSearchRepository teamSearchRepository;
    
    /**
     * Save a team.
     * 
     * @param teamDTO the entity to save
     * @return the persisted entity
     */
    public TeamDTO save(TeamDTO teamDTO) {
        log.debug("Request to save Team : {}", teamDTO);
        Team team = teamMapper.teamDTOToTeam(teamDTO);
        team = teamRepository.save(team);
        TeamDTO result = teamMapper.teamToTeamDTO(team);
        teamSearchRepository.save(team);
        return result;
    }

    /**
     *  Get all the teams.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Team> findAll(Pageable pageable) {
        log.debug("Request to get all Teams");
        Page<Team> result = teamRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one team by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public TeamDTO findOne(Long id) {
        log.debug("Request to get Team : {}", id);
        Team team = teamRepository.findOne(id);
        TeamDTO teamDTO = teamMapper.teamToTeamDTO(team);
        return teamDTO;
    }

    /**
     *  Delete the  team by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Team : {}", id);
        teamRepository.delete(id);
        teamSearchRepository.delete(id);
    }

    /**
     * Search for the team corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Team> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Teams for query {}", query);
        return teamSearchRepository.search(queryStringQuery(query), pageable);
    }
}
