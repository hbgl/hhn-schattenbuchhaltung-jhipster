package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.Division;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Division} entity.
 */
public interface DivisionSearchRepository extends ElasticsearchRepository<Division, Long> {}
