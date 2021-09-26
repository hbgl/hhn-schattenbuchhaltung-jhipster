package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.elasticsearch.ElasticTag;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ElasticTag} entity.
 */
public interface TagSearchRepository extends ElasticsearchRepository<ElasticTag, Long> {}
