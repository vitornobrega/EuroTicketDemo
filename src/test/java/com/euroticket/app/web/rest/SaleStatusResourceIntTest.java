package com.euroticket.app.web.rest;

import com.euroticket.app.EuroTicketDemoApp;
import com.euroticket.app.domain.SaleStatus;
import com.euroticket.app.repository.SaleStatusRepository;
import com.euroticket.app.repository.search.SaleStatusSearchRepository;
import com.euroticket.app.web.rest.dto.SaleStatusDTO;
import com.euroticket.app.web.rest.mapper.SaleStatusMapper;

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
 * Test class for the SaleStatusResource REST controller.
 *
 * @see SaleStatusResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EuroTicketDemoApp.class)
@WebAppConfiguration
@IntegrationTest
public class SaleStatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private SaleStatusRepository saleStatusRepository;

    @Inject
    private SaleStatusMapper saleStatusMapper;

    @Inject
    private SaleStatusSearchRepository saleStatusSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSaleStatusMockMvc;

    private SaleStatus saleStatus;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SaleStatusResource saleStatusResource = new SaleStatusResource();
        ReflectionTestUtils.setField(saleStatusResource, "saleStatusSearchRepository", saleStatusSearchRepository);
        ReflectionTestUtils.setField(saleStatusResource, "saleStatusRepository", saleStatusRepository);
        ReflectionTestUtils.setField(saleStatusResource, "saleStatusMapper", saleStatusMapper);
        this.restSaleStatusMockMvc = MockMvcBuilders.standaloneSetup(saleStatusResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        saleStatusSearchRepository.deleteAll();
        saleStatus = new SaleStatus();
        saleStatus.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createSaleStatus() throws Exception {
        int databaseSizeBeforeCreate = saleStatusRepository.findAll().size();

        // Create the SaleStatus
        SaleStatusDTO saleStatusDTO = saleStatusMapper.saleStatusToSaleStatusDTO(saleStatus);

        restSaleStatusMockMvc.perform(post("/api/sale-statuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(saleStatusDTO)))
                .andExpect(status().isCreated());

        // Validate the SaleStatus in the database
        List<SaleStatus> saleStatuses = saleStatusRepository.findAll();
        assertThat(saleStatuses).hasSize(databaseSizeBeforeCreate + 1);
        SaleStatus testSaleStatus = saleStatuses.get(saleStatuses.size() - 1);
        assertThat(testSaleStatus.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the SaleStatus in ElasticSearch
        SaleStatus saleStatusEs = saleStatusSearchRepository.findOne(testSaleStatus.getId());
        assertThat(saleStatusEs).isEqualToComparingFieldByField(testSaleStatus);
    }

    @Test
    @Transactional
    public void getAllSaleStatuses() throws Exception {
        // Initialize the database
        saleStatusRepository.saveAndFlush(saleStatus);

        // Get all the saleStatuses
        restSaleStatusMockMvc.perform(get("/api/sale-statuses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(saleStatus.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getSaleStatus() throws Exception {
        // Initialize the database
        saleStatusRepository.saveAndFlush(saleStatus);

        // Get the saleStatus
        restSaleStatusMockMvc.perform(get("/api/sale-statuses/{id}", saleStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(saleStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSaleStatus() throws Exception {
        // Get the saleStatus
        restSaleStatusMockMvc.perform(get("/api/sale-statuses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSaleStatus() throws Exception {
        // Initialize the database
        saleStatusRepository.saveAndFlush(saleStatus);
        saleStatusSearchRepository.save(saleStatus);
        int databaseSizeBeforeUpdate = saleStatusRepository.findAll().size();

        // Update the saleStatus
        SaleStatus updatedSaleStatus = new SaleStatus();
        updatedSaleStatus.setId(saleStatus.getId());
        updatedSaleStatus.setName(UPDATED_NAME);
        SaleStatusDTO saleStatusDTO = saleStatusMapper.saleStatusToSaleStatusDTO(updatedSaleStatus);

        restSaleStatusMockMvc.perform(put("/api/sale-statuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(saleStatusDTO)))
                .andExpect(status().isOk());

        // Validate the SaleStatus in the database
        List<SaleStatus> saleStatuses = saleStatusRepository.findAll();
        assertThat(saleStatuses).hasSize(databaseSizeBeforeUpdate);
        SaleStatus testSaleStatus = saleStatuses.get(saleStatuses.size() - 1);
        assertThat(testSaleStatus.getName()).isEqualTo(UPDATED_NAME);

        // Validate the SaleStatus in ElasticSearch
        SaleStatus saleStatusEs = saleStatusSearchRepository.findOne(testSaleStatus.getId());
        assertThat(saleStatusEs).isEqualToComparingFieldByField(testSaleStatus);
    }

    @Test
    @Transactional
    public void deleteSaleStatus() throws Exception {
        // Initialize the database
        saleStatusRepository.saveAndFlush(saleStatus);
        saleStatusSearchRepository.save(saleStatus);
        int databaseSizeBeforeDelete = saleStatusRepository.findAll().size();

        // Get the saleStatus
        restSaleStatusMockMvc.perform(delete("/api/sale-statuses/{id}", saleStatus.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean saleStatusExistsInEs = saleStatusSearchRepository.exists(saleStatus.getId());
        assertThat(saleStatusExistsInEs).isFalse();

        // Validate the database is empty
        List<SaleStatus> saleStatuses = saleStatusRepository.findAll();
        assertThat(saleStatuses).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSaleStatus() throws Exception {
        // Initialize the database
        saleStatusRepository.saveAndFlush(saleStatus);
        saleStatusSearchRepository.save(saleStatus);

        // Search the saleStatus
        restSaleStatusMockMvc.perform(get("/api/_search/sale-statuses?query=id:" + saleStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
