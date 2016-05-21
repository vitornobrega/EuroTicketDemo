package com.euroticket.app.repository.search;

import com.euroticket.app.domain.Payment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Payment entity.
 */
public interface PaymentSearchRepository extends ElasticsearchRepository<Payment, Long> {
}
