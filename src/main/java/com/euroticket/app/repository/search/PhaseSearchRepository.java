package com.euroticket.app.repository.search;

import com.euroticket.app.domain.Phase;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Phase entity.
 */
public interface PhaseSearchRepository extends ElasticsearchRepository<Phase, Long> {
}
