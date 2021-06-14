package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntryField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link HistoryEntryField} entity.
 */
public interface HistoryEntryFieldSearchRepository extends ElasticsearchRepository<HistoryEntryField, Long> {}
