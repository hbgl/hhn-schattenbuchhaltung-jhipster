package dev.hbgl.hhn.schattenbuchhaltung.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CostTypeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CostTypeSearchRepositoryMockConfiguration {

    @MockBean
    private CostTypeSearchRepository mockCostTypeSearchRepository;
}
