package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TagCustomValue} entity.
 */
public interface TagCustomValueSearchRepository extends ElasticsearchRepository<TagCustomValue, Long> {}
