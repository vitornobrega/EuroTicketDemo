package com.euroticket.app.web.rest;

import com.euroticket.app.EuroTicketDemoApp;
import com.euroticket.app.domain.Phase;
import com.euroticket.app.repository.PhaseRepository;
import com.euroticket.app.service.PhaseService;
import com.euroticket.app.repository.search.PhaseSearchRepository;
import com.euroticket.app.web.rest.dto.PhaseDTO;
import com.euroticket.app.web.rest.mapper.PhaseMapper;

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
 * Test class for the PhaseResource REST controller.
 *
 * @see PhaseResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EuroTicketDemoApp.class)
@WebAppConfiguration
@IntegrationTest
public class PhaseResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private PhaseRepository phaseRepository;

    @Inject
    private PhaseMapper phaseMapper;

    @Inject
    private PhaseService phaseService;

    @Inject
    private PhaseSearchRepository phaseSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPhaseMockMvc;

    private Phase phase;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PhaseResource phaseResource = new PhaseResource();
        ReflectionTestUtils.setField(phaseResource, "phaseService", phaseService);
        ReflectionTestUtils.setField(phaseResource, "phaseMapper", phaseMapper);
        this.restPhaseMockMvc = MockMvcBuilders.standaloneSetup(phaseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        phaseSearchRepository.deleteAll();
        phase = new Phase();
        phase.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createPhase() throws Exception {
        int databaseSizeBeforeCreate = phaseRepository.findAll().size();

        // Create the Phase
        PhaseDTO phaseDTO = phaseMapper.phaseToPhaseDTO(phase);

        restPhaseMockMvc.perform(post("/api/phases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(phaseDTO)))
                .andExpect(status().isCreated());

        // Validate the Phase in the database
        List<Phase> phases = phaseRepository.findAll();
        assertThat(phases).hasSize(databaseSizeBeforeCreate + 1);
        Phase testPhase = phases.get(phases.size() - 1);
        assertThat(testPhase.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Phase in ElasticSearch
        Phase phaseEs = phaseSearchRepository.findOne(testPhase.getId());
        assertThat(phaseEs).isEqualToComparingFieldByField(testPhase);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = phaseRepository.findAll().size();
        // set the field null
        phase.setName(null);

        // Create the Phase, which fails.
        PhaseDTO phaseDTO = phaseMapper.phaseToPhaseDTO(phase);

        restPhaseMockMvc.perform(post("/api/phases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(phaseDTO)))
                .andExpect(status().isBadRequest());

        List<Phase> phases = phaseRepository.findAll();
        assertThat(phases).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPhases() throws Exception {
        // Initialize the database
        phaseRepository.saveAndFlush(phase);

        // Get all the phases
        restPhaseMockMvc.perform(get("/api/phases?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(phase.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getPhase() throws Exception {
        // Initialize the database
        phaseRepository.saveAndFlush(phase);

        // Get the phase
        restPhaseMockMvc.perform(get("/api/phases/{id}", phase.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(phase.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPhase() throws Exception {
        // Get the phase
        restPhaseMockMvc.perform(get("/api/phases/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePhase() throws Exception {
        // Initialize the database
        phaseRepository.saveAndFlush(phase);
        phaseSearchRepository.save(phase);
        int databaseSizeBeforeUpdate = phaseRepository.findAll().size();

        // Update the phase
        Phase updatedPhase = new Phase();
        updatedPhase.setId(phase.getId());
        updatedPhase.setName(UPDATED_NAME);
        PhaseDTO phaseDTO = phaseMapper.phaseToPhaseDTO(updatedPhase);

        restPhaseMockMvc.perform(put("/api/phases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(phaseDTO)))
                .andExpect(status().isOk());

        // Validate the Phase in the database
        List<Phase> phases = phaseRepository.findAll();
        assertThat(phases).hasSize(databaseSizeBeforeUpdate);
        Phase testPhase = phases.get(phases.size() - 1);
        assertThat(testPhase.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Phase in ElasticSearch
        Phase phaseEs = phaseSearchRepository.findOne(testPhase.getId());
        assertThat(phaseEs).isEqualToComparingFieldByField(testPhase);
    }

    @Test
    @Transactional
    public void deletePhase() throws Exception {
        // Initialize the database
        phaseRepository.saveAndFlush(phase);
        phaseSearchRepository.save(phase);
        int databaseSizeBeforeDelete = phaseRepository.findAll().size();

        // Get the phase
        restPhaseMockMvc.perform(delete("/api/phases/{id}", phase.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean phaseExistsInEs = phaseSearchRepository.exists(phase.getId());
        assertThat(phaseExistsInEs).isFalse();

        // Validate the database is empty
        List<Phase> phases = phaseRepository.findAll();
        assertThat(phases).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPhase() throws Exception {
        // Initialize the database
        phaseRepository.saveAndFlush(phase);
        phaseSearchRepository.save(phase);

        // Search the phase
        restPhaseMockMvc.perform(get("/api/_search/phases?query=id:" + phase.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phase.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
