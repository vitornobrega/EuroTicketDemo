package com.euroticket.app.web.rest;

import com.euroticket.app.EuroTicketDemoApp;
import com.euroticket.app.domain.Payment;
import com.euroticket.app.repository.PaymentRepository;
import com.euroticket.app.repository.search.PaymentSearchRepository;
import com.euroticket.app.web.rest.dto.PaymentDTO;
import com.euroticket.app.web.rest.mapper.PaymentMapper;

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
 * Test class for the PaymentResource REST controller.
 *
 * @see PaymentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EuroTicketDemoApp.class)
@WebAppConfiguration
@IntegrationTest
public class PaymentResourceIntTest {

    private static final String DEFAULT_CARD_NUMBER = "AAAAA";
    private static final String UPDATED_CARD_NUMBER = "BBBBB";
    private static final String DEFAULT_MONTH_AND_YEAR = "AAAAA";
    private static final String UPDATED_MONTH_AND_YEAR = "BBBBB";

    private static final Integer DEFAULT_CVC = 1;
    private static final Integer UPDATED_CVC = 2;
    private static final String DEFAULT_CARD_NAME = "AAAAA";
    private static final String UPDATED_CARD_NAME = "BBBBB";

    @Inject
    private PaymentRepository paymentRepository;

    @Inject
    private PaymentMapper paymentMapper;

    @Inject
    private PaymentSearchRepository paymentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPaymentMockMvc;

    private Payment payment;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PaymentResource paymentResource = new PaymentResource();
        ReflectionTestUtils.setField(paymentResource, "paymentSearchRepository", paymentSearchRepository);
        ReflectionTestUtils.setField(paymentResource, "paymentRepository", paymentRepository);
        ReflectionTestUtils.setField(paymentResource, "paymentMapper", paymentMapper);
        this.restPaymentMockMvc = MockMvcBuilders.standaloneSetup(paymentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        paymentSearchRepository.deleteAll();
        payment = new Payment();
        payment.setCardNumber(DEFAULT_CARD_NUMBER);
        payment.setMonthAndYear(DEFAULT_MONTH_AND_YEAR);
        payment.setCvc(DEFAULT_CVC);
        payment.setCardName(DEFAULT_CARD_NAME);
    }

    @Test
    @Transactional
    public void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.paymentToPaymentDTO(payment);

        restPaymentMockMvc.perform(post("/api/payments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
                .andExpect(status().isCreated());

        // Validate the Payment in the database
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(databaseSizeBeforeCreate + 1);
        Payment testPayment = payments.get(payments.size() - 1);
        assertThat(testPayment.getCardNumber()).isEqualTo(DEFAULT_CARD_NUMBER);
        assertThat(testPayment.getMonthAndYear()).isEqualTo(DEFAULT_MONTH_AND_YEAR);
        assertThat(testPayment.getCvc()).isEqualTo(DEFAULT_CVC);
        assertThat(testPayment.getCardName()).isEqualTo(DEFAULT_CARD_NAME);

        // Validate the Payment in ElasticSearch
        Payment paymentEs = paymentSearchRepository.findOne(testPayment.getId());
        assertThat(paymentEs).isEqualToComparingFieldByField(testPayment);
    }

    @Test
    @Transactional
    public void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the payments
        restPaymentMockMvc.perform(get("/api/payments?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
                .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].monthAndYear").value(hasItem(DEFAULT_MONTH_AND_YEAR.toString())))
                .andExpect(jsonPath("$.[*].cvc").value(hasItem(DEFAULT_CVC)))
                .andExpect(jsonPath("$.[*].cardName").value(hasItem(DEFAULT_CARD_NAME.toString())));
    }

    @Test
    @Transactional
    public void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc.perform(get("/api/payments/{id}", payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(payment.getId().intValue()))
            .andExpect(jsonPath("$.cardNumber").value(DEFAULT_CARD_NUMBER.toString()))
            .andExpect(jsonPath("$.monthAndYear").value(DEFAULT_MONTH_AND_YEAR.toString()))
            .andExpect(jsonPath("$.cvc").value(DEFAULT_CVC))
            .andExpect(jsonPath("$.cardName").value(DEFAULT_CARD_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get("/api/payments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);
        paymentSearchRepository.save(payment);
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment
        Payment updatedPayment = new Payment();
        updatedPayment.setId(payment.getId());
        updatedPayment.setCardNumber(UPDATED_CARD_NUMBER);
        updatedPayment.setMonthAndYear(UPDATED_MONTH_AND_YEAR);
        updatedPayment.setCvc(UPDATED_CVC);
        updatedPayment.setCardName(UPDATED_CARD_NAME);
        PaymentDTO paymentDTO = paymentMapper.paymentToPaymentDTO(updatedPayment);

        restPaymentMockMvc.perform(put("/api/payments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
                .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = payments.get(payments.size() - 1);
        assertThat(testPayment.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testPayment.getMonthAndYear()).isEqualTo(UPDATED_MONTH_AND_YEAR);
        assertThat(testPayment.getCvc()).isEqualTo(UPDATED_CVC);
        assertThat(testPayment.getCardName()).isEqualTo(UPDATED_CARD_NAME);

        // Validate the Payment in ElasticSearch
        Payment paymentEs = paymentSearchRepository.findOne(testPayment.getId());
        assertThat(paymentEs).isEqualToComparingFieldByField(testPayment);
    }

    @Test
    @Transactional
    public void deletePayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);
        paymentSearchRepository.save(payment);
        int databaseSizeBeforeDelete = paymentRepository.findAll().size();

        // Get the payment
        restPaymentMockMvc.perform(delete("/api/payments/{id}", payment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean paymentExistsInEs = paymentSearchRepository.exists(payment.getId());
        assertThat(paymentExistsInEs).isFalse();

        // Validate the database is empty
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);
        paymentSearchRepository.save(payment);

        // Search the payment
        restPaymentMockMvc.perform(get("/api/_search/payments?query=id:" + payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].monthAndYear").value(hasItem(DEFAULT_MONTH_AND_YEAR.toString())))
            .andExpect(jsonPath("$.[*].cvc").value(hasItem(DEFAULT_CVC)))
            .andExpect(jsonPath("$.[*].cardName").value(hasItem(DEFAULT_CARD_NAME.toString())));
    }
}
