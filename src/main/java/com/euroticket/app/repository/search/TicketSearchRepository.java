package com.euroticket.app.repository.search;

import com.euroticket.app.domain.Ticket;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Ticket entity.
 */
public interface TicketSearchRepository extends ElasticsearchRepository<Ticket, Long> {
}
