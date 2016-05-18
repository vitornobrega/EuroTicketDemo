package com.euroticket.app.repository.search;

import com.euroticket.app.domain.SaleStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the SaleStatus entity.
 */
public interface SaleStatusSearchRepository extends ElasticsearchRepository<SaleStatus, Long> {
}
