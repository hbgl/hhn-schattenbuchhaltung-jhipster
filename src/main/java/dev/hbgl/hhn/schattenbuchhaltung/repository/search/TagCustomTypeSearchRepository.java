package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.TagCustomType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TagCustomType} entity.
 */
public interface TagCustomTypeSearchRepository extends ElasticsearchRepository<TagCustomType, Long> {}
