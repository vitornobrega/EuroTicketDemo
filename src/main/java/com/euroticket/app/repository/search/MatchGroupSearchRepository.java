package com.euroticket.app.repository.search;

import com.euroticket.app.domain.MatchGroup;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the MatchGroup entity.
 */
public interface MatchGroupSearchRepository extends ElasticsearchRepository<MatchGroup, Long> {
}
