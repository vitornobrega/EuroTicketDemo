package com.euroticket.app.repository.search;

import com.euroticket.app.domain.Sale;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Sale entity.
 */
public interface SaleSearchRepository extends ElasticsearchRepository<Sale, Long> {
}
