package com.euroticket.app.web.rest;

import com.euroticket.app.EuroTicketDemoApp;
import com.euroticket.app.domain.MatchGroup;
import com.euroticket.app.repository.MatchGroupRepository;
import com.euroticket.app.service.MatchGroupService;
import com.euroticket.app.repository.search.MatchGroupSearchRepository;
import com.euroticket.app.web.rest.dto.MatchGroupDTO;
import com.euroticket.app.web.rest.mapper.MatchGroupMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MatchGroupResource REST controller.
 *
 * @see MatchGroupResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EuroTicketDemoApp.class)
@WebAppConfiguration
@IntegrationTest
public class MatchGroupResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private MatchGroupRepository matchGroupRepository;

    @Inject
    private MatchGroupMapper matchGroupMapper;

    @Inject
    private MatchGroupService matchGroupService;

    @Inject
    private MatchGroupSearchRepository matchGroupSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMatchGroupMockMvc;

    private MatchGroup matchGroup;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MatchGroupResource matchGroupResource = new MatchGroupResource();
        ReflectionTestUtils.setField(matchGroupResource, "matchGroupService", matchGroupService);
        ReflectionTestUtils.setField(matchGroupResource, "matchGroupMapper", matchGroupMapper);
        this.restMatchGroupMockMvc = MockMvcBuilders.standaloneSetup(matchGroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        matchGroupSearchRepository.deleteAll();
        matchGroup = new MatchGroup();
        matchGroup.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createMatchGroup() throws Exception {
        int databaseSizeBeforeCreate = matchGroupRepository.findAll().size();

        // Create the MatchGroup
        MatchGroupDTO matchGroupDTO = matchGroupMapper.matchGroupToMatchGroupDTO(matchGroup);

        restMatchGroupMockMvc.perform(post("/api/match-groups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(matchGroupDTO)))
                .andExpect(status().isCreated());

        // Validate the MatchGroup in the database
        List<MatchGroup> matchGroups = matchGroupRepository.findAll();
        assertThat(matchGroups).hasSize(databaseSizeBeforeCreate + 1);
        MatchGroup testMatchGroup = matchGroups.get(matchGroups.size() - 1);
        assertThat(testMatchGroup.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the MatchGroup in ElasticSearch
        MatchGroup matchGroupEs = matchGroupSearchRepository.findOne(testMatchGroup.getId());
        assertThat(matchGroupEs).isEqualToComparingFieldByField(testMatchGroup);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = matchGroupRepository.findAll().size();
        // set the field null
        matchGroup.setName(null);

        // Create the MatchGroup, which fails.
        MatchGroupDTO matchGroupDTO = matchGroupMapper.matchGroupToMatchGroupDTO(matchGroup);

        restMatchGroupMockMvc.perform(post("/api/match-groups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(matchGroupDTO)))
                .andExpect(status().isBadRequest());

        List<MatchGroup> matchGroups = matchGroupRepository.findAll();
        assertThat(matchGroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMatchGroups() throws Exception {
        // Initialize the database
        matchGroupRepository.saveAndFlush(matchGroup);

        // Get all the matchGroups
        restMatchGroupMockMvc.perform(get("/api/match-groups?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(matchGroup.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getMatchGroup() throws Exception {
        // Initialize the database
        matchGroupRepository.saveAndFlush(matchGroup);

        // Get the matchGroup
        restMatchGroupMockMvc.perform(get("/api/match-groups/{id}", matchGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(matchGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMatchGroup() throws Exception {
        // Get the matchGroup
        restMatchGroupMockMvc.perform(get("/api/match-groups/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMatchGroup() throws Exception {
        // Initialize the database
        matchGroupRepository.saveAndFlush(matchGroup);
        matchGroupSearchRepository.save(matchGroup);
        int databaseSizeBeforeUpdate = matchGroupRepository.findAll().size();

        // Update the matchGroup
        MatchGroup updatedMatchGroup = new MatchGroup();
        updatedMatchGroup.setId(matchGroup.getId());
        updatedMatchGroup.setName(UPDATED_NAME);
        MatchGroupDTO matchGroupDTO = matchGroupMapper.matchGroupToMatchGroupDTO(updatedMatchGroup);

        restMatchGroupMockMvc.perform(put("/api/match-groups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(matchGroupDTO)))
                .andExpect(status().isOk());

        // Validate the MatchGroup in the database
        List<MatchGroup> matchGroups = matchGroupRepository.findAll();
        assertThat(matchGroups).hasSize(databaseSizeBeforeUpdate);
        MatchGroup testMatchGroup = matchGroups.get(matchGroups.size() - 1);
        assertThat(testMatchGroup.getName()).isEqualTo(UPDATED_NAME);

        // Validate the MatchGroup in ElasticSearch
        MatchGroup matchGroupEs = matchGroupSearchRepository.findOne(testMatchGroup.getId());
        assertThat(matchGroupEs).isEqualToComparingFieldByField(testMatchGroup);
    }

    @Test
    @Transactional
    public void deleteMatchGroup() throws Exception {
        // Initialize the database
        matchGroupRepository.saveAndFlush(matchGroup);
        matchGroupSearchRepository.save(matchGroup);
        int databaseSizeBeforeDelete = matchGroupRepository.findAll().size();

        // Get the matchGroup
        restMatchGroupMockMvc.perform(delete("/api/match-groups/{id}", matchGroup.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean matchGroupExistsInEs = matchGroupSearchRepository.exists(matchGroup.getId());
        assertThat(matchGroupExistsInEs).isFalse();

        // Validate the database is empty
        List<MatchGroup> matchGroups = matchGroupRepository.findAll();
        assertThat(matchGroups).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMatchGroup() throws Exception {
        // Initialize the database
        matchGroupRepository.saveAndFlush(matchGroup);
        matchGroupSearchRepository.save(matchGroup);

        // Search the matchGroup
        restMatchGroupMockMvc.perform(get("/api/_search/match-groups?query=id:" + matchGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(matchGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
