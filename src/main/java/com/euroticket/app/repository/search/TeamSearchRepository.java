package com.euroticket.app.repository.search;

import com.euroticket.app.domain.Team;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Team entity.
 */
public interface TeamSearchRepository extends ElasticsearchRepository<Team, Long> {
}
