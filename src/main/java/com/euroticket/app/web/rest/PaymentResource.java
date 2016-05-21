package com.euroticket.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.euroticket.app.domain.Payment;
import com.euroticket.app.repository.PaymentRepository;
import com.euroticket.app.repository.search.PaymentSearchRepository;
import com.euroticket.app.web.rest.util.HeaderUtil;
import com.euroticket.app.web.rest.dto.PaymentDTO;
import com.euroticket.app.web.rest.mapper.PaymentMapper;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Payment.
 */
@RestController
@RequestMapping("/api")
public class PaymentResource {

    private final Logger log = LoggerFactory.getLogger(PaymentResource.class);
        
    @Inject
    private PaymentRepository paymentRepository;
    
    @Inject
    private PaymentMapper paymentMapper;
    
    @Inject
    private PaymentSearchRepository paymentSearchRepository;
    
    /**
     * POST  /payments : Create a new payment.
     *
     * @param paymentDTO the paymentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new paymentDTO, or with status 400 (Bad Request) if the payment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/payments",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDTO) throws URISyntaxException {
        log.debug("REST request to save Payment : {}", paymentDTO);
        if (paymentDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("payment", "idexists", "A new payment cannot already have an ID")).body(null);
        }
        Payment payment = paymentMapper.paymentDTOToPayment(paymentDTO);
        payment = paymentRepository.save(payment);
        PaymentDTO result = paymentMapper.paymentToPaymentDTO(payment);
        paymentSearchRepository.save(payment);
        return ResponseEntity.created(new URI("/api/payments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("payment", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /payments : Updates an existing payment.
     *
     * @param paymentDTO the paymentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated paymentDTO,
     * or with status 400 (Bad Request) if the paymentDTO is not valid,
     * or with status 500 (Internal Server Error) if the paymentDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/payments",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PaymentDTO> updatePayment(@RequestBody PaymentDTO paymentDTO) throws URISyntaxException {
        log.debug("REST request to update Payment : {}", paymentDTO);
        if (paymentDTO.getId() == null) {
            return createPayment(paymentDTO);
        }
        Payment payment = paymentMapper.paymentDTOToPayment(paymentDTO);
        payment = paymentRepository.save(payment);
        PaymentDTO result = paymentMapper.paymentToPaymentDTO(payment);
        paymentSearchRepository.save(payment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("payment", paymentDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /payments : get all the payments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of payments in body
     */
    @RequestMapping(value = "/payments",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        log.debug("REST request to get all Payments");
        List<Payment> payments = paymentRepository.findAll();
        return paymentMapper.paymentsToPaymentDTOs(payments);
    }

    /**
     * GET  /payments/:id : get the "id" payment.
     *
     * @param id the id of the paymentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the paymentDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/payments/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable Long id) {
        log.debug("REST request to get Payment : {}", id);
        Payment payment = paymentRepository.findOne(id);
        PaymentDTO paymentDTO = paymentMapper.paymentToPaymentDTO(payment);
        return Optional.ofNullable(paymentDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /payments/:id : delete the "id" payment.
     *
     * @param id the id of the paymentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/payments/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.debug("REST request to delete Payment : {}", id);
        paymentRepository.delete(id);
        paymentSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("payment", id.toString())).build();
    }

    /**
     * SEARCH  /_search/payments?query=:query : search for the payment corresponding
     * to the query.
     *
     * @param query the query of the payment search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/payments",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<PaymentDTO> searchPayments(@RequestParam String query) {
        log.debug("REST request to search Payments for query {}", query);
        return StreamSupport
            .stream(paymentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(paymentMapper::paymentToPaymentDTO)
            .collect(Collectors.toList());
    }

}
