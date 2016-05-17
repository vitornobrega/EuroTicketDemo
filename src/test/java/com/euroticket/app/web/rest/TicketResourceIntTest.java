package com.euroticket.app.web.rest;

import com.euroticket.app.EuroTicketDemoApp;
import com.euroticket.app.domain.Ticket;
import com.euroticket.app.repository.TicketRepository;
import com.euroticket.app.repository.search.TicketSearchRepository;
import com.euroticket.app.web.rest.dto.TicketDTO;
import com.euroticket.app.web.rest.mapper.TicketMapper;

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
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TicketResource REST controller.
 *
 * @see TicketResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EuroTicketDemoApp.class)
@WebAppConfiguration
@IntegrationTest
public class TicketResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_LOCATION = "AAAAA";
    private static final String UPDATED_LOCATION = "BBBBB";

    private static final ZonedDateTime DEFAULT_MATCH_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_MATCH_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_MATCH_DATE_STR = dateTimeFormatter.format(DEFAULT_MATCH_DATE);

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_AVAILABLE_QTT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVAILABLE_QTT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_QTT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_QTT = new BigDecimal(2);

    @Inject
    private TicketRepository ticketRepository;

    @Inject
    private TicketMapper ticketMapper;

    @Inject
    private TicketSearchRepository ticketSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TicketResource ticketResource = new TicketResource();
        ReflectionTestUtils.setField(ticketResource, "ticketSearchRepository", ticketSearchRepository);
        ReflectionTestUtils.setField(ticketResource, "ticketRepository", ticketRepository);
        ReflectionTestUtils.setField(ticketResource, "ticketMapper", ticketMapper);
        this.restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        ticketSearchRepository.deleteAll();
        ticket = new Ticket();
        ticket.setLocation(DEFAULT_LOCATION);
        ticket.setMatchDate(DEFAULT_MATCH_DATE);
        ticket.setUnitPrice(DEFAULT_UNIT_PRICE);
        ticket.setAvailableQtt(DEFAULT_AVAILABLE_QTT);
        ticket.setTotalQtt(DEFAULT_TOTAL_QTT);
    }

    @Test
    @Transactional
    public void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.ticketToTicketDTO(ticket);

        restTicketMockMvc.perform(post("/api/tickets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
                .andExpect(status().isCreated());

        // Validate the Ticket in the database
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeCreate + 1);
        Ticket testTicket = tickets.get(tickets.size() - 1);
        assertThat(testTicket.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testTicket.getMatchDate()).isEqualTo(DEFAULT_MATCH_DATE);
        assertThat(testTicket.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testTicket.getAvailableQtt()).isEqualTo(DEFAULT_AVAILABLE_QTT);
        assertThat(testTicket.getTotalQtt()).isEqualTo(DEFAULT_TOTAL_QTT);

        // Validate the Ticket in ElasticSearch
        Ticket ticketEs = ticketSearchRepository.findOne(testTicket.getId());
        assertThat(ticketEs).isEqualToComparingFieldByField(testTicket);
    }

    @Test
    @Transactional
    public void getAllTickets() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the tickets
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
                .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
                .andExpect(jsonPath("$.[*].matchDate").value(hasItem(DEFAULT_MATCH_DATE_STR)))
                .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].availableQtt").value(hasItem(DEFAULT_AVAILABLE_QTT.intValue())))
                .andExpect(jsonPath("$.[*].totalQtt").value(hasItem(DEFAULT_TOTAL_QTT.intValue())));
    }

    @Test
    @Transactional
    public void getTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.matchDate").value(DEFAULT_MATCH_DATE_STR))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE.intValue()))
            .andExpect(jsonPath("$.availableQtt").value(DEFAULT_AVAILABLE_QTT.intValue()))
            .andExpect(jsonPath("$.totalQtt").value(DEFAULT_TOTAL_QTT.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        ticketSearchRepository.save(ticket);
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket
        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(ticket.getId());
        updatedTicket.setLocation(UPDATED_LOCATION);
        updatedTicket.setMatchDate(UPDATED_MATCH_DATE);
        updatedTicket.setUnitPrice(UPDATED_UNIT_PRICE);
        updatedTicket.setAvailableQtt(UPDATED_AVAILABLE_QTT);
        updatedTicket.setTotalQtt(UPDATED_TOTAL_QTT);
        TicketDTO ticketDTO = ticketMapper.ticketToTicketDTO(updatedTicket);

        restTicketMockMvc.perform(put("/api/tickets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
                .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = tickets.get(tickets.size() - 1);
        assertThat(testTicket.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testTicket.getMatchDate()).isEqualTo(UPDATED_MATCH_DATE);
        assertThat(testTicket.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testTicket.getAvailableQtt()).isEqualTo(UPDATED_AVAILABLE_QTT);
        assertThat(testTicket.getTotalQtt()).isEqualTo(UPDATED_TOTAL_QTT);

        // Validate the Ticket in ElasticSearch
        Ticket ticketEs = ticketSearchRepository.findOne(testTicket.getId());
        assertThat(ticketEs).isEqualToComparingFieldByField(testTicket);
    }

    @Test
    @Transactional
    public void deleteTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        ticketSearchRepository.save(ticket);
        int databaseSizeBeforeDelete = ticketRepository.findAll().size();

        // Get the ticket
        restTicketMockMvc.perform(delete("/api/tickets/{id}", ticket.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean ticketExistsInEs = ticketSearchRepository.exists(ticket.getId());
        assertThat(ticketExistsInEs).isFalse();

        // Validate the database is empty
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        ticketSearchRepository.save(ticket);

        // Search the ticket
        restTicketMockMvc.perform(get("/api/_search/tickets?query=id:" + ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].matchDate").value(hasItem(DEFAULT_MATCH_DATE_STR)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].availableQtt").value(hasItem(DEFAULT_AVAILABLE_QTT.intValue())))
            .andExpect(jsonPath("$.[*].totalQtt").value(hasItem(DEFAULT_TOTAL_QTT.intValue())));
    }
}
