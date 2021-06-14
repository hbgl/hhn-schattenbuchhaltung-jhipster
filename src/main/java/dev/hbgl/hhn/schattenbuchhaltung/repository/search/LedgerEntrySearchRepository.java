package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.LedgerEntry;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link LedgerEntry} entity.
 */
public interface LedgerEntrySearchRepository extends ElasticsearchRepository<LedgerEntry, Long> {}
