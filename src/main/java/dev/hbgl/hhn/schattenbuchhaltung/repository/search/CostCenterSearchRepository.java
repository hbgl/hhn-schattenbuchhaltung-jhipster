package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import dev.hbgl.hhn.schattenbuchhaltung.domain.CostCenter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CostCenter} entity.
 */
public interface CostCenterSearchRepository extends ElasticsearchRepository<CostCenter, Long> {}
