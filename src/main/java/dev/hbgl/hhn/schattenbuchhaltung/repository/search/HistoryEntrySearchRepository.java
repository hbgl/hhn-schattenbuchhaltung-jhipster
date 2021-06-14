package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.HistoryEntry;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link HistoryEntry} entity.
 */
public interface HistoryEntrySearchRepository extends ElasticsearchRepository<HistoryEntry, Long> {}
