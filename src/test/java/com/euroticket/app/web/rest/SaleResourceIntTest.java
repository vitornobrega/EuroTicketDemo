package com.euroticket.app.web.rest;

import com.euroticket.app.EuroTicketDemoApp;
import com.euroticket.app.domain.Sale;
import com.euroticket.app.repository.SaleRepository;
import com.euroticket.app.repository.search.SaleSearchRepository;
import com.euroticket.app.web.rest.dto.SaleDTO;
import com.euroticket.app.web.rest.mapper.SaleMapper;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SaleResource REST controller.
 *
 * @see SaleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EuroTicketDemoApp.class)
@WebAppConfiguration
@IntegrationTest
public class SaleResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_SALE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_SALE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_SALE_DATE_STR = dateTimeFormatter.format(DEFAULT_SALE_DATE);

    @Inject
    private SaleRepository saleRepository;

    @Inject
    private SaleMapper saleMapper;

    @Inject
    private SaleSearchRepository saleSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSaleMockMvc;

    private Sale sale;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SaleResource saleResource = new SaleResource();
        ReflectionTestUtils.setField(saleResource, "saleSearchRepository", saleSearchRepository);
        ReflectionTestUtils.setField(saleResource, "saleRepository", saleRepository);
        ReflectionTestUtils.setField(saleResource, "saleMapper", saleMapper);
        this.restSaleMockMvc = MockMvcBuilders.standaloneSetup(saleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        saleSearchRepository.deleteAll();
        sale = new Sale();
        sale.setSaleDate(DEFAULT_SALE_DATE);
    }

    @Test
    @Transactional
    public void createSale() throws Exception {
        int databaseSizeBeforeCreate = saleRepository.findAll().size();

        // Create the Sale
        SaleDTO saleDTO = saleMapper.saleToSaleDTO(sale);

        restSaleMockMvc.perform(post("/api/sales")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(saleDTO)))
                .andExpect(status().isCreated());

        // Validate the Sale in the database
        List<Sale> sales = saleRepository.findAll();
        assertThat(sales).hasSize(databaseSizeBeforeCreate + 1);
        Sale testSale = sales.get(sales.size() - 1);
        assertThat(testSale.getSaleDate()).isEqualTo(DEFAULT_SALE_DATE);

        // Validate the Sale in ElasticSearch
        Sale saleEs = saleSearchRepository.findOne(testSale.getId());
        assertThat(saleEs).isEqualToComparingFieldByField(testSale);
    }

    @Test
    @Transactional
    public void getAllSales() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);

        // Get all the sales
        restSaleMockMvc.perform(get("/api/sales?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
                .andExpect(jsonPath("$.[*].saleDate").value(hasItem(DEFAULT_SALE_DATE_STR)));
    }

    @Test
    @Transactional
    public void getSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);

        // Get the sale
        restSaleMockMvc.perform(get("/api/sales/{id}", sale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sale.getId().intValue()))
            .andExpect(jsonPath("$.saleDate").value(DEFAULT_SALE_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingSale() throws Exception {
        // Get the sale
        restSaleMockMvc.perform(get("/api/sales/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);
        saleSearchRepository.save(sale);
        int databaseSizeBeforeUpdate = saleRepository.findAll().size();

        // Update the sale
        Sale updatedSale = new Sale();
        updatedSale.setId(sale.getId());
        updatedSale.setSaleDate(UPDATED_SALE_DATE);
        SaleDTO saleDTO = saleMapper.saleToSaleDTO(updatedSale);

        restSaleMockMvc.perform(put("/api/sales")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(saleDTO)))
                .andExpect(status().isOk());

        // Validate the Sale in the database
        List<Sale> sales = saleRepository.findAll();
        assertThat(sales).hasSize(databaseSizeBeforeUpdate);
        Sale testSale = sales.get(sales.size() - 1);
        assertThat(testSale.getSaleDate()).isEqualTo(UPDATED_SALE_DATE);

        // Validate the Sale in ElasticSearch
        Sale saleEs = saleSearchRepository.findOne(testSale.getId());
        assertThat(saleEs).isEqualToComparingFieldByField(testSale);
    }

    @Test
    @Transactional
    public void deleteSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);
        saleSearchRepository.save(sale);
        int databaseSizeBeforeDelete = saleRepository.findAll().size();

        // Get the sale
        restSaleMockMvc.perform(delete("/api/sales/{id}", sale.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean saleExistsInEs = saleSearchRepository.exists(sale.getId());
        assertThat(saleExistsInEs).isFalse();

        // Validate the database is empty
        List<Sale> sales = saleRepository.findAll();
        assertThat(sales).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSale() throws Exception {
        // Initialize the database
        saleRepository.saveAndFlush(sale);
        saleSearchRepository.save(sale);

        // Search the sale
        restSaleMockMvc.perform(get("/api/_search/sales?query=id:" + sale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].saleDate").value(hasItem(DEFAULT_SALE_DATE_STR)));
    }
}
